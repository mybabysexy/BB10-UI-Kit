package duc1607.bb10.designsystem.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import duc1607.bb10.designsystem.R
import kotlin.math.abs

/**
 * One step of the user-facing "display size" setting. [scale] multiplies every dp and sp in the
 * kit (layout, icons and text together), so a larger step makes the whole UI bigger.
 */
@Immutable
class Bb10ScaleStep(@StringRes val nameRes: Int, val scale: Float)

object Bb10Scales {
    val Small = Bb10ScaleStep(R.string.bb10_scale_small, 0.85f)
    val Normal = Bb10ScaleStep(R.string.bb10_scale_normal, 1f)
    val Large = Bb10ScaleStep(R.string.bb10_scale_large, 1.25f)
    val ExtraLarge = Bb10ScaleStep(R.string.bb10_scale_extra_large, 1.5f)
    val Huge = Bb10ScaleStep(R.string.bb10_scale_huge, 2f)

    val all = listOf(Small, Normal, Large, ExtraLarge, Huge)

    /** The step whose scale is closest to [scale] (for showing an arbitrary stored value). */
    fun closest(scale: Float): Bb10ScaleStep = all.minBy { abs(it.scale - scale) }
}

/**
 * The user's display-size setting, persisted in SharedPreferences. It is a required argument of
 * [Bb10Theme], so every app built on the kit has to create one with [rememberBb10UiScale] and
 * expose it in its settings with `Bb10ScalePicker`.
 */
@Stable
class Bb10ScaleState internal constructor(
    private val prefs: SharedPreferences,
    initial: Float,
) {
    var value: Float by mutableFloatStateOf(initial)
        private set

    fun update(scale: Float) {
        val clamped = scale.coerceIn(MinScale, MaxScale)
        value = clamped
        prefs.edit().putFloat(KEY, clamped).apply()
    }

    companion object {
        internal const val PREFS = "bb10_ui_kit"
        internal const val KEY = "ui_scale"
        const val MinScale = 0.5f
        const val MaxScale = 3f
    }
}

/**
 * Creates (or restores) the persisted display-size setting. Pass it to [Bb10Theme] and show
 * `Bb10ScalePicker(state)` somewhere in the app's settings.
 */
@Composable
fun rememberBb10UiScale(default: Float = 1f): Bb10ScaleState {
    val context = LocalContext.current
    return remember(context) {
        val prefs = context.applicationContext
            .getSharedPreferences(Bb10ScaleState.PREFS, Context.MODE_PRIVATE)
        Bb10ScaleState(prefs, prefs.getFloat(Bb10ScaleState.KEY, default))
    }
}
