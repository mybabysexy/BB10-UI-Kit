package duc1607.bb10.designsystem

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.center
import androidx.compose.ui.test.down
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.components.Bb10Action
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10NavItem
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10TitleAction
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

/** Keyboard / d-pad / touch behaviour of the screen frame, run on the JVM with Robolectric. */
@OptIn(ExperimentalTestApi::class, ExperimentalComposeUiApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ScaffoldBehaviourTest {
    @get:Rule
    val rule = createComposeRule()

    private var heldItem: String? = null

    /**
     * What the window is like after a touch: in touch mode clickable items refuse focus (they
     * are `focusableInNonTouchMode`) until something asks for keyboard mode.
     */
    private class TouchModeUntilAsked : InputModeManager {
        override var inputMode: InputMode = InputMode.Touch
            private set

        @ExperimentalComposeUiApi
        override fun requestInputMode(inputMode: InputMode): Boolean {
            this.inputMode = inputMode
            return true
        }
    }

    private fun setScreen(
        withHeaderActions: Boolean = true,
        withNavigation: Boolean = false,
        maxVisibleActions: Int = 3,
        afterATouch: Boolean = false,
    ) {
        val touchMode = TouchModeUntilAsked()
        rule.setContent {
            val modeProvider = if (afterATouch) {
                LocalInputModeManager provides touchMode
            } else LocalInputModeManager provides LocalInputModeManager.current
            CompositionLocalProvider(modeProvider) {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Hub",
                    dismissAction = if (withHeaderActions) Bb10TitleAction("Cancel") {} else null,
                    acceptAction = if (withHeaderActions) Bb10TitleAction("Save") {} else null,
                    actions = listOf(
                        Bb10Action("compose", "Compose", Bb10Icons.Edit, primary = true) {},
                        Bb10Action("search", "Search", Bb10Icons.Search) {},
                        Bb10Action("delete", "Delete", Bb10Icons.Delete) {},
                    ),
                    maxVisibleActions = maxVisibleActions,
                    navigationItems = if (withNavigation) {
                        listOf(
                            Bb10NavItem("Inbox", Bb10Icons.Mail),
                            Bb10NavItem("Settings", Bb10Icons.Sliders),
                        )
                    } else emptyList(),
                    autoFocus = false,
                ) {
                    LazyColumn {
                        items(12) { i ->
                            Bb10ListItem(
                                title = "Item $i",
                                onLongClick = { heldItem = "Item $i" },
                                onClick = {},
                            )
                        }
                    }
                }
            }
            }
        }
    }

    @Test
    fun downFromCancelFocusesFirstListItem() {
        setScreen()
        rule.onNodeWithText("Cancel").requestFocus()
        rule.onNodeWithText("Cancel").assertIsFocused()

        rule.onNodeWithText("Cancel").performKeyInput { pressKey(Key.DirectionDown) }

        rule.onNodeWithText("Item 0").assertIsFocused()
    }

    @Test
    fun downFromSaveFocusesFirstListItem() {
        setScreen()
        rule.onNodeWithText("Save").requestFocus()

        rule.onNodeWithText("Save").performKeyInput { pressKey(Key.DirectionDown) }

        rule.onNodeWithText("Item 0").assertIsFocused()
    }

    @Test
    fun downThroughListMovesToNextItem() {
        setScreen()
        rule.onNodeWithText("Item 0").requestFocus()

        rule.onNodeWithText("Item 0").performKeyInput { pressKey(Key.DirectionDown) }

        rule.onNodeWithText("Item 1").assertIsFocused()
    }

    @Test
    fun holdingAnItemPeeksMenuThenOpensItOnRelease() {
        setScreen(withHeaderActions = false, maxVisibleActions = 2)

        // Finger down and held past the long-press time: the app is told which item, the menu
        // is only peeking (its entries are still off screen) and nothing is open yet.
        rule.onNodeWithText("Item 2").performTouchInput { down(center) }
        rule.mainClock.advanceTimeBy(700)
        rule.waitForIdle()
        assertEquals("Item 2", heldItem)
        rule.onNodeWithText("Delete").assertIsNotDisplayed()

        // Lifting the finger slides the menu fully in.
        rule.onNodeWithText("Item 2").performTouchInput { up() }
        rule.mainClock.advanceTimeBy(600)
        rule.waitForIdle()
        rule.onNodeWithText("Delete").assertIsDisplayed()
    }

    @Test
    fun navigationButtonIsOnTheBottomBarAndPushesTheApp() {
        setScreen(withHeaderActions = false, withNavigation = true)
        val titleLeftBefore = rule.onNodeWithText("Hub").getUnclippedBoundsInRoot().left

        rule.onNodeWithContentDescription("Menu").performClick()
        rule.mainClock.advanceTimeBy(500)
        rule.waitForIdle()

        rule.onNodeWithText("Inbox").assertIsDisplayed()
        val titleLeftAfter = rule.onNodeWithText("Hub").getUnclippedBoundsInRoot().left
        assertTrue("app UI should be pushed right", titleLeftAfter > titleLeftBefore + 100.dp)
    }

    @Test
    fun dpadMovesThroughTheContextMenuOpenedByAHold() {
        setScreen(withHeaderActions = false, maxVisibleActions = 1, afterATouch = true)

        // Hold with a finger, lift: the menu opens after a touch, with no key pressed yet.
        rule.onNodeWithText("Item 2").performTouchInput { down(center) }
        rule.mainClock.advanceTimeBy(700)
        rule.onNodeWithText("Item 2").performTouchInput { up() }
        rule.mainClock.advanceTimeBy(600)
        rule.waitForIdle()

        // Focus is already on the first entry, and the d-pad walks down the menu.
        rule.onNodeWithText("Search").assertIsFocused()
        rule.onNodeWithText("Search").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        rule.onNodeWithText("Delete").assertIsFocused()
        rule.onNodeWithText("Delete").performKeyInput { pressKey(Key.DirectionUp) }
        rule.waitForIdle()
        rule.onNodeWithText("Search").assertIsFocused()
    }

    @Test
    fun dpadMovesThroughTheMenuOpenedWithTheMenuKey() {
        setScreen(withHeaderActions = false, maxVisibleActions = 1)
        rule.onNodeWithText("Item 1").requestFocus()

        rule.onNodeWithText("Item 1").performKeyInput { pressKey(Key.Menu) }
        rule.mainClock.advanceTimeBy(600)
        rule.waitForIdle()

        rule.onNodeWithText("Search").assertIsFocused()
        rule.onNodeWithText("Search").performKeyInput { pressKey(Key.DirectionDown) }
        rule.waitForIdle()
        rule.onNodeWithText("Delete").assertIsFocused()
    }
}
