package com.miikaah.kitarakauppaclient.storage;

import java.util.ArrayList;

import com.miikaah.kitarakauppaclient.domain.Product;

public enum Products {
	INSTANCE;

	private ArrayList<Product> products = new ArrayList<Product>();

	public boolean addToList(Product p) {
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
	
	public void removeAllProducts() {
		products.clear();
	}

	public ArrayList<Product> getProductsInList() {
		return products;
	}
	
	/**
	 * Returns an arraylist of products that match given id.
	 * @param manId id by which you want to select products
	 * @return products that match the given id
	 */
	public ArrayList<Product> getProductsByMan(int manId) {
		ArrayList<Product> productsByMan = new ArrayList<Product>();
		for (Product product : products) {
			if (product.getManufacturerId() == manId) {
				productsByMan.add(product);
			}
		}
		return productsByMan;
	}

	public void setProductsInList(ArrayList<Product> products) {
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
