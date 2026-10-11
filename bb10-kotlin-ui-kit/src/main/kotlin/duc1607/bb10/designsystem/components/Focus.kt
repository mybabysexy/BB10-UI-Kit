package duc1607.bb10.designsystem.components

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import duc1607.bb10.designsystem.theme.Bb10
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Whether an item is highlighted: focused with the d-pad / keyboard, or pressed by a finger. The
 * item fills with the darker accent and its text and icons turn white (see [Bb10OnHighlight]).
 */
@Immutable
class Bb10Highlight internal constructor(
    internal val source: MutableInteractionSource,
    val focused: Boolean,
    val pressed: Boolean,
) {
    val active: Boolean get() = focused || pressed
}

/** Tracks the highlight of one item; pass it to [bb10Clickable] and [Bb10OnHighlight]. */
@Composable
fun rememberBb10Highlight(): Bb10Highlight {
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val pressed by source.collectIsPressedAsState()
    return remember(source, focused, pressed) { Bb10Highlight(source, focused, pressed) }
}

/**
 * Wrap the content of a highlightable item in this: while [highlight] is active every [Bb10Text]
 * and [Bb10Icon] inside is drawn white (`onAccent`), as on BB10 where the highlighted row is
 * accent with all-white content.
 */
@Composable
fun Bb10OnHighlight(highlight: Bb10Highlight, content: @Composable () -> Unit) {
    val forced = if (highlight.active) Bb10.colors.onAccent else LocalBb10ContentColor.current
    CompositionLocalProvider(LocalBb10ContentColor provides forced, content = content)
}

/**
 * Paints the highlight fill (darker accent while focused, darker still while pressed) as this
 * modifier's background. [bb10Clickable] does it by itself; use this one only to paint it in a
 * different place than where the touch area is (a bar inside a larger touch area).
 */
@Composable
fun Modifier.bb10HighlightFill(
    highlight: Bb10Highlight,
    circle: Boolean = false,
    slant: Dp = 0.dp,
): Modifier {
    val colors = Bb10.colors
    val fill = when {
        highlight.pressed -> colors.pressedFill
        highlight.focused -> colors.focusFill
        else -> null
    }
    if (fill == null) return this
    return drawWithContent {
        when {
            circle -> drawCircle(fill, radius = minOf(size.width, size.height) / 2)
            slant > 0.dp -> {
                val lean = slant.toPx()
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width - lean, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, fill)
            }
            else -> drawRect(fill)
        }
        drawContent()
    }
}

/**
 * Puts focus on [requester] for d-pad / keyboard use. After a touch the window is in touch mode,
 * where clickable items refuse programmatic focus; leave touch mode first, so that a menu, dialog
 * or picker opened by touch can be driven with the keys straight away.
 */
@OptIn(ExperimentalComposeUiApi::class)
internal fun InputModeManager.focusForKeys(requester: FocusRequester) {
    requestInputMode(InputMode.Keyboard)
    runCatching { requester.requestFocus() }
}

/**
 * The one interaction primitive of the kit. Every focusable component goes through it so that
 * d-pad / keyboard behaviour is identical everywhere:
 *
 * - fills with the accent, a little darker, when focused (a stronger shade while pressed), and
 *   the content turns white through [Bb10OnHighlight]; no outline
 * - [circle] draws the highlight as a circle instead of a rectangle (round buttons), [slant]
 *   leans its right edge (the Back button)
 * - Enter / d-pad center activate (handled by `clickable`), Space also activates
 * - Menu key triggers [onLongClick] (context menu) when provided
 * - [onHold] / [onHoldRelease] give the two phases of a touch press-and-hold: [onHold] fires as
 *   soon as the long-press time has passed with the finger still down, [onHoldRelease] when the
 *   finger lifts (the lift is then not a click). When [onHold] is set, touch no longer calls
 *   [onLongClick]; the Menu key still does.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.bb10Clickable(
    enabled: Boolean = true,
    role: Role? = null,
    focusRequester: FocusRequester? = null,
    /** The item's highlight state; create it with [rememberBb10Highlight] to colour its content. */
    highlight: Bb10Highlight = rememberBb10Highlight(),
    /** False when the fill is painted elsewhere with [bb10HighlightFill]. */
    paintHighlight: Boolean = true,
    circle: Boolean = false,
    /**
     * Makes the right edge of the button lean: the top edge is [slant] longer than the bottom
     * one, like the slash of the Back button. Only the highlight follows it.
     */
    slant: Dp = 0.dp,
    onLongClick: (() -> Unit)? = null,
    onHold: (() -> Unit)? = null,
    onHoldRelease: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier {
    val source = highlight.source
    val currentClick by rememberUpdatedState(onClick)
    val currentLongClick by rememberUpdatedState(onLongClick)
    val currentHold by rememberUpdatedState(onHold)
    val currentHoldRelease by rememberUpdatedState(onHoldRelease)
    val holdEnabled = enabled && onHold != null

    return this
        .alpha(if (enabled) 1f else 0.4f)
        .onKeyEvent { e ->
            if (!enabled || e.type != KeyEventType.KeyDown || e.nativeKeyEvent.repeatCount != 0) {
                return@onKeyEvent false
            }
            when (e.key) {
                Key.Spacebar -> { currentClick(); true }
                Key.Menu -> currentLongClick?.let { it(); true } ?: false
                else -> false
            }
        }
        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
        .then(if (paintHighlight) Modifier.bb10HighlightFill(highlight, circle, slant) else Modifier)
        .pointerInput(holdEnabled) {
            if (!holdEnabled) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                val slop = viewConfiguration.touchSlop
                // Lifted, cancelled or moved (scrolling) before the timeout: not a hold.
                val ended = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                    var done = false
                    while (!done) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { it.id == down.id }
                        done = change == null || !change.pressed ||
                            (change.position - down.position).getDistance() > slop
                    }
                    true
                }
                if (ended != null) return@awaitEachGesture

                currentHold?.invoke()
                // Swallow the rest of the gesture so it is neither a click nor a scroll.
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id }
                    event.changes.forEach { it.consume() }
                    if (change == null || !change.pressed) break
                }
                currentHoldRelease?.invoke()
            }
        }
        .combinedClickable(
            interactionSource = source,
            indication = null,
            enabled = enabled,
            role = role,
            onLongClick = if (onHold != null) null else onLongClick,
            onClick = onClick,
        )
}

/** Short label for a shortcut key: a letter or digit ("C", "S", "4"), "Del", or null otherwise. */
fun Key.hintLabel(): String? {
    if (this == Key.Delete || this == Key.Backspace) return "Del"
    val code = nativeKeyCode
    return when (code) {
        in AndroidKeyEvent.KEYCODE_A..AndroidKeyEvent.KEYCODE_Z ->
            ('A' + (code - AndroidKeyEvent.KEYCODE_A)).toString()
        in AndroidKeyEvent.KEYCODE_0..AndroidKeyEvent.KEYCODE_9 ->
            ('0' + (code - AndroidKeyEvent.KEYCODE_0)).toString()
        else -> null
    }
}
