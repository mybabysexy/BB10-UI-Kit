package duc1607.bb10.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import duc1607.bb10.designsystem.components.Bb10Button
import duc1607.bb10.designsystem.components.Bb10Dialog
import duc1607.bb10.designsystem.components.Bb10InputDialog
import duc1607.bb10.designsystem.components.Bb10TextField
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * A text field has two levels of focus: the d-pad lands on it without entering it, so arrow keys
 * never get trapped as cursor keys. It is entered with Enter / Space / center, a tap or a letter.
 */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TextFieldEditingTest {
    @get:Rule
    val rule = createComposeRule()

    private var text = ""

    private fun screen(password: Boolean = false, initial: String = "") {
        text = initial
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Column {
                    Bb10Button("Above", onClick = {})
                    var value by remember { mutableStateOf(initial) }
                    text = value
                    Bb10TextField(
                        value = value,
                        onValueChange = { value = it },
                        label = "Name",
                        password = password,
                    )
                    Bb10Button("Below", onClick = {})
                }
            }
        }
    }

    private fun press(key: Key) {
        rule.onNodeWithText("Above").performKeyInput { pressKey(key) }
        rule.waitForIdle()
    }

    @Test
    fun theDpadWalksPastAFieldInsteadOfGettingStuckInIt() {
        screen()
        rule.onNodeWithText("Above").requestFocus()

        press(Key.DirectionDown)
        // On the field, but not inside it: no cursor, nothing typed.
        rule.onNodeWithContentDescription("Name").assertIsNotFocused()

        press(Key.DirectionDown)
        rule.onNodeWithText("Below").assertIsFocused()
        press(Key.DirectionUp)
        press(Key.DirectionUp)
        rule.onNodeWithText("Above").assertIsFocused()
    }

    @Test
    fun enterStartsEditingAndEnterEndsIt() {
        screen()
        rule.onNodeWithText("Above").requestFocus()
        press(Key.DirectionDown)

        press(Key.Enter)
        rule.onNodeWithContentDescription("Name").assertIsFocused()

        press(Key.Enter)
        rule.onNodeWithContentDescription("Name").assertIsNotFocused()
        // Back on the highlighted field: the d-pad moves on again.
        press(Key.DirectionDown)
        rule.onNodeWithText("Below").assertIsFocused()
    }

    private fun editingStartsWith(key: Key) {
        screen()
        rule.onNodeWithText("Above").requestFocus()
        press(Key.DirectionDown)

        press(key)

        rule.onNodeWithContentDescription("Name").assertIsFocused()
    }

    @Test
    fun spaceStartsEditing() = editingStartsWith(Key.Spacebar)

    @Test
    fun dpadCenterStartsEditing() = editingStartsWith(Key.DirectionCenter)

    @Test
    fun typingALetterStartsEditingAndKeepsTheLetter() {
        screen(initial = "ab")
        rule.onNodeWithText("Above").requestFocus()
        press(Key.DirectionDown)

        press(Key.C)

        rule.onNodeWithContentDescription("Name").assertIsFocused()
        assertEquals("abc", text)
        press(Key.D)
        assertEquals("abcd", text)
    }

    @Test
    fun whileEditingUpAndDownLeaveTheField() {
        screen()
        rule.onNodeWithText("Above").requestFocus()
        press(Key.DirectionDown)
        press(Key.Enter)
        rule.onNodeWithContentDescription("Name").assertIsFocused()

        press(Key.DirectionDown)

        rule.onNodeWithText("Below").assertIsFocused()
        rule.onNodeWithContentDescription("Name").assertIsNotFocused()
    }

    @Test
    fun theCursorKeysLeaveAtTheEndOfTheText() {
        screen(password = true, initial = "ab")
        rule.onNodeWithContentDescription("Name").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Name").assertIsFocused()

        // The cursor starts at the end: right goes on to the eye button.
        press(Key.DirectionRight)

        rule.onNodeWithContentDescription("Show password").assertIsFocused()
    }

    @Test
    fun tappingTheFieldStartsEditing() {
        screen()

        rule.onNodeWithContentDescription("Name").performClick()
        rule.waitForIdle()

        rule.onNodeWithContentDescription("Name").assertIsFocused()
    }

    @Test
    fun anInputDialogOpensWithTheCursorInTheFieldAndEnterMovesToOk() {
        var typed = ""
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                var value by remember { mutableStateOf("") }
                typed = value
                Bb10InputDialog(
                    title = "Rename",
                    value = value,
                    onValueChange = { value = it },
                    onConfirm = {},
                    onDismiss = {},
                    placeholder = "Name",
                    confirmText = "Save",
                    dismissText = "Cancel",
                )
            }
        }
        rule.waitForIdle()
        rule.onNodeWithText("Name").assertExists()

        // Typing goes straight into the field...
        rule.onNodeWithText("Save").performKeyInput { pressKey(Key.A) }
        rule.waitForIdle()
        assertEquals("a", typed)

        // ...and Enter ends editing by moving to the confirm button.
        rule.onNodeWithText("Save").performKeyInput { pressKey(Key.Enter) }
        rule.waitForIdle()
        rule.onNodeWithText("Save").assertIsFocused()
    }

    @Test
    fun anErrorGreysOutTheConfirmButton() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10InputDialog(
                    title = "Rename",
                    value = "a b",
                    onValueChange = {},
                    onConfirm = {},
                    onDismiss = {},
                    error = "No spaces",
                    confirmText = "Save",
                )
            }
        }
        rule.onNodeWithText("No spaces").assertExists()
        rule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun confirmEnabledFollowsTheForm() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Dialog(title = "T", confirmText = "Go", onConfirm = {}, onDismiss = {}, confirmEnabled = true)
            }
        }
        rule.onNodeWithText("Go").assertIsEnabled()
    }
}
