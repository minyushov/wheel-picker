package com.aigestudio.wheelpicker;

import java.util.List;

public final class WheelAdapter<T> {
	private List<WheelItem<T>> data;
	private OnItemSelectedListener<T> onItemSelectedListener;
	private WheelPicker wheelPicker;

	public void bind(WheelPicker wheelPicker) {
		this.wheelPicker = wheelPicker;
		wheelPicker.setAdapter(this);
	}

	public void setData(List<WheelItem<T>> data) {
		this.data = data;

		if (wheelPicker != null) {
			wheelPicker.setAdapter(this);
			if (data.size() > 0) {
				wheelPicker.setSelectedItemPosition(0, false);
			}
		}
	}

	public List<WheelItem<T>> getData() {
		return data;
	}

	public int getSize() {
		return data != null ? data.size() : 0;
	}

	/**
	 * @return selected item position in wheel's idle state
	 */
	public int getSelectedItemPosition() {
		return wheelPicker.getSelectedItemPosition();
	}

	/**
	 * @return selected item position during wheel's scroll
	 */
	public int getCurrentItemPosition() {
		return wheelPicker.getCurrentItemPosition();
	}

	public void setSelectedItemPosition(int position) {
		wheelPicker.setSelectedItemPosition(position, true);
	}

	public void setSelectedItemPosition(int position, boolean animated) {
		wheelPicker.setSelectedItemPosition(position, animated);
	}

	public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

	public WheelItem<T> getSelectedItem() {
		if (data == null || data.size() == 0 || wheelPicker.getSelectedItemPosition() >= data.size()) {
			return null;
		}

		return data.get(wheelPicker.getSelectedItemPosition());
	}

	public WheelItem<T> getCurrentItem() {
		if (data == null || data.size() == 0 || wheelPicker.getCurrentItemPosition() >= data.size()) {
			return null;
		}

		return data.get(wheelPicker.getCurrentItemPosition());
	}

	void onItemSelected(int position) {
		if (onItemSelectedListener != null && data != null && position < data.size()) {
			onItemSelectedListener.onItemSelected(data.get(position).getData());
		}
	}

	public interface OnItemSelectedListener<T> {
		void onItemSelected(T item);
	}
}
