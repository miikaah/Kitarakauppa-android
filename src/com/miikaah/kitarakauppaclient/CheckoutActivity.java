package com.miikaah.kitarakauppaclient;

import java.util.ArrayList;
import java.util.List;

import com.miikaah.kitarakauppaclient.domain.Product;
import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.ui.ProductsAdapter;
import com.miikaah.kitarakauppaclient.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class CheckoutActivity extends Activity {
	private String firstName;
	private String lastName;
	private String address;
	private String zipCode;
	private String city;
	private String email;
	
	private ProgressDialog pDialog;
	
	// Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
	
	// base url
    private final String BASE_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/android_order.php";
	
	private final static String TAG = "CheckoutActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
	}
	
	public void placeOrder(View view) {
		EditText et = (EditText) findViewById(R.id.Edit_firstname);
		firstName = et.getText().toString();
		et = (EditText) findViewById(R.id.edit_lastName);
		lastName = et.getText().toString();
		et = (EditText) findViewById(R.id.edit_address);
		address = et.getText().toString();
		et = (EditText) findViewById(R.id.edit_zipCode);
		zipCode = et.getText().toString();
		et = (EditText) findViewById(R.id.Edit_city);
		city = et.getText().toString();
		et = (EditText) findViewById(R.id.edit_email);
		email = et.getText().toString();
		new PlaceNewOrder().execute();
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
	
	class PlaceNewOrder extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckoutActivity.this);
            pDialog.setMessage("Tilataan..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("firstName", firstName));
            params.add(new BasicNameValuePair("lastName", lastName));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("zipCode", zipCode));
            params.add(new BasicNameValuePair("city", city));
            params.add(new BasicNameValuePair("email", email));
            
            // Get product ids from Cart           
            JSONArray jArray = new JSONArray();
            for (Product p : Cart.INSTANCE.getProductsInCart()) {
				try {
					JSONObject pids = new JSONObject();
					pids.put("id", p.getId());
					pids.put("amount", p.getQuantity());
					jArray.put(pids);
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}
			}
 
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(BASE_URL,
                    "POST", params, jArray);
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product
                	return "success";                                   
                } else {
                    // failed to create product
                	return "failure";
                }
            } catch (JSONException e) {
                Log.e(TAG, "jsonEx: " + e.getMessage());
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String s) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if (s != null && s.equals("success")) {
            	Toast.makeText(getBaseContext(), "Tilaus onnistui!", Toast.LENGTH_LONG).show();
            	// closing this screen
                finish();
            } else {
            	Toast.makeText(getBaseContext(), "Tilaus ei onnistunut.", Toast.LENGTH_LONG).show();
            }
            
        }
 
    }
}
