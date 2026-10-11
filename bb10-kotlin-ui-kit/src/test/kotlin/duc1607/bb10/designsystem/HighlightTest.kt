package duc1607.bb10.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import duc1607.bb10.designsystem.components.Bb10Action
import duc1607.bb10.designsystem.components.Bb10Button
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.LocalBb10ContentColor
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import duc1607.bb10.designsystem.tokens.bb10DarkColors
import duc1607.bb10.designsystem.tokens.bb10LightColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** What a focused (highlighted) item looks like: the darker accent fill, white content. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HighlightTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun theHighlightIsTheAccentJustDarker() {
        for (colors in listOf(bb10LightColors(), bb10DarkColors())) {
            val accent = colors.accent
            val fill = colors.focusFill
            assertEquals("opaque: it replaces the item's own fill", 1f, fill.alpha, 0f)
            assertTrue(
                "darker than the accent",
                fill.green < accent.green && fill.blue < accent.blue,
            )
            assertTrue("still the accent, not near black", fill.green > accent.green * 0.6f)
            // Pressed is darker still.
            assertTrue(colors.pressedFill.green < fill.green)
        }
    }

    private fun focusedRowSeesWhiteContent(dark: Boolean) {
        var seen = Color.Red
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale(), dark = dark) {
                Bb10ListItem(
                    title = "Inbox",
                    leading = { seen = LocalBb10ContentColor.current },
                    onClick = {},
                )
            }
        }
        assertEquals("no override while idle", Color.Unspecified, seen)

        rule.onNodeWithText("Inbox").requestFocus()
        rule.waitForIdle()
        rule.onNodeWithText("Inbox").assertIsFocused()

        val colors = if (dark) bb10DarkColors() else bb10LightColors()
        assertEquals("title, subtitle and icons are forced white", colors.onAccent, seen)
    }

    @Test
    fun aFocusedRowTurnsItsContentWhiteInTheLightTheme() = focusedRowSeesWhiteContent(dark = false)

    @Test
    fun aFocusedRowTurnsItsContentWhiteInTheDarkTheme() = focusedRowSeesWhiteContent(dark = true)

    @Test
    fun theContentGoesBackWhenFocusLeaves() {
        var seen = Color.Red
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale(), dark = false) {
                Column {
                    Bb10ListItem(
                        title = "First",
                        leading = { seen = LocalBb10ContentColor.current },
                        onClick = {},
                    )
                    Bb10Button("Other", onClick = {})
                }
            }
        }
        rule.onNodeWithText("First").requestFocus()
        rule.waitForIdle()
        assertEquals(bb10LightColors().onAccent, seen)

        rule.onNodeWithText("First").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        rule.onNodeWithText("Other").assertIsFocused()
        assertEquals(Color.Unspecified, seen)
    }

    @Test
    fun theBackButtonCanBeFocused() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale(), dark = false) {
                Bb10Scaffold(
                    title = "Add Account",
                    onBack = {},
                    actions = listOf(Bb10Action("advanced", "Advanced", Bb10Icons.Sliders, primary = true) {}),
                    autoFocus = false,
                ) {}
            }
        }
        rule.onNodeWithContentDescription("Back").requestFocus()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Back").assertIsFocused()
    }
}
