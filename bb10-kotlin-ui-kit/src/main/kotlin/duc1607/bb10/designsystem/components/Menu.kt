package duc1607.bb10.designsystem.components

import androidx.compose.runtime.compositionLocalOf

/**
 * How list items talk to the right-hand menu of the enclosing [Bb10Scaffold] (null when there is
 * no overflow menu). A touch hold first [peek]s (the menu slides in a few mm), and lifting the
 * finger [open]s it fully and dims the app.
 */
internal class Bb10MenuController(
    val peek: () -> Unit,
    val open: () -> Unit,
)

internal val LocalBb10Menu = compositionLocalOf<Bb10MenuController?> { null }
