package com.miikaah.kitarakauppaclient;

import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.ui.ProductsAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class CheckoutActivity extends Activity {
	
	private final static String TAG = "CheckoutActivity";
	
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
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Updating product list");
		ListView listview = (ListView) this.findViewById(R.id.CartListView);
		ProductsAdapter pa = new ProductsAdapter(this, Cart.INSTANCE.getProductsInCart());
		listview.setAdapter(pa);
	}
}
