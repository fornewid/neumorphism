package soup.neumorphism.internal.util

import android.view.View
import android.view.WindowInsets

/**
 *  Add the window inset to current padding
 *
 *  @param left true to add the systemWindowInsetLeft to the padding false otherwise
 *  @param top true to add the systemWindowInsetTop to the padding false otherwise
 *  @param right true to add the systemWindowInsetRight to the padding false otherwise
 *  @param bottom true to add the systemWindowInsetBottom to the padding false otherwise
 */
internal fun View.applyWindowInsets(left: Boolean, top: Boolean, right: Boolean, bottom: Boolean) {
    doOnApplyWindowInset { view, windowInsets, initialPadding ->
        val leftPadding = initialPadding.left +
                (windowInsets.systemWindowInsetLeft.takeIf { left } ?: 0)
        val topPadding = initialPadding.top +
                (windowInsets.systemWindowInsetTop.takeIf { top } ?: 0)
        val rightPadding = initialPadding.right +
                (windowInsets.systemWindowInsetRight.takeIf { right } ?: 0)
        val bottomPadding = initialPadding.bottom +
                (windowInsets.systemWindowInsetBottom.takeIf { bottom } ?: 0)
        view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
    }
}

private fun View.doOnApplyWindowInset(f: (View, WindowInsets, InitialPadding) -> Unit) {
    val initialPadding = recordInitialPaddingForView(this)
    setOnApplyWindowInsetsListener { v, insets ->
        f(v, insets, initialPadding)
        insets
    }

    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private class InitialPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft,
    view.paddingTop,
    view.paddingRight,
    view.paddingBottom
)