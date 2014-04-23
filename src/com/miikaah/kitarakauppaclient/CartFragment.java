package com.miikaah.kitarakauppaclient;


import com.miikaah.kitarakauppaclient.storage.Cart;
import com.miikaah.kitarakauppaclient.ui.ProductsAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CartFragment extends Fragment {
	
	private final static String TAG = "CartFragment";

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		return inflater.inflate(R.layout.cart, container, false);
    }
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "Updating product list");
		ListView listview = (ListView) getActivity().findViewById(R.id.CartListView);
		ProductsAdapter pa = new ProductsAdapter(getActivity(), Cart.INSTANCE.getProductsInCart());
		listview.setAdapter(pa);
	}
	
}
