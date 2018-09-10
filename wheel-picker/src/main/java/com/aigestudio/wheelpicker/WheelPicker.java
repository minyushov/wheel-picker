package com.aigestudio.wheelpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/**
 * 滚轮选择器
 * <p>
 * WheelPicker
 *
 * @author AigeStudio 2015-12-12
 * @author AigeStudio 2016-06-17
 * 更新项目结构
 * <p>
 * New project structure
 * @version 1.1.0
 */
public class WheelPicker extends View implements IDebug, IWheelPicker, Runnable {
	/**
	 * 滚动状态标识值
	 *
	 * @see OnWheelChangeListener#onWheelScrollStateChanged(int)
	 */
	public static final int SCROLL_STATE_IDLE = 0, SCROLL_STATE_DRAGGING = 1,
			SCROLL_STATE_SCROLLING = 2;

	/**
	 * 数据项对齐方式标识值
	 *
	 * @see #setItemAlign(int)
	 */
	public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

	private static final int QUICK_SCROLL_VELOCITY = 10000;
	private static final int TOUCH_SLOP = 4;

	private static final String TAG = WheelPicker.class.getSimpleName();

	private final Handler mHandler = new Handler();

	private Paint mPaint;
	private Paint mSelectedPaint;
	private Scroller mScroller;
	private VelocityTracker mTracker;
	/**
	 * Determines whether the current scrolling animation is triggered by touchEvent or setSelectedItemPosition.
	 * User added eventListeners will only be fired after touchEvents.
	 */
	private boolean isTouchTriggered;

	private OnWheelChangeListener mOnWheelChangeListener;

	private Rect mRectDrawn;
	private Rect mRectCurrentItem;

	private WheelAdapter mAdapter;

	/**
	 * 最宽的文本
	 *
	 * @see #setMaximumWidthText(String)
	 */
	private String mMaxWidthText;

	/**
	 * 滚轮选择器中可见的数据项数量和滚轮选择器将会绘制的数据项数量
	 *
	 * @see #setVisibleItemCount(int)
	 */
	private int mVisibleItemCount, mDrawnItemCount;

	/**
	 * 滚轮选择器将会绘制的Item数量的一半
	 */
	private int mHalfDrawnItemCount;

	/**
	 * 单个文本最大宽高
	 */
	private int mTextMaxWidth, mTextMaxHeight;

	/**
	 * 数据项文本颜色以及被选中的数据项文本颜色
	 *
	 * @see #setItemTextColor(int)
	 * @see #setSelectedItemTextColor(int)
	 */
	private int mItemTextColor, mSelectedItemTextColor;
	private boolean mSelectedItemTextColorEnabled;

	/**
	 * 数据项文本尺寸
	 *
	 * @see #setItemTextSize(int)
	 */
	private int mItemTextSize;

	/**
	 * 数据项之间间距
	 *
	 * @see #setItemSpace(int)
	 */
	private int mItemSpace;

	private boolean mItemIconEnabled;

	private int mItemIconSize;

	/**
	 * 数据项对齐方式
	 *
	 * @see #setItemAlign(int)
	 */
	private int mItemAlign;

	/**
	 * 滚轮选择器单个数据项高度以及单个数据项一半的高度
	 */
	private int mItemHeight, mHalfItemHeight;

	/**
	 * 当前被选中的数据项所显示的数据在数据源中的位置
	 *
	 * @see #setSelectedItemPosition(int)
	 */
	private int mSelectedItemPosition;

	/**
	 * 当前被选中的数据项所显示的数据在数据源中的位置
	 *
	 * @see #getCurrentItemPosition()
	 */
	private int mCurrentItemPosition;

	/**
	 * 滚轮滑动时可以滑动到的最小/最大的Y坐标
	 */
	private int mMinFlingY, mMaxFlingY;

	/**
	 * 滚轮滑动时的最小/最大速度
	 */
	private int mMinimumVelocity = 50, mMaximumVelocity = 8000;

	/**
	 * 滚轮选择器中心坐标
	 */
	private int mWheelCenterX, mWheelCenterY;

	/**
	 * 滚轮选择器绘制中心坐标
	 */
	private int mDrawnCenterX, mDrawnCenterY;

	/**
	 * 滚轮选择器视图区域在Y轴方向上的偏移值
	 */
	private int mScrollOffsetY;

