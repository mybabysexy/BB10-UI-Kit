package duc1607.bb10.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10

/** One destination in the app's own navigation menu (opened from the left end of the bottom bar). */
@Immutable
class Bb10NavItem(
    val title: String,
    val icon: ImageVector,
    val subtitle: String? = null,
    val badge: Int? = null,
)

/**
 * The app's own navigation menu. It sits *behind* the app: [Bb10Scaffold] pushes the whole app UI
 * to the right by [width] to reveal it, and dims the app. The selected
 * destination is highlighted; focus is trapped inside and starts on it while the menu is open.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun Bb10NavDrawer(
    open: Boolean,
    items: List<Bb10NavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    width: Dp,
    modifier: Modifier = Modifier,
) {
    val c = Bb10.colors
    val selectedFocus = remember { FocusRequester() }
    val menuTitle = Bb10.strings.navigationMenu
    val inputMode = LocalInputModeManager.current
    LaunchedEffect(open) {
        if (open) {
            withFrameNanos { }
            inputMode.focusForKeys(selectedFocus)
        }
    }
    Column(
        modifier
            .fillMaxHeight()
            .width(width)
            .background(c.surface)
            .verticalScroll(rememberScrollState())
            .semantics { paneTitle = menuTitle }
            .pointerInput(Unit) {
                val threshold = 48.dp.toPx()
                var drag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { drag = 0f },
                    onDragEnd = { if (-drag > threshold) onDismiss() },
                    onHorizontalDrag = { _, dx -> drag += dx },
                )
            }
            .focusProperties { exit = { FocusRequester.Cancel } }
            .focusGroup(),
    ) {
        items.forEachIndexed { index, item ->
            Bb10LinkItem(
                title = item.title,
                icon = item.icon,
                subtitle = item.subtitle,
                badge = item.badge,
                selected = index == selectedIndex,
                focusRequester = if (index == selectedIndex) selectedFocus else null,
                onClick = {
                    onDismiss()
                    onSelect(index)
                },
            )
        }
    }
}
