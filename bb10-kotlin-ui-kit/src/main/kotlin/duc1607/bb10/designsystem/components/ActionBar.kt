package duc1607.bb10.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10

/**
 * An action for the [Bb10ActionBar]. [shortcut] is a physical-keyboard key that triggers it
 * from anywhere in a [Bb10Scaffold] (letters typed in a text field never trigger it).
 *
 * [primary] marks the app's main call to action: it is drawn large, as an accent circle in the
 * centre of the bar. Only the first primary action is used.
 *
 * [Key.Delete] and [Key.Backspace] are interchangeable as a shortcut: the physical "Del" key of
 * a BlackBerry-style keyboard is Android's DEL (Backspace) and shows the hint "Del".
 */
@Immutable
class Bb10Action(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val shortcut: Key? = null,
    val enabled: Boolean = true,
    val primary: Boolean = false,
    val onClick: () -> Unit,
)

/** Whether [pressed] triggers an action with [shortcut]; the two delete keys count as one. */
internal fun shortcutMatches(shortcut: Key?, pressed: Key): Boolean {
    if (shortcut == null) return false
    if (shortcut == pressed) return true
    fun Key.isDelete() = this == Key.Delete || this == Key.Backspace
    return shortcut.isDelete() && pressed.isDelete()
}

/** How actions are spread over the bar: the primary one, the plain ones, and the overflow. */
internal class ActionSlots(
    val primary: Bb10Action?,
    val visible: List<Bb10Action>,
    val overflow: List<Bb10Action>,
)

/**
 * [maxVisible] counts every slot of the bar, the primary action and the "…" button included.
 * Actions that do not fit go to [ActionSlots.overflow] (the right-hand menu).
 */
internal fun splitActions(actions: List<Bb10Action>, maxVisible: Int): ActionSlots {
    val primary = actions.firstOrNull { it.primary }
    val others = actions.filter { it !== primary }
    val capacity = (maxVisible - if (primary != null) 1 else 0).coerceAtLeast(0)
    return if (others.size <= capacity) {
        ActionSlots(primary, others, emptyList())
    } else {
        val keep = (capacity - 1).coerceAtLeast(0)
        ActionSlots(primary, others.take(keep), others.drop(keep))
    }
}

/** Width of the "..." button at the far right (and of the matching spacer on the far left). */
private val MoreButtonWidth = 52.dp

/** The Back button is a little wider than the others: its right edge is the slash. */
private val BackButtonWidth = 56.dp

/** How far the slash leans: the button is this much narrower at the bottom than at the top. */
private val BackSlashLean = 10.dp

/** Action-bar icons are a touch larger than the regular icons, with room above the label. */
private val ActionIconSize = 28.dp
private val IconLabelGap = 4.dp

/**
 * Gap between the bottom edge of the bar and the label of every action, the primary one
 * included, so all labels share one baseline (icon + label are centred in the bar height).
 */
@Composable
private fun labelBottomPadding(): Dp {
    val d = Bb10.dimens
    return ((d.actionBarHeight - ActionIconSize - IconLabelGap - 14.dp) / 2).coerceAtLeast(4.dp)
}

/**
 * Bottom action bar, BB10 style:
 *
 * - the primary action is a large accent circle in the middle; it sits on the same bottom
 *   margin as its neighbours, it is just bigger, so it rises above the top of the bar
 * - the other actions sit in a tight cluster around it (not spread over the full width)
 * - when some actions do not fit, a lone "..." button at the far right opens the right-hand menu
 * - at the far left, a lone icon button: the navigation menu or Back (see `leading`); never both
 * - with no action at all (only the side buttons) there is no bar: the two buttons float as
 *   small tiles in the corners and the middle of the screen is left free
 *
 * The composable is as tall as its tallest child (the primary circle), so the whole circle is
 * hit-testable; place it over the bottom of the content (see [Bb10Scaffold]).
 */
