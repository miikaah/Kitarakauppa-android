package com.miikaah.kitarakauppaclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CheckoutActivity extends Activity {
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
	}
	
	public void placeOrder(View view) {
		
	}
	
	public void cancelOrder(View view) {
		finish();
	}
}
