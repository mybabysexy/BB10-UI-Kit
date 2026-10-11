package duc1607.bb10.designsystem.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import duc1607.bb10.designsystem.theme.Bb10

/**
 * BB10-style toggle. The whole row is the focus target; Enter/Space/center flips it. When focused
 * the row is not filled: the round thumb gets a translucent accent ring.
 */
@Composable
fun Bb10Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    subtitle: String? = null,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null,
) {
    val c = Bb10.colors
    val d = Bb10.dimens
    val t = Bb10.typography
    val highlight = rememberBb10Highlight()
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = d.listItemMinHeight)
            .bb10Clickable(
                enabled = enabled,
                role = Role.Switch,
                focusRequester = focusRequester,
                highlight = highlight,
                paintHighlight = false,
                onClick = { onCheckedChange(!checked) },
            )
            .semantics { toggleableState = ToggleableState(checked) }
            .padding(horizontal = d.spaceL, vertical = d.spaceS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.spaceM),
    ) {
        if (label != null) {
            Column(Modifier.weight(1f)) {
                Bb10Text(label, style = t.body)
                if (subtitle != null) {
                    Bb10Text(subtitle, style = t.secondary, color = c.textSecondary)
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
        }
        ToggleTrack(checked, highlighted = highlight.active)
    }
}

/**
 * BB10 pill switch: the white thumb carries the state glyph, a bar "I" when on (accent track,
 * thumb on the right) and a ring "O" when off (grey track, thumb on the left).
 */
@Composable
private fun ToggleTrack(checked: Boolean, highlighted: Boolean) {
    val c = Bb10.colors
    val trackW = 52.dp
    val trackH = 28.dp
    val thumb = 22.dp
    val pad = (trackH - thumb) / 2
    val x by animateDpAsState(
        targetValue = if (checked) trackW - thumb - pad else pad,
        animationSpec = tween(120),
        label = "toggleThumb",
    )
    val trackOff = if (c.isDark) Color(0xFF5A5A5A) else Color(0xFFBDBDBD)
    val glyphOff = Color(0xFF8A8A8A)
    Box(Modifier.size(trackW, trackH)) {
        Box(
            Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(if (checked) c.accent else trackOff)
        )
        Box(
            Modifier
                .offset(x = x, y = pad)
                .size(thumb)
                // The ring is drawn around the thumb, outside of it (and of the track).
                .drawBehind {
                    if (highlighted) {
                        drawCircle(c.accent.copy(alpha = 0.45f), radius = size.minDimension / 2 + 5.dp.toPx())
                    }
                }
                .clip(CircleShape)
                .background(Color.White)
                .drawBehind {
                    val stroke = 1.25.dp.toPx()
                    if (checked) {
                        val half = 3.5.dp.toPx()
                        drawLine(
                            color = c.accent,
                            start = Offset(center.x, center.y - half),
                            end = Offset(center.x, center.y + half),
                            strokeWidth = stroke,
                            cap = StrokeCap.Round,
                        )
                    } else {
                        drawCircle(
                            color = glyphOff,
                            radius = 3.25.dp.toPx(),
                            style = Stroke(stroke),
                        )
                    }
                }
        )
    }
}
