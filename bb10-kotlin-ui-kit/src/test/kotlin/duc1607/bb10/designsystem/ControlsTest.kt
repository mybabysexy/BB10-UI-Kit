package duc1607.bb10.designsystem

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import duc1607.bb10.designsystem.components.Bb10Checkbox
import duc1607.bb10.designsystem.components.Bb10Dialog
import duc1607.bb10.designsystem.components.Bb10LazyColumn
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10Dropdown
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10Slider
import duc1607.bb10.designsystem.components.Bb10Stepper
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Checkbox, drop-down, slider and the two-button popup. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ControlsTest {
    @get:Rule
    val rule = createComposeRule()

    private fun screen(content: @Composable () -> Unit) {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Settings", autoFocus = false) { content() }
            }
        }
    }

    // ------------------------------------------------------------ checkbox

    @Test
    fun checkboxFlipsAndExposesItsState() {
        screen {
            var checked by remember { mutableStateOf(false) }
            Bb10Checkbox(checked, { checked = it }, label = "Wi-Fi", icon = Bb10Icons.Star)
        }
        rule.onNodeWithText("Wi-Fi").assertIsOff()
        rule.onNodeWithText("Wi-Fi").performClick()
        rule.onNodeWithText("Wi-Fi").assertIsOn()
        rule.onNodeWithText("Wi-Fi").performClick()
        rule.onNodeWithText("Wi-Fi").assertIsOff()
    }

    @Test
    fun checkboxIsActivatedByTheKeyboard() {
        var checked = false
        screen {
            var state by remember { mutableStateOf(false) }
            checked = state
            Bb10Checkbox(state, { state = it }, label = "Bluetooth")
        }
        rule.onNodeWithText("Bluetooth").requestFocus()
        rule.onNodeWithText("Bluetooth").performKeyInput { pressKey(Key.Spacebar) }
        rule.waitForIdle()
        assertTrue(checked)
    }

    // ------------------------------------------------------------ dropdown

    @Test
    fun dropdownUnfoldsInPlaceAndSelects() {
        var selected = 1
        screen {
            var index by remember { mutableIntStateOf(1) }
            selected = index
            Bb10Dropdown(
                label = "Ringtone",
                options = listOf("Calm", "Bright", "Loud"),
                selectedIndex = index,
                onSelect = { index = it },
            )
        }
        // Closed: the current value is shown at the end of the bar, options are not.
        rule.onNodeWithText("Bright").assertExists()
        rule.onNodeWithText("Calm").assertDoesNotExist()
        rule.onNodeWithText("Ringtone").assert(
            androidx.compose.ui.test.SemanticsMatcher("collapsed") {
                it.config.getOrNull(SemanticsProperties.StateDescription) == "Collapsed"
            },
        )

        rule.onNodeWithText("Ringtone").performClick()
        rule.onNodeWithText("Calm").assertExists()
        rule.onNodeWithText("Loud").assertExists()
        rule.onNodeWithText("Bright").assertIsSelected()

        rule.onNodeWithText("Loud").performClick()
        rule.waitForIdle()
        assertEquals(2, selected)
        // Closed again, showing the new value.
        rule.onNodeWithText("Calm").assertDoesNotExist()
        rule.onNodeWithText("Loud").assertExists()
    }

    // ------------------------------------------------------------ slider

    @Test
    fun sliderReportsProgressAndTakesSemanticsActions() {
        var value = 0.2f
        screen {
            var v by remember { mutableFloatStateOf(0.2f) }
            value = v
            Bb10Slider(
                v, { v = it }, label = "Volume",
                startIcon = Bb10Icons.VolumeLow, endIcon = Bb10Icons.VolumeHigh,
            )
        }
        val info = rule.onNodeWithContentDescription("Volume").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo]
        assertEquals(0.2f, info.current, 0.001f)

        rule.onNodeWithContentDescription("Volume").performSemanticsAction(SemanticsActions.SetProgress) {
            it(0.75f)
        }
        rule.waitForIdle()
        assertEquals(0.75f, value, 0.001f)
    }

    @Test
    fun sliderMovesWithDpadLeftAndRight() {
        var value = 0.5f
        screen {
            var v by remember { mutableFloatStateOf(0.5f) }
            value = v
            Bb10Slider(v, { v = it }, label = "Volume")
        }
        rule.onNodeWithContentDescription("Volume").requestFocus()

        rule.onNodeWithContentDescription("Volume").performKeyInput { pressKey(Key.DirectionRight) }
        rule.waitForIdle()
        assertEquals(0.55f, value, 0.001f)
        rule.onNodeWithContentDescription("Volume").performKeyInput { pressKey(Key.DirectionLeft) }
        rule.onNodeWithContentDescription("Volume").performKeyInput { pressKey(Key.DirectionLeft) }
        rule.waitForIdle()
        assertEquals(0.45f, value, 0.001f)
    }

    @Test
    fun steppedSliderSnapsToStops() {
        var value = 0f
        screen {
            var v by remember { mutableFloatStateOf(0f) }
            value = v
            Bb10Slider(v, { v = it }, valueRange = 0f..10f, steps = 4, label = "Level")
        }
        // 4 stops inside 0..10 means a step of 2.
        rule.onNodeWithContentDescription("Level").performSemanticsAction(SemanticsActions.SetProgress) {
            it(5.1f)
        }
        rule.waitForIdle()
        assertEquals(6f, value, 0.001f)
    }

    // ------------------------------------------------------------ popup with two buttons

    @Test
    fun twoButtonPopupConfirmsAndDismisses() {
        var confirmed = 0
        var dismissed = 0
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Dialog(
                    title = "Delete?",
                    message = "This removes the contact and any linked contacts.",
                    confirmText = "Delete",
                    dismissText = "Cancel",
                    onConfirm = { confirmed++ },
                    onDismiss = { dismissed++ },
                )
            }
        }
        rule.onNodeWithText("Delete?").assertExists()
        rule.onNodeWithText("Cancel").performClick()
        assertEquals(1, dismissed)
        rule.onNodeWithText("Delete").performClick()
        assertEquals(1, confirmed)
    }

    @Test
    fun dropdownOptionsAreAsTallAsTheBarAndLongListsScroll() {
        screen {
            var index by remember { mutableIntStateOf(0) }
            Bb10Dropdown(
                label = "Ringtone",
                options = (1..12).map { "Option $it" },
                selectedIndex = index,
                onSelect = { index = it },
            )
        }
        rule.onNodeWithText("Ringtone").performClick()
        rule.waitForIdle()

        // Rows are 40dp, the same as the bar.
        val first = rule.onNodeWithText("Option 1").getUnclippedBoundsInRoot()
        assertEquals(40f, (first.bottom - first.top).value, 1f)
        // The list is capped (4.5 rows): the last option is cut off until you scroll.
        rule.onNodeWithText("Option 12").assertIsNotDisplayed()
        rule.onNodeWithText("Option 12").performScrollTo()
        rule.onNodeWithText("Option 12").assertIsDisplayed()
    }

    @Test
    fun stepperMovesOneValueAtATimeAndSnapsToWholeNumbers() {
        var value = 3
        screen {
            var v by remember { mutableIntStateOf(3) }
            value = v
            Bb10Stepper(v, { v = it }, range = 1..5, label = "Rating")
        }
        rule.onNodeWithContentDescription("Rating").requestFocus()
        rule.onNodeWithContentDescription("Rating").performKeyInput { pressKey(Key.DirectionRight) }
        rule.waitForIdle()
        assertEquals(4, value)

        // A value between two marks snaps to the nearest one, and the ends clamp.
        rule.onNodeWithContentDescription("Rating").performSemanticsAction(SemanticsActions.SetProgress) {
            it(2.4f)
        }
        rule.waitForIdle()
        assertEquals(2, value)
        rule.onNodeWithContentDescription("Rating").performSemanticsAction(SemanticsActions.SetProgress) {
            it(9f)
        }
        rule.waitForIdle()
        assertEquals(5, value)
    }

    @Test
    fun scrollingListRendersItsRows() {
        screen {
            Bb10LazyColumn {
                items(30) { i -> Bb10ListItem(title = "Row $i", onClick = {}) }
            }
        }
        rule.onNodeWithText("Row 0").assertExists()
    }
}
