package com.example.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import com.aigestudio.wheelpicker.WheelPicker;

import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity {
	private static final String TAG = "SampleActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		ArrayList<String> data = new ArrayList<>();
		data.add("1");
		data.add("2");
		data.add("3");
		data.add("4");
		data.add("5");
		data.add("6");
		data.add("7");
		data.add("8");
		data.add("9");
		data.add("10");
		data.add("11");
		data.add("12");
		data.add("13");
		data.add("14");
		data.add("15");
		data.add("16");
		data.add("17");
		data.add("18");
		data.add("19");
		data.add("20");
		data.add("21");
		data.add("22");
		data.add("23");
		data.add("24");
		data.add("25");

		WheelPicker wheelPicker1 = findViewById(R.id.wheel_picker_1);
		WheelPicker wheelPicker2 = findViewById(R.id.wheel_picker_2);
		WheelPicker wheelPicker3 = findViewById(R.id.wheel_picker_3);
		WheelPicker wheelPicker4 = findViewById(R.id.wheel_picker_4);

		wheelPicker1.setData(data);
		wheelPicker2.setData(data);
		wheelPicker3.setData(data);
		wheelPicker4.setData(data);

		final TextView wheelPicker1Active = findViewById(R.id.wheel_picker_1_active);

		wheelPicker1.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
			@Override
			public void onItemSelected(WheelPicker picker, Object data, int position) {
				Log.d(TAG, "onItemSelected: " + data.toString());
			}
		});

		wheelPicker1.setOnWheelChangeListener(new WheelPicker.OnWheelChangeListener() {
			@Override
			public void onWheelScrolled(int offset) {
//			Log.d(TAG, "onWheelScrolled: " + offset);
			}

			@Override
			public void onWheelSelected(int position) {
				Log.d(TAG, "onWheelSelected: " + position);
			}

			@Override
			public void onWheelScrollStateChanged(int state) {
				Log.d(TAG, "onWheelScrollStateChanged: " + state);
			}
		});

		wheelPicker1.setOnActiveItemChangedListener(new WheelPicker.OnActiveItemChangedListener() {
			@Override
			public void onActiveItemChanged(WheelPicker picker, Object data, int position) {
				wheelPicker1Active.setText(String.valueOf(position));
			}
		});
	}
}
