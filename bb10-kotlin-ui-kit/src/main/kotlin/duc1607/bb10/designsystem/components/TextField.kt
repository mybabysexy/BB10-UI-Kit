package duc1607.bb10.designsystem.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.R
import duc1607.bb10.designsystem.icons.Bb10Icons
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.tokens.touchHeight

/**
 * BB10 form input: an optional [label] above (with " *" when [required]) and a flat grey filled
 * field with an italic placeholder.
 *
 * **Two levels of focus.** Walking the d-pad onto a field only *highlights* it (accent border):
 * the field itself does not take the focus yet, so the arrow keys keep moving between items
 * instead of becoming cursor keys that trap you inside the text. The field starts editing when
 * you press Enter / Space / d-pad center, tap it, or just start typing (that first letter is
 * kept). While editing, left / right move the cursor, but at the start / end of the text they
 * move on to the neighbour, and up / down leave (a multi-line field leaves from its first / last
 * line). Enter in a single-line field, the keyboard's Done and Back all end editing. Typed
 * letters are consumed by the field, so single-key shortcuts in [Bb10Scaffold] never fire while
 * you are typing.
 *
 * States:
 * - **focused**: accent border
 * - **[error]**: red border and the message under the field (also announced by screen readers);
 *   it replaces [helper]
 * - **[helper]**: a hint under the field
 * - **[enabled] = false**: dimmed, cannot be focused or edited
 * - **[readOnly]**: looks normal, can be focused and selected, not edited
 * - **[maxLength]**: input is cut at the limit and a "n/max" counter is shown
 * - **[clearable]**: an X at the end empties the field while it has text
 * - **[password]**: masked, with an eye button at the end
 *
 * Use [keyboardOptions] / [keyboardActions] for the IME action (Next, Search, Done...).
 *
 * @param startEditing start in editing mode (the cursor is already in the field): for the one
 *   field a screen exists for, such as a compose form or the field of an input dialog.
 * @param onDone called when editing ends with Enter (or the keyboard's Done); use it to send the
 *   focus on, e.g. to the confirm button of a dialog. Without it the focus stays on the field.
 * @param focusRequester moves the focus *onto* the field (the highlight); it does not start
 *   editing, use [startEditing] for that.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Bb10TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    required: Boolean = false,
    placeholder: String = "",
    helper: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int? = null,
    clearable: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    password: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    startEditing: Boolean = false,
    onDone: (() -> Unit)? = null,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val fieldSource = remember { MutableInteractionSource() }
    val fieldFocused by fieldSource.collectIsFocusedAsState()
    val shellSource = remember { MutableInteractionSource() }
    val shellFocused by shellSource.collectIsFocusedAsState()
    var revealed by remember { mutableStateOf(false) }
    val masked = password && !revealed
    val strings = Bb10.strings
    val spokenLabel = when {
        label == null -> null
        required -> label + ", " + strings.required
        else -> label
    }
    val editable = enabled && !readOnly
    val showClear = clearable && editable && !password && value.isNotEmpty()
    val borderColor = when {
        error != null -> c.error
        (fieldFocused || shellFocused) && enabled -> c.accent
        else -> null
    }
    val message = error ?: helper
    val counter = if (maxLength != null) {
        stringResource(R.string.bb10_character_counter, value.length, maxLength)
    } else null

    // --- editing mode -------------------------------------------------------------------
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val fieldFocus = remember { FocusRequester() }
    val shellFocus = remember { FocusRequester() }
    var editing by remember { mutableStateOf(false) }
    var activating by remember { mutableStateOf(false) }
    var confirming by remember { mutableStateOf(false) }
    val hadFocus = remember { booleanArrayOf(false) }
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    // The text with its cursor: the caller only sees the string.
    var tfv by remember { mutableStateOf(TextFieldValue(value, TextRange(value.length))) }
    val shown = if (tfv.text == value) tfv else TextFieldValue(value, TextRange(value.length))

    fun beginEditing(typed: String? = null) {
        if (!enabled) return
        var next = shown
        if (typed != null && editable) {
            val text = next.text + typed
            if (maxLength == null || text.length <= maxLength) {
                next = TextFieldValue(text, TextRange(text.length))
                onValueChange(text)
            }
        }
        tfv = next.copy(selection = TextRange(next.text.length))
        editing = true
    }

    fun leaveEditing(focusShell: Boolean) {
        editing = false
        keyboard?.hide()
        if (focusShell) runCatching { shellFocus.requestFocus() }
    }

    // Enter / the keyboard's Done: editing is over, and focus goes where the caller says.
    fun finishEditing() {
        if (!editing) return
        val handOver = onDone
        leaveEditing(focusShell = handOver == null)
        handOver?.invoke()
    }

    fun KeyboardActionScope.imeAction(user: (KeyboardActionScope.() -> Unit)?, action: ImeAction) {
        if (user != null) user() else defaultKeyboardAction(action)
        // Next / Previous have already moved the focus on: do not pull it back.
        if (action == ImeAction.Next || action == ImeAction.Previous) editing = false else finishEditing()
    }

    LaunchedEffect(Unit) {
        if (startEditing) beginEditing()
    }
    LaunchedEffect(editing) {
        if (editing) {
            withFrameNanos { }
            runCatching { fieldFocus.requestFocus() }
            // Could not take the focus (e.g. the field went away): do not stay "editing".
            if (!hadFocus[0]) editing = false
        }
    }
    BackHandler(enabled = editing) { leaveEditing(focusShell = true) }

    Column(modifier.fillMaxWidth().padding(horizontal = d.spaceL, vertical = d.spaceS)) {
        if (label != null) {
            Bb10Text(if (required) "$label *" else label, style = t.secondary)
            Spacer(Modifier.height(d.spaceXs))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = d.touchHeight)
                .alpha(if (enabled) 1f else 0.5f)
                .background(c.field)
                .then(if (borderColor != null) Modifier.border(2.dp, borderColor) else Modifier)
                .padding(start = d.spaceM, end = if (password || showClear) 0.dp else d.spaceM),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.weight(1f).padding(vertical = d.spaceS),
                contentAlignment = Alignment.CenterStart,
            ) {
                BasicTextField(
                    value = shown,
                    onValueChange = { new ->
                        if (maxLength == null || new.text.length <= maxLength) {
                            tfv = new
                            if (new.text != value) onValueChange(new.text)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        // The visible label sits outside the field: give the field its name, and expose
                        // the validation message / hint to screen readers.
                        .semantics {
                            if (spokenLabel != null) contentDescription = spokenLabel
                            if (error != null) this.error(error)
                            else if (helper != null) stateDescription = helper
                            // "Click" from a screen reader starts editing (the field takes no focus by
                            // itself).
                            onClick {
                                beginEditing()
                                true
                            }
                        }
                        // Out of the d-pad's reach until editing starts (see the class comment).
                        .focusProperties { canFocus = editing }
                        .onPreviewKeyEvent { e ->
                            if (!editing) return@onPreviewKeyEvent false
                            val sel = tfv.selection
                            val text = shown.text
                            val layoutResult = layout
                            val down = e.type == KeyEventType.KeyDown
                            when (e.key) {
                                Key.Enter, Key.NumPadEnter -> {
                                    if (!singleLine) return@onPreviewKeyEvent false
                                    // Acted on key-up, so the key-up does not land on the next item.
                                    if (down) confirming = true
                                    else if (e.type == KeyEventType.KeyUp && confirming) {
                                        confirming = false
                                        finishEditing()
                                    }
                                    true
                                }
                                Key.DirectionLeft ->
                                    if (down && sel.collapsed && sel.start == 0) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                        true
                                    } else false
                                Key.DirectionRight ->
                                    if (down && sel.collapsed && sel.end >= text.length) {
                                        focusManager.moveFocus(FocusDirection.Right)
                                        true
                                    } else false
                                Key.DirectionUp -> {
                                    val onFirstLine = singleLine ||
                                        layoutResult == null || layoutResult.getLineForOffset(sel.start) == 0
                                    if (down && onFirstLine) {
                                        focusManager.moveFocus(FocusDirection.Up)
                                        true
                                    } else false
                                }
                                Key.DirectionDown -> {
                                    val onLastLine = singleLine || layoutResult == null ||
                                        layoutResult.getLineForOffset(sel.end) >= layoutResult.lineCount - 1
                                    if (down && onLastLine) {
                                        focusManager.moveFocus(FocusDirection.Down)
                                        true
                                    } else false
                                }
                                else -> false
                            }
                        }
                        .focusRequester(fieldFocus)
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                hadFocus[0] = true
                            } else if (hadFocus[0]) {
                                // Focus went elsewhere (a d-pad move, a tap): editing is over.
                                hadFocus[0] = false
                                editing = false
                            }
                        },
                    enabled = enabled,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    minLines = if (singleLine) 1 else minLines,
                    textStyle = t.body.copy(color = if (enabled) c.textPrimary else c.textDisabled),
                    cursorBrush = SolidColor(c.accent),
                    keyboardOptions = if (password) keyboardOptions.copy(keyboardType = KeyboardType.Password)
                    else keyboardOptions,
                    keyboardActions = KeyboardActions(
                        onDone = { imeAction(keyboardActions.onDone, ImeAction.Done) },
                        onGo = { imeAction(keyboardActions.onGo, ImeAction.Go) },
                        onNext = { imeAction(keyboardActions.onNext, ImeAction.Next) },
                        onPrevious = { imeAction(keyboardActions.onPrevious, ImeAction.Previous) },
                        onSearch = { imeAction(keyboardActions.onSearch, ImeAction.Search) },
                        onSend = { imeAction(keyboardActions.onSend, ImeAction.Send) },
                    ),
                    visualTransformation = if (masked) PasswordVisualTransformation() else VisualTransformation.None,
                    interactionSource = fieldSource,
                    onTextLayout = { layout = it },
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Bb10Text(
                                    placeholder,
                                    style = t.body.copy(fontStyle = FontStyle.Italic),
                                    color = c.textDisabled,
                                )
                            }
                            inner()
                        }
                    },
                )
                if (enabled) {
                    // What the d-pad lands on while the field is not being edited. It
                    // sits over the text and hands the touches back once editing starts.
                    Box(
                        Modifier
                            .matchParentSize()
                            .then(
                                if (focusRequester != null) Modifier.focusRequester(focusRequester)
                                else Modifier
                            )
                            .focusRequester(shellFocus)
                            .onKeyEvent { e ->
                                if (editing || e.isCtrlPressed || e.isMetaPressed) {
                                    return@onKeyEvent false
                                }
                                val isActivate = e.key == Key.Enter || e.key == Key.NumPadEnter ||
                                    e.key == Key.DirectionCenter || e.key == Key.Spacebar
                                if (isActivate) {
                                    // Key-up starts editing (a key-down would hand the
                                    // key-up to the field).
                                    if (e.type == KeyEventType.KeyDown) activating = true
                                    else if (e.type == KeyEventType.KeyUp && activating) {
                                        activating = false
                                        beginEditing()
                                    }
                                    return@onKeyEvent true
                                }
                                if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                                // A letter: start editing and keep it.
                                val code = e.utf16CodePoint
                                if (code <= 0 || Character.isISOControl(code)) return@onKeyEvent false
                                beginEditing(String(Character.toChars(code)))
                                true
                            }
                            .focusable(true, shellSource)
                            .pointerInput(editing) {
                                if (!editing) detectTapGestures { beginEditing() }
                            }
                            // The field itself is the one with the semantics.
                            .clearAndSetSemantics { }
                    )
                }
            }
            if (showClear) {
                val clearHighlight = rememberBb10Highlight()
                Box(
                    Modifier
                        .size(d.touchHeight)
                        .semantics { contentDescription = strings.clearText }
                        .bb10Clickable(
                            role = Role.Button,
                            highlight = clearHighlight,
                            onClick = { onValueChange("") },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Bb10OnHighlight(clearHighlight) {
                        Bb10Icon(Bb10Icons.Close, tint = c.textSecondary)
                    }
                }
            }
            if (password) {
                val revealHighlight = rememberBb10Highlight()
                Box(
                    Modifier
                        .size(d.touchHeight)
                        .semantics {
                            contentDescription =
                                if (revealed) strings.hidePassword else strings.showPassword
                        }
                        .bb10Clickable(
                            enabled = enabled,
                            role = Role.Button,
                            highlight = revealHighlight,
                            onClick = { revealed = !revealed },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Bb10OnHighlight(revealHighlight) {
                        Bb10Icon(
                            if (revealed) Bb10Icons.EyeOff else Bb10Icons.Eye,
                            tint = c.textSecondary,
                        )
                    }
                }
            }
        }
        if (message != null || counter != null) {
            Row(Modifier.fillMaxWidth().padding(top = d.spaceXs)) {
                if (message != null) {
                    Bb10Text(
                        message,
                        style = t.caption,
                        color = if (error != null) c.error else c.textSecondary,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                if (counter != null) {
                    Bb10Text(counter, style = t.caption, color = c.textSecondary)
                }
            }
        }
    }
}
