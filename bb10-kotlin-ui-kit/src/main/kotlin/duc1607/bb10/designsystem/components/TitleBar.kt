package duc1607.bb10.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10

/**
 * A text action in the title bar: Cancel / Back on the left, OK / Send / Save on the right.
 * In BB10 these confirm/dismiss actions live at the top of the screen, not in the action bar.
 */
@Immutable
class Bb10TitleAction(
    val text: String,
    val enabled: Boolean = true,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
) {
    companion object
}

/** "< Back" with the translated label: the usual dismiss action of a screen reached from another. */
@Composable
fun Bb10TitleAction.Companion.back(onClick: () -> Unit): Bb10TitleAction {
    val text = Bb10.strings.back
    return remember(text, onClick) { Bb10TitleAction(text, icon = Bb10Icons.ChevronLeft, onClick = onClick) }
}

/**
 * Search mode of the title bar. While a [Bb10Search] is passed, the title is replaced by a text
 * field, a divider and a Cancel button; the field takes focus when it appears. The app owns the
 * "searching" state: show it by passing a [Bb10Search], leave it from [onClose] (also called by
 * Back), and clear [query] there if it should not outlive the search.
 */
@Immutable
class Bb10Search(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onClose: () -> Unit,
    /** Null: the kit's translated "Search". */
    val placeholder: String? = null,
    /** Null: the kit's translated "Cancel". */
    val cancelText: String? = null,
)

/**
 * The header every screen has (usually it shows the app name), with a thin accent line below.
 *
 * - [dismissAction] (start) and [acceptAction] (end) are the Cancel / OK style actions; when
 *   either is set the title is centred between them.
 * - [subtitle] (a date, a count...) sits under the title, left aligned.
 * - [search] swaps the whole header for the search field + Cancel.
 * - [tabs] swaps the title for global tabs (the app's top-level sections).
 */
@Composable
fun Bb10TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    dismissAction: Bb10TitleAction? = null,
    acceptAction: Bb10TitleAction? = null,
    search: Bb10Search? = null,
    tabs: List<String>? = null,
    selectedTab: Int = 0,
    onTabSelect: (Int) -> Unit = {},
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val centered = dismissAction != null || acceptAction != null
    val slot = 96.dp

    Column(modifier.fillMaxWidth().background(c.surfaceVariant)) {
        if (search != null) {
            SearchHeader(search)
        } else if (tabs != null) {
            // The tabs take the title place; keep the title as the name of the header.
            TabStrip(
                tabs, selectedTab, onTabSelect, TabHeight,
                Modifier.semantics { paneTitle = title },
            )
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(d.titleBarHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when {
                    centered -> Box(
                        Modifier.width(slot).fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (dismissAction != null) TitleActionButton(dismissAction)
                    }
                    else -> Spacer(Modifier.width(d.spaceL))
                }
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
                ) {
                    Bb10Text(
                        text = title,
                        style = t.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.semantics { heading() },
                    )
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
                if (centered) {
                    Box(Modifier.width(slot).fillMaxHeight(), contentAlignment = Alignment.CenterEnd) {
                        if (acceptAction != null) TitleActionButton(acceptAction)
                    }
                } else {
                    Spacer(Modifier.width(d.spaceL))
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(c.accent))
    }
}

/** Search field | divider | Cancel, in place of the title. */
@Composable
private fun SearchHeader(search: Bb10Search) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val focus = remember { FocusRequester() }
    val strings = Bb10.strings
    val placeholder = search.placeholder ?: strings.search
    LaunchedEffect(Unit) {
        withFrameNanos { }
        runCatching { focus.requestFocus() }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(d.titleBarHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .weight(1f)
                .padding(horizontal = d.spaceS, vertical = d.spaceS)
                .fillMaxHeight()
                .background(c.field)
                .padding(horizontal = d.spaceM),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = search.query,
                onValueChange = search.onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus)
                    .semantics { contentDescription = placeholder },
                singleLine = true,
                textStyle = t.body.copy(color = c.textPrimary),
                cursorBrush = SolidColor(c.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (search.query.isEmpty()) {
                            Bb10Text(
                                placeholder,
                                style = t.body.copy(fontStyle = FontStyle.Italic),
                                color = c.textDisabled,
                                maxLines = 1,
                            )
                        }
                        inner()
                    }
                },
            )
        }
        Box(Modifier.width(1.dp).fillMaxHeight(0.6f).background(c.divider))
        TitleActionButton(Bb10TitleAction(search.cancelText ?: strings.cancel, onClick = search.onClose))
    }
}

@Composable
private fun TitleActionButton(action: Bb10TitleAction) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val highlight = rememberBb10Highlight()
    Row(
        Modifier
            .fillMaxHeight()
            .bb10Clickable(
                enabled = action.enabled,
                role = Role.Button,
                highlight = highlight,
                onClick = action.onClick,
            )
            .padding(horizontal = d.spaceL),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Bb10OnHighlight(highlight) {
            if (action.icon != null) Bb10Icon(action.icon, tint = c.accent, size = d.iconSize)
            Bb10Text(action.text, style = Bb10.typography.button, color = c.accent, maxLines = 1)
        }
    }
}
