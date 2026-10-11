package duc1607.bb10.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10

internal enum class PanelSide { Start, End }

/**
 * Closed, peeking (a sliver of the panel is visible while a finger still holds a list item) or
 * fully open (dims the app and takes focus).
 */
internal enum class PanelState { Hidden, Peek, Open }

/** About 2 mm of physical screen: how far the panel slides in while a list item is held. */
internal val PeekWidth = 13.dp

/**
 * A panel that slides in over the screen from the left ([PanelSide.Start]) or right
 * ([PanelSide.End]) edge. In [PanelState.Peek] only [PeekWidth] of it shows, but the app behind
 * is already dimmed with the scrim; in [PanelState.Open] it is fully in, focus is trapped inside
 * and lands on [initialFocus]. Tapping the scrim or swiping the panel back toward its edge
 * dismisses it; Back / Menu are handled by [Bb10Scaffold].
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun Bb10SidePanel(
    title: String,
    state: PanelState,
    side: PanelSide,
    onDismiss: () -> Unit,
    initialFocus: FocusRequester,
    modifier: Modifier = Modifier,
    bottomAligned: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val c = Bb10.colors
    val inputMode = LocalInputModeManager.current
    val start = side == PanelSide.Start
    BoxWithConstraints(modifier.fillMaxSize()) {
        val panelWidth = minOf(maxWidth * 0.8f, 320.dp)
        val shown by animateDpAsState(
            targetValue = when (state) {
                PanelState.Hidden -> 0.dp
                PanelState.Peek -> PeekWidth
                PanelState.Open -> panelWidth
            },
            animationSpec = tween(if (state == PanelState.Open) 180 else 120),
            label = "panelShown",
        )
        // The app is dimmed the moment the panel shows up, peeking included.
        val dim by animateFloatAsState(
            targetValue = if (state == PanelState.Hidden) 0f else 1f,
            animationSpec = tween(120),
            label = "panelDim",
        )
        Box(Modifier.fillMaxSize()) {
            if (dim > 0f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(c.scrim.copy(alpha = c.scrim.alpha * dim))
                        .pointerInput(Unit) { detectTapGestures { onDismiss() } }
                )
            }
            if (shown > 0.dp) {
                if (state == PanelState.Open) {
                    LaunchedEffect(Unit) {
                        withFrameNanos { }
                        inputMode.focusForKeys(initialFocus)
                    }
                }
                val hidden = panelWidth - shown
                Box(
                    Modifier
                        .align(if (start) Alignment.CenterStart else Alignment.CenterEnd)
                        .offset(x = if (start) -hidden else hidden)
                        .fillMaxHeight()
                        .width(panelWidth)
                        .background(c.surface)
                        .semantics { paneTitle = title }
                        .pointerInput(side) {
                            val threshold = 48.dp.toPx()
                            var drag = 0f
                            detectHorizontalDragGestures(
                                onDragStart = { drag = 0f },
                                onDragEnd = {
                                    val toward = if (start) -drag else drag
                                    if (toward > threshold) onDismiss()
                                },
                                onHorizontalDrag = { _, dx -> drag += dx },
                            )
                        }
                        .focusProperties { exit = { FocusRequester.Cancel } }
                        .focusGroup(),
                ) {
                    // More entries than fit: the list scrolls (and focus scrolls it into view).
                    Column(
                        Modifier
                            .align(if (bottomAligned) Alignment.BottomStart else Alignment.TopStart)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        content = content,
                    )
                }
            }
        }
    }
}
