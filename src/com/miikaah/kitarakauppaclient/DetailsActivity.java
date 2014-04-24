package com.miikaah.kitarakauppaclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miikaah.kitarakauppaclient.domain.Product;
import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.storage.Products;

public class DetailsActivity extends Activity {

	// base url
	private final String IMG_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/img/";
	private final String DESC_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/desc/";

	// img
	private ImageView img;
	private Bitmap bmp;

	// text
	private TextView pDetails;

	// selected Product
	private Product p;

	private final static String TAG = "DetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		// init ImageView and TextView
		img = (ImageView) findViewById(R.id.image_product);
		pDetails = (TextView) findViewById(R.id.text_pDetails);

		// Get id that was put in Bundle extras
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

		// Get product associated with the id
		p = Products.INSTANCE.getProductWith(id);

		// Check connection
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			// Update details or display them from memory
			if (p != null) {
				String desc = p.getDesc();
				String pic = p.getPic();
				Log.d(TAG, "desc: " + desc + " pic: " + pic);

				setImg(pic);
				setDesc(desc);
			}
		} else {
			Log.e(TAG, "Network error");
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.details, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_addToCart:
			Log.d(TAG, "Adding to Cart: " + p.getName());            
            // Add product to cart and notify user
            if (Cart.INSTANCE.addToCart(p)) {
            	Toast.makeText(this, p + "\nlisättiin koriin", Toast.LENGTH_LONG).show();
            } else {
            	Toast.makeText(this, p + "\non jo korissa", Toast.LENGTH_LONG).show();
            }
            return true;
		case R.id.action_goBack:
			Log.d(TAG, "Finishing " + TAG);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}		
	}

	private void setImg(String pic) {
		// Download pic if not already in memory
		if (p.getBmp() == null) {
			if (!pic.isEmpty()) {
				new ImageDownloader().execute(IMG_URL + pic);
			} else {
				img.setImageResource(R.drawable.default_small);
			}
		} else {
			img.setImageBitmap(p.getBmp());
		}
	}
	
	private void setDesc(String desc) {
		// Download description if not already in memory
		if (p.getDescString() == null) {
			if (!desc.isEmpty()) {
				new DescDownloader().execute(DESC_URL + desc);
			} else {
				pDetails.setText("Ei kuvausta");
			}
		} else {
			pDetails.setText(p.getDescString());
		}
	}

	/**
	 * Download image from Internet
	 *
	 */
	private class ImageDownloader extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... urls) {
			Log.d(TAG, "url: " + urls[0]);
			bmp = getBitmapFromURL(urls[0]);

			if (bmp != null) {
				return "success";
			}
			return "";
		}

		protected void onPostExecute(String result) {
			if (result.equals("success")) {
				img.setImageBitmap(bmp);
				p.setBmp(bmp);
			}
		}

		/**
		 * This method downloads an Image from the given URL, then decodes and
		 * returns a Bitmap object
		 */
		public Bitmap getBitmapFromURL(String link) {

			try {
				URL url = new URL(link);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);

				return myBitmap;

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage().toString());
				return null;
			}
		}
	}

	/**
	 * Download description from Internet
	 *
	 */
	private class DescDownloader extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String description) {
			String output = "";
			output += p.getName() + "\n";
			output += p.getPrice() + " €\n";
			output += "varastossa: " + p.getStorageTotal() + " kpl\n";
			
			if (description != null) {
				output += description;
				p.setDescString(output);
			} else {
				output += "Kuvauksen lataaminen epäonnistui";
			}			
			pDetails.setText(output);
		}

		// Given a URL, establishes an HttpUrlConnection and retrieves
		// the web page content as a InputStream, which it returns as
		// a string.
		private String downloadUrl(String myurl) throws IOException {
			InputStream is = null;

			try {
				URL url = new URL(myurl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(TAG, "The response is: " + response);
				is = conn.getInputStream();

				// Convert the InputStream into a string
				String contentAsString = convertStreamToString(is);
				return contentAsString;

				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		
		String convertStreamToString(java.io.InputStream is) {
		    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		}
	}
}