	/**
	 * 滚轮选择器中最宽或最高的文本在数据源中的位置
	 */
	private int mTextMaxWidthPosition;

	/**
	 * 用户手指上一次触摸事件发生时事件Y坐标
	 */
	private int mLastPointY;

	/**
	 * 手指触摸屏幕时事件点的Y坐标
	 */
	private int mDownPointY;

	/**
	 * 滚轮选择器的每一个数据项文本是否拥有相同的宽度
	 *
	 * @see #setSameWidth(boolean)
	 */
	private boolean hasSameWidth;

	/**
	 * 是否显示空气感效果
	 *
	 * @see #setAtmospheric(boolean)
	 */
	private boolean hasAtmospheric;

	/**
	 * 是否为点击模式
	 */
	private boolean isClick;

	/**
	 * 是否为强制结束滑动
	 */
	private boolean isForceFinishScroll;
	private boolean isDebug;

	public WheelPicker(Context context) {
		this(context, null);
	}

	public WheelPicker(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelPicker);
		mItemTextSize = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_text_size,
				getResources().getDimensionPixelSize(R.dimen.default_text_size));
		mItemHeight = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_height,
				getResources().getDimensionPixelSize(R.dimen.default_item_height));
		mSelectedItemPosition = a.getInt(R.styleable.WheelPicker_wheel_selected_item_position, 0);
		hasSameWidth = a.getBoolean(R.styleable.WheelPicker_wheel_same_width, false);
		mTextMaxWidthPosition =
				a.getInt(R.styleable.WheelPicker_wheel_maximum_width_text_position, -1);
		mMaxWidthText = a.getString(R.styleable.WheelPicker_wheel_maximum_width_text);
		mSelectedItemTextColorEnabled = a.hasValue(R.styleable.WheelPicker_wheel_selected_item_text_color);
		mSelectedItemTextColor = a.getColor
				(R.styleable.WheelPicker_wheel_selected_item_text_color, -1);
		mItemTextColor = a.getColor(R.styleable.WheelPicker_wheel_item_text_color, 0xFF888888);
		mItemSpace = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_space, 0);
		mItemIconEnabled = a.getBoolean(R.styleable.WheelPicker_wheel_item_icon_enabled, false);
		hasAtmospheric = a.getBoolean(R.styleable.WheelPicker_wheel_atmospheric, false);
		mItemAlign = a.getInt(R.styleable.WheelPicker_wheel_item_align, ALIGN_CENTER);
		a.recycle();

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setTextSize(mItemTextSize);

		mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mSelectedPaint.setTextSize(mItemTextSize);

		// 更新文本对齐方式
		// Update alignment of text
		updateItemTextAlign();

		// 计算文本尺寸
		// Correct sizes of text
		computeTextSize();

		mHalfItemHeight = mItemHeight / 2;

		mScroller = new Scroller(getContext());

		ViewConfiguration conf = ViewConfiguration.get(getContext());
		mMinimumVelocity = conf.getScaledMinimumFlingVelocity();
		mMaximumVelocity = conf.getScaledMaximumFlingVelocity();

		mRectDrawn = new Rect();

		mRectCurrentItem = new Rect();

		mPaint.setColorFilter(new PorterDuffColorFilter(mItemTextColor, PorterDuff.Mode.SRC_IN));
		mPaint.setStyle(Paint.Style.FILL);

		mSelectedPaint.setColorFilter(new PorterDuffColorFilter(mSelectedItemTextColor, PorterDuff.Mode.SRC_IN));
		mSelectedPaint.setStyle(Paint.Style.FILL);
	}

	private void updateVisibleItemCount() {
		if (mVisibleItemCount < 2) {
			throw new ArithmeticException("Wheel's visible item count can not be less than 2!");
		}

		// 确保滚轮选择器可见数据项数量为奇数
		// Be sure count of visible item is odd number
		if (mVisibleItemCount % 2 == 0) {
			mVisibleItemCount += 1;
		}
		mDrawnItemCount = mVisibleItemCount + 2;
		mHalfDrawnItemCount = mDrawnItemCount / 2;
	}

	private void computeTextSize() {
		mTextMaxWidth = mTextMaxHeight = 0;
		if (mAdapter != null && mAdapter.getSize() != 0) {
			if (hasSameWidth) {
				mTextMaxWidth = (int) mPaint.measureText(String.valueOf(mAdapter.getData().get(0)));
			} else if (isPosInRang(mTextMaxWidthPosition)) {
				mTextMaxWidth = (int) mPaint.measureText
						(String.valueOf(mAdapter.getData().get(mTextMaxWidthPosition)));
			} else if (!TextUtils.isEmpty(mMaxWidthText)) {
				mTextMaxWidth = (int) mPaint.measureText(mMaxWidthText);
			} else {
				for (Object obj : mAdapter.getData()) {
					String text = String.valueOf(obj);
					int width = (int) mPaint.measureText(text);
					mTextMaxWidth = Math.max(mTextMaxWidth, width);
				}
			}
		}

		Paint.FontMetrics metrics = mPaint.getFontMetrics();
		mTextMaxHeight = (int) (metrics.bottom - metrics.top);

		if (mItemIconEnabled) {
			mItemIconSize = mTextMaxHeight;
		}
	}

	private void updateItemTextAlign() {
		switch (mItemAlign) {
			case ALIGN_LEFT:
				mPaint.setTextAlign(Paint.Align.LEFT);
				mSelectedPaint.setTextAlign(Paint.Align.LEFT);
				break;
			case ALIGN_RIGHT:
				mPaint.setTextAlign(Paint.Align.RIGHT);
				mSelectedPaint.setTextAlign(Paint.Align.RIGHT);
				break;
			default:
				mPaint.setTextAlign(Paint.Align.CENTER);
				mSelectedPaint.setTextAlign(Paint.Align.CENTER);
				break;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		// 计算原始内容尺寸
		// Correct sizes of original content
		int resultWidth = mTextMaxWidth;
		int resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1);

		if (mItemIconEnabled) {
			resultWidth += mItemIconSize;
		}
		if (isDebug) {
			Log.i(TAG, "Wheel's content size is (" + resultWidth + ":" + resultHeight + ")");
		}

		// 考虑内边距对尺寸的影响
		// Consideration padding influence the view sizes
		resultWidth += getPaddingLeft() + getPaddingRight();
		resultHeight += getPaddingTop() + getPaddingBottom();
		if (isDebug) {
			Log.i(TAG, "Wheel's size is (" + resultWidth + ":" + resultHeight + ")");
		}

		// 考虑父容器对尺寸的影响
		// Consideration sizes of parent can influence the view sizes
		resultWidth = measureSize(modeWidth, sizeWidth, resultWidth);
		resultHeight = measureSize(modeHeight, sizeHeight, resultHeight);

		setMeasuredDimension(resultWidth, resultHeight);
	}

	private int measureSize(int mode, int sizeExpect, int sizeActual) {
		int realSize;
		if (mode == MeasureSpec.EXACTLY) {
			realSize = sizeExpect;
		} else {
			realSize = sizeActual;
			if (mode == MeasureSpec.AT_MOST) {
				realSize = Math.min(realSize, sizeExpect);
			}
		}
		return realSize;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldW, int oldH) {
		// 设置内容区域
		// Set content region
		mRectDrawn.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
				getHeight() - getPaddingBottom());
		if (isDebug) {
			Log.i(TAG, "Wheel's drawn rect size is (" + mRectDrawn.width() + ":" +
					mRectDrawn.height() + ") and location is (" + mRectDrawn.left + ":" +
					mRectDrawn.top + ")");
		}

		// 获取内容区域中心坐标
		// Get the center coordinates of content region
		mWheelCenterX = mRectDrawn.centerX();
		mWheelCenterY = mRectDrawn.centerY();

		// 计算数据项绘制中心
		// Correct item drawn center
		computeDrawnCenter();

		mVisibleItemCount = mRectDrawn.height() / mItemHeight;
		updateVisibleItemCount();

		// 初始化滑动最大坐标
		// Initialize fling max Y-coordinates
		computeFlingLimitY();

		// 计算当前选中的数据项区域
		// Correct region of current select item
		computeCurrentItemRect();
	}

	private void computeDrawnCenter() {
		switch (mItemAlign) {
			case ALIGN_LEFT:
				mDrawnCenterX = mRectDrawn.left;
				break;
			case ALIGN_RIGHT:
				mDrawnCenterX = mRectDrawn.right;
				break;
			default:
				mDrawnCenterX = mWheelCenterX;
				break;
		}
		mDrawnCenterY = (int) (mWheelCenterY - ((mPaint.ascent() + mPaint.descent()) / 2));
	}

	private void computeFlingLimitY() {
		int currentItemOffset = mSelectedItemPosition * mItemHeight;
		mMinFlingY = -mItemHeight * (mAdapter.getSize() - 1) + currentItemOffset;
		mMaxFlingY = currentItemOffset;
	}

	private void computeCurrentItemRect() {
		if (!mSelectedItemTextColorEnabled) {
			return;
		}
		mRectCurrentItem.set(mRectDrawn.left, mWheelCenterY - mHalfItemHeight, mRectDrawn.right,
				mWheelCenterY + mHalfItemHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		if (null != mOnWheelChangeListener) {
//			mOnWheelChangeListener.onWheelScrolled(mScrollOffsetY);
//		}
		int drawnDataStartPos = -mScrollOffsetY / mItemHeight - mHalfDrawnItemCount;
		for (int drawnDataPos = drawnDataStartPos + mSelectedItemPosition,
			 drawnOffsetPos = -mHalfDrawnItemCount;
			 drawnDataPos < drawnDataStartPos + mSelectedItemPosition + mDrawnItemCount;
			 drawnDataPos++, drawnOffsetPos++) {

			String data = "";
			Bitmap icon = null;

			if (isPosInRang(drawnDataPos)) {
				data = String.valueOf(mAdapter.getData().get(drawnDataPos));
				if (mItemIconEnabled) {
					icon = ((WheelItem) mAdapter.getData().get(drawnDataPos)).getIcon();
				}
			}

			int mDrawnItemCenterY = mDrawnCenterY + (drawnOffsetPos * mItemHeight) +
					mScrollOffsetY % mItemHeight;

			if (hasAtmospheric) {
				int alpha = (int) ((mDrawnCenterY - Math.abs(mDrawnCenterY - mDrawnItemCenterY)) *
						1.0F / mDrawnCenterY * 255);
				alpha = alpha < 0 ? 0 : alpha;
				mPaint.setAlpha(alpha);
			}

			int iconLeft = 0;
			int iconTop = 0;

			if (icon != null) {
				iconLeft = mDrawnCenterX - mTextMaxWidth;
				iconTop = mWheelCenterY + (drawnOffsetPos * mItemHeight) + mScrollOffsetY % mItemHeight - icon.getHeight() / 2;
			}

			// 判断是否需要为当前数据项绘制不同颜色
			// Judges need to draw different color for current item or not
			if (mSelectedItemTextColorEnabled) {
				canvas.save();
				canvas.clipRect(mRectCurrentItem, Region.Op.DIFFERENCE);

				if (icon != null) {
					canvas.drawBitmap(icon, iconLeft, iconTop, mPaint);
				}

				if (mItemIconEnabled) {
					canvas.translate(mItemIconSize, 0);
				}

				canvas.drawText(data, mDrawnCenterX, mDrawnItemCenterY, mPaint);
				canvas.restore();

				canvas.save();
				canvas.clipRect(mRectCurrentItem);

				if (icon != null) {
					canvas.drawBitmap(icon, iconLeft, iconTop, mSelectedPaint);
				}

				if (mItemIconEnabled) {
					canvas.translate(mItemIconSize, 0);
				}

				canvas.drawText(data, mDrawnCenterX, mDrawnItemCenterY, mSelectedPaint);
				canvas.restore();
			} else {
				canvas.save();
				canvas.clipRect(mRectDrawn);

				if (icon != null) {
					canvas.drawBitmap(icon, iconLeft, iconTop, mPaint);
				}

				if (mItemIconEnabled) {
					canvas.translate(mItemIconSize, 0);
				}

				canvas.drawText(data, mDrawnCenterX, mDrawnItemCenterY, mPaint);
				canvas.restore();
			}

			if (isDebug) {
				canvas.save();
				canvas.clipRect(mRectDrawn);
				mPaint.setColor(0xFFEE3333);
				int lineCenterY = mWheelCenterY + (drawnOffsetPos * mItemHeight);
				canvas.drawLine(mRectDrawn.left, lineCenterY, mRectDrawn.right, lineCenterY,
						mPaint);
				mPaint.setColor(0xFF3333EE);
				mPaint.setStyle(Paint.Style.STROKE);
				int top = lineCenterY - mHalfItemHeight;
				canvas.drawRect(mRectDrawn.left, top, mRectDrawn.right, top + mItemHeight, mPaint);
				canvas.restore();
			}
		}

		if (isDebug) {
			mPaint.setColor(0x4433EE33);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawRect(0, 0, getPaddingLeft(), getHeight(), mPaint);
			canvas.drawRect(0, 0, getWidth(), getPaddingTop(), mPaint);
			canvas.drawRect(getWidth() - getPaddingRight(), 0, getWidth(), getHeight(), mPaint);
			canvas.drawRect(0, getHeight() - getPaddingBottom(), getWidth(), getHeight(), mPaint);
		}
	}

	private boolean isPosInRang(int position) {
		return position >= 0 && position < mAdapter.getSize();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isTouchTriggered = true;
				if (null != getParent()) {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
				if (null == mTracker) {
					mTracker = VelocityTracker.obtain();
				} else {
					mTracker.clear();
				}
				mTracker.addMovement(event);
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
					isForceFinishScroll = true;
				}
				mDownPointY = mLastPointY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (Math.abs(mDownPointY - event.getY()) < TOUCH_SLOP) {
					isClick = true;
					break;
				}
				isClick = false;
				mTracker.addMovement(event);
				if (null != mOnWheelChangeListener) {
					mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
				}

				// 滚动内容
				// Scroll WheelPicker's content
				float move = event.getY() - mLastPointY;
				if (Math.abs(move) < 1) {
					break;
				}
				mScrollOffsetY += move;
				mLastPointY = (int) event.getY();
				invalidate();

				break;
			case MotionEvent.ACTION_UP:
				if (null != getParent()) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
				if (isClick && !isForceFinishScroll) {
					break;
				}
				mTracker.addMovement(event);

				mTracker.computeCurrentVelocity(1000, mMaximumVelocity);

				// 根据速度判断是该滚动还是滑动
				// Judges the WheelPicker is scroll or fling base on current velocity
				isForceFinishScroll = false;
				int velocity = (int) mTracker.getYVelocity();

				if (Math.abs(velocity) > mMinimumVelocity && !(mScrollOffsetY > mMaxFlingY) && !(mScrollOffsetY < mMinFlingY)) {
					if (Math.abs(velocity) > QUICK_SCROLL_VELOCITY) {
						int dy = 0;

						if (velocity > 0) {
							dy = -mScrollOffsetY;
						}
						if (velocity < 0) {
							dy = -(Math.abs(mMinFlingY) - Math.abs(mScrollOffsetY));
						}

						mScroller.startScroll(0, mScrollOffsetY, 0, dy, 500);
					} else {
						mScroller.fling(0, mScrollOffsetY, 0, velocity, 0, 0, mMinFlingY, mMaxFlingY);
						mScroller.setFinalY(mScroller.getFinalY() +
								computeDistanceToEndPoint(mScroller.getFinalY() % mItemHeight));
					}
				} else {
					mScroller.startScroll(0, mScrollOffsetY, 0,
							computeDistanceToEndPoint(mScrollOffsetY % mItemHeight));
				}

				// 校正坐标
				// Correct coordinates
				if (mScroller.getFinalY() > mMaxFlingY) {
					mScroller.setFinalY(mMaxFlingY);
				} else if (mScroller.getFinalY() < mMinFlingY) {
					mScroller.setFinalY(mMinFlingY);
				}

				mHandler.post(this);
				if (null != mTracker) {
					mTracker.recycle();
					mTracker = null;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				if (null != getParent()) {
					getParent().requestDisallowInterceptTouchEvent(false);
				}
				if (null != mTracker) {
					mTracker.recycle();
					mTracker = null;
				}
				break;
		}
		return true;
	}

	private int computeDistanceToEndPoint(int remainder) {
		if (Math.abs(remainder) > mHalfItemHeight) {
			if (mScrollOffsetY < 0) {
				return -mItemHeight - remainder;
			} else {
				return mItemHeight - remainder;
			}
		} else {
			return -remainder;
		}
	}

	@Override
	public void run() {
		if (null == mAdapter || mAdapter.getSize() == 0) {
			return;
		}

		if (mScroller.isFinished() && !isForceFinishScroll) {
			if (mItemHeight == 0) {
				return;
			}

			int position = (-mScrollOffsetY / mItemHeight + mSelectedItemPosition) % mAdapter.getSize();
			position = position < 0 ? position + mAdapter.getSize() : position;

			if (isDebug) {
				Log.i(TAG, position + ":" + mAdapter.getData().get(position) + ":" + mScrollOffsetY);
			}
			mCurrentItemPosition = position;
			if (mAdapter != null) {
				mAdapter.onItemSelected(position);
			}
			if (null != mOnWheelChangeListener && isTouchTriggered) {
				mOnWheelChangeListener.onWheelSelected(position);
				mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_IDLE);
			}
		}
		if (mScroller.computeScrollOffset()) {
			if (null != mOnWheelChangeListener) {
				mOnWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
			}
			mScrollOffsetY = mScroller.getCurrY();
			postInvalidate();
			mHandler.postDelayed(this, 16);
		}
	}

	@Override
	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	@Override
	public int getVisibleItemCount() {
		return mVisibleItemCount;
	}

	@Override
	public void setVisibleItemCount(int count) {
		mVisibleItemCount = count;
		updateVisibleItemCount();
		requestLayout();
	}

	int getSelectedItemPosition() {
		return mSelectedItemPosition;
	}

	void setSelectedItemPosition(int position) {
		setSelectedItemPosition(position, true);
	}

	void setSelectedItemPosition(int position, final boolean animated) {
		isTouchTriggered = false;
		if (animated && mScroller.isFinished()) { // We go non-animated regardless of "animated" parameter if scroller is in motion
			int itemDifference = position - mCurrentItemPosition;
			if (itemDifference == 0) {
				return;
			}

			mScroller.startScroll(0, mScroller.getCurrY(), 0, (-itemDifference) * mItemHeight);
			mHandler.post(this);
		} else {
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			position = Math.min(position, mAdapter.getSize() - 1);
			position = Math.max(position, 0);
			mSelectedItemPosition = position;
			mCurrentItemPosition = position;
			mScrollOffsetY = 0;
			computeFlingLimitY();
			requestLayout();
			invalidate();
		}
	}

	int getCurrentItemPosition() {
		return mCurrentItemPosition;
	}

	void setAdapter(WheelAdapter adapter) {
		if (null == adapter) {
			throw new NullPointerException("WheelAdapter can not be null!");
		}
		mAdapter = adapter;

		// 重置位置
		if (mSelectedItemPosition > mAdapter.getSize() - 1 || mCurrentItemPosition > mAdapter.getSize() - 1) {
			mSelectedItemPosition = mCurrentItemPosition = mAdapter.getSize() - 1;
		} else {
			mSelectedItemPosition = mCurrentItemPosition;
		}
		mScrollOffsetY = 0;
		computeTextSize();
		computeFlingLimitY();
		requestLayout();
		invalidate();
	}

	public void setSameWidth(boolean hasSameWidth) {
		this.hasSameWidth = hasSameWidth;
		computeTextSize();
		requestLayout();
		invalidate();
	}

	@Override
	public boolean hasSameWidth() {
		return hasSameWidth;
	}

	@Override
	public void setOnWheelChangeListener(OnWheelChangeListener listener) {
		mOnWheelChangeListener = listener;
	}

	@Override
	public String getMaximumWidthText() {
		return mMaxWidthText;
	}

	@Override
	public void setMaximumWidthText(String text) {
		if (null == text) {
			throw new NullPointerException("Maximum width text can not be null!");
		}
		mMaxWidthText = text;
		computeTextSize();
		requestLayout();
		invalidate();
	}

	@Override
	public int getMaximumWidthTextPosition() {
		return mTextMaxWidthPosition;
	}

	@Override
	public void setMaximumWidthTextPosition(int position) {
		if (!isPosInRang(position)) {
			throw new ArrayIndexOutOfBoundsException("Maximum width text Position must in [0, " +
					mAdapter.getSize() + "), but current is " + position);
		}
		mTextMaxWidthPosition = position;
		computeTextSize();
		requestLayout();
		invalidate();
	}

	@Override
	public int getSelectedItemTextColor() {
		return mSelectedItemTextColor;
	}

	@Override
	public void setSelectedItemTextColor(int color) {
		mSelectedItemTextColor = color;
		computeCurrentItemRect();
		invalidate();
	}

	@Override
	public int getItemTextColor() {
		return mItemTextColor;
	}

	@Override
	public void setItemTextColor(int color) {
		mItemTextColor = color;
		invalidate();
	}

	@Override
	public int getItemTextSize() {
		return mItemTextSize;
	}

	@Override
	public void setItemTextSize(int size) {
		mItemTextSize = size;
		mPaint.setTextSize(mItemTextSize);
		mSelectedPaint.setTextSize(mItemTextSize);
		computeTextSize();
		requestLayout();
		invalidate();
	}

	@Override
	public int getItemSpace() {
		return mItemSpace;
	}

	@Override
	public void setItemSpace(int space) {
		mItemSpace = space;
		requestLayout();
		invalidate();
	}

	@Override
	public void setAtmospheric(boolean hasAtmospheric) {
		this.hasAtmospheric = hasAtmospheric;
		invalidate();
	}

	@Override
	public boolean hasAtmospheric() {
		return hasAtmospheric;
	}

	@Override
	public int getItemAlign() {
		return mItemAlign;
	}

	@Override
	public void setItemAlign(int align) {
		mItemAlign = align;
		updateItemTextAlign();
		computeDrawnCenter();
		invalidate();
	}

	@Override
	public Typeface getTypeface() {
		if (null != mPaint) {
			return mPaint.getTypeface();
		}
		return null;
	}

	@Override
	public void setTypeface(Typeface tf) {
		if (null != mPaint) {
			mPaint.setTypeface(tf);
		}
		if (mSelectedPaint != null) {
			mSelectedPaint.setTypeface(tf);
		}
		computeTextSize();
		requestLayout();
		invalidate();
	}

	/**
	 * 滚轮选择器滚动时监听接口
	 *
	 * @author AigeStudio 2016-06-17
	 * 新项目结构
	 * <p>
	 * New project structure
	 * @since 2016-06-17
	 */
	public interface OnWheelChangeListener {
		/**
		 * 当滚轮选择器滚动时回调该方法
		 * 滚轮选择器滚动时会将当前滚动位置与滚轮初始位置之间的偏移距离返回，该偏移距离有正负之分，正值表示
		 * 滚轮正在往上滚动，负值则表示滚轮正在往下滚动
		 * <p>
		 * Invoke when WheelPicker scroll stopped
		 * WheelPicker will return a distance offset which between current scroll position and
		 * initial position, this offset is a positive or a negative, positive means WheelPicker is
		 * scrolling from bottom to top, negative means WheelPicker is scrolling from top to bottom
		 *
		 * @param offset
		 * 		当前滚轮滚动距离上一次滚轮滚动停止后偏移的距离
		 * 		<p>
		 * 		Distance offset which between current scroll position and initial position
		 */
		void onWheelScrolled(int offset);

		/**
		 * 当滚轮选择器停止后回调该方法
		 * 滚轮选择器停止后会回调该方法并将当前选中的数据项在数据列表中的位置返回
		 * <p>
		 * Invoke when WheelPicker scroll stopped
		 * This method will be called when WheelPicker stop and return current selected item data's
		 * position in list
		 *
		 * @param position
		 * 		当前选中的数据项在数据列表中的位置
		 * 		<p>
		 * 		Current selected item data's position in list
		 */
		void onWheelSelected(int position);

		/**
		 * 当滚轮选择器滚动状态改变时回调该方法
		 * 滚动选择器的状态总是会在静止、拖动和滑动三者之间切换，当状态改变时回调该方法
		 * <p>
		 * Invoke when WheelPicker's scroll state changed
		 * The state of WheelPicker always between idle, dragging, and scrolling, this method will
		 * be called when they switch
		 *
		 * @param state
		 * 		滚轮选择器滚动状态，其值仅可能为下列之一
		 * 		{@link WheelPicker#SCROLL_STATE_IDLE}
		 * 		表示滚动选择器处于静止状态
		 * 		{@link WheelPicker#SCROLL_STATE_DRAGGING}
		 * 		表示滚动选择器处于拖动状态
		 * 		{@link WheelPicker#SCROLL_STATE_SCROLLING}
		 * 		表示滚动选择器处于滑动状态
		 * 		<p>
		 * 		State of WheelPicker, only one of the following
		 * 		{@link WheelPicker#SCROLL_STATE_IDLE}
		 * 		Express WheelPicker in state of idle
		 * 		{@link WheelPicker#SCROLL_STATE_DRAGGING}
		 * 		Express WheelPicker in state of dragging
		 * 		{@link WheelPicker#SCROLL_STATE_SCROLLING}
		 * 		Express WheelPicker in state of scrolling
		 */
		void onWheelScrollStateChanged(int state);
	}
}
