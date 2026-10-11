package duc1607.bb10.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import duc1607.bb10.designsystem.components.Bb10Button
import duc1607.bb10.designsystem.components.Bb10DatePicker
import duc1607.bb10.designsystem.components.Bb10DatePickerDialog
import duc1607.bb10.designsystem.components.Bb10DatePickerField
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10PushHost
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10TimePicker
import duc1607.bb10.designsystem.components.Bb10TimePickerDialog
import duc1607.bb10.designsystem.components.Bb10TimePickerField
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.LocalTime

/** Pickers, multi-select, and the push transition. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PickersAndFlowTest {
    @get:Rule
    val rule = createComposeRule()

    private fun screen(content: @androidx.compose.runtime.Composable () -> Unit) {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Picker", autoFocus = false) { content() }
            }
        }
    }

    // ------------------------------------------------------------ pickers

    @Test
    fun timePickerMovesWithDpadAndKeepsTheOtherColumn() {
        var time = LocalTime.of(6, 30)
        screen {
            var value by remember { mutableStateOf(LocalTime.of(6, 30)) }
            time = value
            Bb10TimePicker(value, { value = it })
        }
        rule.onNodeWithContentDescription("Hour").requestFocus()
        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        assertEquals(LocalTime.of(7, 30), time)

        rule.onNodeWithContentDescription("Minute").requestFocus()
        rule.onNodeWithContentDescription("Minute").performKeyInput { pressKey(Key.DirectionUp) }
        rule.waitForIdle()
        assertEquals(LocalTime.of(7, 29), time)
    }

    @Test
    fun twelveHourPickerHasAmPmAndKeepsTheDay() {
        var time = LocalTime.of(14, 5)
        screen {
            var value by remember { mutableStateOf(LocalTime.of(14, 5)) }
            time = value
            Bb10TimePicker(value, { value = it }, is24Hour = false)
        }
        // 14:05 is 2 PM; one step down in the hour column is 1 PM, still afternoon.
        rule.onNodeWithContentDescription("Hour").requestFocus()
        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.DirectionUp) }
        rule.waitForIdle()
        assertEquals(LocalTime.of(13, 5), time)
    }

    @Test
    fun datePickerClampsTheDayWhenTheMonthIsShorter() {
        var date = LocalDate.of(2026, 1, 31)
        screen {
            var value by remember { mutableStateOf(LocalDate.of(2026, 1, 31)) }
            date = value
            Bb10DatePicker(value, { value = it })
        }
        rule.onNodeWithContentDescription("Month").requestFocus()
        rule.onNodeWithContentDescription("Month").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        assertEquals(LocalDate.of(2026, 2, 28), date)
    }

    @Test
    fun pickerFieldUnfoldsInPlace() {
        screen {
            var value by remember { mutableStateOf(LocalTime.of(6, 0)) }
            Bb10TimePickerField("Alarm time", value, { value = it })
        }
        rule.onNodeWithContentDescription("Hour").assertDoesNotExist()
        rule.onNodeWithText("Alarm time").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Hour").assertExists()
        rule.onNodeWithContentDescription("Minute").assertExists()
    }

    @Test
    fun datePickerDialogReportsTodayWhenTodayIsPressed() {
        var confirmed: LocalDate? = null
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10DatePickerDialog(
                    title = "To date",
                    date = LocalDate.of(2020, 5, 17),
                    onConfirm = { confirmed = it },
                    onDismiss = {},
                )
            }
        }
        rule.onNodeWithText("To date").assertExists()
        rule.onNodeWithText("Today").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("OK").performClick()
        assertEquals(LocalDate.now(), confirmed)
    }

    // ------------------------------------------------------------ multi-select

    @Test
    fun checkedRowExposesItsStateAndSelectionModeShowsCancel() {
        var cancelled = 0
        var checked by mutableStateOf(true)
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Hub",
                    selectionMode = true,
                    onCancelSelection = { cancelled++ },
                    onBack = null,
                    autoFocus = false,
                ) {
                    Column {
                        Bb10ListItem("Mail", checked = checked, onClick = { checked = !checked })
                        Bb10ListItem("Call", checked = false, onClick = {})
                    }
                }
            }
        }
        rule.onNodeWithText("Mail").assertIsOn()
        rule.onNodeWithText("Cancel").performClick()
        assertEquals(1, cancelled)
    }

    // ------------------------------------------------------------ push transition

    @Test
    fun pushedPageSlidesInTakesFocusAndGoesBack() {
        var pushed by mutableStateOf<String?>(null)
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10PushHost(
                    pushed = pushed,
                    content = {
                        Bb10Scaffold(title = "Inbox", autoFocus = false) {
                            Column {
                                Bb10ListItem("Mail one", onClick = { pushed = "one" })
                                Bb10ListItem("Mail two", onClick = { pushed = "two" })
                            }
                        }
                    },
                    page = { id ->
                        Bb10Scaffold(title = "Detail $id", onBack = { pushed = null }) {
                            Column {
                                Bb10ListItem("Body of $id", onClick = {})
                                Bb10Button("Close", onClick = { pushed = null })
                            }
                        }
                    },
                )
            }
        }
        rule.onNodeWithText("Detail two").assertDoesNotExist()

        rule.onNodeWithText("Mail two").requestFocus()
        rule.onNodeWithText("Mail two").performClick()
        rule.mainClock.advanceTimeBy(600)
        rule.waitForIdle()
        rule.onNodeWithText("Detail two").assertExists()
        // Focus went into the page, not into the screen behind it.
        rule.onNodeWithText("Body of two").assertIsFocused()

        rule.onNodeWithText("Close").performClick()
        rule.mainClock.advanceTimeBy(600)
        rule.waitForIdle()
        rule.onNodeWithText("Detail two").assertDoesNotExist()
        // Back on the inbox, focus returned to the item that opened the page.
        rule.onNodeWithText("Mail two").assertIsFocused()
    }

    @Test
    fun dateFieldAndColumnsUseTheKitsMonthAndWeekdayNames() {
        screen {
            Bb10DatePickerField("Start", LocalDate.of(2026, 10, 11), {})
        }
        // Names come from string resources, not from the device date locale.
        rule.onNodeWithText("October 11, 2026").assertExists()
        rule.onNodeWithText("Start").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("October").assertExists()
        rule.onNodeWithText("Sunday").assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "vi")
    fun dateNamesFollowTheKitLanguage_vietnamese() {
        screen {
            Bb10DatePickerField("Bắt đầu", LocalDate.of(2026, 10, 11), {})
        }
        rule.onNodeWithText("11 tháng mười, 2026").assertExists()
        rule.onNodeWithText("Bắt đầu").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Chủ nhật").assertExists()
    }

    @Test
    fun timeFieldShowsAmPmFromResources() {
        screen {
            Bb10TimePickerField("Alarm", LocalTime.of(18, 5), {}, is24Hour = false)
        }
        rule.onNodeWithText("6:05 PM").assertExists()
    }

    @Test
    fun enterInADropdownPickerKeepsTheValueAndClosesIt() {
        var time = LocalTime.of(6, 30)
        screen {
            var value by remember { mutableStateOf(LocalTime.of(6, 30)) }
            time = value
            Bb10TimePickerField("Alarm time", value, { value = it })
        }
        rule.onNodeWithText("Alarm time").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Hour").requestFocus()
        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        assertEquals(LocalTime.of(7, 30), time)

        // Enter (also Space / d-pad center) is "done": the dropdown closes, the value stays and
        // focus goes back to the bar.
        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.Enter) }
        rule.mainClock.advanceTimeBy(500)
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Hour").assertDoesNotExist()
        assertEquals(LocalTime.of(7, 30), time)
        rule.onNodeWithText("Alarm time").assertIsFocused()
    }

    @Test
    fun spaceAndDpadCenterAlsoCloseTheDropdownPicker() {
        screen {
            var value by remember { mutableStateOf(LocalDate.of(2026, 10, 11)) }
            Bb10DatePickerField("Start", value, { value = it })
        }
        rule.onNodeWithText("Start").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Month").requestFocus()
        rule.onNodeWithContentDescription("Month").performKeyInput { pressKey(Key.Spacebar) }
        rule.mainClock.advanceTimeBy(500)
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Month").assertDoesNotExist()

        rule.onNodeWithText("Start").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Day").requestFocus()
        rule.onNodeWithContentDescription("Day").performKeyInput { pressKey(Key.DirectionCenter) }
        rule.mainClock.advanceTimeBy(500)
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Day").assertDoesNotExist()
    }

    @Test
    fun enterInAPopupPickerMovesFocusToOkInsteadOfConfirming() {
        var confirmed: LocalTime? = null
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10TimePickerDialog(
                    title = "Alarm",
                    time = LocalTime.of(6, 30),
                    onConfirm = { confirmed = it },
                    onDismiss = {},
                )
            }
        }
        rule.waitForIdle()
        // The picker has the focus when the popup opens, not the OK button.
        rule.onNodeWithContentDescription("Hour").assertIsFocused()

        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Hour").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()

        // Nothing was confirmed; focus is on OK, and a second Enter there confirms the 07:30.
        assertEquals(null, confirmed)
        rule.onNodeWithText("OK").assertIsFocused()
        rule.onNodeWithText("OK").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()
        assertEquals(LocalTime.of(7, 30), confirmed)
    }

    @Test
    fun openingAPickerNearTheBottomScrollsThePageSoItAllShows() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Settings", autoFocus = false) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        // Pushes the picker field below the fold.
                        Spacer(Modifier.height(420.dp))
                        var time by remember { mutableStateOf(LocalTime.of(13, 30)) }
                        Bb10TimePickerField("Alarm time", time, { time = it })
                        Spacer(Modifier.height(200.dp))
                    }
                }
            }
        }
        // The bar starts outside the 470dp screen, so it cannot be clicked where it is.
        rule.onNodeWithText("Alarm time").performScrollTo().performClick()
        rule.mainClock.advanceTimeBy(800)
        rule.waitForIdle()

        // The unfolded picker, not only its bar, is inside the screen.
        val hour = rule.onNodeWithContentDescription("Hour").getUnclippedBoundsInRoot()
        assertTrue("picker bottom ${hour.bottom} must be on screen", hour.bottom <= 470.dp)
        val bar = rule.onNodeWithText("Alarm time").getUnclippedBoundsInRoot()
        assertTrue("bar must still be on screen", bar.top >= 0.dp)
    }
}
