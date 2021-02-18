package soup.neumorphism.model

import android.content.Context
import android.content.res.TypedArray
import android.util.Log
import soup.neumorphism.R

class MenuStyle(context: Context, ta: TypedArray) {

    val iconSize: Int

    init {
        iconSize = ta.getDimension(
            R.styleable.NeumorphBottomNavigation_neumorph_iconSize,
            context.resources.getDimension(R.dimen.neumorph_icon_size)
        ).toInt()
    }
}