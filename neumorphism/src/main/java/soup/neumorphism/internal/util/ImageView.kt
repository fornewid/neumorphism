package soup.neumorphism.internal.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.annotation.ColorInt

private const val ICON_STATE_ANIMATOR_DURATION: Long = 250

internal fun ImageView.colorAnimator(
    @ColorInt from: Int,
    @ColorInt to: Int,
    mode: PorterDuff.Mode?,
    durationInMillis: Long
): Animator {
    return ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
        duration = durationInMillis
        addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            mode?.let { setColorFilter(color, mode) } ?: run { setColorFilter(color) }
        }
    }
}

internal fun ImageView.setColorStateListAnimator(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int,
    mode: PorterDuff.Mode?
) {
    val stateList = StateListAnimator().apply {
        addState(
            intArrayOf(android.R.attr.state_selected),
            colorAnimator(unselectedColor, color, mode, ICON_STATE_ANIMATOR_DURATION)
        )
        addState(
            intArrayOf(),
            colorAnimator(color, unselectedColor, mode, ICON_STATE_ANIMATOR_DURATION)
        )
    }

    stateListAnimator = stateList

    // Refresh the drawable state to avoid the unselected animation on view creation
    refreshDrawableState()
}