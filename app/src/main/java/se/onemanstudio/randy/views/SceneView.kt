package se.onemanstudio.randy.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.github.ajalt.colormath.extensions.android.colorint.toColorInt
import se.onemanstudio.randy.models.Joint
import se.onemanstudio.randy.utils.Colors
import se.onemanstudio.randy.utils.NumberUtils
import se.onemanstudio.randy.utils.SceneUtils
import timber.log.Timber


class SceneView : SurfaceView, Runnable {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var gameThread: Thread? = null
    private var ourHolder: SurfaceHolder? = null // We need a SurfaceHolder when we use Paint and Canvas in a thread

    @Volatile
    var playing = false // A boolean which we will set and unset when the game is running- or not.
    var paused = true // whether the world is paused at the start

    private lateinit var canvas: Canvas
    private var paint: Paint

    private var fps: Long = 0 // This variable tracks the game frame rate
    private var timeThisFrame: Long = 0 // This is used to help calculate the fps

    // The size of the screen in pixels
    private var screenX = 0
    private var screenY = 0

    // The actual size of the scene that we are working on
    private var sceneWidth = 0
    private var sceneHeight = 0

    private var testObjectBasicSpeed = 0f

    private var joints: ArrayList<Joint> = arrayListOf()

    init {
        // Initialize ourHolder and paint objects
        ourHolder = holder
        paint = Paint()

        paint.isAntiAlias = true
    }

    fun initializeTheScene(activity: AppCompatActivity) {
        val display: Display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        screenX = size.x
        screenY = size.y

        this@SceneView.doOnLayout {
            sceneWidth = this@SceneView.measuredWidth
            sceneHeight = this@SceneView.measuredHeight
            createWorld()
        }
    }

    private fun createWorld() {
        Timber.d("The world (screen) is %d x %d pixels", screenX, screenY)
        Timber.d("The scene is %d x %d pixels", sceneWidth, sceneHeight)

        NumberUtils.maxSceneWidth = sceneWidth
        NumberUtils.maxSceneHeight = sceneHeight

        //SceneUtils.maxSceneX = sceneWidth.toFloat()
        //SceneUtils.maxSceneY = sceneHeight.toFloat()
    }

    override fun run() {
        while (playing) {
            //Timber.d("Playing and paused is %b", paused)
            val startFrameTime = System.currentTimeMillis() // Save the current time

            update() // Update the frame
            draw() // Draw the frame

            calculateFps(startFrameTime)
        }
    }

