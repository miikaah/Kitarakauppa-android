package com.miikaah.kitarakauppaclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DummyActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("You Dummy!", "Closing");
		finish();
	}
}
