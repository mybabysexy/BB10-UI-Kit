package duc1607.bb10.designsystem.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import duc1607.bb10.designsystem.R

/**
 * The few texts the kit itself puts on screen or in the accessibility tree (names of icon-only
 * buttons, password toggle, search defaults...).
 *
 * They come from the kit's Android string resources (`bb10_*`, English and Vietnamese are
 * included), so they follow the device language. To translate or reword them, either provide
 * `values-xx/strings.xml` with the same names in your app, or override single entries:
 * `Bb10Theme(strings = rememberBb10Strings().copy(moreActions = "..."))`.
 */
@Immutable
data class Bb10Strings(
    /** The button at the left end of the bottom bar that opens the app's navigation menu. */
    val navigationMenu: String,
    /** The button at the right end of the bottom bar that opens the right-hand menu. */
    val moreActions: String,
    val back: String,
    val cancel: String,
    val search: String,
    val showPassword: String,
    val hidePassword: String,
    val clearText: String,
    /** Appended to the label of a required text field for screen readers. */
    val required: String,
    /** State of an open / closed dropdown for screen readers. */
    val expanded: String,
    val collapsed: String,
    val displaySize: String,
    val ok: String,
    val today: String,
    /** Names of the pickers' columns for screen readers. */
    val day: String,
    val month: String,
    val year: String,
    val hour: String,
    val minute: String,
) {
    companion object {
        /** Reads every entry from the string resources of [context]. */
        fun from(context: Context) = Bb10Strings(
            navigationMenu = context.getString(R.string.bb10_navigation_menu),
            moreActions = context.getString(R.string.bb10_more_actions),
            back = context.getString(R.string.bb10_back),
            cancel = context.getString(R.string.bb10_cancel),
            search = context.getString(R.string.bb10_search),
            showPassword = context.getString(R.string.bb10_show_password),
            hidePassword = context.getString(R.string.bb10_hide_password),
            clearText = context.getString(R.string.bb10_clear_text),
            required = context.getString(R.string.bb10_required),
            expanded = context.getString(R.string.bb10_expanded),
            collapsed = context.getString(R.string.bb10_collapsed),
            displaySize = context.getString(R.string.bb10_display_size),
            ok = context.getString(R.string.bb10_ok),
            today = context.getString(R.string.bb10_today),
            day = context.getString(R.string.bb10_day),
            month = context.getString(R.string.bb10_month),
            year = context.getString(R.string.bb10_year),
            hour = context.getString(R.string.bb10_hour),
            minute = context.getString(R.string.bb10_minute),
        )
    }
}

/** The kit's strings in the current device language (re-read when the configuration changes). */
@Composable
fun rememberBb10Strings(): Bb10Strings {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration) { Bb10Strings.from(context) }
}

val LocalBb10Strings = staticCompositionLocalOf<Bb10Strings> { error("Bb10Theme is missing") }
