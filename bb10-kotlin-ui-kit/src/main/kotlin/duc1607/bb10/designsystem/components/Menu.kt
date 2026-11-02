package duc1607.bb10.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.tokens.Bb10Dimens
import kotlin.math.roundToInt

/**
 * How items talk to the right-hand menu that acts on them, provided through [LocalBb10Menu]
 * ([Bb10Scaffold] does it for its overflow menu; an app with its own frame can do the same).
 * A touch hold first [peek]s (the menu slides in as far as its icons), the finger then [move]s,
 * possibly onto one of those icons, and lifting it is a [release]: the entry under the finger
 * runs, or else the menu opens fully. The Menu key [open]s it at once.
 */
@Stable
class Bb10MenuController(
    val peek: () -> Unit,
    val open: () -> Unit,
    /** The finger that holds the item, in root coordinates. */
    val move: (Offset) -> Unit,
    val release: () -> Unit,
)

/** The menu that items under it open with [bb10Clickable]'s `onMenu`; null when there is none. */
val LocalBb10Menu = compositionLocalOf<Bb10MenuController?> { null }

/** How far the right-hand menu slides in while an item is still held: its column of icons. */
val Bb10Dimens.menuPeekWidth: Dp get() = spaceL + iconSize + spaceS

/**
 * A finger holding an item while the right-hand menu peeks in: where it is, and which entry of
 * [Bb10MenuEntries] it is on. The owner of the menu feeds it from [Bb10MenuController.move] and
 * asks [end] on release.
 */
@Stable
class Bb10MenuHold {
    /** Where the finger is, in root coordinates; null when no item is held. */
    var point by mutableStateOf<Offset?>(null)
        private set

    // What is on screen of each entry, in root coordinates, by action id.
    internal val targets = mutableStateMapOf<String, Pair<Bb10Action, Rect>>()

    // How far left of the icons the finger may still be, in px.
    internal var slop = 0f

    /** The entry under the finger, when it is on one that can be used. */
    val hovered: Bb10Action?
        get() {
            val p = point ?: return null
            return targets.values.firstOrNull { (action, r) ->
                action.enabled && p.y >= r.top && p.y < r.bottom && p.x >= r.left - slop
            }?.first
        }

    fun move(to: Offset) {
        point = to
    }

    /** The finger lifted: the entry it was on, if any. */
    fun end(): Bb10Action? {
        val picked = hovered
        point = null
        return picked
    }
}

/**
 * The entries of a right-hand menu, filling its panel: the [actions] in the middle of the height
 * and the destructive ones (Delete, Remove) at the bottom on their own. More than fit scroll.
 *
 * While an item is held over the peeking menu ([hold]), only the icons show at the edge of the
 * screen; the entry under the finger slides out with its label, with an accent edge, and is the
 * one that runs when the finger lifts.
 */
@Composable
fun Bb10MenuEntries(
    actions: List<Bb10Action>,
    onPick: (Bb10Action) -> Unit,
    modifier: Modifier = Modifier,
    hold: Bb10MenuHold? = null,
    showHints: Boolean = false,
    /** Given to the first entry, for the focus to land on when the menu opens. */
    firstFocus: FocusRequester? = null,
) {
    val d = Bb10.dimens
    val density = LocalDensity.current
    if (hold != null) hold.slop = with(density) { d.spaceL.toPx() }
    val main = actions.filterNot { it.destructive }
    val bottom = actions.filter { it.destructive }
    var origin by remember { mutableStateOf(Offset.Zero) }
    // The bounds in root are not cut to the screen: the part of the panel off it counts too.
    var screenRight by remember { mutableFloatStateOf(Float.POSITIVE_INFINITY) }

    @Composable
    fun Entry(action: Bb10Action, focus: FocusRequester?) {
        if (hold != null) {
            DisposableEffect(action.id, hold) {
                onDispose { hold.targets.remove(action.id) }
            }
        }
        Bb10LinkItem(
            title = action.label,
            icon = action.icon,
            hint = if (showHints) action.shortcut?.hintLabel() else null,
            enabled = action.enabled,
            focusRequester = focus,
            modifier = if (hold == null) Modifier else Modifier.onGloballyPositioned {
                hold.targets[action.id] = action to it.boundsInRoot()
            },
            onClick = { onPick(action) },
        )
    }

    Box(
        modifier.fillMaxSize().onGloballyPositioned {
            origin = it.positionInRoot()
            screenRight = it.findRootCoordinates().size.width.toFloat()
        }
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val height = constraints.maxHeight
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                CenteredWithFooter(minHeight = height) {
                    Column(Modifier.fillMaxWidth()) {
                        main.forEachIndexed { i, a -> Entry(a, if (i == 0) firstFocus else null) }
                    }
                    Column(Modifier.fillMaxWidth()) {
                        if (main.isNotEmpty() && bottom.isNotEmpty()) Bb10Divider()
                        bottom.forEachIndexed { i, a -> Entry(a, if (i == 0 && main.isEmpty()) firstFocus else null) }
                    }
                }
            }
        }
        val hovered = hold?.hovered
        val target = hovered?.let { hold.targets[it.id]?.second }
        if (hovered != null && target != null) {
            HeldEntry(hovered, target.copy(right = minOf(target.right, screenRight)), origin)
        }
    }
}

/** The first of two children centred in at least [minHeight] px, the second at the bottom. */
@Composable
private fun CenteredWithFooter(minHeight: Int, content: @Composable () -> Unit) {
    Layout(content) { measurables, constraints ->
        val loose = constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
        val (middle, footer) = measurables.map { it.measure(loose) }
        val height = maxOf(minHeight, middle.height + footer.height)
        val footerTop = height - footer.height
        layout(constraints.maxWidth, height) {
            middle.place(0, ((height - middle.height) / 2).coerceAtMost(footerTop - middle.height).coerceAtLeast(0))
            footer.place(0, footerTop)
        }
    }
}

/**
 * The entry under a finger that holds an item: drawn over the peeking menu, it slides out to the
 * left from the column of icons as far as its label needs, its right edge where the icons end
 * ([target]: what is on screen of the entry).
 */
@Composable
private fun HeldEntry(action: Bb10Action, target: Rect, origin: Offset) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val accent = c.accent
    val slide = remember(action.id) { Animatable(1f) }
    LaunchedEffect(action.id) { slide.animateTo(0f, tween(120)) }
    Box(
        Modifier.layout { measurable, constraints ->
            val row = measurable.measure(Constraints(maxWidth = constraints.maxWidth))
            val shown = target.right - origin.x
            val hidden = (row.width - shown).coerceAtLeast(0f)
            layout(0, 0) {
                row.place(
                    x = (shown - row.width + hidden * slide.value).roundToInt(),
                    y = (target.top - origin.y).roundToInt(),
                )
            }
        }
    ) {
        Row(
            Modifier
                .background(c.surface)
                .drawBehind { drawRect(accent, size = Size(3.dp.toPx(), size.height)) }
                .layout { measurable, constraints ->
                    val h = target.height.roundToInt()
                    val p = measurable.measure(constraints.copy(minHeight = h, maxHeight = h))
                    layout(p.width, h) { p.place(0, 0) }
                }
                .padding(start = d.spaceL),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(d.spaceM),
        ) {
            Bb10Icon(action.icon, tint = c.textPrimary)
            Bb10Text(action.label, style = Bb10.typography.body, color = c.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(d.menuPeekWidth))
        }
    }
}
