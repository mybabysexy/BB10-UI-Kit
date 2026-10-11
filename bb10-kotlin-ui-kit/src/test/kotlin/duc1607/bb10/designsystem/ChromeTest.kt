package duc1607.bb10.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import duc1607.bb10.designsystem.components.Bb10Action
import duc1607.bb10.designsystem.components.Bb10NavItem
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10TabBar
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

/** Bottom bar with no actions, Back vs navigation menu, tabs, stacked subtitle. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ChromeTest {
    @get:Rule
    val rule = createComposeRule()

    private val nav = listOf(Bb10NavItem("Inbox", Bb10Icons.Mail))

    private fun action(name: String) = Bb10Action(name, name, Bb10Icons.Add) {}

    @Test
    fun withoutActionsOnlyTwoCornerTilesRemain() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Hub",
                    actions = listOf(action("A"), action("B")),
                    maxVisibleActions = 0, // nothing in the middle: both go to the menu
                    navigationItems = nav,
                    autoFocus = false,
                ) {}
            }
        }
        val menu = rule.onNodeWithContentDescription("Menu").getUnclippedBoundsInRoot()
        val more = rule.onNodeWithContentDescription("More").getUnclippedBoundsInRoot()

        // Left tile in the bottom-left corner, right tile in the bottom-right corner.
        assertTrue("menu tile at the left edge", abs(menu.left.value) < 1f)
        assertTrue("more tile at the right edge", abs((more.right - 320.dp).value) < 1f)
        assertTrue("both sit at the bottom", abs((menu.bottom - more.bottom).value) < 1f)
        // Tiles, not a bar: they are square and far apart.
        assertTrue(abs((menu.width - menu.height).value) < 1f)
        assertTrue(more.left - menu.right > 150.dp)
    }

    @Test
    fun backButtonTakesTheLeftEndInsteadOfTheNavigationMenu() {
        var backs = 0
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Detail",
                    actions = listOf(action("Open")),
                    onBack = { backs++ },
                    autoFocus = false,
                ) {}
            }
        }
        rule.onNodeWithContentDescription("Menu").assertDoesNotExist()
        rule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, backs)
    }

    @Test
    fun navigationMenuAndBackCannotBeCombined() {
        try {
            rule.setContent {
                Bb10Theme(scale = rememberBb10UiScale()) {
                    Bb10Scaffold(
                        title = "Both",
                        navigationItems = nav,
                        onBack = {},
                        autoFocus = false,
                    ) {}
                }
            }
            fail("expected the scaffold to refuse both a navigation menu and Back")
        } catch (e: Throwable) {
            var cause: Throwable? = e
            var found = false
            while (cause != null) {
                if (cause.message?.contains("either the navigation menu or Back") == true) found = true
                cause = cause.cause
            }
            assertTrue("unexpected failure: $e", found)
        }
    }

    @Test
    fun localTabsSwitchAndExposeSelection() {
        var chosen = 0
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Lists", autoFocus = false) {
                    var index by remember { mutableIntStateOf(0) }
                    chosen = index
                    Bb10TabBar(listOf("Tasks", "Notes"), index, { index = it })
                }
            }
        }
        rule.onNodeWithText("Tasks").assertIsSelected()
        rule.onNodeWithText("Notes").performClick()
        rule.waitForIdle()
        assertEquals(1, chosen)
        rule.onNodeWithText("Notes").assertIsSelected()
    }

    @Test
    fun globalTabsReplaceTheTitleInTheHeader() {
        var picked = 0
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                var index by remember { mutableIntStateOf(0) }
                picked = index
                Bb10Scaffold(
                    title = "Calculator",
                    tabs = listOf("Calc", "Tip", "Convert"),
                    selectedTab = index,
                    onTabSelect = { index = it },
                    autoFocus = false,
                ) {}
            }
        }
        rule.onNodeWithText("Calc").assertIsSelected()
        // The title is not drawn: the tabs take its place.
        rule.onNodeWithText("Calculator").assertDoesNotExist()
        rule.onNodeWithText("Convert").performClick()
        rule.waitForIdle()
        assertEquals(2, picked)
    }

    @Test
    fun subtitleSitsUnderTheTitleLeftAligned() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "BlackBerry ID", subtitle = "03:04:59 11-10-2026", autoFocus = false) {}
            }
        }
        val title = rule.onNodeWithText("BlackBerry ID").getUnclippedBoundsInRoot()
        val subtitle = rule.onNodeWithText("03:04:59 11-10-2026").getUnclippedBoundsInRoot()
        assertTrue("subtitle below title", subtitle.top >= title.bottom - 1.dp)
        assertTrue("same left edge", abs((subtitle.left - title.left).value) < 1f)
    }

    @Test
    fun tabsAreCompact() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Lists", autoFocus = false) {
                    Bb10TabBar(listOf("Tasks", "Notes"), 0, {})
                }
            }
        }
        val tab = rule.onNodeWithText("Tasks").getUnclippedBoundsInRoot()
        assertEquals(40f, (tab.bottom - tab.top).value, 1f)
    }

    @Test
    fun backButtonHasItsSlantedEdge_andPrimaryStaysCentred() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Detail",
                    actions = listOf(
                        Bb10Action("open", "Open", Bb10Icons.Add, primary = true) {},
                        action("Share"),
                        action("Hidden"),
                    ),
                    maxVisibleActions = 2, // only the primary fits: the others go to the menu
                    onBack = {},
                    autoFocus = false,
                ) {}
            }
        }
        val back = rule.onNodeWithContentDescription("Back").getUnclippedBoundsInRoot()
        val more = rule.onNodeWithContentDescription("More").getUnclippedBoundsInRoot()
        val primary = rule.onNodeWithContentDescription("Open").getUnclippedBoundsInRoot()

        // Back is a touch wider than the other buttons (its right edge is the slanted slash);
        // the "..." end keeps its 52dp and the primary stays in the middle.
        assertEquals(56f, (back.right - back.left).value, 0.5f)
        assertEquals(52f, (more.right - more.left).value, 0.5f)
        assertEquals(0f, back.left.value, 0.5f)
        assertEquals(160f, (primary.left + (primary.right - primary.left) / 2).value, 1f)
    }
}
