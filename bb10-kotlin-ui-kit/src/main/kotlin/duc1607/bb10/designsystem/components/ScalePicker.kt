package duc1607.bb10.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import duc1607.bb10.designsystem.theme.Bb10
import duc1607.bb10.designsystem.theme.Bb10ScaleState
import duc1607.bb10.designsystem.theme.Bb10ScaleStep
import duc1607.bb10.designsystem.theme.Bb10Scales
import duc1607.bb10.designsystem.tokens.touchHeight
import kotlin.math.abs

/**
 * Display-size setting: one big "A" per step of [steps], growing in size, with the name of the
 * selected step underneath. The picker itself is drawn with the current scale, so it stays
 * readable and tappable when the user has already made the UI bigger.
 *
 * Every app must show this (or an equivalent) in its settings: the same `rememberBb10UiScale()`
 * state is the required `scale` argument of `Bb10Theme`.
 */
@Composable
fun Bb10ScalePicker(
    state: Bb10ScaleState,
    modifier: Modifier = Modifier,
    label: String? = null,
    steps: List<Bb10ScaleStep> = Bb10Scales.all,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val selected = steps.minBy { abs(it.scale - state.value) }
    val selectedName = stringResource(selected.nameRes)

    Column(modifier.fillMaxWidth().padding(horizontal = d.spaceL, vertical = d.spaceS)) {
        Bb10Text(label ?: Bb10.strings.displaySize, style = t.secondary)
        Spacer(Modifier.height(d.spaceXs))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(d.spaceXs)) {
            steps.forEachIndexed { index, step ->
                val isSelected = step === selected
                val stepName = stringResource(step.nameRes)
                val highlight = rememberBb10Highlight()
                Box(
                    Modifier
                        .weight(1f)
                        .height(d.touchHeight + 8.dp)
                        .then(
                            if (isSelected) Modifier.background(c.accentBrush) else Modifier.background(c.field)
                        )
                        .bb10Clickable(
                            role = Role.RadioButton,
                            highlight = highlight,
                            onClick = { state.update(step.scale) },
                        )
                        .semantics {
                            this.selected = isSelected
                            contentDescription = stepName
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    // Letters grow with the step so the choice is obvious without reading.
                    Bb10OnHighlight(highlight) {
                        Bb10Text(
                            "A",
                            style = t.body.copy(fontSize = (13 + index * 3).sp),
                            color = if (isSelected) c.onAccent else c.textPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(d.spaceXs))
        Bb10Text(selectedName, style = t.caption, color = c.textSecondary)
    }
}
