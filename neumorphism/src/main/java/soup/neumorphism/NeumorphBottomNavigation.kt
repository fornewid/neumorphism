package soup.neumorphism

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import soup.neumorphism.internal.util.applyWindowInsets
import soup.neumorphism.internal.util.getChildren
import soup.neumorphism.model.MenuParser
import soup.neumorphism.model.MenuStyle
import soup.neumorphism.model.SavedState

class NeumorphBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var listener: OnMenuItemSelectedListener? = null
    private val menuStyle: MenuStyle

    @MenuRes
    private var menuResource = -1

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NeumorphBottomNavigation)

        val menuResource = ta.getResourceId(R.styleable.NeumorphBottomNavigation_neumorph_menuResource, -1)
        val leftInset = ta.getBoolean(R.styleable.NeumorphBottomNavigation_neumorph_setLeftInset, false)
        val rightInset = ta.getBoolean(R.styleable.NeumorphBottomNavigation_neumorph_setRightInset, false)
        val topInset = ta.getBoolean(R.styleable.NeumorphBottomNavigation_neumorph_setTopInset, false)
        val bottomInset = ta.getBoolean(R.styleable.NeumorphBottomNavigation_neumorph_setBottomInset, false)

        menuStyle = MenuStyle(context, ta)

        ta.recycle()

        if (menuResource >= 0) {
            setMenuResource(menuResource)
        }

        applyWindowInsets(leftInset, rightInset, topInset, bottomInset)
    }

    fun setMenuResource(@MenuRes menuResource: Int) {
        this.menuResource = menuResource

        val menu = MenuParser(context).parse(menuResource, menuStyle)
        val menuItemClickListener: (View) -> Unit = { view -> setMenuItemSelected(view.id) }

        removeAllViews()

        menu.items.forEach {
            NeumorphFloatingActionButton(context)
                .apply {
                    bind(it)
                    setOnClickListener(menuItemClickListener)
                }.also(::addView)
        }

        getHorizontalFlow().apply {
            referencedIds = menu.items.map { it.id }.toIntArray()
        }.also(::addView)
    }

    fun setMenuItemSelected(id: Int, isSelected: Boolean = true) {
        setMenuItemSelected(id, isSelected, true)
    }

    fun setOnMenuItemSelectedListener(action: (Int) -> Unit) {
        setOnMenuItemSelectedListener(object : OnMenuItemSelectedListener {
            override fun onItemSelected(id: Int) {
                action(id)
            }
        })
    }

    fun setOnMenuItemSelectedListener(listener: OnMenuItemSelectedListener) {
        this.listener = listener
    }

    private fun setMenuItemSelected(id: Int, isSelected: Boolean, dispatchAction: Boolean) {
        val selectedMenuItem = getSelectedMenuItem()
        when {
            isSelected && selectedMenuItem?.id != id -> {
                selectedMenuItem?.isSelected = false
                getItemById(id)?.let {
                    it.isSelected = true
                    if (dispatchAction) {
                        listener?.onItemSelected(id)
                    }
                }
            }
            !isSelected -> {
                getItemById(id)?.let {
                    it.isSelected = false
                }
            }
        }
    }

    private fun getItemById(id: Int): AppCompatImageButton? {
        return getChildren()
            .filterIsInstance<AppCompatImageButton>()
            .firstOrNull { it.id == id }
    }

    private fun getSelectedMenuItem(): View? {
        return getChildren().firstOrNull { it.isSelected }
    }

    private fun getSelectedMenuItemId(): Int {
        return getSelectedMenuItem()?.id ?: -1
    }

    private fun getHorizontalFlow() = Flow(context).apply {
        setOrientation(Flow.HORIZONTAL)
        setHorizontalStyle(Flow.CHAIN_SPREAD)
        setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, Bundle()).apply {
            menuRes = menuResource
            selectedMenuItemId = getSelectedMenuItemId()
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)

                if (state.menuRes != -1) {
                    setMenuResource(state.menuRes)
                }
                if (state.selectedMenuItemId != -1) {
                    setMenuItemSelected(
                        state.selectedMenuItemId,
                        isSelected = true,
                        dispatchAction = false
                    )
                }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    interface OnMenuItemSelectedListener {
        fun onItemSelected(id: Int)
    }
}