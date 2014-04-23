package com.miikaah.kitarakauppaclient.ui;

import java.util.ArrayList;

import com.miikaah.kitarakauppaclient.R;
import com.miikaah.kitarakauppaclient.domain.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductsAdapter extends ArrayAdapter<Product> {

	public ProductsAdapter(Context context, ArrayList<Product> products) {
		super(context, R.layout.item_product, products);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Product product = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_product, null);
		}
		// Lookup view for data population
		TextView pName = (TextView) convertView.findViewById(R.id.productName);
		TextView pPrice = (TextView) convertView.findViewById(R.id.productPrice);
		// Populate the data into the template view using the data object
		pName.setText(product.getName());
		pPrice.setText(String.valueOf(product.getPrice()));
		// Return the completed view to render on screen
		return convertView;
	}
}
