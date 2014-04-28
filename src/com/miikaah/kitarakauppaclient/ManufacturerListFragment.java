package com.miikaah.kitarakauppaclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.miikaah.kitarakauppaclient.domain.Manufacturer;
import com.miikaah.kitarakauppaclient.util.JSONParser;

public class ManufacturerListFragment extends ListFragment {
	private ArrayList<Manufacturer> manufacturersList;
	private IOnItemSelectedListener mListener;
	private int layout;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    // base url
    private final String BASE_URL = "https://users.metropolia.fi/~miikaah/php/harjoitustyo/json.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MANUFACTURERS = "valmistajat";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "nimi";
 
    // products JSONArray
    JSONArray manufacturers = null;
	
	private static final String TAG = "ManufacturerListFragment";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We need to use a different list item layout for devices older than Honeycomb
        layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;
        
        manufacturersList = new ArrayList<Manufacturer>();
        
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
        mListener.onItemSelected(manufacturersList.get(position));
    }
    
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("valmistaja", "kaikki"));
            
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(args[0], "GET", params, null);
 
            // Check your log cat for JSON response
            Log.i(TAG, json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Manufacturers
                    manufacturers = json.getJSONArray(TAG_MANUFACTURERS);
 
                    // looping through All Products
                    for (int i = 0; i < manufacturers.length(); i++) {
                        JSONObject c = manufacturers.getJSONObject(i);
 
                        // Storing each json item in variable   
                        int id = Integer.parseInt(c.getString(TAG_ID));
                        String name = c.getString(TAG_NAME);
                        
                        // Add product to list
                        manufacturersList.add(new Manufacturer(id, name));
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
            /**
             * Updating parsed JSON data into ListView
             * */
        	setListAdapter(new ArrayAdapter<Manufacturer>(getActivity(), layout, manufacturersList)); 
        }
 
    }
}
