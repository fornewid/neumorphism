package soup.neumorphism.internal.util

import android.view.View
import android.view.ViewGroup


internal fun ViewGroup.getChildren(): Sequence<View> = object : Sequence<View> {
    override fun iterator() = this@getChildren.iterator()
}

private operator fun ViewGroup.iterator(): Iterator<View> = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() = getChildAt(index++) ?: throw IndexOutOfBoundsException()
    override fun remove() = removeViewAt(--index)
}