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
		data.add("One");
		data.add("Two");
		data.add("Three");
		data.add("Four");
		data.add("Five");
		data.add("Six");
		data.add("Seven");
		data.add("Eight");
		data.add("Nine");
		data.add("Ten");

		WheelPicker wheelPicker = findViewById(R.id.wheel_picker);
		wheelPicker.setData(data);
	}
}
