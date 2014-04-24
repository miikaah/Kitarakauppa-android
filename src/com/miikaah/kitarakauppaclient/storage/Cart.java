package com.miikaah.kitarakauppaclient.storage;

import java.util.ArrayList;

import com.miikaah.kitarakauppaclient.domain.Product;

public enum Cart {	
	INSTANCE;
	
	private ArrayList<Product> products = new ArrayList<Product>();
	
	public boolean addToCart(Product p) {		
		if (!products.contains(p)) {
			products.add(p);
			return true;
		}
		return false;
	}
	
	public Product getProductAt(int index) {
		return products.get(index);
	}
	
	public boolean removeProductAt(int index) {
		if (products.remove(index) != null) {
			return true;
		}
		return false;
	}
	
	public boolean removeProduct(Product p) {
		if (products.contains(p)) {
			products.remove(p);
			return true;
		} 
		return false;
	}
	
	public ArrayList<Product> getProductsInCart() {
		return products;
	}
	
	public void setProductsInCart(ArrayList<Product> products) {
		this.products = products;
	}
	
	public int getSize() {
		return products.size();
	}
	
	public Product getProductWith(int id) {
		for (Product p : products) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}
}
