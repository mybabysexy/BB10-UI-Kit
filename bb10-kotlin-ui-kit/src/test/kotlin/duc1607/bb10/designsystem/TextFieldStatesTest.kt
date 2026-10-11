package duc1607.bb10.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import duc1607.bb10.designsystem.components.Bb10ScalePicker
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10Search
import duc1607.bb10.designsystem.components.Bb10TextField
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Input states and the kit's own (translatable) strings. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TextFieldStatesTest {
    @get:Rule
    val rule = createComposeRule()

    private fun field(content: @androidx.compose.runtime.Composable () -> Unit) {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Form", autoFocus = false) { content() }
            }
        }
    }

    private fun hasErrorSemantics(text: String) = SemanticsMatcher("error = $text") {
        it.config.getOrNull(SemanticsProperties.Error) == text
    }

    @Test
    fun errorShowsMessageAndIsExposedToScreenReaders() {
        field {
            Bb10TextField(
                value = "abc",
                onValueChange = {},
                label = "Email",
                error = "Not an address",
                helper = "We never share it",
            )
        }
        rule.onNodeWithText("Not an address").assertExists()
        // The error replaces the helper text.
        rule.onNodeWithText("We never share it").assertDoesNotExist()
        rule.onNodeWithContentDescription("Email").assert(hasErrorSemantics("Not an address"))
    }

    @Test
    fun helperIsShownWhenThereIsNoError() {
        field {
            Bb10TextField(value = "", onValueChange = {}, label = "Email", helper = "We never share it")
        }
        rule.onNodeWithText("We never share it").assertExists()
    }

    @Test
    fun disabledFieldCannotBeEdited() {
        field {
            Bb10TextField(value = "locked", onValueChange = {}, label = "Account", enabled = false)
        }
        rule.onNodeWithContentDescription("Account").assertIsNotEnabled()
    }

    @Test
    fun maxLengthCutsInputAndCountsCharacters() {
        var text = ""
        field {
            var value by remember { mutableStateOf("") }
            text = value
            Bb10TextField(value = value, onValueChange = { value = it }, label = "Code", maxLength = 5)
        }
        rule.onNodeWithText("0/5").assertExists()

        rule.onNodeWithContentDescription("Code").performTextInput("12345")
        rule.onNodeWithText("5/5").assertExists()

        // At the limit further typing is ignored.
        rule.onNodeWithContentDescription("Code").performTextInput("6")
        rule.onNodeWithText("5/5").assertExists()
        assertEquals("12345", text)
    }

    @Test
    fun clearButtonEmptiesTheFieldAndHidesItself() {
        field {
            var value by remember { mutableStateOf("hello") }
            Bb10TextField(value = value, onValueChange = { value = it }, label = "Note", clearable = true)
        }
        rule.onNodeWithContentDescription("Clear").assertExists()

        rule.onNodeWithContentDescription("Clear").performClick()

        rule.onNodeWithContentDescription("Clear").assertDoesNotExist()
        rule.onNodeWithContentDescription("Note").assertTextEquals("")
    }

    @Test
    fun readOnlyFieldHasNoClearButton() {
        field {
            Bb10TextField(value = "fixed", onValueChange = {}, label = "Id", readOnly = true, clearable = true)
        }
        rule.onNodeWithContentDescription("Clear").assertDoesNotExist()
    }

    // ------------------------------------------------------------ string resources

    @Test
    fun kitStringsFollowTheDeviceLanguage_english() {
        field {
            Bb10TextField(value = "x", onValueChange = {}, label = "Pw", password = true)
        }
        rule.onNodeWithContentDescription("Show password").assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "vi")
    fun kitStringsFollowTheDeviceLanguage_vietnamese() {
        field {
            Bb10TextField(value = "x", onValueChange = {}, label = "Pw", password = true, clearable = true)
        }
        rule.onNodeWithContentDescription("Hiện mật khẩu").assertExists()
    }

    @Test
    @Config(sdk = [34], qualifiers = "vi")
    fun searchHeaderAndScalePickerUseTranslatedDefaults() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Danh bạ",
                    search = Bb10Search(query = "", onQueryChange = {}, onClose = {}),
                    autoFocus = false,
                ) {
                    Bb10ScalePicker(rememberBb10UiScale())
                }
            }
        }
        // Cancel button and the field's name come from the string resources.
        rule.onNodeWithText("Hủy").assertExists()
        rule.onNodeWithContentDescription("Tìm kiếm").assertExists()
        // The display-size picker: label and step names.
        rule.onNodeWithText("Cỡ hiển thị").assertExists()
        rule.onNodeWithText("Vừa").assertExists()
    }
}
