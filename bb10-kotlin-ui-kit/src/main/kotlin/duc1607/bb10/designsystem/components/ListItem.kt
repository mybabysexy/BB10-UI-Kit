package duc1607.bb10.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10

/** Width of the accent strip a checked row uncovers. */
private val CheckStripWidth = 44.dp

/**
 * A list row: optional leading/trailing slots, thin divider underneath.
 * [onFocused] fires when the row takes focus (d-pad). [onLongClick] makes the row a context-menu
 * source: it is called when the row is held (or on the Menu key) so the app can remember which
 * item the menu acts on, and the enclosing [Bb10Scaffold]'s right-hand menu then opens: it peeks
 * in while the finger is held and slides fully in, dimming the app, when the finger lifts.
 */
@Composable
fun Bb10ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
    /**
     * Multi-select state. Null: the list is not in selection mode. Otherwise the row can be
     * (un)checked: a checked row slides to the right to uncover an accent strip with a tick and
     * takes a light accent shade as background; unchecking slides it back.
     */
    checked: Boolean? = null,
    /** Spoken after the title, e.g. "Unread"; for state that is only shown visually. */
    stateDescription: String? = null,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
    onFocused: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val menu = LocalBb10Menu.current
    val isChecked = checked == true
    val highlight = rememberBb10Highlight()
    // The checked row moves aside to uncover the strip, and moves back when unchecked.
    val shift by animateDpAsState(
        targetValue = if (isChecked) CheckStripWidth else 0.dp,
        animationSpec = tween(150),
        label = "rowShift",
    )
    val tint by animateColorAsState(
        targetValue = if (isChecked) c.accent.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(150),
        label = "rowTint",
    )
    Column(modifier.fillMaxWidth().background(c.background)) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = d.listItemMinHeight)
                .background(tint)
                .then(
                    if (onFocused != null) {
                        Modifier.onFocusChanged { if (it.isFocused) onFocused() }
                    } else Modifier
                )
                .semantics {
                    this.selected = selected
                    if (stateDescription != null) this.stateDescription = stateDescription
                    if (checked != null) toggleableState = ToggleableState(checked)
                }
                .bb10Clickable(
                    enabled = enabled,
                    role = if (checked != null) Role.Checkbox else Role.Button,
                    focusRequester = focusRequester,
                    highlight = highlight,
                    // Menu key: let the app pick the item, then open the right-hand menu.
                    onLongClick = onLongClick?.let { cb ->
                        { cb(); menu?.open?.invoke() }
                    },
                    // Touch hold: the menu peeks in while held, opens fully on release.
                    onHold = if (onLongClick != null && menu != null) {
                        { onLongClick(); menu.peek() }
                    } else null,
                    onHoldRelease = if (onLongClick != null && menu != null) {
                        { menu.open() }
                    } else null,
                    onClick = onClick,
                )
                // Over the highlight, so the "selected" mark stays visible on it.
                .drawBehind {
                    if (selected) {
                        val mark = if (highlight.active) c.onAccent else c.accent
                        drawRect(mark, size = Size(3.dp.toPx(), size.height))
                    }
                },
            contentAlignment = Alignment.CenterStart,
        ) {
            // The strip that the row uncovers when it slides right.
            if (shift > 0.dp) {
                Box(Modifier.matchParentSize()) {
                    Box(
                        Modifier.width(shift).fillMaxHeight().background(c.accent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Bb10Icon(Bb10Icons.Check, tint = c.onAccent, size = 20.dp)
                    }
                }
            }
            Bb10OnHighlight(highlight) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = shift)
                        .padding(horizontal = d.spaceL, vertical = d.spaceS),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.spaceM),
                ) {
                    leading?.invoke()
                    Column(Modifier.weight(1f)) {
                        Bb10Text(title, style = t.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (subtitle != null) {
                            Bb10Text(
                                subtitle,
                                style = t.secondary,
                                color = c.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    trailing?.invoke()
                }
            }
        }
        Bb10Divider()
    }
}

/**
 * Navigation / settings link: a (usually coloured) icon, the title, an optional subtitle and a
 * count under the icon, with a divider underneath. [selected] fills the row with the accent.
 * [hint] (a keyboard shortcut) is drawn small and light grey in the top-right corner of the row.
 * Used by the left navigation drawer, the right menu and settings lists.
 */
@Composable
fun Bb10LinkItem(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    subtitle: String? = null,
    badge: Int? = null,
    hint: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val textColor = if (selected) c.onAccent else c.textPrimary
    val secondaryColor = if (selected) c.onAccent else c.textSecondary
    val highlight = rememberBb10Highlight()
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth()) {
            Bb10OnHighlight(highlight) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = d.listItemMinHeight)
                        .background(if (selected) c.accent else Color.Transparent)
                        .semantics { this.selected = selected }
                        .bb10Clickable(
                            enabled = enabled,
                            role = Role.Button,
                            focusRequester = focusRequester,
                            highlight = highlight,
                            onClick = onClick,
                        )
                        .padding(horizontal = d.spaceL, vertical = d.spaceS),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.spaceM),
                ) {
                    if (icon != null) {
                        Column(Modifier.width(d.iconSize), horizontalAlignment = Alignment.CenterHorizontally) {
                            Bb10Icon(icon, tint = iconTint.takeOrElse { textColor })
                            if (badge != null) {
                                Bb10Text(badge.toString(), style = t.caption, color = secondaryColor, maxLines = 1)
                            }
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Bb10Text(title, style = t.body, color = textColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (subtitle != null) {
                            Bb10Text(
                                subtitle,
                                style = t.secondary,
                                color = secondaryColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                if (hint != null) {
                    Bb10Text(
                        hint,
                        style = t.caption,
                        color = secondaryColor.copy(alpha = 0.7f),
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = d.spaceS),
                    )
                }
            }
        }
        Bb10Divider()
    }
}

/**
 * Section header: small uppercase label over a thin accent line. Its background is slightly
 * translucent so it can be used as a sticky header (`stickyHeader` of a `LazyColumn`).
 */
@Composable
fun Bb10ListHeader(
    text: String,
    modifier: Modifier = Modifier,
    trailing: String? = null,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    // Slightly see-through, so a header that sticks to the top of a list lets the rows scrolling
    // underneath show faintly.
    Column(modifier.fillMaxWidth().background(c.surfaceVariant.copy(alpha = 0.88f))) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = d.spaceL, vertical = d.spaceS),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Bb10Text(
                text.uppercase(),
                style = t.header,
                color = c.textSecondary,
                modifier = Modifier.semantics { heading() },
            )
            if (trailing != null) Bb10Text(trailing, style = t.header, color = c.textSecondary)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(c.accent))
    }
}

/** Small accent square used as an "unread" marker. */
@Composable
fun Bb10Marker(visible: Boolean = true, modifier: Modifier = Modifier) {
    val size = 8.dp
    Spacer(
        modifier
            .width(size)
            .height(size)
            .background(if (visible) Bb10.colors.accent else Color.Transparent)
    )
}
