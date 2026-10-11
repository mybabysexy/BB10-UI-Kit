package duc1607.bb10.catalog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Opens every page of the catalog through the navigation menu; none may crash. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CatalogSmokeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun everySectionRenders() {
        rule.setContent { CatalogApp() }
        val sections = listOf(
            "Header", "Action bar", "Menus", "Tabs", "Lists", "Forms", "Pickers", "Dialogs", "Icons", "Accessibility",
            "Foundations",
        )
        sections.forEach { title ->
            // The app's own menu button is the last one (pages may embed demo scaffolds with their own).
            rule.onAllNodesWithContentDescription("Menu").onLast().performClick()
            rule.mainClock.advanceTimeBy(400)
            rule.waitForIdle()
            // The entry in the open navigation menu.
            rule.onAllNodesWithText(title).onFirst().performScrollTo().performClick()
            rule.mainClock.advanceTimeBy(500)
            rule.waitForIdle()
            // The page is showing (its title is the header) and the app is still alive.
            assertTrue("page $title not showing", rule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty())
            rule.onAllNodesWithContentDescription("Menu").onLast().assertIsDisplayed()
        }
    }
}
