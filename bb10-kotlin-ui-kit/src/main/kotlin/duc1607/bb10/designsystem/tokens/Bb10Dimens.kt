package duc1607.bb10.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Bb10Dimens(
    val spaceXs: Dp,
    val spaceS: Dp,
    val spaceM: Dp,
    val spaceL: Dp,
    val cornerRadius: Dp,
    val dividerThickness: Dp,
    val focusBorder: Dp,
    val titleBarHeight: Dp,
    val actionBarHeight: Dp,
    val primaryActionSize: Dp,
    val listItemMinHeight: Dp,
    val iconSize: Dp,
    val controlHeight: Dp,
    /** Smallest size of anything that can be touched (accessibility guideline: 48dp). */
    val minTouchTarget: Dp,
)

/** Height of interactive controls: the design height, but never below [Bb10Dimens.minTouchTarget]. */
val Bb10Dimens.touchHeight: Dp
    get() = if (controlHeight > minTouchTarget) controlHeight else minTouchTarget

val Bb10DimensRegular = Bb10Dimens(
    spaceXs = 4.dp, spaceS = 8.dp, spaceM = 12.dp, spaceL = 16.dp,
    cornerRadius = 2.dp, dividerThickness = 1.dp, focusBorder = 2.dp,
    titleBarHeight = 56.dp, actionBarHeight = 64.dp, primaryActionSize = 56.dp, listItemMinHeight = 56.dp,
    iconSize = 24.dp, controlHeight = 44.dp, minTouchTarget = 48.dp,
)

/** Used automatically on square (e.g. 720x720) screens: less vertical chrome. */
val Bb10DimensCompact = Bb10Dimens(
    spaceXs = 4.dp, spaceS = 6.dp, spaceM = 10.dp, spaceL = 12.dp,
    cornerRadius = 2.dp, dividerThickness = 1.dp, focusBorder = 2.dp,
    titleBarHeight = 48.dp, actionBarHeight = 52.dp, primaryActionSize = 48.dp, listItemMinHeight = 48.dp,
    iconSize = 22.dp, controlHeight = 40.dp, minTouchTarget = 48.dp,
)
