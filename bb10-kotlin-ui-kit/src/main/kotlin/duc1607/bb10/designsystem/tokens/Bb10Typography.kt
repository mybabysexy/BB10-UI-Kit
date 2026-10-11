package duc1607.bb10.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class Bb10Typography(
    val title: TextStyle,
    val header: TextStyle,
    val body: TextStyle,
    val secondary: TextStyle,
    val caption: TextStyle,
    val button: TextStyle,
    val action: TextStyle,
)

/**
 * Text styles of the kit. They always use the system font ([FontFamily.Default]), so the app
 * follows the font the user picked in the device settings; there is no custom-font option.
 */
fun bb10Typography(): Bb10Typography {
    fun style(size: Float, weight: FontWeight = FontWeight.Normal, tracking: Float = 0f) = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = size.sp,
        fontWeight = weight,
        letterSpacing = tracking.sp,
        lineHeight = (size * 1.3f).sp,
    )
    return Bb10Typography(
        title = style(18f, FontWeight.Light),
        header = style(11f, FontWeight.Medium, 0.8f),
        body = style(15f),
        secondary = style(13f),
        caption = style(11f),
        button = style(14f, FontWeight.Medium),
        action = style(10f),
    )
}
