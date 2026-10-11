package duc1607.bb10.designsystem.components

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10
import kotlinx.coroutines.delay

/**
 * Screen frame: the [title] header on top (every screen has one, usually the app name), the
 * [content] in the middle and the action bar at the bottom, plus the two menus BB10 apps use:
 *
 * - **right**: the menu with the [actions] that do not fit in the bar (the "..." button at the
 *   right end of the bar, the Menu key, or holding a list item that has `onLongClick`). It
 *   slides over the app: while a list item is held it only peeks in (about 2 mm) and the app is
 *   dimmed at once; when the finger lifts it opens fully.
 * - **left**: the app's own navigation menu, when [navigationItems] is not empty (the button at
 *   the left end of the action bar). It sits behind the app: the
 *   whole app UI is pushed to the right and dimmed to reveal it.
 *
 * An action with `primary = true` is drawn as a large circle floating above the middle of the
 * action bar; the other actions cluster around it. With no actions at all the bar disappears and
 * only the side buttons remain, as two tiles in the corners.
 *
 * The left end of the bar holds [onBack] (a Back button, for a screen reached from another) *or*
 * the navigation menu, never both. System Back calls [onBack] when nothing else is open.
 * While [selectionMode] is on (multi-select in a list) it holds Cancel instead, with its label;
 * [onCancelSelection] ends the mode (Back does too). The app swaps `actions` for the actions
 * that apply to the selection.
 *
 * [tabs] replaces the title in the header with global tabs (the app's top-level sections, as in
 * a calculator); local tabs under the header are `Bb10TabBar`.
 * Confirm/dismiss actions (Cancel, OK, Send) belong in the header: [dismissAction] / [acceptAction].
 * Pass a [search] to turn the header into the search field + Cancel.
 *
 * Keyboard handling lives here:
 * - an action's `shortcut` key triggers it (only if nothing deeper, e.g. a text field, consumed the key)
 * - Menu opens/closes the right menu (a focused list item with a long-click handler gets Menu first)
 * - Back closes search, or whichever menu is open
 * - on start, focus moves to the first focusable element so keys work before the first touch
 */
