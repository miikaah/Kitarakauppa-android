package com.miikaah.kitarakauppaclient;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.miikaah.kitarakauppaclient.domain.Product;
import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.ui.ProductsAdapter;

public class CartFragment extends Fragment implements View.OnTouchListener {
	
	// product that was selected with itemLongClick
    Product selectedProduct;
    
    // Listview that has product details tables inside it
    ListView listview;
	
	private final static String TAG = "CartFragment";
	
	private ActionMode mActionMode;
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.cart, menu);	        
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
	            case R.id.action_removeFromCart:
	                Log.d(TAG, "Removing from Cart: " + selectedProduct.getName());
	                
	                // Remove product from cart and notify user
	                if (Cart.INSTANCE.removeProduct(selectedProduct)) {
	                	ArrayAdapter aa = (ArrayAdapter) listview.getAdapter();
	                	aa.remove(selectedProduct);
	                	listview.setAdapter(aa);
	                	aa.notifyDataSetChanged();
	                } else {
	                	Log.e(TAG, "Error trying to remove product from cart");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {		
		return inflater.inflate(R.layout.cart, container, false);
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		listview = (ListView) getActivity().findViewById(R.id.CartListView);
		OnItemLongClickListener listener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Log.d(TAG, "onItemLongClick id: " + id + " pos: " + position);
				ArrayList<Product> productsList = Cart.INSTANCE.getProductsInCart();
				selectedProduct = productsList.get(position);
				if (mActionMode != null) {
		            return false;
		        }

		        // Start the CAB using the ActionMode.Callback defined above
		        mActionMode = getActivity().startActionMode(mActionModeCallback);
		        view.setSelected(true);
				return true;
			}
		};
		listview.setOnItemLongClickListener(listener);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Updating product list");
		ProductsAdapter pa = new ProductsAdapter(getActivity(), Cart.INSTANCE.getProductsInCart());
		listview.setAdapter(pa);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
