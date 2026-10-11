package duc1607.bb10.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import duc1607.bb10.designsystem.theme.Bb10

/**
 * Colour that text and icons must take, whatever they asked for: set (to white) by an item that
 * is highlighted, so its title, subtitle and icons all turn white on the accent fill. Unspecified
 * means no override.
 */
internal val LocalBb10ContentColor = compositionLocalOf { Color.Unspecified }

@Composable
fun Bb10Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = Bb10.typography.body,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    val resolved = LocalBb10ContentColor.current.takeOrElse {
        color.takeOrElse { style.color.takeOrElse { Bb10.colors.textPrimary } }
    }
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = resolved, textAlign = textAlign ?: style.textAlign),
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun Bb10Icon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Bb10.colors.textPrimary,
    size: Dp = Bb10.dimens.iconSize,
    /** Null for decorative icons (next to a text label); set it when the icon stands alone. */
    contentDescription: String? = null,
) {
    Image(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(LocalBb10ContentColor.current.takeOrElse { tint }),
    )
}

@Composable
fun Bb10Divider(modifier: Modifier = Modifier, color: Color = Bb10.colors.divider) {
    Box(
        modifier
            .fillMaxWidth()
            .height(Bb10.dimens.dividerThickness)
            .background(color)
    )
}
