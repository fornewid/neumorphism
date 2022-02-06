package soup.neumorphism.internal.drawable

import kotlin.math.pow

sealed class ShadowCoverage {

    class Oval(
        val startAngle: Float
    ) : ShadowCoverage()

    class Rectangle(
        val radius: Float
    ) : ShadowCoverage() {

        private var coverageFlags = Int.MAX_VALUE

        fun setCoverage(vararg sides: Sides) {
            coverageFlags = 0
            sides.forEach(::addCoverage)
        }

        fun hasCoverage(sides: Sides): Boolean {
            return coverageFlags and sides.flag != 0
        }

        fun addCoverage(sides: Sides) {
            coverageFlags = coverageFlags or sides.flag
        }

        fun removeCoverage(sides: Sides) {
            coverageFlags = coverageFlags and sides.flag.inv()
        }

        enum class Sides {
            TOP_LEFT_CORNER,
            TOP_LINE,
            TOP_RIGHT_CORNER,
            RIGHT_LINE,
            BOTTOM_RIGHT_CORNER,
            BOTTOM_LINE,
            BOTTOM_LEFT_CORNER,
            LEFT_LINE;

            val flag get() = 2.0.pow(ordinal).toInt()
        }
    }
}