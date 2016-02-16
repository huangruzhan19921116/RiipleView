package com.ironman.rippleview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ruzhan on 16/2/10.
 */
public class RippleView extends View
    implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {
  /** 内圆画笔 */
  private Paint mInPaint = new Paint();
  /** 内圆边画笔 */
  private Paint mInStrokePaint = new Paint();
  /** 外圆画笔 */
  private Paint mOutPaint = new Paint();
  /** 外圆边画笔 */
  private Paint mOutStrokePaint = new Paint();
  /** 值动画 */
  private ValueAnimator valueRadiusAnimator;
  /** 圆心坐标 */
  private float mCx;
  private float mCy;
  /** 三个水波半径 */
  private float mChangeRadius01;
  private float mChangeRadius02;
  private float mChangeRadius03;

  private static final float PADDING = 0.5f;
  /** 水波动画次数 */
  private static final int RIPPLE_COUNT = 1;
  /** 圆边宽度 */
  private static final int STROKE_WIDTH = 1;
  /** 初始圆的半径 */
  private static int COMMON_RADIUS = 0;
  /** 设置初始圆半径值 */
  private static int COMMON_RADIUS_VALUE = 60;
  /** 每一个水波出现的间隔 */
  private static int RIPPLE_SPACE_VALUE = 50;
  /** 一次水波动画的时间 */
  private int mRippleTime = 3000;
  /** 外圆水波的透明度变化量 */
  private int mOutAlpha;
  /** 外圆水波边的透明度变化量 */
  private int mOutStrokeAlpha;
  /** 对外提供的监听 */
  private RippleStateListener mListener;
  /** 圆颜色 */
  private String CIRCLE_COLOR_IN = "#1AFFFFFF";
  /** 圆边的颜色 */
  private String CIRCLE_COLOR_OUT = "#80FFFFFF";

  public RippleView(Context context) {
    this(context, null);
  }

  public RippleView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  /** 重置三个水波半径 */
  private void initCircleRadius() {
    mChangeRadius01 = COMMON_RADIUS;
    mChangeRadius02 = COMMON_RADIUS;
    mChangeRadius03 = COMMON_RADIUS;
  }

  private void init() {
    COMMON_RADIUS = dip2px(getContext(), COMMON_RADIUS_VALUE);
    initCircleRadius();

    mInPaint.setColor(Color.parseColor(CIRCLE_COLOR_IN));
    mInPaint.setAntiAlias(true);
    mInPaint.setStyle(Paint.Style.FILL);

    mInStrokePaint.setColor(Color.parseColor(CIRCLE_COLOR_OUT));
    mInStrokePaint.setAntiAlias(true);
    mInStrokePaint.setStyle(Paint.Style.STROKE);
    mInStrokePaint.setStrokeWidth(dip2px(getContext(), STROKE_WIDTH));

    mOutPaint.setColor(Color.parseColor(CIRCLE_COLOR_IN));
    mOutPaint.setAntiAlias(true);
    mOutPaint.setStyle(Paint.Style.FILL);

    mOutStrokePaint.setColor(Color.parseColor(CIRCLE_COLOR_OUT));
    mOutStrokePaint.setAntiAlias(true);
    mOutStrokePaint.setStyle(Paint.Style.STROKE);
    mOutStrokePaint.setStrokeWidth(dip2px(getContext(), STROKE_WIDTH));

    mOutAlpha = mOutPaint.getAlpha();
    mOutStrokeAlpha = mOutStrokePaint.getAlpha();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    //画内圆和内圆边
    canvas.drawCircle(mCx, mCy, COMMON_RADIUS, mInPaint);
    canvas.drawCircle(mCx, mCy, COMMON_RADIUS, mInStrokePaint);

    //画外圆和外圆边
    canvas.drawCircle(mCx, mCy, mChangeRadius01, mOutPaint);
    canvas.drawCircle(mCx, mCy, mChangeRadius01, mOutStrokePaint);

    canvas.drawCircle(mCx, mCy, mChangeRadius02, mOutPaint);
    canvas.drawCircle(mCx, mCy, mChangeRadius02, mOutStrokePaint);

    canvas.drawCircle(mCx, mCy, mChangeRadius03, mOutPaint);
    canvas.drawCircle(mCx, mCy, mChangeRadius03, mOutStrokePaint);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    //获取圆心
    mCx = getMeasuredWidth() / 2;
    mCy = getMeasuredHeight() / 2;
    rippleStart();
  }

  /** 涟漪开启 */
  public void rippleStart() {
    if (valueRadiusAnimator == null) {
      valueRadiusAnimator =
          createValueAnimator(COMMON_RADIUS, (int) (mCx + COMMON_RADIUS), mRippleTime, RIPPLE_COUNT,
              this, this);
    } else {
      if (!valueRadiusAnimator.isRunning()) {
        valueRadiusAnimator.start();
      }
    }
  }

  /** 涟漪关闭 */
  public void rippleStop() {
    if (valueRadiusAnimator != null) {
      valueRadiusAnimator.end();
    }
  }

  @Override public void onAnimationStart(Animator animation) {
    if (mListener != null) {
      mListener.startState();
    }
  }

  @Override public void onAnimationEnd(Animator animation) {
    if (mListener != null) {
      mListener.stopState();
    }
    initCircleRadius();
  }

  @Override public void onAnimationCancel(Animator animation) {
    initCircleRadius();
  }

  @Override public void onAnimationRepeat(Animator animation) {
    initCircleRadius();
  }

  /** 更新涟漪半径 */
  @Override public void onAnimationUpdate(ValueAnimator animation) {
    mChangeRadius01 = (int) animation.getAnimatedValue();
    if (mChangeRadius01 > COMMON_RADIUS + RIPPLE_SPACE_VALUE) {
      mChangeRadius02 = mChangeRadius01 - RIPPLE_SPACE_VALUE;
    }
    if (mChangeRadius01 > COMMON_RADIUS + RIPPLE_SPACE_VALUE * 2) {
      mChangeRadius03 = mChangeRadius01 - RIPPLE_SPACE_VALUE * 2;
    }
    float percent = COMMON_RADIUS / mChangeRadius01;
    setOutCircleAlpha(percent);
    postInvalidate();
  }

  /** 更新涟漪透明度 */
  private void setOutCircleAlpha(float percent) {
    mOutPaint.setAlpha((int) (mOutAlpha * percent));
    mOutStrokePaint.setAlpha((int) (mOutStrokeAlpha * percent));
  }

  /** 涟漪开启和关闭回调 */
  public interface RippleStateListener {
    void startState();

    void stopState();
  }

  public void setRippleStateListener(RippleStateListener listener) {
    mListener = listener;
  }

  public ValueAnimator createValueAnimator(int startValue, int endValue, long duration,
      int repeatCount, ValueAnimator.AnimatorUpdateListener updateListener,
      Animator.AnimatorListener listener) {

    ValueAnimator valueAnimator = new ValueAnimator();
    valueAnimator.setIntValues(startValue, endValue);
    valueAnimator.setDuration(duration);
    valueAnimator.setRepeatCount(repeatCount);
    if (updateListener != null) {
      valueAnimator.addUpdateListener(updateListener);
    }
    if (listener != null) {
      valueAnimator.addListener(listener);
    }
    valueAnimator.start();
    return valueAnimator;
  }

  public int dip2px(Context context, float dpValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + PADDING);
  }

  public void setCircleInColor(String color) {
    CIRCLE_COLOR_IN = color;
  }

  public void setCircleOutColor(String color) {
    CIRCLE_COLOR_OUT = color;
  }

  public void setRippleTime(int time) {
    mRippleTime = time;
  }
}

