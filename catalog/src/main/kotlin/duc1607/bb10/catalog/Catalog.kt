package duc1607.bb10.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.components.Bb10Action
import duc1607.bb10.designsystem.components.Bb10Button
import duc1607.bb10.designsystem.components.Bb10Checkbox
import duc1607.bb10.designsystem.components.Bb10DatePicker
import duc1607.bb10.designsystem.components.Bb10DatePickerDialog
import duc1607.bb10.designsystem.components.Bb10DatePickerField
import duc1607.bb10.designsystem.components.Bb10Dialog
import duc1607.bb10.designsystem.components.Bb10InputDialog
import duc1607.bb10.designsystem.components.Bb10Divider
import duc1607.bb10.designsystem.components.Bb10Dropdown
import duc1607.bb10.designsystem.components.Bb10Icon
import duc1607.bb10.designsystem.components.Bb10LazyColumn
import duc1607.bb10.designsystem.components.Bb10LinkItem
import duc1607.bb10.designsystem.components.Bb10ListHeader
import duc1607.bb10.designsystem.components.Bb10ListItem
import duc1607.bb10.designsystem.components.Bb10Marker
import duc1607.bb10.designsystem.components.Bb10NavItem
import duc1607.bb10.designsystem.components.Bb10PickerColumn
import duc1607.bb10.designsystem.components.Bb10PushHost
import duc1607.bb10.designsystem.components.Bb10ScalePicker
import duc1607.bb10.designsystem.components.Bb10Scaffold
import duc1607.bb10.designsystem.components.Bb10Search
import duc1607.bb10.designsystem.components.Bb10Slider
import duc1607.bb10.designsystem.components.Bb10Stepper
import duc1607.bb10.designsystem.components.Bb10TabBar
import duc1607.bb10.designsystem.components.Bb10Text
import duc1607.bb10.designsystem.components.Bb10TextField
import androidx.compose.ui.focus.FocusRequester
import duc1607.bb10.designsystem.components.Bb10TitleAction
import duc1607.bb10.designsystem.components.Bb10TitleBar
import duc1607.bb10.designsystem.components.Bb10TimePicker
import duc1607.bb10.designsystem.components.Bb10TimePickerDialog
import duc1607.bb10.designsystem.components.Bb10TimePickerField
import duc1607.bb10.designsystem.components.Bb10Toggle
import duc1607.bb10.designsystem.components.back
import duc1607.bb10.designsystem.components.bb10Clickable
import duc1607.bb10.designsystem.components.rememberBb10Highlight
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.theme.Bb10ScaleState
import duc1607.bb10.designsystem.theme.Bb10Theme
import duc1607.bb10.designsystem.theme.rememberBb10UiScale
import duc1607.bb10.designsystem.tokens.Bb10Accents
import duc1607.bb10.designsystem.tokens.touchHeight
import java.time.LocalDate
import java.time.LocalTime

private class Section(val title: String, val subtitle: String, val icon: ImageVector)

private val sections = listOf(
    Section("Foundations", "Display size, colours, type", Bb10Icons.Sliders),
    Section("Header", "Title, Cancel / OK, search", Bb10Icons.Edit),
    Section("Action bar", "Primary action, cluster, menu", Bb10Icons.Check),
    Section("Menus", "Right menu, hold to peek", Bb10Icons.More),
    Section("Tabs", "Local and global", Bb10Icons.Keyboard),
    Section("Lists", "List items and links", Bb10Icons.Mail),
    Section("Forms", "Inputs, toggles, buttons", Bb10Icons.Keyboard),
    Section("Pickers", "Date, time and any list", Bb10Icons.Check),
    Section("Dialogs", "Alert, confirm, input", Bb10Icons.Info),
    Section("Icons", "Drawn in code", Bb10Icons.Star),
    Section("Accessibility", "What is built in", Bb10Icons.Eye),
)

