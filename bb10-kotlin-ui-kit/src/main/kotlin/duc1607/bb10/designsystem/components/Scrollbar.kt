package duc1607.bb10.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10
import kotlinx.coroutines.delay

private val ScrollbarWidth = 3.dp
private val ScrollbarMinThumb = 24.dp

/**
 * A `LazyColumn` with the kit's scroll bar: a thin line at the right edge that shows while the
 * list moves and fades out shortly after it stops. Use it for every list that can scroll.
 */
@Composable
fun Bb10LazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(modifier.bb10Scrollbar(state), state, content = content)
}

/** `verticalScroll` plus the kit's scroll bar, for a page that is a plain `Column`. */
@Composable
fun Modifier.bb10VerticalScroll(state: ScrollState): Modifier =
    this.bb10Scrollbar(state).verticalScroll(state)

/**
 * Draws a thin scroll bar over a scrolling list. It appears while the list is scrolling and
 * fades after about a second; with [alwaysVisible] it stays (used where the bar is the only
 * hint that a short list has more rows, such as an unfolded drop-down).
 *
 * Apply it *before* the scroll modifier / to the `LazyColumn` itself so that it is drawn over
 * the viewport, not over the content.
 */
@Composable
fun Modifier.bb10Scrollbar(state: LazyListState, alwaysVisible: Boolean = false): Modifier {
    val alpha = scrollbarAlpha(state.isScrollInProgress, alwaysVisible)
    val color = Bb10.colors.textSecondary
    return drawWithContent {
        drawContent()
        if (alpha > 0f) {
            val info = state.layoutInfo
            val visible = info.visibleItemsInfo
            if (info.totalItemsCount > 0 && visible.isNotEmpty()) {
                val average = visible.sumOf { it.size }.toFloat() / visible.size
                val content = average * info.totalItemsCount
                val offset = state.firstVisibleItemIndex * average + state.firstVisibleItemScrollOffset
                drawScrollbar(content, offset, color.copy(alpha = 0.6f * alpha))
            }
        }
    }
}

@Composable
fun Modifier.bb10Scrollbar(state: ScrollState, alwaysVisible: Boolean = false): Modifier {
    val alpha = scrollbarAlpha(state.isScrollInProgress, alwaysVisible)
    val color = Bb10.colors.textSecondary
    return drawWithContent {
        drawContent()
        val max = state.maxValue
        if (alpha > 0f && max > 0 && max != Int.MAX_VALUE) {
            drawScrollbar(size.height + max, state.value.toFloat(), color.copy(alpha = 0.6f * alpha))
        }
    }
}

/** 1 while the list scrolls, held for a moment after it stops, then eased to 0. */
@Composable
private fun scrollbarAlpha(scrolling: Boolean, alwaysVisible: Boolean): Float {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(scrolling) {
        if (scrolling) {
            shown = true
        } else {
            delay(900)
            shown = false
        }
    }
    val target = if (alwaysVisible || shown) 1f else 0f
    return animateFloatAsState(
        targetValue = target,
        animationSpec = tween(if (target > 0f) 80 else 400),
        label = "scrollbar",
    ).value
}

/** Draws the thumb for a viewport of the draw scope's height over [content] px of content. */
private fun DrawScope.drawScrollbar(content: Float, offset: Float, color: androidx.compose.ui.graphics.Color) {
    val viewport = size.height
    if (content <= viewport + 1f) return
    val thumb = (viewport * viewport / content).coerceIn(ScrollbarMinThumb.toPx(), viewport)
    val progress = (offset / (content - viewport)).coerceIn(0f, 1f)
    val width = ScrollbarWidth.toPx()
    drawRect(
        color = color,
        topLeft = Offset(size.width - width, (viewport - thumb) * progress),
        size = Size(width, thumb),
    )
}
