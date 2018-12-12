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

public class WheelPicker extends View implements Runnable {
	public static final int SCROLL_STATE_IDLE = 0;
	public static final int SCROLL_STATE_DRAGGING = 1;
	public static final int SCROLL_STATE_SCROLLING = 2;

	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_RIGHT = 2;

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

	private final Rect mRectDrawn = new Rect();
	private final Rect mRectCurrentItem = new Rect();

	private final Rect rectContent = new Rect();
	private final Rect rectIcon = new Rect();
	private final Rect rectText = new Rect();

	private WheelAdapter mAdapter;

	private String mMaxWidthText;

	private int mVisibleItemCount, mDrawnItemCount;

	private int mHalfDrawnItemCount;

	private int mTextMaxWidth, mTextMaxHeight;

	private int mItemTextColor, mSelectedItemTextColor;
	private boolean mSelectedItemTextColorEnabled;

	private int mItemTextSize;

	private int mItemSpace;

	private boolean mItemIconEnabled;

	private final int mItemIconSize;
	private final int mItemIconPadding;

	private int mItemAlign;

	private int mItemHeight, mHalfItemHeight;

	private int mSelectedItemPosition;

	private int mCurrentItemPosition;

	private int mMinFlingY, mMaxFlingY;

	private int mMinimumVelocity = 50, mMaximumVelocity = 8000;

	private int mWheelCenterX, mWheelCenterY;

	private int mDrawnCenterX, mDrawnCenterY;

	private int mScrollOffsetY;

	private int mTextMaxWidthPosition;

	private int mLastPointY;

	private int mDownPointY;

	private boolean hasSameWidth;

	private boolean hasAtmospheric;

	private boolean isClick;

	private boolean isForceFinishScroll;
	private boolean isDebug;

	public WheelPicker(Context context) {
		this(context, null);
	}

