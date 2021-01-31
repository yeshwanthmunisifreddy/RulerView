package technology.nine.ruler;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

public class RulerView extends View {

    private int mMinVelocity;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mWidth;
    private int mHeight;

    private boolean showResult;
    private boolean showUnit;
    private boolean showIndicator;
    private boolean mAlphaEnable;

    private int mStrokeCap;

    private static final int ROUND = 1;
    private static final int BUTT = 0;
    private static final int SQUARE = 2;

    private static final float DEFAULT_VALUE = 170;
    private static final float MAX_VALUE = 250f;
    private static final float MIN_VALUE = 0f;
    private static final float SPAN_VALUE = 0.1f;
    private static final float MIN_COUNT = 10f;
    private static final int ITEM_MAX_HEIGHT = 36;
    private static final int ITEM_MAX_WIDTH = 3;
    private static final int ITEM_MIN_HEIGHT = 20;
    private static final int ITEM_MIN_WIDTH = 2;
    private static final int ITEM_MIDDLE_HEIGHT = 28;
    private static final int ITEM_MIDDLE_WIDTH = 2;
    private static final int INDICATOR_WIDTH = 3;
    private static final int INDICATOR_HEIGHT = 39;

    private static final int LINE = 1;
    private static final int TRIANGLE = 2;

    private static final int defaultItemSpacing = 6;
    private static final int textMarginTop = 8;
    private static final int scaleTextSize = 15;
    private static final int unitTextSize = 15;
    private static final int resultTextSize = 24;

    private int mIndicatorType;

    private int resultHeight = 0;

    private float mValue;
    private float mMaxValue;
    private float mMinValue;
    private float mMinSpanCount;
    private int mSpanCountMiddle;

    private int mItemSpacing;
    private float mPerSpanValue;
    private int mMaxLineHeight;
    private int mMiddleLineHeight;
    private int mMinLineHeight;

    private int mMinLineWidth;
    private int mMaxLineWidth;
    private int mMiddleLineWidth;

    private int mTextMarginTop;
    private float mScaleTextHeight;
    private float mResultTextHeight;
    private float mUnitTextHeight = 0;

    private int mIndcatorColor = 0xff50b586;
    private int mIndcatorWidth;
    private int mIndcatorHeight;

    private int mScaleTextColor = 0X80222222;
    private int mScaleTextSize;
    private int mResultTextColor = 0xff50b586;
    private int mResultTextSize;
    private int mUnitTextColor = 0Xff666666;
    private int mUnitTextSize;
    private String unit;

    private int mMinLineColor = 0X80222222;
    private int mMaxLineColor = 0X80222222;
    private int mMiddleLineColor = 0X80222222;

    private Paint mScaleTextPaint;
    private Paint mLinePaint;
    private Paint mResultTextPaint;
    private Paint mUnitTextPaint;
    private Paint mIndicatorPaint;

    private int mTotalLine;
    private int mMaxOffset;
    private float mOffset;
    private int mLastX, mMove;

    private OnChooseResulterListener mListener;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setAttr(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(context, attrs, defStyleAttr);
    }

