package com.example.sample;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import com.aigestudio.wheelpicker.WheelAdapter;
import com.aigestudio.wheelpicker.WheelItem;
import com.aigestudio.wheelpicker.WheelPicker;

import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity {
	private static final String TAG = "SampleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		ArrayList<WheelItem<String>> data = new ArrayList<>();
		data.add(new WheelItem<>("Text 1"));
		data.add(new WheelItem<>(getResources(), "Text 2", R.drawable.icon_off_18));
		data.add(new WheelItem<>(getResources(), "Text 3", R.drawable.icon_off_18));
		data.add(new WheelItem<>(getResources(), "Text 4", R.drawable.icon_off_18));
		data.add(new WheelItem<>(getResources(), "Text 5", R.drawable.icon_off_18));
		data.add(new WheelItem<>("Text 6"));
		data.add(new WheelItem<>("Text 7"));
		data.add(new WheelItem<>("Text 8"));
		data.add(new WheelItem<>("Text 9"));
		data.add(new WheelItem<>("Text 10"));
		data.add(new WheelItem<>("Text 11"));
		data.add(new WheelItem<>(getResources(), "Text 12", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 13", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 14", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 15", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 16", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 17", R.drawable.icon_on_18));
		data.add(new WheelItem<>(getResources(), "Text 18", R.drawable.icon_on_18));
		data.add(new WheelItem<>("Text 19"));
		data.add(new WheelItem<>("Text 20"));
		data.add(new WheelItem<>("Text 21"));
		data.add(new WheelItem<>("Text 22"));
		data.add(new WheelItem<>("Text 23"));
		data.add(new WheelItem<>("Text 24"));
		data.add(new WheelItem<>("Text 25"));

		WheelPicker wheelPicker3 = findViewById(R.id.wheel_picker);

		WheelAdapter<String> adapter = new WheelAdapter<>();
		adapter.setData(data);
		adapter.setOnItemSelectedListener(new WheelAdapter.OnItemSelectedListener<String>() {
			@Override
			public void onItemSelected(String item) {
				Log.d(TAG, "onItemSelected: " + item);
			}
		});
		adapter.bind(wheelPicker3);
	}
}
