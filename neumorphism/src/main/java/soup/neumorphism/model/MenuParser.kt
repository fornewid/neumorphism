package soup.neumorphism.model

import android.content.Context
import android.content.res.TypedArray
import android.content.res.XmlResourceParser
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Xml
import androidx.core.content.ContextCompat
import soup.neumorphism.R
import soup.neumorphism.internal.util.getValueFromAttr

class MenuParser(private val context: Context) {

    companion object {
        private const val XML_MENU_ITEM_TAG = "item"
    }

    fun parse(menuResource: Int, menuStyle: MenuStyle): Menu {
        val parser = context.resources.getLayout(menuResource)
        val attrs = Xml.asAttributeSet(parser)

        return parseMenu(parser, attrs, menuStyle)
    }

    private fun parseMenu(
        parser: XmlResourceParser,
        attrs: AttributeSet?,
        menuStyle: MenuStyle
    ): Menu {
        val items = mutableListOf<MenuItem>()
        var eventType = -1
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            val element = parser.name
            if (eventType == XmlResourceParser.START_TAG
                && element == XML_MENU_ITEM_TAG
            ) {
                items.add(parseMenuItem(attrs, menuStyle))
            }

            eventType = parser.next()
        }

        return Menu(items)
    }

    private fun parseMenuItem(attrs: AttributeSet?, menuStyle: MenuStyle): MenuItem {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NeumorphMenuItem)

        val item = MenuItem(
            getSelectedIconColor(ta),
            getUnSelectedIconColor(ta),
            ta.getResourceId(R.styleable.NeumorphMenuItem_android_icon, 0),
            ta.getResourceId(R.styleable.NeumorphMenuItem_android_id, 0),
            ta.getText(R.styleable.NeumorphMenuItem_android_title),
            getIconTintMode(ta),
            menuStyle
        )
        ta.recycle()

        return item
    }

    private fun getSelectedIconColor(ta: TypedArray): Int {
        return ta.getColor(
            R.styleable.NeumorphMenuItem_selected_color,
            context.getValueFromAttr(R.attr.colorAccent)
        )
    }

    private fun getUnSelectedIconColor(ta: TypedArray): Int {
        return ta.getColor(
            R.styleable.NeumorphMenuItem_unselected_color,
            context.getValueFromAttr(R.attr.colorAccent)
        )
    }

    private fun getIconTintMode(sAttr: TypedArray): PorterDuff.Mode? {
        return when (sAttr.getInt(R.styleable.NeumorphMenuItem_icon_tint_mode, -1)) {
            3 -> PorterDuff.Mode.SRC_OVER
            5 -> PorterDuff.Mode.SRC_IN
            9 -> PorterDuff.Mode.SRC_ATOP
            14 -> PorterDuff.Mode.MULTIPLY
            15 -> PorterDuff.Mode.SCREEN
            16 -> PorterDuff.Mode.ADD
            else -> null
        }
    }
}