    private void setAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.RulerView,
                        defStyleAttr, 0);

        mValue = a.getFloat(R.styleable.RulerView_defaultSelectedValue, DEFAULT_VALUE);
        mMaxValue = a.getFloat(R.styleable.RulerView_maxValue, MAX_VALUE);
        mMinValue = a.getFloat(R.styleable.RulerView_minValue, MIN_VALUE);
        mMinSpanCount = a.getFloat(R.styleable.RulerView_minSpanCount, MIN_COUNT);
        mSpanCountMiddle = (int) (a.getFloat(R.styleable.RulerView_minSpanCount, MIN_COUNT) / 2);
        float precision = a.getFloat(R.styleable.RulerView_spanValue, SPAN_VALUE);
        mPerSpanValue = precision * 10;

        mItemSpacing = a.getDimensionPixelSize(R.styleable.RulerView_itemSpacing,
                dp2px(defaultItemSpacing));
        mMaxLineHeight = a.getDimensionPixelSize(R.styleable.RulerView_maxLineHeight,
                dp2px(ITEM_MAX_HEIGHT));
        mMinLineHeight = a.getDimensionPixelSize(R.styleable.RulerView_minLineHeight,
                dp2px(ITEM_MIN_HEIGHT));
        mMiddleLineHeight = a.getDimensionPixelSize(R.styleable.RulerView_middleLineHeight,
                dp2px(ITEM_MIDDLE_HEIGHT));

        mMinLineWidth = a.getDimensionPixelSize(R.styleable.RulerView_minLineWidth,
                dp2px(ITEM_MIN_WIDTH));
        mMiddleLineWidth = a.getDimensionPixelSize(R.styleable.RulerView_middleLineWidth,
                dp2px(ITEM_MIDDLE_WIDTH));
        mMaxLineWidth = a.getDimensionPixelSize(R.styleable.RulerView_maxLineWidth,
                dp2px(ITEM_MAX_WIDTH));
        mMaxLineColor = a.getColor(R.styleable.RulerView_maxLineColor, mMaxLineColor);
        mMiddleLineColor = a.getColor(R.styleable.RulerView_middleLineColor, mMiddleLineColor);
        mMinLineColor = a.getColor(R.styleable.RulerView_minLineColor, mMinLineColor);


        mIndcatorColor = a.getColor(R.styleable.RulerView_indicatorColor, mIndcatorColor);
        mIndcatorWidth = a.getDimensionPixelSize(R.styleable.RulerView_indicatorWidth,
                dp2px(INDICATOR_WIDTH));
        mIndcatorHeight = a.getDimensionPixelSize(R.styleable.RulerView_indicatorHeight,
                dp2px(INDICATOR_HEIGHT));
        mIndicatorType = a.getInt(R.styleable.RulerView_indicatorType, LINE);

        mScaleTextColor = a.getColor(R.styleable.RulerView_scaleTextColor, mScaleTextColor);
        mScaleTextSize = a.getDimensionPixelSize(R.styleable.RulerView_scaleTextSize,
                sp2px(scaleTextSize));
        mTextMarginTop = a.getDimensionPixelSize(R.styleable.RulerView_textMarginTop,
                dp2px(textMarginTop));

        mResultTextColor = a.getColor(R.styleable.RulerView_resultTextColor, mResultTextColor);
        mResultTextSize = a.getDimensionPixelSize(R.styleable.RulerView_resultTextSize,
                sp2px(resultTextSize));

        mUnitTextColor = a.getColor(R.styleable.RulerView_unitTextColor, mUnitTextColor);
        mUnitTextSize = a.getDimensionPixelSize(R.styleable.RulerView_unitTextSize,
                sp2px(unitTextSize));


        showUnit = a.getBoolean(R.styleable.RulerView_showUnit, true);
        unit = a.getString(R.styleable.RulerView_unit);
        if (TextUtils.isEmpty(unit)) {
            unit = "kg";
        }

        showResult = a.getBoolean(R.styleable.RulerView_showResult, true);
        showIndicator = a.getBoolean(R.styleable.RulerView_showIndicator, true);
        mAlphaEnable = a.getBoolean(R.styleable.RulerView_alphaEnable, true);

        mStrokeCap = a.getInt(R.styleable.RulerView_strokeCap, ROUND);

        init(context);
        a.recycle();
    }

    protected void init(Context context) {
        mScroller = new Scroller(context);
        mMinVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();

        mScaleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScaleTextPaint.setTextSize(mScaleTextSize);
        mScaleTextPaint.setColor(mScaleTextColor);
        mScaleTextPaint.setAntiAlias(true);
        mScaleTextHeight = getFontHeight(mScaleTextPaint);

        mResultTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mResultTextPaint.setTextSize(mResultTextSize);
        mResultTextPaint.setColor(mResultTextColor);
        mResultTextPaint.setAntiAlias(true);
        mResultTextPaint.setAlpha(255);
        mResultTextHeight = getFontHeight(mResultTextPaint);

        if (showUnit) {
            mUnitTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mUnitTextPaint.setTextSize(mUnitTextSize);
            mUnitTextPaint.setColor(mUnitTextColor);
            mUnitTextPaint.setAntiAlias(true);
            mUnitTextPaint.setAlpha(232);
            mUnitTextHeight = getFontHeight(mUnitTextPaint);
            // Log.i("TAG", "单位字的高度: " + mUnitTextHeight);
        }

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mStrokeCap == ROUND) {
            mLinePaint.setStrokeCap(
                    Paint.Cap.ROUND);
        } else if (mStrokeCap == BUTT) {
            mLinePaint.setStrokeCap(Paint.Cap.BUTT);
        } else if (mStrokeCap == SQUARE) {
            mLinePaint.setStrokeCap(Paint.Cap.SQUARE);
        }
        mLinePaint.setAntiAlias(true);

        if (showIndicator) {
            mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mIndicatorPaint.setStrokeWidth(mIndcatorWidth);
            mIndicatorPaint.setAntiAlias(true);
            mIndicatorPaint.setColor(mIndcatorColor);
            if (mStrokeCap == ROUND) {
                mIndicatorPaint.setStrokeCap(
                        Paint.Cap.ROUND);
            } else if (mStrokeCap == BUTT) {
                mIndicatorPaint.setStrokeCap(Paint.Cap.BUTT);
            } else if (mStrokeCap == SQUARE) {
                mIndicatorPaint.setStrokeCap(Paint.Cap.SQUARE);
            }

        }

        initViewParam(mValue, mMinValue, mMaxValue, mPerSpanValue);
    }

    public void initViewParam(float defaultValue, float minValue, float maxValue,
                              float perSpanValue) {
        this.mValue = defaultValue;
        this.mMaxValue = maxValue;
        this.mMinValue = minValue;
        this.mPerSpanValue = (int) (perSpanValue * mMinSpanCount);

        this.mTotalLine = (int) ((int) (mMaxValue * mMinSpanCount - mMinValue * mMinSpanCount) / mPerSpanValue + 1);
        mMaxOffset = -(mTotalLine - 1) * mItemSpacing;

        mOffset = (mMinValue - mValue) / mPerSpanValue * mItemSpacing * mMinSpanCount;
        invalidate();
        setVisibility(VISIBLE);
    }

    public void setTextFontFamily(Typeface typeface) {
        mScaleTextPaint.setTypeface(typeface);
    }

    public void setDefaultSelectedValue(float selectedValue) {
        this.mValue = selectedValue;
    }

    public void setMaxValue(Float maxValue) {
        this.mMaxValue = maxValue;
    }

    public void setMinValue(Float minValue) {
        this.mMinValue = minValue;
    }

    public void setMinSpanCount(Float minSpanCount) {
        this.mMinSpanCount = minSpanCount;
        this.mSpanCountMiddle = (int) (minSpanCount / 2);
    }

    public void setSpanValue(Float spanValue) {
        this.mPerSpanValue = spanValue * 10;
    }

    public void setItemSpacing(int itemSpacing) {
        this.mItemSpacing = dp2px(itemSpacing);

    }

    public void setMaxLineHeight(int maxLineHeight) {
        this.mMaxLineHeight = dp2px(maxLineHeight);

    }

    public void setMinLineHeight(int minLineHeight) {
        this.mMinLineHeight = dp2px(minLineHeight);

    }

    public void setMiddleLineHeight(int middleLineHeight) {
        this.mMiddleLineHeight = dp2px(middleLineHeight);

    }

    public void setMinLineWidth(int minLineWidth) {
        this.mMinLineWidth = dp2px(minLineWidth);

    }

    public void setMiddleLineWidth(int middleLineWidth) {
        this.mMiddleLineWidth = dp2px(middleLineWidth);

    }

    public void setMaxLineWidth(int maxLineWidth) {
        this.mMaxLineWidth = dp2px(maxLineWidth);

    }

    public void setIndicatorWidth(int mIndcatorWidth) {
        this.mIndcatorWidth = dp2px(mIndcatorWidth);

    }

    public void setIndicatorHeight(int mIndcatorHeight) {
        this.mIndcatorHeight = dp2px(mIndcatorHeight);

    }

    public void setIndicatorType(int mIndicatorType) {
        this.mIndicatorType = mIndicatorType;

    }

    public void setMaxLineColor(int mMaxLineColor) {
        this.mMaxLineColor = mMaxLineColor;
    }

    public void setMiddleLineColor(int mMiddleLineColor) {
        this.mMiddleLineColor = mMiddleLineColor;
    }

    public void setMinLineColor(int mMinLineColor) {
        this.mMinLineColor = mMinLineColor;
    }

    public void setIndicatorColor(int mIndcatorColor) {
        this.mIndcatorColor = mIndcatorColor;
    }

    public void setScaleTextColor(int mScaleTextColor) {
        this.mScaleTextColor = mScaleTextColor;
    }

    public void mResultTextColor(int mResultTextColor) {
        this.mResultTextColor = mResultTextColor;
    }

    public void setUnitTextColor(int mUnitTextColor) {
        this.mUnitTextColor = mUnitTextColor;
    }

    public void setScaleTextSize(int mScaleTextSize) {
        this.mScaleTextColor = sp2px(mScaleTextSize);
    }

    public void setTextMarginTop(int mTextMarginTop) {
        this.mTextMarginTop = sp2px(mTextMarginTop);
    }

    public void setResultTextSize(int mResultTextSize) {
        this.mResultTextSize = sp2px(mResultTextSize);
    }

    public void setUnitTextSize(int mUnitTextSize) {
        this.mScaleTextColor = sp2px(mUnitTextSize);
    }


    public void isShowUnit(boolean showUnit) {
        this.showUnit = showUnit;
    }

    public void isShowResult(boolean showResult) {
        this.showResult = showResult;
    }

    public void isShowIndicator(boolean showIndicator) {
        this.showIndicator = showIndicator;
    }

    public void setAlphaEnable(boolean mAlphaEnable) {
        this.mAlphaEnable = mAlphaEnable;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void mStrokeCap(int mStrokeCap) {
        this.mStrokeCap = mStrokeCap;
    }


    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public void setChooseValueChangeListener(OnChooseResulterListener listener) {
        mListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            mHeight = h;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawScaleAndNum(canvas);
        if (showIndicator)
            drawIndicator(canvas);

        if (showResult) {
            if (mPerSpanValue == 1) {
                drawResult(canvas, String.format(getPrecisionFormat(1), mValue));
            } else if (mPerSpanValue == 10) {
                drawResult(canvas, String.format(getPrecisionFormat(0), mValue));
            }
        }
    }


    private void drawIndicator(Canvas canvas) {
        int srcPointX = mWidth / 2;
        if (mIndicatorType == LINE) {
            canvas.drawLine(srcPointX, resultHeight, srcPointX, mIndcatorHeight + resultHeight,
                    mIndicatorPaint);
        } else if (mIndicatorType == TRIANGLE) {
            Path path = new Path();
            path.moveTo(srcPointX - mItemSpacing, resultHeight);
            path.lineTo(srcPointX + mItemSpacing, resultHeight);
            path.lineTo(srcPointX, mMinLineHeight + resultHeight);
            path.close();

            canvas.drawPath(path, mIndicatorPaint);
        }
    }

    /**
     * @param canvas
     */
    private void drawScaleAndNum(Canvas canvas) {
        float left, height;
        String value;
        int alpha;
        float scale;
        int srcPointX = mWidth / 2;

        for (int i = 0; i < mTotalLine; i++) {
            left = srcPointX + mOffset + i * mItemSpacing;

            if (left < 0 || left > mWidth) {
                continue;
            }


            if (i % mMinSpanCount == 0) {
                height = mMaxLineHeight;
                mLinePaint.setColor(mMaxLineColor);
                mLinePaint.setStrokeWidth(mMaxLineWidth);
            } else if (i % mSpanCountMiddle == 0) {
                height = mMiddleLineHeight;
                mLinePaint.setColor(mMiddleLineColor);
                mLinePaint.setStrokeWidth(mMiddleLineWidth);
            } else {
                height = mMinLineHeight;
                mLinePaint.setColor(mMinLineColor);
                mLinePaint.setStrokeWidth(mMinLineWidth);
            }

            if (mAlphaEnable) {
                // Log.i("TAG", "drawScaleAndNum: ================");
                scale = 1 - Math.abs(left - srcPointX) / srcPointX;
                // Log.i("TAG", "drawScaleAndNum: scale====" + scale);
                alpha = (int) (255 * scale * scale);
                // Log.i("TAG", "drawScaleAndNum: alpha===" + alpha);
                mLinePaint.setAlpha(alpha);
                mScaleTextPaint.setAlpha(alpha);
            }

            canvas.drawLine(left, resultHeight, left, height + resultHeight, mLinePaint);

            if (i % mMinSpanCount == 0) {
                value = String.valueOf((int) (mMinValue + i * mPerSpanValue / mMinSpanCount));
                canvas.drawText(value, left - mScaleTextPaint.measureText(value) / 2,
                        resultHeight + height + mTextMarginTop + mScaleTextHeight,
                        mScaleTextPaint);
            }
        }
    }

    /**
     * 画结果和单位
     *
     * @param canvas
     * @param resultText
     */
    private void drawResult(Canvas canvas, String resultText) {
        int srcPointX = mWidth / 2;

      /*  canvas.drawText(resultText, srcPointX - mResultTextPaint.measureText(resultText) / 2,
                Math.max(mResultTextHeight, mUnitTextHeight),
                mResultTextPaint);

*/

        /*if (showUnit) {
            canvas.drawText(unit, srcPointX + mResultTextPaint.measureText(resultText) / 2 + 10,
                    Math.max(mResultTextHeight, mUnitTextHeight), mUnitTextPaint);
        }*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int xPosition = (int) event.getX();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScroller.forceFinished(true);
                mLastX = xPosition;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMove = (mLastX - xPosition);
                changeMoveAndValue();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                countMoveEnd();
                countVelocityTracker();
                return false;
            // break;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }

    private void countVelocityTracker() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > mMinVelocity) {
            mScroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        }
    }

    private void countMoveEnd() {
        mOffset -= mMove;
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset >= 0) {
            mOffset = 0;
        }

        mLastX = 0;
        mMove = 0;

        mValue = mMinValue
                + Math.round(Math.abs(mOffset) * 1.0f / mItemSpacing) * mPerSpanValue / mMinSpanCount;
        mOffset = (mMinValue - mValue) * mMinSpanCount / mPerSpanValue
                * mItemSpacing;
        notifyValueChange();
        postInvalidate();
    }

    private void changeMoveAndValue() {
        mOffset -= mMove;
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
            mMove = 0;
            mScroller.forceFinished(true);
        } else if (mOffset >= 0) {
            mOffset = 0;
            mMove = 0;
            mScroller.forceFinished(true);
        }
        mValue = mMinValue
                + Math.round(Math.abs(mOffset) * 1.0f / mItemSpacing) * mPerSpanValue / mMinSpanCount;
        notifyValueChange();
        postInvalidate();
    }

    private void notifyValueChange() {
        if (null != mListener) {
            mListener.onChooseValueChange(mValue);
        }
    }

    public interface OnChooseResulterListener {
        void onChooseValueChange(float value);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mScroller.getCurrX() == mScroller.getFinalX()) { // over
                countMoveEnd();
            } else {
                int xPosition = mScroller.getCurrX();
                mMove = (mLastX - xPosition);
                changeMoveAndValue();
                mLastX = xPosition;
            }
        }
    }

    /**
     * dp&px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(float dpVal) {
        Resources r = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                r.getDisplayMetrics());
    }

    /**
     * sp&px
     *
     * @param spVal
     * @return
     */
    public static int sp2px(float spVal) {
        Resources r = Resources.getSystem();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal,
                r.getDisplayMetrics());
    }

    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }
}