@Composable
internal fun Bb10ActionBar(
    slots: ActionSlots,
    modifier: Modifier = Modifier,
    leading: Bb10Action? = null,
    leadingIsBack: Boolean = false,
    /** Selection mode's Cancel shows its label under the icon; Back and the menu do not. */
    leadingShowsLabel: Boolean = false,
    moreFocusRequester: FocusRequester? = null,
    onMore: () -> Unit = {},
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val primary = slots.primary
    val hasOverflow = slots.overflow.isNotEmpty()
    val barHeight = d.actionBarHeight
    val strings = Bb10.strings
    val more = remember(onMore, strings) {
        Bb10Action("more", strings.moreActions, Bb10Icons.More, onClick = onMore)
    }
    // Both ends are reserved when either exists, so the primary action stays centred.
    val sides = hasOverflow || leading != null
    val sideWidth = if (leadingIsBack) BackButtonWidth else MoreButtonWidth

    if (primary == null && slots.visible.isEmpty()) {
        // Nothing in the middle: no bar, just the side buttons as tiles in the corners.
        val tile = c.surfaceVariant.copy(alpha = 0.9f)
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            if (leading != null) {
                // Back alone in a corner tile: no slash, just the chevron.
                ActionBarItem(leading, Modifier.size(barHeight).background(tile), showLabel = leadingShowsLabel)
            }
            Spacer(Modifier.weight(1f))
            if (hasOverflow) {
                ActionBarItem(
                    more,
                    Modifier.size(barHeight).background(tile),
                    focusRequester = moreFocusRequester,
                    showLabel = false,
                )
            }
        }
        return
    }

    BoxWithConstraints(modifier.fillMaxWidth()) {
        val count = slots.visible.size
        val primarySlot = if (primary != null) d.primaryActionSize + 32.dp else 0.dp
        val reserved = (if (sides) sideWidth * 2 else 0.dp) + primarySlot
        // The primary action is always exactly in the middle: the other actions are split into
        // two halves of equal width, and the extra one (odd count, or a single action) goes to
        // the right of it, where the right thumb is.
        val leftCount = if (primary != null) count / 2 else 0
        val rightCount = count - leftCount
        val itemWidth = when {
            count == 0 -> 0.dp
            primary == null -> minOf(72.dp, (maxWidth - reserved) / count)
            else -> minOf(72.dp, ((maxWidth - reserved) / 2) / maxOf(leftCount, rightCount))
        }

        // The visible bar: divider + surface, only as tall as the plain actions.
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(c.surfaceVariant)
        ) {
            Bb10Divider()
            Spacer(Modifier.height(barHeight))
        }
        Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            if (leading != null) {
                ActionBarItem(
                    leading,
                    modifier = Modifier.width(sideWidth).height(barHeight),
                    showLabel = leadingShowsLabel,
                    // Back: the right edge of the button leans, and it is the accent slash.
                    slant = if (leadingIsBack) BackSlashLean else 0.dp,
                )
            } else if (sides) {
                Spacer(Modifier.width(sideWidth))
            }
            if (primary == null) {
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    slots.visible.forEach { a ->
                        ActionBarItem(a, Modifier.width(itemWidth).height(barHeight))
                    }
                }
            } else {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.Bottom) {
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        slots.visible.take(leftCount).forEach { a ->
                            ActionBarItem(a, Modifier.width(itemWidth).height(barHeight))
                        }
                    }
                    PrimaryActionItem(primary, Modifier.width(primarySlot))
                    Row(
                        Modifier.weight(1f),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        slots.visible.drop(leftCount).forEach { a ->
                            ActionBarItem(a, Modifier.width(itemWidth).height(barHeight))
                        }
                    }
                }
            }
            if (hasOverflow) {
                if (sideWidth > MoreButtonWidth) Spacer(Modifier.width(sideWidth - MoreButtonWidth))
                ActionBarItem(
                    more,
                    modifier = Modifier.width(MoreButtonWidth).height(barHeight),
                    focusRequester = moreFocusRequester,
                    showLabel = false,
                )
            } else if (sides) {
                Spacer(Modifier.width(sideWidth))
            }
        }
    }
}

