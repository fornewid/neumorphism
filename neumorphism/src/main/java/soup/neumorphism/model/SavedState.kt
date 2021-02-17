package soup.neumorphism.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View

internal class SavedState : View.BaseSavedState {

    constructor(superState: Parcelable?) : super(superState)

    constructor(superState: Parcelable?, bundle: Bundle) : super(superState) {
        this.bundle = bundle
    }

    constructor(source: Parcel, loader: ClassLoader?) : super(source) {
        bundle = source.readBundle(loader)
    }

    private var bundle: Bundle? = null

    var menuRes: Int
        get() = bundle?.getInt(MENU_RES) ?: -1
        set(value) {
            bundle?.putInt(MENU_RES, value)
        }

    var selectedMenuItemId: Int
        get() = bundle?.getInt(SELECTED_MENU_ITEM) ?: -1
        set(value) {
            bundle?.putInt(SELECTED_MENU_ITEM, value)
        }

    override fun writeToParcel(out: Parcel?, flags: Int) {
        super.writeToParcel(out, flags)
        out?.writeBundle(bundle)
    }

    companion object {
        private const val MENU_RES = "menuResource"
        private const val SELECTED_MENU_ITEM = "selectedMenuItem"

        @JvmField
        val CREATOR = object : Parcelable.ClassLoaderCreator<SavedState> {
            override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState {
                return SavedState(source, loader)
            }

            override fun createFromParcel(source: Parcel): SavedState {
                return SavedState(source, null)
            }

            override fun newArray(size: Int): Array<SavedState> {
                return arrayOf()
            }
        }
    }
}