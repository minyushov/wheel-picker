package com.aigestudio.wheelpicker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public final class WheelItem<T> {
	private final T data;
	private final Bitmap icon;

	public WheelItem(T data) {
		this.data = data;
		this.icon = null;
	}

	public WheelItem(Resources resources, T data, int drawableId) {
		this.data = data;
		this.icon = BitmapFactory.decodeResource(resources, drawableId).copy(Bitmap.Config.ARGB_8888, true);
	}

	public WheelItem(T data, Bitmap bitmap) {
		this.data = data;
		this.icon = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	}

	public T getData() {
		return data;
	}

	public Bitmap getIcon() {
		return icon;
	}

	@Override
	public String toString() {
		return data.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		icon.recycle();
	}
}
