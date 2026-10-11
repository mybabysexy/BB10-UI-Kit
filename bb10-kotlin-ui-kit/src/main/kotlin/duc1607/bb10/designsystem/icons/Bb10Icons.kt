package duc1607.bb10.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Flat, thin line icons drawn in code (no BlackBerry assets). Tint them with Bb10Icon. */
object Bb10Icons {
    val Add = stroked("add") {
        line(12f, 5f, 12f, 19f); line(5f, 12f, 19f, 12f)
    }
    val Search = stroked("search") {
        moveTo(16.5f, 10.5f)
        arcTo(6f, 6f, 0f, true, true, 4.5f, 10.5f)
        arcTo(6f, 6f, 0f, true, true, 16.5f, 10.5f)
        line(15f, 15f, 20f, 20f)
    }
    val Edit = stroked("edit") {
        moveTo(5f, 19f); lineTo(6f, 14.5f); lineTo(15.5f, 5f); lineTo(19f, 8.5f); lineTo(9.5f, 18f); close()
        line(13.5f, 7f, 17f, 10.5f)
    }
    val Delete = stroked("delete") {
        line(5f, 7f, 19f, 7f)
        moveTo(7f, 7f); lineTo(8f, 20f); lineTo(16f, 20f); lineTo(17f, 7f)
        moveTo(10f, 7f); lineTo(10f, 4f); lineTo(14f, 4f); lineTo(14f, 7f)
        line(10f, 10f, 10f, 17f); line(14f, 10f, 14f, 17f)
    }
    val Check = stroked("check") {
        moveTo(5f, 12.5f); lineTo(10f, 17.5f); lineTo(19f, 7f)
    }
    val Mail = stroked("mail") {
        moveTo(4f, 6f); lineTo(20f, 6f); lineTo(20f, 18f); lineTo(4f, 18f); close()
        moveTo(4f, 6f); lineTo(12f, 13f); lineTo(20f, 6f)
    }
    val Sliders = stroked("sliders") {
        line(4f, 7f, 20f, 7f); line(4f, 12f, 20f, 12f); line(4f, 17f, 20f, 17f)
        line(9f, 4f, 9f, 10f); line(15f, 9f, 15f, 15f); line(8f, 14f, 8f, 20f)
    }
    val Keyboard = stroked("keyboard") {
        moveTo(3f, 7f); lineTo(21f, 7f); lineTo(21f, 17f); lineTo(3f, 17f); close()
        line(6f, 10f, 8f, 10f); line(11f, 10f, 13f, 10f); line(16f, 10f, 18f, 10f)
        line(7f, 14f, 17f, 14f)
    }
    val ChevronLeft = stroked("chevronLeft") {
        moveTo(15f, 5f); lineTo(8f, 12f); lineTo(15f, 19f)
    }
    val Menu = stroked("menu") {
        line(4f, 7f, 20f, 7f); line(4f, 12f, 20f, 12f); line(4f, 17f, 20f, 17f)
    }
    val Eye = stroked("eye") {
        moveTo(3f, 12f); quadTo(12f, 3f, 21f, 12f); quadTo(12f, 21f, 3f, 12f); close()
        moveTo(15f, 12f)
        arcTo(3f, 3f, 0f, true, true, 9f, 12f)
        arcTo(3f, 3f, 0f, true, true, 15f, 12f)
    }
    val EyeOff = stroked("eyeOff") {
        moveTo(3f, 12f); quadTo(12f, 3f, 21f, 12f); quadTo(12f, 21f, 3f, 12f); close()
        moveTo(15f, 12f)
        arcTo(3f, 3f, 0f, true, true, 9f, 12f)
        arcTo(3f, 3f, 0f, true, true, 15f, 12f)
        line(4f, 20f, 20f, 4f)
    }
    val ChevronDown = stroked("chevronDown") {
        moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f)
    }
    val ChevronUp = stroked("chevronUp") {
        moveTo(6f, 15f); lineTo(12f, 9f); lineTo(18f, 15f)
    }
    val VolumeLow = stroked("volumeLow") {
        moveTo(4f, 10f); lineTo(8f, 10f); lineTo(13f, 6f); lineTo(13f, 18f); lineTo(8f, 14f); lineTo(4f, 14f); close()
    }
    val VolumeHigh = stroked("volumeHigh") {
        moveTo(3f, 10f); lineTo(6f, 10f); lineTo(11f, 6f); lineTo(11f, 18f); lineTo(6f, 14f); lineTo(3f, 14f); close()
        moveTo(14f, 9f); quadTo(16.5f, 12f, 14f, 15f)
        moveTo(17f, 6.5f); quadTo(21f, 12f, 17f, 17.5f)
    }
    /** Circle with a slash: cancel the current mode (e.g. multi-select). */
    val Cancel = stroked("cancel") {
        moveTo(21f, 12f)
        arcTo(9f, 9f, 0f, true, true, 3f, 12f)
        arcTo(9f, 9f, 0f, true, true, 21f, 12f)
        line(5.6f, 5.6f, 18.4f, 18.4f)
    }
    /** Two stacked boxes, the front one ticked: "select more". */
    val Checklist = stroked("checklist") {
        moveTo(9f, 5f); lineTo(20f, 5f); lineTo(20f, 14f)
        moveTo(4f, 9f); lineTo(15f, 9f); lineTo(15f, 20f); lineTo(4f, 20f); close()
        moveTo(6.5f, 14.5f); lineTo(9f, 17f); lineTo(12.5f, 12f)
    }
    /** A circular arrow: reload the data. In BB10 apps this is a menu item, not pull-to-refresh. */
    val Refresh = stroked("refresh") {
        // Circle centred on (12, 12), radius 7: the lower half, then the upper-left up to
        // 1:30, where the arrow head sits.
        moveTo(19f, 12f)
        arcTo(7f, 7f, 0f, true, true, 5f, 12f)
        arcTo(7f, 7f, 0f, false, true, 16.95f, 7.05f)
        moveTo(12f, 7.05f); lineTo(16.95f, 7.05f); lineTo(16.95f, 2.1f)
    }
    val Close = stroked("close") {
        line(6f, 6f, 18f, 18f); line(18f, 6f, 6f, 18f)
    }
    val Lock = stroked("lock") {
        moveTo(6f, 11f); lineTo(18f, 11f); lineTo(18f, 20f); lineTo(6f, 20f); close()
        moveTo(8.5f, 11f); lineTo(8.5f, 8f)
        arcTo(3.5f, 3.5f, 0f, false, true, 15.5f, 8f)
        lineTo(15.5f, 11f)
    }
    val Star = stroked("star") {
        moveTo(12f, 3f); lineTo(14.23f, 8.93f); lineTo(20.56f, 9.22f); lineTo(15.61f, 13.17f)
        lineTo(17.29f, 19.28f); lineTo(12f, 15.8f); lineTo(6.71f, 19.28f); lineTo(8.39f, 13.17f)
        lineTo(3.44f, 9.22f); lineTo(9.77f, 8.93f); close()
    }
    val Info = stroked("info") {
        moveTo(21f, 12f)
        arcTo(9f, 9f, 0f, true, true, 3f, 12f)
        arcTo(9f, 9f, 0f, true, true, 21f, 12f)
        line(12f, 11f, 12f, 17f); line(12f, 7.5f, 12f, 8.5f)
    }
    /** The BB10 overflow glyph: three small flat squares, stacked vertically. */
    val More = ImageVector.Builder("more", 24.dp, 24.dp, 24f, 24f).apply {
        path(fill = SolidColor(Color.Black)) {
            for (y in listOf(4.5f, 10.5f, 16.5f)) {
                moveTo(10.5f, y); horizontalLineTo(13.5f); verticalLineTo(y + 3f); horizontalLineTo(10.5f); close()
            }
        }
    }.build()

    private fun PathBuilder.line(x1: Float, y1: Float, x2: Float, y2: Float) {
        moveTo(x1, y1); lineTo(x2, y2)
    }

    private fun stroked(name: String, block: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                pathBuilder = block,
            )
        }.build()
}
