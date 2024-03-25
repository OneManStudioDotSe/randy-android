package se.onemanstudio.randy.models

import android.graphics.Point
import se.onemanstudio.randy.utils.NumberUtils

class Joint(id: Int, location: Point) {
    var center: Point
    private var isVisible = false
    var jointId = 0
    var destination: Point? = null
    private var speed: Int = 1

    var currentPosition: Point? = null
    var previousPosition: Point? = null

    init {
        isVisible = true
        jointId = id
        speed = NumberUtils.randomWithin(1, 5)

        center = location
    }

    fun isGoingSomewhere() : Boolean {
        return destination != null
    }
}