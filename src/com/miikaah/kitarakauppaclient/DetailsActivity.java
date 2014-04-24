package com.miikaah.kitarakauppaclient;

import com.miikaah.kitarakauppaclient.domain.Product;
import com.miikaah.kitarakauppaclient.storage.Cart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DetailsActivity extends Activity {

	// base url
	private final String IMG_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/img/";
	private final String DESC_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/desc/";

	private final static String TAG = "DetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		int id;
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				id = -1;
			} else {
				id = extras.getInt("id");
			}
		} else {
			id = (Integer) savedInstanceState.getSerializable("id");
		}
		Log.d(TAG, "id: " + id);
		
		Product p = Cart.INSTANCE.getProductWith(id);
		if (p != null) {
			String desc = p.getDesc();
			String pic = p.getPic();
			Log.d(TAG, "desc: " + desc + " pic: " + pic);
		}		
	}

	private class ImageDownloader extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		protected void onPostExecute(String s) {
			finish();
		}
	}
}
