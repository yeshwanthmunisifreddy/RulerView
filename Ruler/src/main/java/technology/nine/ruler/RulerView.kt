package technology.nine.ruler

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller

class RulerView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    private var mMinVelocity = 0
    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mWidth = 0
    private var mHeight = 0
    private var showResult = false
    private var showUnit = false
    private var showIndicator = false
    private var mAlphaEnable = false
    private var mStrokeCap = 0
    private var mIndicatorType = 0
    private val resultHeight = 0
    private var mValue = 0f
    private var mMaxValue = 0f
    private var mMinValue = 0f
    private var mMinSpanCount = 0f
    private var mSpanCountMiddle = 0
    private var mItemSpacing = 0
    private var mPerSpanValue = 0f
    private var mMaxLineHeight = 0
    private var mMiddleLineHeight = 0
    private var mMinLineHeight = 0
    private var mMinLineWidth = 0
    private var mMaxLineWidth = 0
    private var mMiddleLineWidth = 0
    private var mTextMarginTop = 0
    private var mScaleTextHeight = 0f
    private var mResultTextHeight = 0f
    private var mUnitTextHeight = 0f
    private var mIndcatorColor = -0xaf4a7a
    private var mIndcatorWidth = 0
    private var mIndcatorHeight = 0
    private var mScaleTextColor = -0x7fddddde
    private var mScaleTextSize = 0
    private var mResultTextColor = -0xaf4a7a
    private var mResultTextSize = 0
    private var mUnitTextColor = -0x99999a
    private var mUnitTextSize = 0
    private var unit: String? = null
    private var mMinLineColor = -0x7fddddde
    private var mMaxLineColor = -0x7fddddde
    private var mMiddleLineColor = -0x7fddddde
    private var mScaleTextPaint: Paint? = null
    private var mLinePaint: Paint? = null
    private var mResultTextPaint: Paint? = null
    private var mUnitTextPaint: Paint? = null
    private var mIndicatorPaint: Paint? = null
    private var mTotalLine = 0
    private var mMaxOffset = 0
    private var mOffset = 0f
    private var mLastX = 0
    private var mMove = 0
    private var mListener: OnChooseResulterListener? = null

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null) : this(context, attrs, 0) {
        setAttr(context, attrs, 0)
    }

    private fun setAttr(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = getContext().theme
            .obtainStyledAttributes(
                attrs, R.styleable.RulerView,
                defStyleAttr, 0
            )
        mValue = a.getFloat(R.styleable.RulerView_defaultSelectedValue, DEFAULT_VALUE)
        mMaxValue = a.getFloat(R.styleable.RulerView_maxValue, MAX_VALUE)
        mMinValue = a.getFloat(R.styleable.RulerView_minValue, MIN_VALUE)
        mMinSpanCount = a.getFloat(R.styleable.RulerView_minSpanCount, MIN_COUNT)
        mSpanCountMiddle = (a.getFloat(R.styleable.RulerView_minSpanCount, MIN_COUNT) / 2).toInt()
        val precision = a.getFloat(R.styleable.RulerView_spanValue, SPAN_VALUE)
        mPerSpanValue = precision * 10
        mItemSpacing = a.getDimensionPixelSize(
            R.styleable.RulerView_itemSpacing,
            dp2px(defaultItemSpacing.toFloat())
        )
        mMaxLineHeight = a.getDimensionPixelSize(
            R.styleable.RulerView_maxLineHeight,
            dp2px(ITEM_MAX_HEIGHT.toFloat())
        )
        mMinLineHeight = a.getDimensionPixelSize(
            R.styleable.RulerView_minLineHeight,
            dp2px(ITEM_MIN_HEIGHT.toFloat())
        )
        mMiddleLineHeight = a.getDimensionPixelSize(
            R.styleable.RulerView_middleLineHeight,
            dp2px(ITEM_MIDDLE_HEIGHT.toFloat())
        )
        mMinLineWidth = a.getDimensionPixelSize(
            R.styleable.RulerView_minLineWidth,
            dp2px(ITEM_MIN_WIDTH.toFloat())
        )
        mMiddleLineWidth = a.getDimensionPixelSize(
            R.styleable.RulerView_middleLineWidth,
            dp2px(ITEM_MIDDLE_WIDTH.toFloat())
        )
        mMaxLineWidth = a.getDimensionPixelSize(
            R.styleable.RulerView_maxLineWidth,
            dp2px(ITEM_MAX_WIDTH.toFloat())
        )
        mMaxLineColor = a.getColor(R.styleable.RulerView_maxLineColor, mMaxLineColor)
        mMiddleLineColor = a.getColor(R.styleable.RulerView_middleLineColor, mMiddleLineColor)
        mMinLineColor = a.getColor(R.styleable.RulerView_minLineColor, mMinLineColor)
        mIndcatorColor = a.getColor(R.styleable.RulerView_indicatorColor, mIndcatorColor)
        mIndcatorWidth = a.getDimensionPixelSize(
            R.styleable.RulerView_indicatorWidth,
            dp2px(INDICATOR_WIDTH.toFloat())
        )
        mIndcatorHeight = a.getDimensionPixelSize(
            R.styleable.RulerView_indicatorHeight,
            dp2px(INDICATOR_HEIGHT.toFloat())
        )
        mIndicatorType = a.getInt(R.styleable.RulerView_indicatorType, LINE)
        mScaleTextColor = a.getColor(R.styleable.RulerView_scaleTextColor, mScaleTextColor)
        mScaleTextSize = a.getDimensionPixelSize(
            R.styleable.RulerView_scaleTextSize,
            sp2px(scaleTextSize.toFloat())
        )
        mTextMarginTop = a.getDimensionPixelSize(
            R.styleable.RulerView_textMarginTop,
            dp2px(textMarginTop.toFloat())
        )
        mResultTextColor = a.getColor(R.styleable.RulerView_resultTextColor, mResultTextColor)
        mResultTextSize = a.getDimensionPixelSize(
            R.styleable.RulerView_resultTextSize,
            sp2px(resultTextSize.toFloat())
        )
        mUnitTextColor = a.getColor(R.styleable.RulerView_unitTextColor, mUnitTextColor)
        mUnitTextSize = a.getDimensionPixelSize(
            R.styleable.RulerView_unitTextSize,
            sp2px(unitTextSize.toFloat())
        )
        showUnit = a.getBoolean(R.styleable.RulerView_showUnit, true)
        unit = a.getString(R.styleable.RulerView_unit)
        if (TextUtils.isEmpty(unit)) {
            unit = "kg"
        }
        showResult = a.getBoolean(R.styleable.RulerView_showResult, true)
        showIndicator = a.getBoolean(R.styleable.RulerView_showIndicator, true)
        mAlphaEnable = a.getBoolean(R.styleable.RulerView_alphaEnable, true)
        mStrokeCap = a.getInt(R.styleable.RulerView_strokeCap, ROUND)
        init(context)
        a.recycle()
    }

    protected fun init(context: Context?) {
        mScroller = Scroller(context)
        mMinVelocity = ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity
        mScaleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mScaleTextPaint!!.textSize = mScaleTextSize.toFloat()
        mScaleTextPaint!!.color = mScaleTextColor
        mScaleTextPaint!!.isAntiAlias = true
        mScaleTextHeight = getFontHeight(mScaleTextPaint!!)
        mResultTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mResultTextPaint!!.textSize = mResultTextSize.toFloat()
        mResultTextPaint!!.color = mResultTextColor
        mResultTextPaint!!.isAntiAlias = true
        mResultTextPaint!!.alpha = 255
        mResultTextHeight = getFontHeight(mResultTextPaint!!)
        if (showUnit) {
            mUnitTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mUnitTextPaint!!.textSize = mUnitTextSize.toFloat()
            mUnitTextPaint!!.color = mUnitTextColor
            mUnitTextPaint!!.isAntiAlias = true
            mUnitTextPaint!!.alpha = 232
            mUnitTextHeight = getFontHeight(mUnitTextPaint!!)
            // Log.i("TAG", "单位字的高度: " + mUnitTextHeight);
        }
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (mStrokeCap == ROUND) {
            mLinePaint!!.strokeCap = Paint.Cap.ROUND
        } else if (mStrokeCap == BUTT) {
            mLinePaint!!.strokeCap = Paint.Cap.BUTT
        } else if (mStrokeCap == SQUARE) {
            mLinePaint!!.strokeCap = Paint.Cap.SQUARE
        }
        mLinePaint!!.isAntiAlias = true
        if (showIndicator) {
            mIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mIndicatorPaint!!.strokeWidth = mIndcatorWidth.toFloat()
            mIndicatorPaint!!.isAntiAlias = true
            mIndicatorPaint!!.color = mIndcatorColor
            if (mStrokeCap == ROUND) {
                mIndicatorPaint!!.strokeCap = Paint.Cap.ROUND
            } else if (mStrokeCap == BUTT) {
                mIndicatorPaint!!.strokeCap = Paint.Cap.BUTT
            } else if (mStrokeCap == SQUARE) {
                mIndicatorPaint!!.strokeCap = Paint.Cap.SQUARE
            }
        }
        initViewParam(mValue, mMinValue, mMaxValue, mPerSpanValue)
    }

    fun initViewParam(
        defaultValue: Float, minValue: Float, maxValue: Float,
        perSpanValue: Float
    ) {
        mValue = defaultValue
        mMaxValue = maxValue
        mMinValue = minValue
        mPerSpanValue = (perSpanValue * mMinSpanCount)
        mTotalLine =
            ((mMaxValue * mMinSpanCount - mMinValue * mMinSpanCount).toInt() / mPerSpanValue + 1).toInt()
        mMaxOffset = -(mTotalLine - 1) * mItemSpacing
        mOffset = (mMinValue - mValue) / mPerSpanValue * mItemSpacing * mMinSpanCount
        invalidate()
        visibility = VISIBLE
    }

    fun setTextFontFamily(typeface: Typeface?) {
        mScaleTextPaint!!.typeface = typeface
        setInvalidate()
    }

    fun setDefaultSelectedValue(selectedValue: Float) {
        mValue = selectedValue
        setInvalidate()
    }

    fun setMaxValue(maxValue: Float) {
        mMaxValue = maxValue
        setInvalidate()
    }

    fun setMinValue(minValue: Float) {
        mMinValue = minValue
        setInvalidate()
    }

    fun setMinSpanCount(minSpanCount: Float) {
        mMinSpanCount = minSpanCount
        mSpanCountMiddle = (minSpanCount / 2).toInt()
        setInvalidate()
    }

    fun setSpanValue(spanValue: Float) {
        mPerSpanValue = spanValue * 10
        setInvalidate()
    }

    fun setItemSpacing(itemSpacing: Int) {
        mItemSpacing = dp2px(itemSpacing.toFloat())
        setInvalidate()
    }

    fun setMaxLineHeight(maxLineHeight: Int) {
        mMaxLineHeight = dp2px(maxLineHeight.toFloat())
        setInvalidate()
    }

    fun setMinLineHeight(minLineHeight: Int) {
        mMinLineHeight = dp2px(minLineHeight.toFloat())
        setInvalidate()
    }

    fun setMiddleLineHeight(middleLineHeight: Int) {
        mMiddleLineHeight = dp2px(middleLineHeight.toFloat())
        setInvalidate()
    }

    fun setMinLineWidth(minLineWidth: Int) {
        mMinLineWidth = dp2px(minLineWidth.toFloat())
        setInvalidate()
    }

    fun setMiddleLineWidth(middleLineWidth: Int) {
        mMiddleLineWidth = dp2px(middleLineWidth.toFloat())
        setInvalidate()
    }

    fun setMaxLineWidth(maxLineWidth: Int) {
        mMaxLineWidth = dp2px(maxLineWidth.toFloat())
        setInvalidate()
    }

    fun setIndicatorWidth(mIndcatorWidth: Int) {
        this.mIndcatorWidth = dp2px(mIndcatorWidth.toFloat())
        setInvalidate()
    }

    fun setIndicatorHeight(mIndcatorHeight: Int) {
        this.mIndcatorHeight = dp2px(mIndcatorHeight.toFloat())
        setInvalidate()
    }

    fun setIndicatorType(mIndicatorType: Int) {
        this.mIndicatorType = mIndicatorType
        setInvalidate()
    }

    fun setMaxLineColor(mMaxLineColor: Int) {
        this.mMaxLineColor = mMaxLineColor
        setInvalidate()
    }

    fun setMiddleLineColor(mMiddleLineColor: Int) {
        this.mMiddleLineColor = mMiddleLineColor
        setInvalidate()
    }

    fun setMinLineColor(mMinLineColor: Int) {
        this.mMinLineColor = mMinLineColor
        setInvalidate()
    }

    fun setIndicatorColor(mIndcatorColor: Int) {
        this.mIndcatorColor = mIndcatorColor
        setInvalidate()
    }

    fun setScaleTextColor(mScaleTextColor: Int) {
        this.mScaleTextColor = mScaleTextColor
        setInvalidate()
    }

    fun setResultTextColor(mResultTextColor: Int) {
        this.mResultTextColor = mResultTextColor
        setInvalidate()
    }

    fun setUnitTextColor(mUnitTextColor: Int) {
        this.mUnitTextColor = mUnitTextColor
        setInvalidate()
    }

    fun setScaleTextSize(mScaleTextSize: Int) {
        mScaleTextColor = sp2px(mScaleTextSize.toFloat())
        setInvalidate()
    }

    fun setTextMarginTop(mTextMarginTop: Int) {
        this.mTextMarginTop = sp2px(mTextMarginTop.toFloat())
        setInvalidate()
    }

    fun setResultTextSize(mResultTextSize: Int) {
        this.mResultTextSize = sp2px(mResultTextSize.toFloat())
        setInvalidate()
    }

    fun setUnitTextSize(mUnitTextSize: Int) {
        mScaleTextColor = sp2px(mUnitTextSize.toFloat())
        setInvalidate()
    }

    fun isShowUnit(showUnit: Boolean) {
        this.showUnit = showUnit
        setInvalidate()
    }

    fun isShowResult(showResult: Boolean) {
        this.showResult = showResult
        setInvalidate()
    }

    fun isShowIndicator(showIndicator: Boolean) {
        this.showIndicator = showIndicator
        setInvalidate()
    }

    fun setAlphaEnable(mAlphaEnable: Boolean) {
        this.mAlphaEnable = mAlphaEnable
        setInvalidate()
    }

    fun setUnit(unit: String?) {
        this.unit = unit
        setInvalidate()
    }

    fun mStrokeCap(mStrokeCap: Int) {
        this.mStrokeCap = mStrokeCap
        setInvalidate()
    }

    fun setChooseValueChangeListener(listener: OnChooseResulterListener?) {
        mListener = listener
    }

    private fun setInvalidate() {
        mTotalLine =
            ((mMaxValue * mMinSpanCount - mMinValue * mMinSpanCount).toInt() / mPerSpanValue + 1).toInt()
        mMaxOffset = -(mTotalLine - 1) * mItemSpacing
        mOffset = (mMinValue - mValue) / mPerSpanValue * mItemSpacing * mMinSpanCount
        invalidate()
        visibility = VISIBLE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            mWidth = w
            mHeight = h
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawScaleAndNum(canvas)
        if (showIndicator) drawIndicator(canvas)
        if (showResult) {
            if (mPerSpanValue == 1f) {
                drawResult(canvas, String.format(getPrecisionFormat(1), mValue))
            } else if (mPerSpanValue == 10f) {
                drawResult(canvas, String.format(getPrecisionFormat(0), mValue))
            }
        }
    }

    private fun drawIndicator(canvas: Canvas) {
        val srcPointX = mWidth / 2
        if (mIndicatorType == LINE) {
            canvas.drawLine(
                srcPointX.toFloat(),
                resultHeight.toFloat(),
                srcPointX.toFloat(),
                (mIndcatorHeight + resultHeight).toFloat(),
                mIndicatorPaint!!
            )
        } else if (mIndicatorType == TRIANGLE) {
            val path = Path()
            path.moveTo((srcPointX - mItemSpacing).toFloat(), resultHeight.toFloat())
            path.lineTo((srcPointX + mItemSpacing).toFloat(), resultHeight.toFloat())
            path.lineTo(srcPointX.toFloat(), (mMinLineHeight + resultHeight).toFloat())
            path.close()
            canvas.drawPath(path, mIndicatorPaint!!)
        }
    }

    /**
     * @param canvas
     */
    private fun drawScaleAndNum(canvas: Canvas) {
        var left: Float
        var height: Float
        var value: String
        var alpha: Int
        var scale: Float
        val srcPointX = mWidth / 2
        for (i in 0 until mTotalLine) {
            left = srcPointX + mOffset + i * mItemSpacing
            if (left < 0 || left > mWidth) {
                continue
            }
            if (i % mMinSpanCount == 0f) {
                height = mMaxLineHeight.toFloat()
                mLinePaint!!.color = mMaxLineColor
                mLinePaint!!.strokeWidth = mMaxLineWidth.toFloat()
            } else if (i % mSpanCountMiddle == 0) {
                height = mMiddleLineHeight.toFloat()
                mLinePaint!!.color = mMiddleLineColor
                mLinePaint!!.strokeWidth = mMiddleLineWidth.toFloat()
            } else {
                height = mMinLineHeight.toFloat()
                mLinePaint!!.color = mMinLineColor
                mLinePaint!!.strokeWidth = mMinLineWidth.toFloat()
            }
            if (mAlphaEnable) {
                // Log.i("TAG", "drawScaleAndNum: ================");
                scale = 1 - Math.abs(left - srcPointX) / srcPointX
                // Log.i("TAG", "drawScaleAndNum: scale====" + scale);
                alpha = (255 * scale * scale).toInt()
                // Log.i("TAG", "drawScaleAndNum: alpha===" + alpha);
                mLinePaint!!.alpha = alpha
                mScaleTextPaint!!.alpha = alpha
            }
            canvas.drawLine(left, resultHeight.toFloat(), left, height + resultHeight, mLinePaint!!)
            if (i % mMinSpanCount == 0f) {
                value = (mMinValue + i * mPerSpanValue / mMinSpanCount).toInt().toString()
                canvas.drawText(
                    value, left - mScaleTextPaint!!.measureText(value) / 2,
                    resultHeight + height + mTextMarginTop + mScaleTextHeight,
                    mScaleTextPaint!!
                )
            }
        }
    }

    /**
     * 画结果和单位
     *
     * @param canvas
     * @param resultText
     */
    private fun drawResult(canvas: Canvas, resultText: String) {
        val srcPointX = mWidth / 2

        /*  canvas.drawText(resultText, srcPointX - mResultTextPaint.measureText(resultText) / 2,
                Math.max(mResultTextHeight, mUnitTextHeight),
                mResultTextPaint);

*/

        /*if (showUnit) {
            canvas.drawText(unit, srcPointX + mResultTextPaint.measureText(resultText) / 2 + 10,
                    Math.max(mResultTextHeight, mUnitTextHeight), mUnitTextPaint);
        }*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val xPosition = event.x.toInt()
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mScroller!!.forceFinished(true)
                mLastX = xPosition
                mMove = 0
            }
            MotionEvent.ACTION_MOVE -> {
                mMove = mLastX - xPosition
                changeMoveAndValue()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                countMoveEnd()
                countVelocityTracker()
                return false
            }
            else -> {
            }
        }
        mLastX = xPosition
        return true
    }

    private fun countVelocityTracker() {
        mVelocityTracker!!.computeCurrentVelocity(1000)
        val xVelocity = mVelocityTracker!!.xVelocity
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller!!.fling(0, 0, xVelocity.toInt(), 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0)
        }
    }

    private fun countMoveEnd() {
        mOffset -= mMove.toFloat()
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
        } else if (mOffset >= 0) {
            mOffset = 0f
        }
        mLastX = 0
        mMove = 0
        mValue = (mMinValue
                + Math.round(Math.abs(mOffset) * 1.0f / mItemSpacing) * mPerSpanValue / mMinSpanCount)
        mOffset = ((mMinValue - mValue) * mMinSpanCount / mPerSpanValue
                * mItemSpacing)
        notifyValueChange()
        postInvalidate()
    }

    private fun changeMoveAndValue() {
        mOffset -= mMove.toFloat()
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset.toFloat()
            mMove = 0
            mScroller!!.forceFinished(true)
        } else if (mOffset >= 0) {
            mOffset = 0f
            mMove = 0
            mScroller!!.forceFinished(true)
        }
        mValue = (mMinValue
                + Math.round(Math.abs(mOffset) * 1.0f / mItemSpacing) * mPerSpanValue / mMinSpanCount)
        notifyValueChange()
        postInvalidate()
    }

    private fun notifyValueChange() {
        if (null != mListener) {
            mListener!!.onChooseValueChange(mValue)
        }
    }

    interface OnChooseResulterListener {
        fun onChooseValueChange(value: Float)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller!!.computeScrollOffset()) {
            if (mScroller!!.currX == mScroller!!.finalX) { // over
                countMoveEnd()
            } else {
                val xPosition = mScroller!!.currX
                mMove = mLastX - xPosition
                changeMoveAndValue()
                mLastX = xPosition
            }
        }
    }

    companion object {
        const val ROUND = 1
        const val BUTT = 0
        const val SQUARE = 2
        private const val DEFAULT_VALUE = 170f
        private const val MAX_VALUE = 250f
        private const val MIN_VALUE = 0f
        private const val SPAN_VALUE = 0.1f
        private const val MIN_COUNT = 10f
        private const val ITEM_MAX_HEIGHT = 36
        private const val ITEM_MAX_WIDTH = 3
        private const val ITEM_MIN_HEIGHT = 20
        private const val ITEM_MIN_WIDTH = 2
        private const val ITEM_MIDDLE_HEIGHT = 28
        private const val ITEM_MIDDLE_WIDTH = 2
        private const val INDICATOR_WIDTH = 3
        private const val INDICATOR_HEIGHT = 39
        const val LINE = 1
        const val TRIANGLE = 2
        private const val defaultItemSpacing = 6
        private const val textMarginTop = 8
        private const val scaleTextSize = 15
        private const val unitTextSize = 15
        private const val resultTextSize = 24
        fun getFontHeight(paint: Paint): Float {
            val fm = paint.fontMetrics
            return fm.descent - fm.ascent
        }

        /**
         * dp&px
         *
         * @param dpVal
         * @return
         */
        fun dp2px(dpVal: Float): Int {
            val r = Resources.getSystem()
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpVal,
                r.displayMetrics
            ).toInt()
        }

        /**
         * sp&px
         *
         * @param spVal
         * @return
         */
        fun sp2px(spVal: Float): Int {
            val r = Resources.getSystem()
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spVal,
                r.displayMetrics
            ).toInt()
        }

        fun getPrecisionFormat(precision: Int): String {
            return "%." + precision + "f"
        }
    }

    init {
        setAttr(context, attrs, defStyleAttr)
    }
}