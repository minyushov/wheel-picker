package com.aigestudio.wheelpicker;

import android.graphics.Typeface;

/**
 * Interface of WheelPicker
 */
public interface IWheelPicker {
	/**
	 * Get the count of current visible items in WheelPicker
	 */
	int getVisibleItemCount();

	/**
	 * Set the count of current visible items in WheelPicker
	 * The count of current visible items in WheelPicker must greater than 1
	 * Notice:count of current visible items in WheelPicker will always is an odd number, even you
	 * can set an even number for it, it will be change to an odd number eventually
	 * By default, the count of current visible items in WheelPicker is 7
	 */
	void setVisibleItemCount(int count);

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
	void setSameWidth(boolean hasSameSize);

	/**
	 * Whether items has same width or not
	 */
	boolean hasSameWidth();

	void setOnWheelChangeListener(WheelPicker.OnWheelChangeListener listener);

	/**
	 * Get maximum width text
	 */
	String getMaximumWidthText();

	/**
	 * Set maximum width text
	 *
	 * @see #setSameWidth(boolean)
	 */
	void setMaximumWidthText(String text);

	/**
	 * Get the position of maximum width text in data source
	 */
	int getMaximumWidthTextPosition();

	/**
	 * Set the position of maximum width text in data source
	 *
	 * @see #setSameWidth(boolean)
	 */
	void setMaximumWidthTextPosition(int position);

	/**
	 * Get text color of current selected item
	 * For example 0xFF123456
	 */
	int getSelectedItemTextColor();

	/**
	 * Set text color of current selected item
	 * For example 0xFF123456
	 */
	void setSelectedItemTextColor(int color);

	/**
	 * Get text color of items
	 * For example 0xFF123456
	 */
	int getItemTextColor();

	/**
	 * Set text color of items
	 * For example 0xFF123456
	 */
	void setItemTextColor(int color);

	/**
	 * Get text size of items
	 * Unit in px
	 */
	int getItemTextSize();

	/**
	 * Set text size of items
	 * Unit in px
	 */
	void setItemTextSize(int size);

	/**
	 * Get space between items
	 * Unit in px
	 */
	int getItemSpace();

	/**
	 * Set space between items
	 * Unit in px
	 */
	void setItemSpace(int space);

	/**
	 * Set whether WheelPicker has atmospheric or not
	 * WheelPicker's items will be transparent from center to ends if atmospheric display
	 */
	void setAtmospheric(boolean hasAtmospheric);

	/**
	 * Whether WheelPicker has atmospheric or not
	 */
	boolean hasAtmospheric();

	/**
	 * Get alignment of WheelPicker
	 */
	int getItemAlign();

	/**
	 * Set alignment of WheelPicker
	 * The default alignment of WheelPicker is {@link WheelPicker#ALIGN_CENTER}
	 *
	 * @param align
	 * 		{@link WheelPicker#ALIGN_CENTER}
	 * 		{@link WheelPicker#ALIGN_LEFT}
	 * 		{@link WheelPicker#ALIGN_RIGHT}
	 */
	void setItemAlign(int align);

	/**
	 * Get typeface of item text
	 */
	Typeface getTypeface();

	/**
	 * Set typeface of item text
	 * Set typeface of item text maybe cause WheelPicker size change
	 */
	void setTypeface(Typeface tf);
}