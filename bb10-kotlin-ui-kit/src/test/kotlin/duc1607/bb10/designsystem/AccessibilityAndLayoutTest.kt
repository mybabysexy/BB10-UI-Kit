package duc1607.bb10.designsystem

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import duc1607.bb10.designsystem.components.Bb10Action
import duc1607.bb10.designsystem.components.Bb10LinkItem
import duc1607.bb10.designsystem.components.Bb10ListHeader
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10NavItem
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10TextField
import duc1607.bb10.designsystem.components.Bb10Toggle
import duc1607.bb10.designsystem.components.hintLabel
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10Strings
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.abs

/** Accessibility semantics, touch targets, key aliases and bottom-bar geometry. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccessibilityAndLayoutTest {
    @get:Rule
    val rule = createComposeRule()

    private fun hasHeading() = SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private fun action(name: String, key: Key? = null, primary: Boolean = false, onClick: () -> Unit = {}) =
        Bb10Action(name, name, Bb10Icons.Search, key, primary = primary, onClick = onClick)

    private fun screen(
        actions: List<Bb10Action>,
        maxVisible: Int = 5,
        withNavigation: Boolean = false,
    ) {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(
                    title = "Hub",
                    actions = actions,
                    maxVisibleActions = maxVisible,
                    navigationItems = if (withNavigation) {
                        listOf(Bb10NavItem("Inbox", Bb10Icons.Mail))
                    } else emptyList(),
                    autoFocus = false,
                ) {
                    LazyColumn {
                        items(3) { i -> Bb10ListItem(title = "Item $i", onClick = {}) }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------- bottom bar geometry

    @Test
    fun primaryIsCentredAndLoneActionSitsToItsRight() {
        screen(listOf(action("Compose", primary = true), action("Search")))
        val primary = rule.onNodeWithContentDescription("Compose").getUnclippedBoundsInRoot()
        val search = rule.onNodeWithText("Search").getUnclippedBoundsInRoot()

        val primaryCentre = primary.left + primary.width / 2
        assertTrue(
            "primary must be at the middle of the 320dp screen, was $primaryCentre",
            abs((primaryCentre - 160.dp).value) < 1f,
        )
        assertTrue("the other action is right of the primary", search.left > primary.right - 1.dp)
    }

    @Test
    fun primaryIsCentredWithTwoOthers_firstLeftSecondRight() {
        screen(listOf(action("Compose", primary = true), action("Search"), action("Share")))
        val primary = rule.onNodeWithContentDescription("Compose").getUnclippedBoundsInRoot()
        val search = rule.onNodeWithText("Search").getUnclippedBoundsInRoot()
        val share = rule.onNodeWithText("Share").getUnclippedBoundsInRoot()

        assertTrue(abs((primary.left + primary.width / 2 - 160.dp).value) < 1f)
        assertTrue("first action on the left", search.right <= primary.left + 1.dp)
        assertTrue("second action on the right", share.left >= primary.right - 1.dp)
    }

    @Test
    fun primaryStaysCentredWithNavigationAndMenuButtons() {
        // maxVisible = 2: only the primary fits, "Search" and "Share" go to the right-hand menu.
        screen(
            listOf(action("Compose", primary = true), action("Search"), action("Share")),
            maxVisible = 2,
            withNavigation = true,
        )
        val primary = rule.onNodeWithContentDescription("Compose").getUnclippedBoundsInRoot()
        assertTrue(abs((primary.left + primary.width / 2 - 160.dp).value) < 1f)
        rule.onNodeWithContentDescription("Menu").assertExists()
        rule.onNodeWithContentDescription("More").assertExists()
    }

    // -------------------------------------------------------------- keys

    @Test
    fun delKeyTriggersTheDeleteAction() {
        var deleted = 0
        screen(listOf(action("Delete", Key.Delete) { deleted++ }))
        rule.onNodeWithText("Item 0").requestFocus()

        // The physical Del key is Android's DEL, i.e. Backspace in Compose.
        rule.onNodeWithText("Item 0").performKeyInput { pressKey(Key.Backspace) }

        assertEquals(1, deleted)
    }

    @Test
    fun hintsNameTheDeleteKeyDel() {
        assertEquals("Del", Key.Backspace.hintLabel())
        assertEquals("Del", Key.Delete.hintLabel())
        assertEquals("C", Key.C.hintLabel())
    }

    // -------------------------------------------------------------- accessibility

    @Test
    fun iconOnlyButtonsAndPrimaryActionHaveNames() {
        screen(
            listOf(action("Compose", primary = true), action("Search"), action("Share")),
            maxVisible = 2,
            withNavigation = true,
        )
        rule.onNodeWithContentDescription("Menu").assertExists()
        rule.onNodeWithContentDescription("More").assertExists()
        rule.onNodeWithContentDescription("Compose").assertExists()
    }

    @Test
    fun translatedStringsReplaceTheDefaults() {
        rule.setContent {
            Bb10Theme(
                scale = rememberBb10UiScale(),
                strings = rememberBb10Strings().copy(navigationMenu = "Danh mục", moreActions = "Thêm"),
            ) {
                Bb10Scaffold(
                    title = "Hub",
                    actions = listOf(
                        Bb10Action("a", "A", Bb10Icons.Add) {},
                        Bb10Action("b", "B", Bb10Icons.Add) {},
                    ),
                    maxVisibleActions = 1,
                    navigationItems = listOf(
                        Bb10NavItem("Inbox", Bb10Icons.Mail),
                    ),
                    autoFocus = false,
                ) {}
            }
        }
        rule.onNodeWithContentDescription("Danh mục").assertExists()
        rule.onNodeWithContentDescription("Thêm").assertExists()
    }

    @Test
    fun titleAndListHeadersAreHeadings() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Hub", autoFocus = false) {
                    Bb10ListHeader("Inbox")
                }
            }
        }
        rule.onNodeWithText("Hub").assert(hasHeading())
        rule.onNodeWithText("INBOX").assert(hasHeading())
    }

    @Test
    fun toggleExposesItsStateAndLinkItemItsSelection() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                val on = androidx.compose.runtime.remember { mutableStateOf(false) }
                Bb10Scaffold(title = "Settings", autoFocus = false) {
                    androidx.compose.foundation.layout.Column {
                        Bb10Toggle(on.value, { on.value = it }, label = "Notifications")
                        Bb10LinkItem("Hub", selected = true, onClick = {})
                        Bb10LinkItem("Keys", selected = false, onClick = {})
                    }
                }
            }
        }
        rule.onNodeWithText("Notifications").assertIsOff()
        rule.onNodeWithText("Notifications").performClick()
        rule.onNodeWithText("Notifications").assertIsOn()
        rule.onNodeWithText("Hub").assertIsSelected()
    }

    @Test
    fun requiredFieldIsAnnouncedAsRequired() {
        rule.setContent {
            Bb10Theme(scale = rememberBb10UiScale()) {
                Bb10Scaffold(title = "Form", autoFocus = false) {
                    Bb10TextField(
                        value = "",
                        onValueChange = {},
                        label = "User ID",
                        required = true,
                        placeholder = "Enter User ID",
                    )
                    Bb10TextField(
                        value = "secret",
                        onValueChange = {},
                        label = "Password",
                        password = true,
                    )
                }
            }
        }
        rule.onNodeWithContentDescription("User ID, required").assertExists()
        rule.onNodeWithContentDescription("Show password").assertExists()
        rule.onNodeWithContentDescription("Show password").performClick()
        rule.onNodeWithContentDescription("Hide password").assertExists()
    }

    @Test
    fun everyTouchTargetIsAtLeast48dp() {
        screen(
            listOf(action("Compose", primary = true), action("Search"), action("Share")),
            withNavigation = true,
        )
        val min = 48.dp
        val nodes = rule.onAllNodes(hasClickAction()).fetchSemanticsNodes()
        assertTrue("expected clickable nodes", nodes.isNotEmpty())
        nodes.forEachIndexed { index, node ->
            val bounds = rule.onAllNodes(hasClickAction())[index].getUnclippedBoundsInRoot()
            assertTrue(
                "node $index (${node.config}) is ${bounds.width} x ${bounds.height}, below 48dp",
                bounds.height >= min - 0.5.dp && bounds.width >= min - 0.5.dp,
            )
        }
    }

    // -------------------------------------------------------------- scaling

    @Test
    fun displaySizeScalesDensityAndIsReadableFromTheTheme() {
        var density = 0f
        var factor = 0f
        lateinit var state: duc1607.bb10.designsystem.theme.Bb10ScaleState
        rule.setContent {
            state = rememberBb10UiScale()
            Bb10Theme(scale = state) {
                density = LocalDensity.current.density
                factor = Bb10.uiScale
            }
        }
        val baseDensity = density
        assertEquals(1f, factor, 0.001f)

        rule.runOnUiThread { state.update(2f) }
        rule.waitForIdle()

        assertEquals(2f, factor, 0.001f)
        assertEquals(baseDensity * 2f, density, 0.001f)
    }

    @Test
    fun scaleIsClampedToTheSupportedRange() {
        lateinit var state: duc1607.bb10.designsystem.theme.Bb10ScaleState
        rule.setContent { state = rememberBb10UiScale() }
        rule.runOnUiThread { state.update(10f) }
        assertEquals(3f, state.value, 0.001f)
        rule.runOnUiThread { state.update(0.1f) }
        assertEquals(0.5f, state.value, 0.001f)
    }

    @Test
    fun noPrimaryMeansNoExtraSlots() {
        // splitActions: without a primary action every slot is used by plain actions.
        val a = (1..4).map { Bb10Action("a$it", "A$it", Bb10Icons.Add) {} }
        val slots = duc1607.bb10.designsystem.components.splitActions(a, 5)
        assertNull(slots.primary)
        assertEquals(4, slots.visible.size)
        assertTrue(slots.overflow.isEmpty())
    }

    @Test
    fun overflowTakesTheLastVisibleSlot() {
        val a = (1..5).map { Bb10Action("a$it", "A$it", Bb10Icons.Add) {} } +
            Bb10Action("p", "P", Bb10Icons.Add, primary = true) {}
        val slots = duc1607.bb10.designsystem.components.splitActions(a, 5)
        // 5 slots: primary + 3 plain + "..."; the rest overflow.
        assertEquals("p", slots.primary?.id)
        assertEquals(listOf("a1", "a2", "a3"), slots.visible.map { it.id })
        assertEquals(listOf("a4", "a5"), slots.overflow.map { it.id })
    }
}