@Composable
fun Bb10Scaffold(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    dismissAction: Bb10TitleAction? = null,
    acceptAction: Bb10TitleAction? = null,
    search: Bb10Search? = null,
    actions: List<Bb10Action> = emptyList(),
    maxVisibleActions: Int = 5,
    navigationItems: List<Bb10NavItem> = emptyList(),
    selectedNavigation: Int = 0,
    onNavigate: (Int) -> Unit = {},
    onBack: (() -> Unit)? = null,
    selectionMode: Boolean = false,
    onCancelSelection: () -> Unit = {},
    tabs: List<String>? = null,
    selectedTab: Int = 0,
    onTabSelect: (Int) -> Unit = {},
    autoFocus: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val focusManager = LocalFocusManager.current
    val showHints = LocalConfiguration.current.keyboard != Configuration.KEYBOARD_NOKEYS
    val slots = splitActions(actions, maxVisibleActions)
    val hasOverflow = slots.overflow.isNotEmpty()
    val hasNavigation = navigationItems.isNotEmpty()
    require(onBack == null || !hasNavigation) {
        "The left end of the bottom bar is either the navigation menu or Back, not both: " +
            "pass navigationItems or onBack"
    }

    var menuState by remember { mutableStateOf(PanelState.Hidden) }
    var navOpen by remember { mutableStateOf(false) }
    var restoreMoreFocus by remember { mutableStateOf(false) }
    var restoreFocus by remember { mutableStateOf(false) }
    val moreFocus = remember { FocusRequester() }

    // Panels stay composed for their animation; wait it out before moving focus back.
    fun closeOverflow() {
        menuState = PanelState.Hidden
        restoreMoreFocus = true
    }
    fun closeNavigation() {
        navOpen = false
        restoreFocus = true
    }
    fun openOverflow() {
        navOpen = false
        menuState = PanelState.Open
    }
    fun peekOverflow() {
        if (!navOpen && menuState == PanelState.Hidden) menuState = PanelState.Peek
    }
    fun openNavigation() {
        menuState = PanelState.Hidden
        navOpen = true
    }

    // List items talk to the right-hand menu through this (hold = peek, release = open).
    val menuController = remember(hasOverflow) {
        if (hasOverflow) Bb10MenuController(peek = { peekOverflow() }, open = { openOverflow() }) else null
    }

    // A page of a Bb10PushHost is focused by the host; do not grab focus in the screen behind.
    val inPage = LocalBb10InPage.current
    if (autoFocus && !inPage) {
        LaunchedEffect(Unit) {
            withFrameNanos { }
            runCatching { focusManager.moveFocus(FocusDirection.Next) }
        }
    }
    LaunchedEffect(restoreMoreFocus) {
        if (restoreMoreFocus) {
            delay(PANEL_EXIT_MS)
            runCatching { moreFocus.requestFocus() }
            restoreMoreFocus = false
        }
    }
    LaunchedEffect(restoreFocus) {
        if (restoreFocus) {
            delay(PANEL_EXIT_MS)
            runCatching { focusManager.moveFocus(FocusDirection.Next) }
            restoreFocus = false
        }
    }
    // Registered first, so search or a menu that is open wins when Back is pressed.
    BackHandler(enabled = onBack != null && search == null && menuState == PanelState.Hidden && !navOpen) {
        onBack?.invoke()
    }
    // Selection mode ends before the screen is left.
    BackHandler(enabled = selectionMode && search == null && menuState == PanelState.Hidden && !navOpen) {
        onCancelSelection()
    }
    BackHandler(enabled = search != null) { search?.onClose?.invoke() }
    BackHandler(enabled = menuState != PanelState.Hidden || navOpen) {
        if (menuState != PanelState.Hidden) closeOverflow() else closeNavigation()
    }

    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(c.surface)
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                if (e.key == Key.Menu && hasOverflow) {
                    if (menuState == PanelState.Open) closeOverflow() else openOverflow()
                    return@onKeyEvent true
                }
                if (e.isCtrlPressed || e.isAltPressed || e.isMetaPressed) return@onKeyEvent false
                val action = actions.firstOrNull { it.enabled && shortcutMatches(it.shortcut, e.key) }
                if (action != null) {
                    action.onClick()
                    true
                } else false
            }
    ) {
        val strings = Bb10.strings
        // The lone button at the left end of the bar: Back, or the navigation menu.
        val leading = when {
            // Selection mode: Cancel (icon + label) replaces Back / the navigation menu.
            selectionMode -> Bb10Action("cancel", strings.cancel, Bb10Icons.Cancel, onClick = onCancelSelection)
            onBack != null -> Bb10Action("back", strings.back, Bb10Icons.ChevronLeft, onClick = onBack)
            hasNavigation -> Bb10Action("menu", strings.navigationMenu, Bb10Icons.Menu) {
                if (navOpen) closeNavigation() else openNavigation()
            }
            else -> null
        }
        val navWidth = minOf(maxWidth * 0.8f, 320.dp)
        val push by animateDpAsState(
            targetValue = if (navOpen) navWidth else 0.dp,
            animationSpec = tween(200),
            label = "navPush",
        )

        // The navigation menu lives behind the app and is revealed by pushing the app aside.
        if (hasNavigation && !selectionMode && (navOpen || push > 0.dp)) {
            Bb10NavDrawer(
                open = navOpen,
                items = navigationItems,
                selectedIndex = selectedNavigation,
                onSelect = onNavigate,
                onDismiss = { closeNavigation() },
                width = navWidth,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }

        CompositionLocalProvider(LocalBb10Menu provides menuController) {
            Box(
                Modifier
                    .fillMaxSize()
                    .offset(x = push)
                    .background(c.background),
            ) {
                Column(Modifier.fillMaxSize()) {
                    Bb10TitleBar(
                        title = title,
                        subtitle = subtitle,
                        dismissAction = dismissAction,
                        acceptAction = acceptAction,
                        search = search,
                        tabs = tabs,
                        selectedTab = selectedTab,
                        onTabSelect = onTabSelect,
                    )
                    val hasMiddle = slots.primary != null || slots.visible.isNotEmpty()
                    val hasBar = hasMiddle || hasOverflow || leading != null
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        // The action bar floats over the content (its primary circle sticks out
                        // above the bar), so the content only gets padded by the visible bar.
                        Box(
                            Modifier
                                .fillMaxSize()
                                // Only a real bar takes room; two corner tiles float over the content.
                                .padding(bottom = if (hasMiddle) d.actionBarHeight + d.dividerThickness else 0.dp),
                            content = content,
                        )
                        if (hasBar) {
                            Bb10ActionBar(
                                slots = slots,
                                modifier = Modifier.align(Alignment.BottomCenter),
                                leading = leading,
                                leadingIsBack = onBack != null && !selectionMode,
                                leadingShowsLabel = selectionMode,
                                moreFocusRequester = moreFocus,
                                onMore = { openOverflow() },
                            )
                        }
                    }
                }
                if (push > 0.dp) {
                    // Dims the pushed app in step with the push, and turns it into the handle
                    // that closes the menu: tap it or swipe it back to the left.
                    val progress = (push / navWidth).coerceIn(0f, 1f)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(c.scrim.copy(alpha = c.scrim.alpha * progress))
                            .pointerInput(Unit) { detectTapGestures { closeNavigation() } }
                            .pointerInput(Unit) {
                                val threshold = 48.dp.toPx()
                                var drag = 0f
                                detectHorizontalDragGestures(
                                    onDragStart = { drag = 0f },
                                    onDragEnd = { if (-drag > threshold) closeNavigation() },
                                    onHorizontalDrag = { _, dx -> drag += dx },
                                )
                            }
                    )
                    Box(Modifier.fillMaxHeight().width(1.dp).background(c.divider))
                }
            }
        }

        if (hasOverflow) {
            Bb10OverflowMenu(
                state = menuState,
                actions = slots.overflow,
                showHints = showHints,
                onDismiss = { closeOverflow() },
            )
        }
    }
}

private const val PANEL_EXIT_MS = 220L