@Composable
private fun ActionBarItem(
    action: Bb10Action,
    modifier: Modifier,
    focusRequester: FocusRequester? = null,
    showLabel: Boolean = true,
    /** The Back button's lean (0 for every other button): see [BackSlashLean]. */
    slant: Dp = 0.dp,
) {
    val c = Bb10.colors
    val highlight = rememberBb10Highlight()
    Box(
        modifier
            // Icon-only buttons (menu, "...") have no visible label: name them for TalkBack.
            .semantics { if (!showLabel) contentDescription = action.label }
            .bb10Clickable(
                enabled = action.enabled,
                role = Role.Button,
                focusRequester = focusRequester,
                highlight = highlight,
                slant = slant,
                onClick = action.onClick,
            )
            .then(
                if (slant > 0.dp) {
                    // The slash is the right edge of the button: a line along the lean.
                    Modifier.drawBehind {
                        val lean = slant.toPx()
                        drawLine(
                            color = c.accent,
                            start = Offset(size.width, 0f),
                            end = Offset(size.width - lean, size.height),
                            strokeWidth = 1.5.dp.toPx(),
                        )
                    }
                } else Modifier
            )
    ) {
        Bb10OnHighlight(highlight) {
            Column(
                if (showLabel) {
                    Modifier.align(Alignment.BottomCenter).padding(bottom = labelBottomPadding())
                } else {
                    // The chevron sits in the part left of the slash.
                    Modifier.align(Alignment.Center).padding(end = slant)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Bb10Icon(action.icon, size = ActionIconSize)
                if (showLabel) {
                    Spacer(Modifier.height(IconLabelGap))
                    Bb10Text(
                        action.label,
                        style = Bb10.typography.action,
                        color = c.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/**
 * The big call-to-action: accent circle with the icon and the label underneath, on the same
 * bottom margin as [ActionBarItem]; only the circle is larger than a plain icon.
 */
@Composable
private fun PrimaryActionItem(
    action: Bb10Action,
    modifier: Modifier,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    Column(
        modifier.padding(bottom = labelBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(d.primaryActionSize)
                .clip(CircleShape)
                .background(c.accent)
                // The label under the circle is not part of the button: name the button itself.
                .semantics { contentDescription = action.label }
                .bb10Clickable(
                    enabled = action.enabled,
                    role = Role.Button,
                    circle = true,
                    onClick = action.onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Bb10Icon(action.icon, tint = c.onAccent, size = ActionIconSize)
        }
        Spacer(Modifier.height(IconLabelGap))
        Bb10Text(
            action.label,
            style = Bb10.typography.action,
            color = c.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The right-hand menu: a panel that slides in from the right edge with the actions that did not
 * fit in the action bar. It peeks in while a list item is held and opens fully (dimming the app)
 * when the finger lifts or the Menu key / "..." is used. Focus is trapped inside while it is open
 * and lands on the first entry; Back / Menu / tapping the scrim / swiping it away close it.
 */
@Composable
internal fun Bb10OverflowMenu(
    state: PanelState,
    actions: List<Bb10Action>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showHints: Boolean = false,
) {
    val first = remember { FocusRequester() }
    Bb10SidePanel(
        title = Bb10.strings.moreActions,
        state = state,
        side = PanelSide.End,
        onDismiss = onDismiss,
        initialFocus = first,
        modifier = modifier,
        bottomAligned = true,
    ) {
        actions.forEachIndexed { index, a ->
            Bb10LinkItem(
                title = a.label,
                icon = a.icon,
                hint = if (showHints) a.shortcut?.hintLabel() else null,
                enabled = a.enabled,
                focusRequester = if (index == 0) first else null,
                onClick = {
                    onDismiss()
                    a.onClick()
                },
            )
        }
    }
}
