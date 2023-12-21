package se.onemanstudio.randy

object Unused {
    private fun drawPaddle() {
        // Draw the paddle
        //canvas.drawRect(paddle.getRect(), paint)
    }

    private fun drawBall() {
        // Draw the ball
        //canvas.drawRect(ball.getRect(), paint)
    }

    private fun drawBricks() {
        // Change the brush color for drawing
        //paint.color = Color.argb(255, 249, 129, 0)

        // Draw the bricks if visible
        //for (i in 0 until numBricks) {
        //    if (bricks.get(i).getVisibility()) {
        //        canvas.drawRect(bricks.get(i).getRect(), paint)
        //    }
        //}
    }

    private fun drawInformation() {
        // Choose the brush color for drawing
        //paint.color = Color.argb(255, 255, 255, 255)

        // Draw the score
        //paint!!.textSize = 40f
        //canvas.drawText("Score: $score   Lives: $lives", 10f, 50f, paint!!)

        // Has the player cleared the screen?
        //if (score == numBricks * 10) {
        //    paint!!.textSize = 90f
        //    canvas.drawText("YOU HAVE WON!", 10f, (screenY / 2).toFloat(), paint!!)
        //}

        // Has the player lost?
        //if (lives <= 0) {
        //    paint!!.textSize = 90f
        //    canvas.drawText("YOU HAVE LOST!", 10f, (screenY / 2).toFloat(), paint!!)
        //}
    }
}