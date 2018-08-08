package com.aigestudio.wheelpicker;

import android.graphics.Typeface;

import java.util.List;

/**
 * 滚轮选择器方法接口
 * <p>
 * Interface of WheelPicker
 *
 * @author AigeStudio 2015-12-03
 * @author AigeStudio 2015-12-08
 * @author AigeStudio 2015-12-12
 * @author AigeStudio 2016-06-17
 * 更新项目结构
 * <p>
 * New project structure
 * @version 1.1.0
 */
public interface IWheelPicker {

  /**
   * 获取滚轮选择器可见数据项的数量
   * <p>
   * Get the count of current visible items in WheelPicker
   *
   * @return 滚轮选择器可见数据项的数量
   */
  int getVisibleItemCount();

  /**
   * 设置滚轮选择器可见数据项数量
   * 滚轮选择器的可见数据项数量必须为大于1的整数
   * 这里需要注意的是，滚轮选择器会始终显示奇数个数据项，即便你为其设置偶数个数据项，最终也会被转换为奇数
   * 默认情况下滚轮选择器可见数据项数量为7
   * <p>
   * Set the count of current visible items in WheelPicker
   * The count of current visible items in WheelPicker must greater than 1
   * Notice:count of current visible items in WheelPicker will always is an odd number, even you
   * can set an even number for it, it will be change to an odd number eventually
   * By default, the count of current visible items in WheelPicker is 7
   *
   * @param count
   * 		滚轮选择器可见数据项数量
   */
  void setVisibleItemCount(int count);

  /**
   * 获取当前被选中的数据项所显示的数据在数据源中的位置
   * 需要注意的是，当滚轮选择器滚动时并不会改变该方法的返回值，该方法会始终返回
   * {@link #setSelectedItemPosition(int)}所设置的值，当且仅当调用
   * {@link #setSelectedItemPosition(int)}设置新值后，该方法所返回的值才会改变
   * 如果你只是想获取滚轮静止时当前被选中的数据项所显示的数据在数据源中的位置，你可以通过
   * {@link #getCurrentItemPosition()}
   * <p>
   * Get the position of current selected item in data source
   * Notice:The value by return will not change when WheelPicker scroll, this method will always
   * return the value which {@link #setSelectedItemPosition(int)} set, the value this method
   * return will be changed if and only if call the
   * {@link #setSelectedItemPosition(int)}
   * set a new value
   *
   * @return 当前被选中的数据项所显示的数据在数据源中的位置
   */
  int getSelectedItemPosition();

  /**
   * 设置当前被选中的数据项所显示的数据在数据源中的位置
   * 调用该方法会导致滚动选择器的位置被重新初始化，什么意思呢？假如你滑动选择到第五个数据项的时候调用该方
   * 法重新将当前被选中的数据项所显示的数据在数据源中的位置设置为第三个，那么滚轮选择器会清除掉上一次滚动
   * 的相关数据参数，并将重置一系列的数据，重新将第三个数据作为滚轮选择器的起点，这个行为很可能会影响你之
   * 前所根据这些参数改变的一些属性，比如
   * {@link com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener}和
   * 此你总该在调用该方法后考虑到相关影响
   * 的值，否则会抛出异常
   * 默认情况下，当前被选中的数据项所显示的数据在数据源中的位置为0
   * <p>
   * Set the position of current selected item in data source
   * Call this method and set a new value may be reinitialize the location of WheelPicker. For
   * example, you call this method after scroll the WheelPicker and set selected item position
   * with a new value, WheelPicker will clear the related parameters last scroll set and reset
   * series of data, and make the position 3 as a new starting point of WheelPicker, this behavior
   * maybe influenced some attribute you set last time, such as parameters of method in
   * {@link com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener} and
   * You should always set a value which greater than or equal to 0 and less than data source's
   * length
   * By default, position of current selected item in data source is 0
   *
   * @param position
   * 		当前被选中的数据项所显示的数据在数据源中的位置
   */
  void setSelectedItemPosition(int position);

  /**
   * 获取当前被选中的数据项所显示的数据在数据源中的位置
   * 与{@link #getSelectedItemPosition()}不同的是，该方法所返回的结果会因为滚轮选择器的改变而改变
   * <p>
   * Get the position of current selected item in data source
   * The difference between {@link #getSelectedItemPosition()}, the value this method return will
   * change by WheelPicker scrolled
   *
   * @return 当前被选中的数据项所显示的数据在数据源中的位置
   */
  int getCurrentItemPosition();

  /**
   * 设置数据项是否有相同的宽度
   * 滚轮选择器在确定尺寸大小时会通过遍历数据源来计算每一条数据文本的宽度以找到最宽的文本作为滚轮选择器的
   * 最终宽度，当数据源的数据非常多时，这个过程可能会消耗大量的时间导致效率降低，而且在大部分数据量多情况
   * 下，数据文本大都有相同的宽度，这种情况下调用该方法告诉滚轮选择器数据宽度相同则可以免去上述计算时间，
   * 提升效率
   * 有些时候，你所加载的数据源确实是每条数据文本的宽度都不同，但是你知道最宽的数据文本在数据源中的位置，
   * 这时你可以调用{@link #setMaximumWidthTextPosition(int)}方法告诉滚轮选择器最宽的这条数据文本在数据
   * 源的什么位置，滚轮选择器则会根据该位置找到该条数据文本并将其宽度作为滚轮选择器的宽度。如果你不知道位
   * 置，但是知道最宽的数据文本，那么你也可以直接通过调用{@link #setMaximumWidthText(String)}告诉滚轮选
   * 择器最宽的文本是什么，滚轮选择器会根据这条文本计算宽度并将其作为滚轮选择器的宽度
   * <p>
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
   *
   * @param hasSameSize
   * 		是否有相同的宽度
   */
  void setSameWidth(boolean hasSameSize);

