package soup.neumorphism.internal.drawable

sealed class ShadowCoverage {

    class Oval(
        val startAngle: Float
    ) : ShadowCoverage()

    class Rectangle(
        val radius: Float
    ) : ShadowCoverage()
}