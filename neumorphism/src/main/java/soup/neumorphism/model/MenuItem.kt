package soup.neumorphism.model

import android.graphics.PorterDuff
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

class MenuItem(
    @ColorInt val selectedIconColor: Int,
    @ColorInt val unselectedIconColor: Int,
    @DrawableRes val icon: Int,
    val id: Int,
    val title: CharSequence,
    val tintMode: PorterDuff.Mode?,
    val menuStyle: MenuStyle
)