  /**
   * 数据项是否有相同宽度
   * <p>
   * Whether items has same width or not
   *
   * @return 是否有相同宽度
   */
  boolean hasSameWidth();

  /**
   * 设置滚轮滚动状态改变监听器
   *
   * @param listener
   * 		滚轮滚动状态改变监听器
   *
   * @see com.aigestudio.wheelpicker.WheelPicker.OnWheelChangeListener
   */
  void setOnWheelChangeListener(WheelPicker.OnWheelChangeListener listener);


  /**
   * @param listener
   * 		{@link com.aigestudio.wheelpicker.WheelPicker.OnActiveItemChangedListener}
   */
  void setOnActiveItemChangedListener(WheelPicker.OnActiveItemChangedListener listener);

  /**
   * 获取最宽的文本
   * <p>
   * Get maximum width text
   *
   * @return 最宽的文本
   */
  String getMaximumWidthText();

  /**
   * 设置最宽的文本
   * <p>
   * Set maximum width text
   *
   * @param text
   * 		最宽的文本
   *
   * @see #setSameWidth(boolean)
   */
  void setMaximumWidthText(String text);

  /**
   * 获取最宽的文本在数据源中的位置
   * <p>
   * Get the position of maximum width text in data source
   *
   * @return 最宽的文本在数据源中的位置
   */
  int getMaximumWidthTextPosition();

  /**
   * 设置最宽的文本在数据源中的位置
   * <p>
   * Set the position of maximum width text in data source
   *
   * @param position
   * 		最宽的文本在数据源中的位置
   *
   * @see #setSameWidth(boolean)
   */
  void setMaximumWidthTextPosition(int position);

  /**
   * 获取当前选中的数据项文本颜色
   * <p>
   * Get text color of current selected item
   * For example 0xFF123456
   *
   * @return 当前选中的数据项文本颜色
   */
  int getSelectedItemTextColor();

  /**
   * 设置当前选中的数据项文本颜色
   * <p>
   * Set text color of current selected item
   * For example 0xFF123456
   *
   * @param color
   * 		当前选中的数据项文本颜色，16位颜色值
   */
  void setSelectedItemTextColor(int color);

  /**
   * 获取数据项文本颜色
   * <p>
   * Get text color of items
   * For example 0xFF123456
   *
   * @return 数据项文本颜色
   */
  int getItemTextColor();

  /**
   * 设置数据项文本颜色
   * <p>
   * Set text color of items
   * For example 0xFF123456
   *
   * @param color
   * 		数据项文本颜色，16位颜色值
   */
  void setItemTextColor(int color);

  /**
   * 获取数据项文本尺寸大小
   * <p>
   * Get text size of items
   * Unit in px
   *
   * @return 数据项文本尺寸大小
   */
  int getItemTextSize();

  /**
   * 设置数据项文本尺寸大小
   * <p>
   * Set text size of items
   * Unit in px
   *
   * @param size
   * 		设置数据项文本尺寸大小，单位：px
   */
  void setItemTextSize(int size);

  /**
   * 获取滚轮选择器数据项之间间距
   * <p>
   * Get space between items
   * Unit in px
   *
   * @return 滚轮选择器数据项之间间距
   */
  int getItemSpace();

  /**
   * 设置滚轮选择器数据项之间间距
   * <p>
   * Set space between items
   * Unit in px
   *
   * @param space
   * 		滚轮选择器数据项之间间距，单位：px
   */
  void setItemSpace(int space);

  /**
   * 设置滚轮选择器是否有空气感
   * 开启空气感的滚轮选择器将呈现中间不透明逐渐向两端透明过度的渐变效果
   * <p>
   * Set whether WheelPicker has atmospheric or not
   * WheelPicker's items will be transparent from center to ends if atmospheric display
   *
   * @param hasAtmospheric
   * 		滚轮选择器是否有空气感
   */
  void setAtmospheric(boolean hasAtmospheric);

  /**
   * 滚轮选择器是否有空气感
   * <p>
   * Whether WheelPicker has atmospheric or not
   *
   * @return 滚轮选择器是否有空气感
   */
  boolean hasAtmospheric();

  /**
   * 获取滚轮选择器数据项的对齐方式
   * <p>
   * Get alignment of WheelPicker
   *
   * @return 滚轮选择器数据项的对齐方式
   */
  int getItemAlign();

  /**
   * 设置滚轮选择器数据项的对齐方式
   * 默认对齐方式为居中对齐{@link WheelPicker#ALIGN_CENTER}
   * <p>
   * Set alignment of WheelPicker
   * The default alignment of WheelPicker is {@link WheelPicker#ALIGN_CENTER}
   *
   * @param align
   * 		对齐方式标识值
   * 		该值仅能是下列值之一：
   * 		{@link WheelPicker#ALIGN_CENTER}
   * 		{@link WheelPicker#ALIGN_LEFT}
   * 		{@link WheelPicker#ALIGN_RIGHT}
   */
  void setItemAlign(int align);

  /**
   * 获取数据项文本字体对象
   * <p>
   * Get typeface of item text
   *
   * @return 文本字体对象
   */
  Typeface getTypeface();

  /**
   * 设置数据项文本字体对象
   * 数据项文本字体的设置可能会导致滚轮大小的改变
   * <p>
   * Set typeface of item text
   * Set typeface of item text maybe cause WheelPicker size change
   *
   * @param tf
   * 		字体对象
   */
  void setTypeface(Typeface tf);
}