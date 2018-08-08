package com.aigestudio.wheelpicker;

import java.util.List;

public final class WheelAdapter<T> {
	private List<WheelItem<T>> data;
	private OnItemSelectedListener<T> onItemSelectedListener;

	public void bind(WheelPicker wheelPicker) {
		wheelPicker.setAdapter(this);
	}

	public void setData(List<WheelItem<T>> data) {
		this.data = data;
	}

	public List<WheelItem<T>> getData() {
		return data;
	}

	public int getSize() {
		return data != null ? data.size() : 0;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
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
