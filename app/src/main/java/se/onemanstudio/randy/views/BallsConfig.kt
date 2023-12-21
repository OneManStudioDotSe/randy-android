package se.onemanstudio.randy.views

object BallsConfig {
    var jointsHaveRandomSpeed = false
    var addRandomAmountOfJoints = false
    var jointsHaveRandomColor = false
    var connectionsAnimate = false
    var distanceToDrawConnection = 600f

    var connectionStyle = ConnectionStyle.SIMPLE

    const val bezierVariation = 1

    enum class ConnectionStyle {
        SIMPLE, CURVES, BEZIER, SINUS
    }
}