/** A browsable catalog of every component of the kit, drawn with the kit itself. */
@Composable
fun CatalogApp() {
    val scale = rememberBb10UiScale()
    var dark by rememberSaveable { mutableStateOf(true) }
    var accentIndex by rememberSaveable { mutableIntStateOf(0) }
    var section by rememberSaveable { mutableIntStateOf(0) }
    var showAbout by remember { mutableStateOf(false) }
    var pushed by remember { mutableStateOf<String?>(null) }
    var selecting by remember { mutableStateOf(false) }

    Bb10Theme(scale = scale, dark = dark, accent = Bb10Accents.all[accentIndex]) {
        Bb10PushHost(
            pushed = pushed,
            page = { id ->
                Bb10Scaffold(
                    title = id,
                    subtitle = "Slid in from the right, the page behind dimmed",
                    onBack = { pushed = null },
                ) {
                    Bb10Text(
                        "Back (or the Back key) slides this page out again.",
                        style = Bb10.typography.body,
                        modifier = Modifier.padding(Bb10.dimens.spaceL),
                    )
                }
            },
            content = {
        Bb10Scaffold(
            title = sections[section].title,
            selectionMode = selecting && section == 5,
            onCancelSelection = { selecting = false },
            actions = listOf(
                Bb10Action("theme", "Toggle theme", Bb10Icons.Sliders, Key.T) { dark = !dark },
                // Everything below goes to the right-hand menu (hold a list item in "Menus").
                Bb10Action("about", "About", Bb10Icons.Info, Key.A) { showAbout = true },
                // Reloading is a menu item; there is no pull-to-refresh.
                Bb10Action("refresh", "Refresh", Bb10Icons.Refresh, Key.R) {},
                Bb10Action("reset", "Reset display size", Bb10Icons.Delete, Key.Delete) { scale.update(1f) },
            ),
            maxVisibleActions = 2,
            navigationItems = sections.map { Bb10NavItem(it.title, it.icon, it.subtitle) },
            selectedNavigation = section,
            onNavigate = { section = it },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (section) {
                    0 -> Foundations(scale, accentIndex) { accentIndex = it }
                    1 -> HeaderSection()
                    2 -> ActionBarSection()
                    3 -> MenusSection()
                    4 -> TabsSection()
                    5 -> ListsSection(
                        selecting = selecting,
                        onSelecting = { selecting = it },
                        onOpenPage = { pushed = "A page" },
                    )
                    6 -> FormsSection()
                    7 -> PickersSection()
                    8 -> DialogsSection()
                    9 -> IconsSection()
                    else -> AccessibilitySection()
                }
                Spacer(Modifier.height(24.dp))
            }
        }
            },
        )
        if (showAbout) {
            Bb10Dialog(
                title = "BB10 Kotlin UI Kit",
                message = "Catalog of every component. Change the display size on the Foundations page.",
                confirmText = "OK",
                onConfirm = { showAbout = false },
                onDismiss = { showAbout = false },
            )
        }
    }
}

// ------------------------------------------------------------------ building blocks

@Composable
private fun Demo(title: String, note: String? = null, content: @Composable () -> Unit) {
    val c = Bb10.colors
    val d = Bb10.dimens
    Column(Modifier.fillMaxWidth().padding(horizontal = d.spaceL, vertical = d.spaceS)) {
        Bb10Text(title, style = Bb10.typography.title)
        if (note != null) Bb10Text(note, style = Bb10.typography.secondary, color = c.textSecondary)
        Spacer(Modifier.height(d.spaceS))
        content()
    }
}

/** A fixed-size window for components that fill their parent (a whole Scaffold). */
@Composable
private fun Frame(height: Int = 260, content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(height.dp)
            .border(1.dp, Bb10.colors.divider)
    ) { content() }
}

@Composable
private fun Note(text: String) {
    Bb10Text(
        text,
        style = Bb10.typography.secondary,
        color = Bb10.colors.textSecondary,
        modifier = Modifier.padding(horizontal = Bb10.dimens.spaceL, vertical = Bb10.dimens.spaceXs),
    )
}

// ------------------------------------------------------------------ sections