	public WheelPicker(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelPicker);
		mItemTextSize = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_text_size, getResources().getDimensionPixelSize(R.dimen.default_text_size));
		mItemHeight = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_height, getResources().getDimensionPixelSize(R.dimen.default_item_height));
		mSelectedItemPosition = a.getInt(R.styleable.WheelPicker_wheel_selected_item_position, 0);
		hasSameWidth = a.getBoolean(R.styleable.WheelPicker_wheel_same_width, false);
		mTextMaxWidthPosition = a.getInt(R.styleable.WheelPicker_wheel_maximum_width_text_position, -1);
		mMaxWidthText = a.getString(R.styleable.WheelPicker_wheel_maximum_width_text);
		mSelectedItemTextColorEnabled = a.hasValue(R.styleable.WheelPicker_wheel_selected_item_text_color);
		mSelectedItemTextColor = a.getColor(R.styleable.WheelPicker_wheel_selected_item_text_color, -1);
		mItemTextColor = a.getColor(R.styleable.WheelPicker_wheel_item_text_color, 0xFF888888);
		mItemSpace = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_space, 0);
		mItemIconEnabled = a.getBoolean(R.styleable.WheelPicker_wheel_item_icon_enabled, false);
		if (mItemIconEnabled) {
			mItemIconSize = a.getDimensionPixelSize(R.styleable.WheelPicker_wheel_item_icon_size, 0);
			mItemIconPadding = a.getDimensionPixelOffset(R.styleable.WheelPicker_wheel_item_icon_padding, 0);
		} else {
			mItemIconSize = 0;
			mItemIconPadding = 0;
		}
		hasAtmospheric = a.getBoolean(R.styleable.WheelPicker_wheel_atmospheric, false);
		mItemAlign = a.getInt(R.styleable.WheelPicker_wheel_item_align, ALIGN_CENTER);
		a.recycle();

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mPaint.setTextSize(mItemTextSize);

		mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mSelectedPaint.setTextSize(mItemTextSize);

		// Update alignment of text
		updateItemTextAlign();

		// Correct sizes of text
		computeTextSize();

		mHalfItemHeight = mItemHeight / 2;

		mScroller = new Scroller(getContext());

		ViewConfiguration conf = ViewConfiguration.get(getContext());
		mMinimumVelocity = conf.getScaledMinimumFlingVelocity();
		mMaximumVelocity = conf.getScaledMaximumFlingVelocity();

		mPaint.setColorFilter(new PorterDuffColorFilter(mItemTextColor, PorterDuff.Mode.SRC_IN));
		mPaint.setStyle(Paint.Style.FILL);

		mSelectedPaint.setColorFilter(new PorterDuffColorFilter(mSelectedItemTextColor, PorterDuff.Mode.SRC_IN));
		mSelectedPaint.setStyle(Paint.Style.FILL);
	}

	private void updateVisibleItemCount() {
		if (mVisibleItemCount < 2) {
			throw new ArithmeticException("Wheel's visible item count can not be less than 2!");
		}

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

		updateContentPositions();
	}

	private void updateContentPositions() {
		int contentWidth = mTextMaxWidth + mItemIconPadding + mItemIconSize;

		rectContent.set(
				mRectDrawn.centerX() - contentWidth / 2,
				mRectDrawn.top,
				mRectDrawn.centerX() + contentWidth / 2,
				mRectDrawn.bottom
		);

		rectIcon.set(
				rectContent.left,
				rectContent.top,
				rectContent.left + mItemIconSize,
				rectContent.bottom
		);

		rectText.set(
				rectContent.right - mTextMaxWidth,
				rectContent.top,
				rectContent.right,
				rectContent.bottom
		);
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

		// Correct sizes of original content
		int resultWidth = mTextMaxWidth + mItemIconSize;
		int resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1);

		if (isDebug) {
			Log.i(TAG, "Wheel's content size is (" + resultWidth + ":" + resultHeight + ")");
		}

		// Consideration padding influence the view sizes
		resultWidth += getPaddingLeft() + getPaddingRight();
		resultHeight += getPaddingTop() + getPaddingBottom();
		if (isDebug) {
			Log.i(TAG, "Wheel's size is (" + resultWidth + ":" + resultHeight + ")");
		}

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
		// Set content region
		mRectDrawn.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
				getHeight() - getPaddingBottom());
		if (isDebug) {
			Log.i(TAG, "Wheel's drawn rect size is (" + mRectDrawn.width() + ":" +
					mRectDrawn.height() + ") and location is (" + mRectDrawn.left + ":" +
					mRectDrawn.top + ")");
		}

		// Get the center coordinates of content region
		mWheelCenterX = mRectDrawn.centerX();
		mWheelCenterY = mRectDrawn.centerY();

		// Correct item drawn center
		computeDrawnCenter();

		mVisibleItemCount = mRectDrawn.height() / mItemHeight;
		updateVisibleItemCount();

		// Initialize fling max Y-coordinates
		computeFlingLimitY();

		// Correct region of current select item
		computeCurrentItemRect();

		updateContentPositions();
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

			int iconTop = 0;

			if (icon != null) {
				iconTop = mWheelCenterY + (drawnOffsetPos * mItemHeight) + mScrollOffsetY % mItemHeight - icon.getHeight() / 2;
			}

			// Judges need to draw different color for current item or not
			if (mSelectedItemTextColorEnabled) {
				canvas.save();
				canvas.clipRect(mRectCurrentItem, Region.Op.DIFFERENCE);

				if (icon != null) {
					canvas.drawBitmap(icon, rectIcon.left, iconTop, mPaint);
				}

				canvas.drawText(data, rectText.centerX(), mDrawnItemCenterY, mPaint);
				canvas.restore();

				canvas.save();
				canvas.clipRect(mRectCurrentItem);

				if (icon != null) {
					canvas.drawBitmap(icon, rectIcon.left, iconTop, mSelectedPaint);
				}

				canvas.drawText(data, rectText.centerX(), mDrawnItemCenterY, mSelectedPaint);
				canvas.restore();
			} else {
				canvas.save();
				canvas.clipRect(mRectDrawn);

				if (icon != null) {
					canvas.drawBitmap(icon, rectIcon.left, iconTop, mPaint);
				}

				canvas.drawText(data, rectText.centerX(), mDrawnItemCenterY, mPaint);
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

	/**
	 * Get the count of current visible items in WheelPicker
	 */
	public int getVisibleItemCount() {
		return mVisibleItemCount;
	}

	/**
	 * Set the count of current visible items in WheelPicker
	 * The count of current visible items in WheelPicker must greater than 1
	 * Notice:count of current visible items in WheelPicker will always is an odd number, even you
	 * can set an even number for it, it will be change to an odd number eventually
	 * By default, the count of current visible items in WheelPicker is 7
	 */
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

	/**
	 * Set items of WheelPicker if has same width
	 * WheelPicker will traverse the data source to calculate each data text width to find out the
	 * maximum text width for the final view width, this process maybe spends a lot of time and
	 * reduce efficiency when data source has large amount data, in most large amount data case,
	 * data text always has same width, you can call this method tell to WheelPicker your data
	 * source has same width to save time and improve efficiency.
	 * Sometimes the data source you set is positively has different text width, but maybe you know
	 * the maximum width text's position in data source, then you can call
	 * {@link #setMaximumWidthTextPosition(int)} tell to WheelPicker where is the maximum width text
	 * in data source, WheelPicker will calculate its width base on this text which found by
	 * position. If you don't know the position of maximum width text in data source, but you have
	 * maximum width text, you can call {@link #setMaximumWidthText(String)} tell to WheelPicker
	 * what maximum width text is directly, WheelPicker will calculate its width base on this text.
	 */
	public void setSameWidth(boolean hasSameWidth) {
		this.hasSameWidth = hasSameWidth;
		computeTextSize();
		requestLayout();
		invalidate();
	}

	/**
	 * Whether items has same width or not
	 */
	public boolean hasSameWidth() {
		return hasSameWidth;
	}

	public void setOnWheelChangeListener(OnWheelChangeListener listener) {
		mOnWheelChangeListener = listener;
	}

	public String getMaximumWidthText() {
		return mMaxWidthText;
	}

	public void setMaximumWidthText(String text) {
		if (null == text) {
			throw new NullPointerException("Maximum width text can not be null!");
		}
		mMaxWidthText = text;
		computeTextSize();
		requestLayout();
		invalidate();
	}

	/**
	 * Get the position of maximum width text in data source
	 */
	public int getMaximumWidthTextPosition() {
		return mTextMaxWidthPosition;
	}

	/**
	 * Set the position of maximum width text in data source
	 */
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

	public int getSelectedItemTextColor() {
		return mSelectedItemTextColor;
	}

	public void setSelectedItemTextColor(int color) {
		mSelectedItemTextColor = color;
		computeCurrentItemRect();
		invalidate();
	}

	public int getItemTextColor() {
		return mItemTextColor;
	}

	public void setItemTextColor(int color) {
		mItemTextColor = color;
		invalidate();
	}

	public int getItemTextSize() {
		return mItemTextSize;
	}

	public void setItemTextSize(int size) {
		mItemTextSize = size;
		mPaint.setTextSize(mItemTextSize);
		mSelectedPaint.setTextSize(mItemTextSize);
		computeTextSize();
		requestLayout();
		invalidate();
	}

	public int getItemSpace() {
		return mItemSpace;
	}

	public void setItemSpace(int space) {
		mItemSpace = space;
		requestLayout();
		invalidate();
	}

	/**
	 * Set whether WheelPicker has atmospheric or not
	 * WheelPicker's items will be transparent from center to ends if atmospheric display
	 */
	public void setAtmospheric(boolean hasAtmospheric) {
		this.hasAtmospheric = hasAtmospheric;
		invalidate();
	}

	/**
	 * Whether WheelPicker has atmospheric or not
	 */
	public boolean hasAtmospheric() {
		return hasAtmospheric;
	}

	public int getItemAlign() {
		return mItemAlign;
	}

	public void setItemAlign(int align) {
		mItemAlign = align;
		updateItemTextAlign();
		computeDrawnCenter();
		invalidate();
	}

	public Typeface getTypeface() {
		if (null != mPaint) {
			return mPaint.getTypeface();
		}
		return null;
	}

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

	public interface OnWheelChangeListener {
		/**
		 * Invoke when WheelPicker scroll stopped
		 * WheelPicker will return a distance offset which between current scroll position and
		 * initial position, this offset is a positive or a negative, positive means WheelPicker is
		 * scrolling from bottom to top, negative means WheelPicker is scrolling from top to bottom
		 *
		 * @param offset
		 * 		Distance offset which between current scroll position and initial position
		 */
		void onWheelScrolled(int offset);

		/**
		 * Invoke when WheelPicker scroll stopped
		 * This method will be called when WheelPicker stop and return current selected item data's
		 * position in list
		 *
		 * @param position
		 * 		Current selected item data's position in list
		 */
		void onWheelSelected(int position);

		/**
		 * Invoke when WheelPicker's scroll state changed
		 * The state of WheelPicker always between idle, dragging, and scrolling, this method will
		 * be called when they switch
		 *
		 * @param state
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