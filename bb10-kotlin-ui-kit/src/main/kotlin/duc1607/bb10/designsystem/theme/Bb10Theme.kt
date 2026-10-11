package duc1607.bb10.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import duc1607.bb10.designsystem.tokens.Bb10Accents
import duc1607.bb10.designsystem.tokens.Bb10Colors
import duc1607.bb10.designsystem.tokens.Bb10Dimens
import duc1607.bb10.designsystem.tokens.Bb10DimensCompact
import duc1607.bb10.designsystem.tokens.Bb10DimensRegular
import duc1607.bb10.designsystem.tokens.Bb10Typography
import duc1607.bb10.designsystem.tokens.bb10DarkColors
import duc1607.bb10.designsystem.tokens.bb10LightColors
import duc1607.bb10.designsystem.tokens.bb10Typography
import kotlin.math.abs

val LocalBb10Colors = staticCompositionLocalOf<Bb10Colors> { error("Bb10Theme is missing") }
val LocalBb10Typography = staticCompositionLocalOf<Bb10Typography> { error("Bb10Theme is missing") }
val LocalBb10Dimens = staticCompositionLocalOf<Bb10Dimens> { error("Bb10Theme is missing") }
val LocalBb10UiScale = staticCompositionLocalOf { 1f }

/** Access point: `Bb10.colors.accent`, `Bb10.typography.body`, `Bb10.dimens.spaceL`. */
object Bb10 {
    val colors: Bb10Colors
        @Composable @ReadOnlyComposable get() = LocalBb10Colors.current
    val typography: Bb10Typography
        @Composable @ReadOnlyComposable get() = LocalBb10Typography.current
    val dimens: Bb10Dimens
        @Composable @ReadOnlyComposable get() = LocalBb10Dimens.current
    val strings: Bb10Strings
        @Composable @ReadOnlyComposable get() = LocalBb10Strings.current

    /** The display-size factor currently applied by [Bb10Theme]. */
    val uiScale: Float
        @Composable @ReadOnlyComposable get() = LocalBb10UiScale.current
}

/** True on (nearly) square screens such as 720x720. */
@Composable
fun rememberIsSquare(): Boolean {
    val c = LocalConfiguration.current
    return abs(c.screenWidthDp - c.screenHeightDp) <= 40
}

/**
 * @param scale the user's display-size setting, required: every app built on the kit must offer
 *   it (see `Bb10ScalePicker`). It multiplies all dp and sp of the UI together; 1f is the
 *   designed size. Create it once with `rememberBb10UiScale()`.
 *
 * @param strings texts the kit shows itself (names of icon-only buttons...). Read from the
 *   kit's string resources, so they follow the device language; see [Bb10Strings].
 *
 * Text always uses the system font (and respects the system font size).
 */
@Composable
fun Bb10Theme(
    scale: Bb10ScaleState,
    dark: Boolean = true,
    accent: Color = Bb10Accents.Blue,
    compact: Boolean = rememberIsSquare(),
    strings: Bb10Strings = rememberBb10Strings(),
    content: @Composable () -> Unit,
) {
    val colors = remember(dark, accent) {
        if (dark) bb10DarkColors(accent) else bb10LightColors(accent)
    }
    val typography = remember { bb10Typography() }
    val dimens = if (compact) Bb10DimensCompact else Bb10DimensRegular

    // Scaling is done by scaling the density, so every dp / sp (including icons and the fixed
    // sizes inside components) grows together, and layout constraints are re-computed in the
    // new, smaller dp space.
    val base = LocalDensity.current
    val factor = scale.value
    val density = remember(base.density, base.fontScale, factor) {
        Density(density = base.density * factor, fontScale = base.fontScale)
    }

    CompositionLocalProvider(
        LocalDensity provides density,
        LocalBb10UiScale provides factor,
        LocalBb10Colors provides colors,
        LocalBb10Typography provides typography,
        LocalBb10Dimens provides dimens,
        LocalBb10Strings provides strings,
        content = content,
    )
}
