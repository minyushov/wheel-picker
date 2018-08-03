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
//		data.add("26");
//		data.add("27");
//		data.add("28");
//		data.add("29");
//		data.add("30");
//		data.add("31");
//		data.add("32");
//		data.add("33");
//		data.add("34");
//		data.add("35");
//		data.add("36");
//		data.add("37");
//		data.add("38");
//		data.add("39");
//		data.add("40");
//		data.add("41");
//		data.add("42");
//		data.add("43");
//		data.add("44");
//		data.add("45");
//		data.add("46");
//		data.add("47");
//		data.add("48");
//		data.add("49");
//		data.add("50");

		WheelPicker wheelPicker = findViewById(R.id.wheel_picker);
		wheelPicker.setData(data);
	}
}