    private fun calculateFps(startFrameTime: Long) {
        // Calculate the fps this frame. We can then use the result to time animations and more.
        timeThisFrame = System.currentTimeMillis() - startFrameTime
        if (timeThisFrame >= 1) {
            fps = 1000 / timeThisFrame
            //Timber.d("fps: %d", fps)
        }
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        when (motionEvent.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                paused = false
                if (motionEvent.x > screenX / 2) {
                    //paddle.setMovementState(paddle.RIGHT)
                } else {
                    //paddle.setMovementState(paddle.LEFT)
                }
            }

            //MotionEvent.ACTION_UP -> paddle.setMovementState(paddle.STOPPED)
        }
        return true
    }

    // Everything that needs to be updated goes in here (movement, collision detection etc.)
    private fun update() {
        if (!paused) {
            testObjectBasicSpeed += 1

            for (joint in joints) {
                if (joint.isGoingSomewhere()) {
                    SceneUtils.calculateNextJointPosition(joint)

                    if (SceneUtils.isWithinJitter(joint.center, joint.destination!!)) {
                        joint.destination = null
                        //Timber.d("I stopped moving joint %d (A) and I will set a new direction", joint.jointId)
                        joint.destination = SceneUtils.setNewDirection(joint.jointId)
                        //joint.speed = SceneUtils.setNewSpeed()
                    }
                }
            }
        }
    }

    ////////////////////////////////////
    // Start of drawing methods
    ////////////////////////////////////
    /**
     * Draw the newly updated scene
     */
    private fun draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder!!.surface.isValid) {
            canvas = ourHolder!!.lockCanvas() // Lock the canvas ready to draw

            drawBackgroundAndLimits()
            drawJoints()
            drawConnections()
            //drawTestItems()

            ourHolder!!.unlockCanvasAndPost(canvas) // Draw everything to the screen
        } else {
            //Timber.w("The surface was invalid!")
        }
    }

    private fun drawBackgroundAndLimits() {
        canvas.drawColor(Colors.white.toColorInt())
        paint.style = Paint.Style.FILL
        //upper left corner
        paint.color = Colors.red.toColorInt()
        canvas.drawRect(RectF(0f, 0f, SIZE_TEST_SQUARE, SIZE_TEST_SQUARE), paint)

        //upper right corner
        paint.color = Colors.green.toColorInt()
        canvas.drawRect(RectF((sceneWidth - SIZE_TEST_SQUARE), 0f, sceneWidth.toFloat(), SIZE_TEST_SQUARE), paint)

        //bottom left corner
        paint.color = Colors.blue.toColorInt()
        canvas.drawRect(RectF(0f, (sceneHeight - SIZE_TEST_SQUARE), SIZE_TEST_SQUARE, sceneHeight.toFloat()), paint)

        //bottom right corner
        paint.color = Colors.yellow.toColorInt()
        canvas.drawRect(
            RectF(
                (sceneWidth - SIZE_TEST_SQUARE),
                (sceneHeight - SIZE_TEST_SQUARE),
                sceneWidth.toFloat(),
                sceneHeight.toFloat()
            ), paint
        )

        paint.color = Colors.black.toColorInt()
        paint.strokeWidth = WIDTH_BORDER
        canvas.drawLine(0f, 0f, 0f, sceneHeight.toFloat(), paint)//left
        canvas.drawLine(0f, sceneHeight.toFloat(), sceneWidth.toFloat(), sceneHeight.toFloat(), paint)//bottom
        canvas.drawLine(0f, 0f, sceneHeight.toFloat(), 0f, paint)//top
        canvas.drawLine(sceneWidth.toFloat(), 0f, sceneWidth.toFloat(), sceneHeight.toFloat(), paint)//right
    }

    private fun drawJoints() {
        for (joint in joints) {
            paint.color = Colors.purpleOrchid.toColorInt()
            paint.style = Paint.Style.FILL

            canvas.drawCircle(joint.center.x.toFloat(), joint.center.y.toFloat(), JOINT_SIZE, paint)

            if (joint.isGoingSomewhere()) {
                //drawDestinationLine(joint)
            }
        }
    }

    private fun drawConnections() {
        paint.strokeWidth = WIDTH_DESTINATION

        for (jointOuter in joints) {
            paint.color = Colors.redFireBrick.toColorInt()
            paint.style = Paint.Style.FILL

            for (jointInner in joints) {
                if (jointOuter.jointId != jointInner.jointId && jointInner.destination != null) {
                    val distance = NumberUtils.getDistanceBetweenTwoPoints(jointOuter.center, jointInner.center)
                    //Timber.d("Distance between %d (%d x %d) and %d is %f", jointOuter.jointId, jointOuter.center.x, jointOuter.center.y, jointInner.jointId, distance)

                    paint.style = Paint.Style.STROKE
                    if (distance <= 666f) {
                        when (BallsConfig.connectionStyle) {
                            BallsConfig.ConnectionStyle.SIMPLE -> {
                                canvas.drawLine(
                                    jointOuter.center.x.toFloat(), jointOuter.center.y.toFloat(),
                                    jointInner.center.x.toFloat(), jointInner.center.y.toFloat(),
                                    paint
                                )
                            }

                            BallsConfig.ConnectionStyle.CURVES -> {
                                val drawPathAsCurve: Path = SceneUtils.calculatePathForCurve(
                                    PointF(jointOuter.center.x.toFloat(), jointOuter.center.y.toFloat()),
                                    PointF(jointInner.center.x.toFloat(), jointInner.center.y.toFloat())
                                )

                                canvas.drawPath(drawPathAsCurve, paint)
                            }

                            BallsConfig.ConnectionStyle.BEZIER -> {
                                // Read https://stackoverflow.com/questions/30073682/how-to-draw-bezier-curve-in-android
                                // and http://android-coding.blogspot.com/2012/04/draw-cubic-bezier-on-path-cubicto.html
                                // and http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html
                                canvas.drawPath(SceneUtils.calculatePathForBezier(jointOuter, jointInner), paint)
                            }

                            BallsConfig.ConnectionStyle.SINUS -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun drawDestinationLine(joint: Joint) {
        paint.strokeWidth = WIDTH_DESTINATION

        paint.color = Colors.black.toColorInt()
        canvas.drawLine(
            joint.center.x.toFloat(), joint.center.y.toFloat(),
            joint.destination!!.x.toFloat(),
            joint.destination!!.y.toFloat(),
            paint
        )
    }

    private fun drawTestItems() {
        paint.color = Colors.orangeCoral.toColorInt()

        canvas.drawRect(RectF(testObjectBasicSpeed, 800f, testObjectBasicSpeed + 50, (800 + 50).toFloat()), paint)
        canvas.drawRect(
            RectF(2 * testObjectBasicSpeed, 900f, 2 * testObjectBasicSpeed + 50, (900 + 50).toFloat()),
            paint
        )
    }

    /////////////////////////////////////////
    // Exposed methods to interact with the scene
    /////////////////////////////////////////
    fun pause() {
        playing = false
        try {
            gameThread!!.join()
        } catch (e: InterruptedException) {
            Timber.e("Error occurred when joining thread: %s", e.printStackTrace().toString())
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
    }

    fun togglePlayback() {
        paused = !paused
    }

    fun addJoints() {
        for (i in 0..AMOUNT_OF_JOINTS) {
            val start = Point(NumberUtils.randomXinScene(), NumberUtils.randomYinScene())

            joints.add(Joint(i, start))
        }
    }

    fun addDestinationForAll() {
        for (i in 0..AMOUNT_OF_JOINTS) {
            joints[i].destination = SceneUtils.addDestinationForAll(i)
        }
    }

    companion object {
        const val SIZE_TEST_SQUARE = 50f
        const val WIDTH_BORDER = 10f
        const val WIDTH_DESTINATION = 2f
        const val WIDTH_NORMAL = 1f

        const val AMOUNT_OF_JOINTS = 6
        const val JOINT_SIZE = 24f
    }
}
