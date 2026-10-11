package duc1607.bb10.designsystem.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.tokens.touchHeight
import kotlinx.coroutines.launch

internal val DropdownBarHeight = 40.dp

/** The unfolded list shows at most this many rows, then scrolls (with a thin scroll bar). */
private const val MaxVisibleOptions = 4.5f

/**
 * The frame of every drop-down: a grey bar with the [label] (bold) and the current [value] at the
 * end. Opening it turns the bar into an accent header and unfolds [content] under it, in place;
 * the list slowly drops down while the chevron turns 180 degrees counter-clockwise (and back
 * clockwise on closing). Tapping the bar or Back closes it.
 *
 * Use it for anything that should unfold inline: [Bb10Dropdown] fills it with options, the date
 * and time pickers (`Bb10DatePickerField`, `Bb10TimePickerField`) with a picker. [content]
 * receives a `close` function.
 *
 * The bar is 40dp tall to keep the page compact; its touch area is still 48dp.
 *
 * When the panel opens near the bottom of a scrolling page, the page scrolls as the content
 * unfolds so that the whole panel (bar and content) ends up on screen.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bb10DropdownPanel(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ColumnScope.(close: () -> Unit) -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val strings = Bb10.strings
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val wholePanel = remember { BringIntoViewRequester() }
    val barFocus = remember { FocusRequester() }
    // Closing from inside (a choice was made, Enter pressed, Back): the unfolded content leaves,
    // so put focus back on the bar instead of losing it.
    val closeAndFocusBar = {
        expanded = false
        runCatching { barFocus.requestFocus() }
        Unit
    }
    val chevronAngle by animateFloatAsState(
        targetValue = if (expanded) -180f else 0f,
        animationSpec = tween(220),
        label = "dropdownChevron",
    )
    BackHandler(enabled = expanded) { closeAndFocusBar() }

    val onHeader = if (expanded) c.onAccent else c.textPrimary
    val highlight = rememberBb10Highlight()
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = d.spaceL, vertical = d.spaceXs)
            // While it unfolds, keep the whole panel (bar and content) on screen: a panel near
            // the bottom of a scrolling page scrolls the page up as it grows.
            .bringIntoViewRequester(wholePanel)
            .onSizeChanged {
                if (expanded) scope.launch { wholePanel.bringIntoView() }
            },
    ) {
        // 48dp touch area around the 40dp bar.
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = d.touchHeight)
                .semantics {
                    stateDescription = if (expanded) strings.expanded else strings.collapsed
                }
                // The highlight is painted on the bar below, not on the whole touch area.
                .bb10Clickable(
                    enabled = enabled,
                    role = Role.DropdownList,
                    focusRequester = barFocus,
                    highlight = highlight,
                    paintHighlight = false,
                    onClick = { expanded = !expanded },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(DropdownBarHeight)
                    .then(
                        if (expanded) Modifier.background(c.accentBrush) else Modifier.background(c.field)
                    )
                    .bb10HighlightFill(highlight)
                    .padding(horizontal = d.spaceM),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.spaceS),
            ) {
                Bb10OnHighlight(highlight) {
                    Bb10Text(
                        label,
                        style = t.button.copy(fontWeight = FontWeight.Bold),
                        color = onHeader,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (!expanded) {
                        Bb10Text(value, style = t.body, color = c.textSecondary, maxLines = 1)
                    }
                    Bb10Icon(
                        Bb10Icons.ChevronDown,
                        tint = if (expanded) c.onAccent else c.textSecondary,
                        size = 20.dp,
                        modifier = Modifier.rotate(chevronAngle),
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(tween(220)),
            exit = shrinkVertically(tween(180)),
        ) {
            Column { content { closeAndFocusBar() } }
        }
    }
}

/**
 * BB10 drop-down: the options unfold under the bar, with a tick on the selected one. Choosing an
 * option calls [onSelect] and closes it. Focus lands on the selected option. Options are as tall
 * as the bar and grey except the chosen one (white in the light theme); more than 4.5 rows
 * scroll, with a thin scroll bar.
 */
@Composable
fun Bb10Dropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Bb10DropdownPanel(
        label = label,
        value = options.getOrNull(selectedIndex).orEmpty(),
        modifier = modifier,
        enabled = enabled,
    ) { close ->
        OptionList(options, selectedIndex) { index ->
            close()
            onSelect(index)
        }
    }
}

@Composable
private fun OptionList(
    options: List<String>,
    selectedIndex: Int,
    onChoose: (Int) -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val selectedFocus = remember { FocusRequester() }
    val inputMode = LocalInputModeManager.current
    LaunchedEffect(Unit) {
        withFrameNanos { }
        inputMode.focusForKeys(selectedFocus)
    }
    val listState = rememberScrollState()
    // Not-chosen options are grey; the chosen one is the flat highlight blue, with white text.
    val idleFill = c.surfaceVariant
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(max = DropdownBarHeight * MaxVisibleOptions)
            // The bar stays visible: it is the hint that there are more options.
            .bb10Scrollbar(listState, alwaysVisible = true),
    ) {
        Column(Modifier.verticalScroll(listState)) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                val optionHighlight = rememberBb10Highlight()
                Row(
                    Modifier
                        .fillMaxWidth()
                        // Same height as the bar above.
                        .height(DropdownBarHeight)
                        .background(if (isSelected) c.focusFill else idleFill)
                        .semantics { this.selected = isSelected }
                        .bb10Clickable(
                            role = Role.Button,
                            focusRequester = if (isSelected) selectedFocus else null,
                            highlight = optionHighlight,
                            flat = true,
                            onClick = { onChoose(index) },
                        )
                        .padding(horizontal = d.spaceM),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val forced = if (isSelected || optionHighlight.active) c.onAccent
                    else LocalBb10ContentColor.current
                    CompositionLocalProvider(LocalBb10ContentColor provides forced) {
                        Bb10Text(option, style = t.body, modifier = Modifier.weight(1f))
                        if (isSelected) Bb10Icon(Bb10Icons.Check)
                    }
                }
                Bb10Divider()
            }
        }
    }
}
