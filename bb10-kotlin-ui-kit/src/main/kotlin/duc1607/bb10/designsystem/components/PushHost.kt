package duc1607.bb10.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.input.pointer.pointerInput
import duc1607.bb10.designsystem.theme.Bb10

/** True inside a page of a [Bb10PushHost]: its scaffold leaves the initial focus to the host. */
internal val LocalBb10InPage = staticCompositionLocalOf { false }

/**
 * Screen-to-screen transition, the way BB10 does it: opening a screen from a list slides the new
 * screen in from the right edge while the screen underneath is dimmed (as the context menu
 * does); going back slides it out to the right and the dim fades away.
 *
 * [content] is the base screen; it stays composed (scroll position, focus and state are kept).
 * Set [pushed] to a non-null value to open [page] for it, and back to null to close it. Give the
 * page's scaffold `onBack = { pushed = null }` so its Back button and the system Back close it.
 *
 * While a page is open, focus is kept inside it (so d-pad cannot wander into the screen behind),
 * and when it closes focus returns to the item that opened it.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T : Any> Bb10PushHost(
    pushed: T?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    page: @Composable (T) -> Unit,
) {
    val c = Bb10.colors
    val showing = pushed != null
    // Keep composing the page while it slides out after `pushed` went back to null.
    var last by remember { mutableStateOf<T?>(null) }
    if (pushed != null) last = pushed

    val dim by animateFloatAsState(
        targetValue = if (showing) 1f else 0f,
        animationSpec = tween(220),
        label = "pushDim",
    )
    val baseFocus = remember { FocusRequester() }
    val pageFocus = remember { FocusRequester() }
    var wasShowing by remember { mutableStateOf(false) }

    LaunchedEffect(showing) {
        withFrameNanos { }
        if (showing) {
            runCatching { pageFocus.requestFocus() }
        } else if (wasShowing) {
            // Back to the base screen: its focus restorer puts focus on the item that opened the page.
            runCatching { baseFocus.requestFocus() }
        }
        wasShowing = showing
    }

    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .focusRequester(baseFocus)
                .focusRestorer()
                .focusGroup(),
        ) { content() }

        if (dim > 0f) {
            // Dims the base screen and swallows touches meant for it.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(c.scrim.copy(alpha = c.scrim.alpha * dim))
                    .pointerInput(Unit) { detectTapGestures { } }
            )
        }

        AnimatedVisibility(
            visible = showing,
            enter = slideInHorizontally(tween(240)) { it },
            exit = slideOutHorizontally(tween(200)) { it },
        ) {
            val shown = last
            if (shown != null) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .focusRequester(pageFocus)
                        .focusProperties { exit = { FocusRequester.Cancel } }
                        .focusGroup(),
                ) {
                    CompositionLocalProvider(LocalBb10InPage provides true) { page(shown) }
                }
            }
        }
    }
}
