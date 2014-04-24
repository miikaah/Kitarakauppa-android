package com.miikaah.kitarakauppaclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.miikaah.kitarakauppaclient.domain.Product;
import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.storage.Products;
import com.miikaah.kitarakauppaclient.util.JSONParser;

public class ProductListFragment extends ListFragment {
	private IOnItemSelectedListener mListener;
	private int layout;
	
	// Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    // base url
    private final String BASE_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/json.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "tuotteet";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "nimi";
    private static final String TAG_PRICE = "hinta";
    private static final String TAG_MANUFACTURER_ID = "valmistaja_id";
    private static final String TAG_PIC = "kuva";
    private static final String TAG_DESC = "kuvaus";
    private static final String TAG_CATEGORY_ID = "kategoria_id";
    private static final String TAG_STORAGE_TOTAL = "varastosaldo";
 
    // products JSONArray
    JSONArray products = null;
    
    // product that was selected with itemLongClick
    Product selectedProduct;
	
	private static final String TAG = "ProductListFragment";
	
	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.products, menu);	        
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.action_addToCart:
	                Log.d(TAG, "Adding to Cart: " + selectedProduct.getName());
	                
	                // Add product to cart and notify user
	                if (Cart.INSTANCE.addToCart(selectedProduct)) {
	                	Toast.makeText(getActivity(), selectedProduct + "\nlisättiin koriin", Toast.LENGTH_LONG).show();
	                } else {
	                	Toast.makeText(getActivity(), selectedProduct + "\non jo korissa", Toast.LENGTH_LONG).show();
	                }
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	            	Log.e(TAG, "Action Item error.");
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
 
        // Loading products in Background Thread
        Activity a = getActivity();
        ConnectivityManager connMgr = (ConnectivityManager) a.getSystemService(a.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadAllProducts().execute(BASE_URL);
		} else {
			Log.e(TAG, "No network connection available.");
		}
        
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		OnItemLongClickListener listener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Log.d(TAG, "onItemLongClick id: " + id + " pos: " + position);
				selectedProduct = Products.INSTANCE.getProductAt(position);
				if (mActionMode != null) {
		            return false;
		        }

		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getActivity().startActionMode(mActionModeCallback);
		        view.setSelected(true);
				return true;
			}
		};
		getListView().setOnItemLongClickListener(listener);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IOnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
        }
    }

	@Override
	public void onStart(){
		super.onStart();
			
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
        mListener.onItemSelected(Products.INSTANCE.getProductAt(position));
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Ladataan tuotteita...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("kategoria", "kaikki"));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(args[0], "GET", params);
 
            // Check your log cat for JSON response
            Log.i(TAG, json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable                                           
                        String pic = c.getString(TAG_PIC);
                        String name = c.getString(TAG_NAME);
                        String desc = c.getString(TAG_DESC);
                        int id = Integer.parseInt(c.getString(TAG_ID));                        
                        int sTotal = Integer.parseInt(c.getString(TAG_STORAGE_TOTAL));
                        int categoryId = Integer.parseInt(c.getString(TAG_CATEGORY_ID));                        
                        int manufacturerId = Integer.parseInt(c.getString(TAG_MANUFACTURER_ID));
                        double price = Double.parseDouble(c.getString(TAG_PRICE));
                        
                        // Add product to Products Singleton
                        Products.INSTANCE.addToList(new Product(id, name, price, manufacturerId, pic, desc, categoryId, sTotal, 1));
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
        	setListAdapter(new ArrayAdapter<Product>(getActivity(), layout, Products.INSTANCE.getProductsInList()));
        }
 
    }
}
