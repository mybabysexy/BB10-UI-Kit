package duc1607.bb10.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
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
    /**
     * Accent-filled elements (toggle on, checked box, primary button, the unfolded drop-down bar,
     * the primary circle) are not flat: a vertical gradient from [accentTop] to [accentBottom].
     */
    val accentTop: Color,
    val accentBottom: Color,
    /**
     * A bar that is highlighted (focused or pressed: drop-down bar, button, tab...): a vertical
     * gradient from [selectionTop] to [selectionBottom], with white content.
     */
    val selectionTop: Color,
    val selectionBottom: Color,
    /** Invalid input: field border and its message. */
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    /**
     * Highlight of a focused *row* (list item, option, nav entry): a flat, darker accent. Text
     * and icons on it turn white (`onAccent`), in either theme. Opaque: it replaces the row's
     * own fill. Highlighted bars use the [selectionBrush] gradient instead.
     */
    val focusFill: Color,
    /** The same, a little darker still, while a finger holds the item down. */
    val pressedFill: Color,
    val scrim: Color,
) {
    val accentBrush: Brush get() = Brush.verticalGradient(listOf(accentTop, accentBottom))
    val selectionBrush: Brush get() = Brush.verticalGradient(listOf(selectionTop, selectionBottom))
}

/** The gradient and highlight colours of one accent (exact BB10 samples for the default blue). */
private class AccentShades(
    val top: Color,
    val bottom: Color,
    val selectionTop: Color,
    val selectionBottom: Color,
    val highlight: Color,
    val pressed: Color,
)

private fun shadesOf(accent: Color): AccentShades =
    if (accent == Bb10Accents.Blue) {
        AccentShades(
            top = Color(0xFF0080B2),
            bottom = Color(0xFF008FC9),
            selectionTop = Color(0xFF087098),
            selectionBottom = Color(0xFF065F81),
            highlight = Color(0xFF087099),
            pressed = Color(0xFF065F81),
        )
    } else {
        AccentShades(
            top = lerp(accent, Color.Black, 0.12f),
            bottom = lerp(accent, Color.Black, 0.02f),
            selectionTop = lerp(accent, Color.Black, 0.24f),
            selectionBottom = lerp(accent, Color.Black, 0.36f),
            highlight = lerp(accent, Color.Black, 0.24f),
            pressed = lerp(accent, Color.Black, 0.36f),
        )
    }

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

fun bb10DarkColors(accent: Color = Bb10Accents.Blue): Bb10Colors {
    val shades = shadesOf(accent)
    return Bb10Colors(
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
    accentTop = shades.top,
    accentBottom = shades.bottom,
    selectionTop = shades.selectionTop,
    selectionBottom = shades.selectionBottom,
    focusFill = shades.highlight,
    pressedFill = shades.pressed,
    scrim = Color(0x99000000),
)
}

fun bb10LightColors(accent: Color = Bb10Accents.Blue): Bb10Colors {
    val shades = shadesOf(accent)
    return Bb10Colors(
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
    accentTop = shades.top,
    accentBottom = shades.bottom,
    selectionTop = shades.selectionTop,
    selectionBottom = shades.selectionBottom,
    focusFill = shades.highlight,
    pressedFill = shades.pressed,
    scrim = Color(0x66000000),
)
}
