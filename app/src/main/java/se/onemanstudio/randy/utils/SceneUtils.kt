package se.onemanstudio.randy.utils

import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import se.onemanstudio.randy.models.Joint
import se.onemanstudio.randy.views.BallsConfig
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

object SceneUtils {
    fun addDestinationForAll(id: Int): Point {
        return NumberUtils.randomDirectionPoint(id)
    }

    fun setNewDirection(jointId: Int) : Point {
        return NumberUtils.randomDirectionPoint(jointId)
    }

    fun calculateNextJointPosition(joint: Joint) {
        val deltaX = joint.destination!!.x - joint.center.x
        val deltaY = joint.destination!!.y - joint.center.y
        val angle = atan2(deltaY.toDouble(), deltaX.toDouble())

        val newX = joint.center.x + (2f * cos(angle))
        val newY = joint.center.y + (2f * sin(angle))

        //joint.previousPosition = Point(joint.center.x, joint.center.y)

        joint.center.x = ceil(newX).toInt()
        joint.center.y = ceil(newY).toInt()

        //joint.currentPosition = Point(joint.center.x, joint.center.y)
    }

    fun isWithinJitter(center: Point, destination: Point): Boolean {
        val variant = 2
        return center.x in destination.x - variant..destination.x + variant &&
                center.y in destination.y - variant..destination.y + variant
    }

    fun calculatePathForCurve(pointA: PointF, pointB: PointF): Path {
        // From https://stackoverflow.com/questions/37432826/how-to-draw-a-curved-line-between-2-points-on-canvas
        val myPath = Path()
        myPath.moveTo(pointA.x, pointA.y)
        val x2 = (pointB.x + pointA.x) / 3
        val y2 = (pointB.y + pointA.y) / 3
        myPath.quadTo(x2, y2, pointB.x, pointB.y)

        return myPath
    }

    fun calculatePathForBezier(jointOuter: Joint, jointInner: Joint): Path {
        val startX = jointOuter.center.x.toFloat()
        val startY = jointOuter.center.y.toFloat()

        val endX = jointInner.center.x.toFloat()
        val endY = jointInner.center.y.toFloat()

        val path = Path()
        path.moveTo(startX, startY)

        when(BallsConfig.bezierVariation) {
            1 -> {
                val conX1 = startX
                val conY1 = endY

                val conX2 = endX
                val conY2 = (endY + startY) / 2

                path.cubicTo(conX1, conY1, conX2, conY2, endX, endY)
            }

            2 -> {
                val conX1 = startX
                val conY1 = (endY + startY) / 2

                val conX2 = endX
                val conY2 = startY

                path.cubicTo(conX1, conY1, conX2, conY2, endX, endY)
            }

            3 -> {
                val conX1 = endX
                val conY1 = startY

                val conX2 = startX
                val conY2 = endY

                path.cubicTo(conX1, conY1, conX2, conY2, endX, endY)
            }

            4 -> {
                val conX1 = (endX - startX) / 2
                val conY1 = endY * 1.2

                val conX2 = (endX - startX) / 2
                val conY2 = startY * 1.2

                path.cubicTo(conX1, conY1.toFloat(), conX2, conY2.toFloat(), endX, endY)
            }
        }

        return  path
    }
}