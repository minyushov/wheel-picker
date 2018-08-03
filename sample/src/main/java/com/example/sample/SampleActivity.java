package com.example.sample;

import android.os.Bundle;

import java.util.ArrayList;

import com.aigestudio.wheelpicker.WheelPicker;

import androidx.appcompat.app.AppCompatActivity;

public class SampleActivity extends AppCompatActivity {
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

		wheelPicker1.setData(data);
		wheelPicker2.setData(data);
		wheelPicker3.setData(data);
	}
}
