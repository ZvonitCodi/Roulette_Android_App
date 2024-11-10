package com.example.roulette

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.*

class RouletteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rouletteNumbers = arrayOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var angleOffset = 0f
    private var highlightedSector: Float? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        val radius = width.coerceAtMost(height) / 2 * 0.9f
        val centerX = width / 2
        val centerY = height / 2
        val sectorAngle = 360f / rouletteNumbers.size

        for (i in rouletteNumbers.indices) {
            paint.color = when {
                i == 0 -> Color.GREEN
                i % 2 == 0 -> Color.RED
                else -> Color.BLACK
            }

            val startAngle = i * sectorAngle + angleOffset
            canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, startAngle, sectorAngle, true, paint)

            paint.color = Color.WHITE
            paint.textSize = 17f
            val textAngle = Math.toRadians((startAngle + sectorAngle / 2).toDouble())
            val textX = centerX + radius * 0.7f * cos(textAngle).toFloat()
            val textY = centerY + radius * 0.7f * sin(textAngle).toFloat()
            canvas.drawText(rouletteNumbers[i].toString(), textX, textY, paint)

            if (highlightedSector != null && i == (highlightedSector!! / sectorAngle).toInt()) {
                val circleRadius = 20f
                val circleX = centerX + radius * 0.9f * cos(Math.toRadians((startAngle + sectorAngle / 2).toDouble())).toFloat()
                val circleY = centerY + radius * 0.9f * sin(Math.toRadians((startAngle + sectorAngle / 2).toDouble())).toFloat()

                paint.color = Color.WHITE
                paint.style = Paint.Style.FILL
                canvas.drawCircle(circleX, circleY, circleRadius, paint)
            }
        }
    }

    fun setRotationAngle(angle: Float) {
        angleOffset = angle
        invalidate()
    }

    fun setSectorHighlight(sectorAngle: Float) {
        highlightedSector = sectorAngle
        invalidate()
    }
}