@Composable
private fun Foundations(
    scale: Bb10ScaleState,
    accentIndex: Int,
    onAccent: (Int) -> Unit,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    Demo("Display size", "Required in every app: multiplies all dp and sp together.") {
        Bb10ScalePicker(scale)
    }
    Demo("Accent") {
        Row(horizontalArrangement = Arrangement.spacedBy(d.spaceS)) {
            Bb10Accents.all.forEachIndexed { i, color ->
                // A swatch is not the theme accent, so it darkens its own colour when focused.
                val highlight = rememberBb10Highlight()
                Box(
                    Modifier
                        .size(d.touchHeight)
                        .background(if (highlight.active) lerp(color, Color.Black, 0.22f) else color)
                        .semantics { contentDescription = "Accent ${i + 1}" }
                        .bb10Clickable(
                            role = Role.RadioButton,
                            highlight = highlight,
                            paintHighlight = false,
                            onClick = { onAccent(i) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (i == accentIndex) Bb10Icon(Bb10Icons.Check, tint = Color.White)
                }
            }
        }
    }
    Demo("Colour tokens") {
        val tokens = listOf(
            "background" to c.background, "surface" to c.surface, "surfaceVariant" to c.surfaceVariant,
            "divider" to c.divider, "field" to c.field, "accent" to c.accent,
            "textPrimary" to c.textPrimary, "textSecondary" to c.textSecondary, "textDisabled" to c.textDisabled,
        )
        tokens.forEach { (name, color) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                Box(Modifier.size(28.dp).background(color).border(1.dp, c.divider))
                Spacer(Modifier.width(d.spaceM))
                Bb10Text(name, style = Bb10.typography.secondary)
            }
        }
    }
    Demo("Type", "System font. Hierarchy comes from size and weight only.") {
        val t = Bb10.typography
        Bb10Text("title 18 light", style = t.title)
        Bb10Text("HEADER 11 MEDIUM", style = t.header, color = c.textSecondary)
        Bb10Text("body 15", style = t.body)
        Bb10Text("secondary 13", style = t.secondary)
        Bb10Text("caption 11", style = t.caption)
        Bb10Text("button 14 medium", style = t.button)
        Bb10Text("action 10", style = t.action)
    }
}

@Composable
private fun HeaderSection() {
    var query by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    Demo("Plain", "Every screen has a header, usually the app name.") {
        Bb10TitleBar("Contacts")
    }
    Demo("Cancel / Save", "Confirm and dismiss actions live here, never in the bottom bar.") {
        Bb10TitleBar(
            "Compose",
            dismissAction = Bb10TitleAction("Cancel") {},
            acceptAction = Bb10TitleAction("Send") {},
        )
    }
    Demo("Disabled action") {
        Bb10TitleBar(
            "Compose",
            dismissAction = Bb10TitleAction("Cancel") {},
            acceptAction = Bb10TitleAction("Send", enabled = false) {},
        )
    }
    Demo("Subtitle", "A date or a count goes under the title, left aligned.") {
        Bb10TitleBar("BlackBerry ID", subtitle = "03:04:59 11-10-2026")
    }
    Demo("Back in the header", "For a screen reached from another, prefer Back in the bottom bar.") {
        Bb10TitleBar("Details", dismissAction = Bb10TitleAction.back {})
    }
    Demo("Search", "Field | divider | Cancel replaces the title.") {
        if (searching) {
            Bb10TitleBar(
                "Contacts",
                search = Bb10Search(
                    query = query,
                    onQueryChange = { query = it },
                    onClose = { searching = false; query = "" },
                    placeholder = "Search contacts",
                ),
            )
        } else {
            Bb10Button("Start searching", onClick = { searching = true })
        }
    }
}

@Composable
private fun ActionBarSection() {
    fun a(name: String, icon: ImageVector = Bb10Icons.Add, primary: Boolean = false) =
        Bb10Action(name, name, icon, primary = primary) {}

    Note("The primary action is always exactly in the middle, bigger and floating above the bar.")
    Demo("Primary + 2 actions") {
        Frame {
            Bb10Scaffold(
                title = "Messages",
                actions = listOf(a("Search", Bb10Icons.Search), a("Compose", Bb10Icons.Edit, true), a("Delete", Bb10Icons.Delete)),
                autoFocus = false,
            ) {}
        }
    }
    Demo("Primary + 1 action", "The lone action sits to the right, under the right thumb.") {
        Frame {
            Bb10Scaffold(
                title = "Messages",
                actions = listOf(a("Compose", Bb10Icons.Edit, true), a("Search", Bb10Icons.Search)),
                autoFocus = false,
            ) {}
        }
    }
    Demo("Navigation + menu", "Left button opens the app menu, right button the action menu.") {
        Frame {
            Bb10Scaffold(
                title = "Messages",
                actions = listOf(
                    a("Compose", Bb10Icons.Edit, true),
                    a("Search", Bb10Icons.Search),
                    a("Delete", Bb10Icons.Delete),
                    a("Share", Bb10Icons.Star),
                ),
                maxVisibleActions = 3,
                navigationItems = listOf(Bb10NavItem("Inbox", Bb10Icons.Mail), Bb10NavItem("Sent", Bb10Icons.Mail)),
                autoFocus = false,
            ) {}
        }
    }
    Demo("Only menus, no actions", "The bar disappears: two tiles in the corners, the middle stays free.") {
        Frame {
            Bb10Scaffold(
                title = "Photos",
                actions = listOf(a("Share"), a("Delete", Bb10Icons.Delete)),
                maxVisibleActions = 0,
                navigationItems = listOf(Bb10NavItem("Albums", Bb10Icons.Mail)),
                autoFocus = false,
            ) {}
        }
    }
    Demo("Back instead of the navigation menu", "Same place, never both.") {
        Frame {
            Bb10Scaffold(
                title = "Message",
                subtitle = "03:04:59 11-10-2026",
                actions = listOf(a("Open", Bb10Icons.Edit)),
                maxVisibleActions = 1,
                onBack = {},
                autoFocus = false,
            ) {}
        }
    }
    Demo("No primary action") {
        Frame {
            Bb10Scaffold(
                title = "Settings",
                actions = listOf(a("Add"), a("Edit", Bb10Icons.Edit), a("Delete", Bb10Icons.Delete)),
                autoFocus = false,
            ) {}
        }
    }
}

@Composable
private fun MenusSection() {
    var held by remember { mutableStateOf("nothing yet") }
    Note("Touch: hold an item. The right menu peeks in about 2 mm and the app dims at once; when you let go it slides fully in.")
    Note("Keys: Menu opens it, Del triggers Delete from anywhere, Back closes it.")
    Bb10ListHeader("Last held: $held")
    listOf("Linh", "Minh", "Tuấn").forEach { name ->
        Bb10ListItem(
            title = name,
            subtitle = "Hold me",
            onLongClick = { held = name },
            onClick = {},
        )
    }
}

@Composable
private fun TabsSection() {
    var local by remember { mutableIntStateOf(0) }
    var global by remember { mutableIntStateOf(0) }
    Note("Local tabs sit under the header and switch views of the current screen. Global tabs are the app's top-level sections and take the header's place.")
    Demo("Local tabs") {
        Frame(height = 200) {
            Bb10Scaffold(title = "Choose a list", autoFocus = false) {
                Column {
                    Bb10TabBar(listOf("Tasks", "Notes"), local, { local = it })
                    Bb10ListHeader(if (local == 0) "Task lists" else "Note lists")
                    Bb10Text(
                        "Nothing here",
                        style = Bb10.typography.body,
                        color = Bb10.colors.textSecondary,
                        modifier = Modifier.padding(Bb10.dimens.spaceL),
                    )
                }
            }
        }
    }
    Demo("Global tabs (replace the header)") {
        Frame(height = 200) {
            Bb10Scaffold(
                title = "Calculator",
                tabs = listOf("Calculator", "Bonus", "Convert"),
                selectedTab = global,
                onTabSelect = { global = it },
                autoFocus = false,
            ) {
                Bb10Text(
                    "Page ${global + 1}",
                    style = Bb10.typography.title,
                    modifier = Modifier.padding(Bb10.dimens.spaceL),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListsSection(
    selecting: Boolean,
    onSelecting: (Boolean) -> Unit,
    onOpenPage: () -> Unit,
) {
    var selected by remember { mutableIntStateOf(0) }
    val checked = remember { mutableStateListOf<Int>() }
    Note("Sticky day headers are slightly see-through. Multi-select: a checked row slides right to uncover the accent tick and takes a light accent shade; Cancel appears at the left of the bottom bar.")
    Demo("Sticky headers and multi-select") {
        Frame(height = 260) {
            Bb10LazyColumn {
                listOf("Today" to listOf("Linh", "Minh", "Tuấn"), "Yesterday" to listOf("Mẹ", "Shopee")).forEach { (day, names) ->
                    stickyHeader(key = day) { Bb10ListHeader(day, trailing = names.size.toString()) }
                    items(names, key = { it }) { name ->
                        val id = name.hashCode()
                        Bb10ListItem(
                            title = name,
                            subtitle = "Hold, then Select",
                            checked = if (selecting) id in checked else null,
                            onLongClick = {},
                            onClick = {
                                if (selecting) {
                                    if (id in checked) checked.remove(id) else checked.add(id)
                                }
                            },
                        )
                    }
                }
            }
        }
        Row(Modifier.padding(top = Bb10.dimens.spaceS)) {
            Bb10Button("Select more", modifier = Modifier.weight(1f), onClick = {
                checked.clear()
                onSelecting(true)
            })
            Bb10Button("Open a page", modifier = Modifier.weight(1f), onClick = onOpenPage)
        }
    }
    Bb10ListHeader("List items", trailing = "3")
    Bb10ListItem(
        "Linh", subtitle = "Lịch họp sáng thứ Hai",
        leading = { Bb10Marker() },
        trailing = { Bb10Text("09:41", style = Bb10.typography.caption, color = Bb10.colors.textSecondary) },
        stateDescription = "Unread",
        onClick = {},
    )
    Bb10ListItem("Minh", subtitle = "Ảnh chuyến đi Đà Lạt", leading = { Bb10Marker(visible = false) }, onClick = {})
    Bb10ListItem("Selected row", selected = true, onClick = {})
    Bb10ListHeader("Link items")
    Bb10LinkItem("Display and Actions", icon = Bb10Icons.Sliders, iconTint = Bb10Accents.Blue, onClick = {})
    Bb10LinkItem("Privacy and Security", icon = Bb10Icons.Lock, iconTint = Bb10Accents.Orange, onClick = {})
    Bb10LinkItem("Messages", icon = Bb10Icons.Mail, subtitle = "mspoonauer@gmail.com", badge = 87, onClick = {})
    Bb10LinkItem("With shortcut hint", icon = Bb10Icons.Delete, hint = "Del", onClick = {})
    Bb10LinkItem("Disabled", icon = Bb10Icons.Star, enabled = false, onClick = {})
    Bb10ListHeader("Selection (accent fill)")
    listOf("Hub", "Notifications", "Messages").forEachIndexed { i, name ->
        Bb10LinkItem(name, icon = Bb10Icons.Mail, selected = selected == i, onClick = { selected = i })
    }
}

@Composable
private fun FormsSection() {
    var text by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var on by remember { mutableStateOf(true) }
    var off by remember { mutableStateOf(false) }
    var lock by remember { mutableStateOf(true) }
    var wifi by remember { mutableStateOf(true) }
    var alarm by remember { mutableStateOf(false) }
    var profile by remember { mutableIntStateOf(0) }
    var ringtone by remember { mutableIntStateOf(2) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var level by remember { mutableFloatStateOf(2f) }
    var rating by remember { mutableIntStateOf(3) }
    var code by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("not-an-address") }
    var note by remember { mutableStateOf("Tap the X to clear") }
    Bb10ListHeader("Text fields")
    Bb10TextField(text, { text = it }, label = "User ID", required = true, placeholder = "Enter User ID")
    Bb10TextField(pass, { pass = it }, label = "Activation Password", required = true, placeholder = "Enter Password", password = true)
    Bb10TextField(body, { body = it }, label = "Message", placeholder = "Write your message", singleLine = false, minLines = 3)
    Bb10ListHeader("Input states")
    Bb10TextField(
        email, { email = it }, label = "Error",
        error = if ("@" in email) null else "Enter a valid address",
        helper = "Type an @ to clear the error",
    )
    Bb10TextField("", {}, label = "Helper text", placeholder = "Optional", helper = "Shown under the field")
    Bb10TextField(code, { code = it }, label = "Limited (5)", placeholder = "Code", maxLength = 5)
    Bb10TextField(note, { note = it }, label = "Clearable", clearable = true)
    Bb10TextField("Cannot edit", {}, label = "Read only", readOnly = true)
    Bb10TextField("Locked", {}, label = "Disabled", enabled = false)
    var time by remember { mutableStateOf(LocalTime.of(6, 30)) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var pickDate by remember { mutableStateOf(false) }
    var pickTime by remember { mutableStateOf(false) }
    Bb10ListHeader("Date and time")
    Bb10TimePickerField("Alarm time", time, { time = it })
    Bb10DatePickerField("Start date", date, { date = it })
    Row(Modifier.fillMaxWidth()) {
        Bb10Button("Date popup", modifier = Modifier.weight(1f), onClick = { pickDate = true })
        Bb10Button("Time popup", modifier = Modifier.weight(1f), onClick = { pickTime = true })
    }
    if (pickDate) {
        Bb10DatePickerDialog(
            title = "To date", date = date,
            onConfirm = { date = it; pickDate = false },
            onDismiss = { pickDate = false },
        )
    }
    if (pickTime) {
        Bb10TimePickerDialog(
            title = "Alarm time", time = time, is24Hour = false, minuteStep = 5,
            onConfirm = { time = it; pickTime = false },
            onDismiss = { pickTime = false },
        )
    }
    Bb10ListHeader("Checkboxes")
    Bb10Checkbox(lock, { lock = it }, label = "Rotation lock", icon = Bb10Icons.Lock)
    Bb10Checkbox(wifi, { wifi = it }, label = "Wi-Fi", subtitle = "Connected", icon = Bb10Icons.Star)
    Bb10Checkbox(alarm, { alarm = it }, label = "Alarm", icon = Bb10Icons.Info)
    Bb10Checkbox(true, {}, label = "Disabled", enabled = false)
    Bb10ListHeader("Drop-down")
    Bb10Dropdown(
        label = "Active profile",
        options = listOf("Normal", "Phone calls only", "Vibrate only", "Silent"),
        selectedIndex = profile,
        onSelect = { profile = it },
    )
    Bb10Dropdown(
        label = "Ringtone",
        options = listOf("Lively", "Open", "Bright", "Calm", "Browse"),
        selectedIndex = ringtone,
        onSelect = { ringtone = it },
    )
    Bb10ListHeader("Slider")
    Bb10Slider(
        volume, { volume = it }, label = "Volume",
        startIcon = Bb10Icons.VolumeLow, endIcon = Bb10Icons.VolumeHigh,
    )
    Bb10Slider(level, { level = it }, valueRange = 0f..4f, steps = 3, label = "Level (5 stops)")
    Bb10ListHeader("Stepper")
    Bb10Stepper(rating, { rating = it }, range = 1..5, label = "Rating")
    Bb10Stepper(
        rating, { rating = it }, range = 1..5, label = "Rating with icons",
        startIcon = Bb10Icons.Star, endIcon = Bb10Icons.Star,
    )
    Bb10Slider(0.3f, {}, label = "Disabled", enabled = false)
    Bb10ListHeader("Toggles")
    Bb10Toggle(on, { on = it }, label = "Notifications", subtitle = "On shows I")
    Bb10Toggle(off, { off = it }, label = "Vibrate", subtitle = "Off shows O")
    Bb10Toggle(true, {}, label = "Disabled", enabled = false)
    Bb10ListHeader("Buttons")
    Note("Full width by default: flat bar, bold centred text, like a drop-down bar. In a row, give each button weight(1f).")
    Bb10Button("Display colour settings", {})
    Bb10Button("Save", {}, primary = true)
    Bb10Button("Off", {}, enabled = false)
    Row(Modifier.fillMaxWidth()) {
        Bb10Button("Cancel", modifier = Modifier.weight(1f), onClick = {})
        Bb10Button("Save", modifier = Modifier.weight(1f), onClick = {}, primary = true)
    }
}

@Composable
private fun PickersSection() {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time24 by remember { mutableStateOf(LocalTime.of(6, 30)) }
    var time12 by remember { mutableStateOf(LocalTime.of(18, 45)) }
    var minutes by remember { mutableIntStateOf(2) }
    var fieldDate by remember { mutableStateOf(LocalDate.now()) }
    var fieldTime by remember { mutableStateOf(LocalTime.of(7, 0)) }
    var dateDialog by remember { mutableStateOf(false) }
    var timeDialog by remember { mutableStateOf(false) }
    val durations = listOf(5, 10, 15, 30, 45, 60).map { "$it min" }

    Note("Scrolling columns: the zone in the middle is two thin accent lines. Between columns a short divider sits above and another below the zone, never inside it. The weekday / month name fades in as a row nears the zone.")
    Note("Keys: up / down change a row, left / right change column. Enter, Space or d-pad center means done: in a drop-down it keeps the value and closes; in a popup it moves focus to OK instead of confirming.")
    Demo("Date picker (inline)", "Day | month | year. The day keeps within the month's length.") {
        Bb10DatePicker(date, { date = it })
    }
    Demo("Time picker, 24 hours") {
        Bb10TimePicker(time24, { time24 = it })
    }
    Demo("Time picker, 12 hours, 15-minute steps") {
        Bb10TimePicker(time12, { time12 = it }, is24Hour = false, minuteStep = 15)
    }
    Demo("Any list: Bb10PickerColumn", "Used on its own for a duration, a quantity...") {
        Bb10PickerColumn(
            labels = durations,
            selectedIndex = minutes,
            onSelectedChange = { minutes = it },
            description = "Snooze",
        )
    }
    Demo("In a drop-down", "The bar shows the value; the picker unfolds in place.") {
        Bb10DatePickerField("Start date", fieldDate, { fieldDate = it })
        Bb10TimePickerField("Alarm time", fieldTime, { fieldTime = it })
    }
    Demo("In a popup") {
        Row(Modifier.fillMaxWidth()) {
            Bb10Button("Date popup", modifier = Modifier.weight(1f), onClick = { dateDialog = true })
            Bb10Button("Time popup", modifier = Modifier.weight(1f), onClick = { timeDialog = true })
        }
    }
    if (dateDialog) {
        Bb10DatePickerDialog(
            title = "To date", date = fieldDate,
            onConfirm = { fieldDate = it; dateDialog = false },
            onDismiss = { dateDialog = false },
        )
    }
    if (timeDialog) {
        Bb10TimePickerDialog(
            title = "Alarm time", time = fieldTime,
            onConfirm = { fieldTime = it; timeDialog = false },
            onDismiss = { timeDialog = false },
        )
    }
}

@Composable
private fun DialogsSection() {
    var alert by remember { mutableStateOf(false) }
    var confirm by remember { mutableStateOf(false) }
    var rename by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var passwordForm by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var again by remember { mutableStateOf("") }
    Note("Buttons are one full-width row flush with the bottom of the box. One button: accent OK. Two buttons: both neutral, split by a divider. The app behind is dimmed.")
    Column(Modifier.fillMaxWidth()) {
        Bb10Button("Alert (OK)", { alert = true })
        Bb10Button("Popup with two buttons", { confirm = true })
        Bb10Button("Popup with one field", { rename = true })
        Bb10Button("Popup with two password fields", { passwordForm = true })
    }
    Note("With a field the cursor is already in it. Enter moves to the confirm button; OK stays grey until the form is valid.")
    if (alert) {
        Bb10Dialog(
            title = "Mobilfunknetz: 919",
            message = "Probewarnung für Deutschland. Es besteht keine Gefahr.",
            confirmText = "OK",
            onConfirm = { alert = false },
            onDismiss = { alert = false },
        )
    }
    if (rename) {
        Bb10InputDialog(
            title = "Rename",
            value = name,
            onValueChange = { name = it },
            placeholder = "Name",
            maxLength = 20,
            confirmText = "Save",
            dismissText = "Cancel",
            confirmEnabled = name.isNotBlank(),
            onConfirm = { rename = false },
            onDismiss = { rename = false },
        )
    }
    if (passwordForm) {
        // Several fields: Bb10Dialog with the fields as content. Enter in the first field moves
        // on to the second, Enter in the second to the confirm button.
        val second = remember { FocusRequester() }
        val mismatch = again.isNotEmpty() && again != password
        Bb10Dialog(
            title = "Device password",
            message = "Set the device password",
            confirmText = "OK",
            dismissText = "Cancel",
            focusContent = true,
            confirmEnabled = password.length >= 6 && !mismatch,
            onConfirm = { passwordForm = false },
            onDismiss = { passwordForm = false },
        ) { focusConfirm ->
            Column {
                Bb10TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    password = true,
                    startEditing = true,
                    onDone = { runCatching { second.requestFocus() } },
                )
                Bb10TextField(
                    value = again,
                    onValueChange = { again = it },
                    placeholder = "Confirm password",
                    password = true,
                    error = if (mismatch) "The passwords do not match" else null,
                    focusRequester = second,
                    onDone = focusConfirm,
                )
            }
        }
    }
    if (confirm) {
        Bb10Dialog(
            title = "Delete?",
            message = "This deletes the selected contact and any linked contacts.",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = { confirm = false },
            onDismiss = { confirm = false },
        )
    }
}

@Composable
private fun IconsSection() {
    val icons = listOf(
        "Add" to Bb10Icons.Add, "Search" to Bb10Icons.Search, "Edit" to Bb10Icons.Edit,
        "Delete" to Bb10Icons.Delete, "Check" to Bb10Icons.Check, "Mail" to Bb10Icons.Mail,
        "Sliders" to Bb10Icons.Sliders, "Keyboard" to Bb10Icons.Keyboard, "ChevronLeft" to Bb10Icons.ChevronLeft,
        "Menu" to Bb10Icons.Menu, "Eye" to Bb10Icons.Eye, "EyeOff" to Bb10Icons.EyeOff,
        "Lock" to Bb10Icons.Lock, "Star" to Bb10Icons.Star, "Info" to Bb10Icons.Info, "More" to Bb10Icons.More,
        "Refresh" to Bb10Icons.Refresh, "Cancel" to Bb10Icons.Cancel, "Checklist" to Bb10Icons.Checklist,
    )
    Note("Thin line icons drawn in code; tint them with Bb10Icon.")
    icons.chunked(3).forEach { row ->
        Row(Modifier.fillMaxWidth().padding(horizontal = Bb10.dimens.spaceL, vertical = Bb10.dimens.spaceS)) {
            row.forEach { (name, icon) ->
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Bb10Icon(icon, size = 32.dp)
                    Bb10Text(name, style = Bb10.typography.caption, color = Bb10.colors.textSecondary)
                }
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun AccessibilitySection() {
    Bb10ListHeader("Built in")
    listOf(
        "Touch targets are at least 48dp, at every display size",
        "Icon-only buttons (menu, more, primary action, show password) have names",
        "Header and list headers are headings",
        "Toggle, checkbox, selection and state (e.g. Unread, Expanded) are exposed",
        "Slider reports its range and accepts set-progress actions, d-pad left / right",
        "Required fields are announced as required",
        "Menus and dialogs announce their title",
        "All texts the kit shows itself can be translated (Bb10Strings)",
        "Everything works with d-pad and keyboard, with a visible focus ring",
        "Display size multiplies dp and sp together; system font size is respected",
    ).forEach { Bb10ListItem(it, onClick = {}) }
    Bb10Divider()
}
