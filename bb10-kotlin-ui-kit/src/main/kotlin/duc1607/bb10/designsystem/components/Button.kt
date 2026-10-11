package duc1607.bb10.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.tokens.touchHeight

/**
 * BB10 button: a flat bar that looks like a [Bb10Dropdown] bar without the chevron, with its text
 * **bold and centred**. It is full width by default (like the "Display colour settings" button
 * of the system Display page); in a `Row` give each button `Modifier.weight(1f)`.
 *
 * The bar is 40dp tall inside a 48dp touch area, with the same side and vertical margins as a
 * drop-down, so a button and a drop-down stack evenly in a settings page. Focused, it fills with
 * the darker accent and the text turns white.
 *
 * [primary] makes it the accent colour, for the one button that confirms the page; the confirm
 * action of a screen otherwise belongs in the title bar and the main action in the bottom bar.
 */
@Composable
fun Bb10Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    primary: Boolean = false,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val highlight = rememberBb10Highlight()
    Box(
        modifier
            .defaultMinSize(minWidth = 88.dp)
            .padding(horizontal = d.spaceL, vertical = d.spaceXs),
    ) {
        // 48dp touch area around the 40dp bar.
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = d.touchHeight)
                .bb10Clickable(
                    enabled = enabled,
                    role = Role.Button,
                    focusRequester = focusRequester,
                    highlight = highlight,
                    // Painted on the bar below, not on the whole touch area.
                    paintHighlight = false,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(DropdownBarHeight)
                    .then(
                        if (primary) Modifier.background(c.accentBrush) else Modifier.background(c.field)
                    )
                    .bb10HighlightFill(highlight)
                    .padding(horizontal = d.spaceM),
                contentAlignment = Alignment.Center,
            ) {
                Bb10OnHighlight(highlight) {
                    Bb10Text(
                        text = text,
                        style = Bb10.typography.button.copy(fontWeight = FontWeight.Bold),
                        color = if (primary) c.onAccent else c.textPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
