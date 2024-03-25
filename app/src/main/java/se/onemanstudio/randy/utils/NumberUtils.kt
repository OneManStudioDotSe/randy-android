package se.onemanstudio.randy.utils

import android.graphics.Point
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

object NumberUtils {
    var maxSceneWidth = 0
    var maxSceneHeight = 0

    private const val SAFE_SPACE_AROUND = 50 + 24 //24 is the size of the joint

    fun randomWithin(from: Int, to: Int) : Int {
        val rand = Random.nextInt(abs(to - from)) + from
        Timber.d("Random number is: %d (%d and %d)", rand, from, to)
        return rand
    }

    fun randomXinScene(): Int {
        val rand = Random.nextInt(0 + SAFE_SPACE_AROUND, maxSceneWidth - SAFE_SPACE_AROUND)
        if(rand > 900) {
            Timber.d("maxSceneWidth is %d (%d) - %d", maxSceneWidth, (maxSceneWidth - SAFE_SPACE_AROUND), rand)
        }
        return rand
    }

    fun randomYinScene(): Int {
        return Random.nextInt(0 + SAFE_SPACE_AROUND, maxSceneHeight - SAFE_SPACE_AROUND)
    }

    fun randomDirectionPoint(id: Int): Point {
        val point = Point(randomXinScene(), randomYinScene())
        //Timber.d("The joint %d will go to %d x %d", id, point.x, point.y)
        return point
    }

    fun randomSpeed(id: Int): Int {
        return Random.nextInt(0, 5)
    }

    fun getDistanceBetweenTwoPoints(pointA: Point, pointB: Point): Double {
        return sqrt((pointB.x - pointA.x).toDouble().pow(2.0) + (pointB.y - pointA.y).toDouble().pow(2.0))
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515

        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}