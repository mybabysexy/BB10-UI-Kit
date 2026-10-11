package duc1607.bb10.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Immutable
data class Bb10Colors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val divider: Color,
    /** Fill of text inputs. */
    val field: Color,
    val accent: Color,
    val onAccent: Color,
    /** Invalid input: field border and its message. */
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    /**
     * Highlight of the focused item (d-pad): the accent, a little darker. Text and icons on it
     * turn white (`onAccent`), in either theme. Opaque: it replaces the item's own fill.
     */
    val focusFill: Color,
    /** The same, a little darker still, while a finger holds the item down. */
    val pressedFill: Color,
    val scrim: Color,
)

/** Accent colors (approximate BB10 palette). One accent is used across the whole app. */
object Bb10Accents {
    val Blue = Color(0xFF0092CC)
    val Teal = Color(0xFF00A38F)
    val Green = Color(0xFF6BAA2B)
    val Orange = Color(0xFFE67E22)
    val Red = Color(0xFFD9363E)
    val Purple = Color(0xFF8E5BC4)
    val all = listOf(Blue, Teal, Green, Orange, Red, Purple)
}

fun bb10DarkColors(accent: Color = Bb10Accents.Blue) = Bb10Colors(
    isDark = true,
    background = Color(0xFF000000),
    surface = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFF262626),
    divider = Color(0xFF3A3A3A),
    field = Color(0xFF262626),
    accent = accent,
    onAccent = Color.White,
    error = Color(0xFFFF5A5F),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA6A6A6),
    textDisabled = Color(0xFF666666),
    focusFill = lerp(accent, Color.Black, 0.22f),
    pressedFill = lerp(accent, Color.Black, 0.36f),
    scrim = Color(0x99000000),
)

fun bb10LightColors(accent: Color = Bb10Accents.Blue) = Bb10Colors(
    isDark = false,
    background = Color(0xFFF8F8F8),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDEDED),
    divider = Color(0xFFD6D6D6),
    field = Color(0xFFEEEEEE),
    accent = accent,
    onAccent = Color.White,
    error = Color(0xFFD9363E),
    textPrimary = Color(0xFF262626),
    textSecondary = Color(0xFF6E6E6E),
    textDisabled = Color(0xFFB0B0B0),
    focusFill = lerp(accent, Color.Black, 0.22f),
    pressedFill = lerp(accent, Color.Black, 0.36f),
    scrim = Color(0x66000000),
)
