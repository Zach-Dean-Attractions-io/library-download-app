package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.math.abs
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var loadingOverlayW = 0
    private var loadingOverlayH = 0
    private var loadingAngle = 0f

    private var completedText = "Download"
    private var loadingText = "Loading..."

    private var buttonText = ""

    private val valueAnimator = ValueAnimator().apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { currentValueAnimator ->
            loadingOverlayW = currentValueAnimator.animatedValue as Int
            loadingAngle = currentValueAnimator.animatedFraction * 360
            this@LoadingButton.invalidate()
        }
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        if(old == ButtonState.Completed && new == ButtonState.Loading) {
            isClickable = false
            buttonText = loadingText
            valueAnimator.apply {
                setIntValues(0, widthSize)
            }
            valueAnimator.start()
        } else if (old == ButtonState.Loading && new == ButtonState.Completed) {
            // Finish the current animation
            valueAnimator.repeatCount = 0
            valueAnimator.doOnEnd {
                loadingOverlayW = 0
                isClickable = true
                buttonText = completedText
            }

        }

        invalidate()
    }

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
    }

    private val loadingBackgroundPaint = Paint()

    private val loadingCirclePaint = Paint()

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {

            completedText = getString(R.styleable.LoadingButton_completedText) ?: "Completed"
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: "Loading..."

            buttonText = completedText

            textPaint.apply {
                color = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
                textSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    getInteger(R.styleable.LoadingButton_buttonTextSize, 10).toFloat(),
                    context.resources.displayMetrics
                )
            }

            loadingBackgroundPaint.apply {
                color = getColor(R.styleable.LoadingButton_loadingBackgroundColor, Color.BLACK)
            }

            loadingCirclePaint.apply {
                color = getColor(R.styleable.LoadingButton_loadingCircleColor, Color.WHITE)
            }

        }
    }

    fun changeState(newState: ButtonState) {
        buttonState = newState
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, loadingOverlayW.toFloat(), loadingOverlayH.toFloat(), loadingBackgroundPaint)

        canvas?.drawText(
            buttonText,
            canvas?.width / 2.0f,
            canvas?.height / 2.0f + abs(textPaint.fontMetrics.bottom),
            textPaint
        )

        if(buttonState == ButtonState.Loading) {
            canvas?.drawArc(
                10f, canvas?.height / 4.0f, canvas?.height / 2f, 3 * canvas?.height / 4f,
                0f, loadingAngle, true, loadingCirclePaint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h

        loadingOverlayH = h

        setMeasuredDimension(w, h)
    }

}