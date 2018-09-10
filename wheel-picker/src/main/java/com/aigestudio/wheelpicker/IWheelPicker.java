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