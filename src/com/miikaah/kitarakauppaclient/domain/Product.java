package com.miikaah.kitarakauppaclient.domain;

import android.graphics.Bitmap;

public class Product {
	private int id;
	private String name;
	private double price;
	private int manufacturerId;
	private String pic;
	private String desc;
	private int categoryId;
	private int storageTotal;
	private int quantity;
	private String descString;
	private Bitmap bmp;
	
	public Product(int id, String name, double price, int manufacturerId,
			String pic, String desc, int categoryId, int storageTotal,
			int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.manufacturerId = manufacturerId;
		this.pic = pic;
		this.desc = desc;
		this.categoryId = categoryId;
		this.storageTotal = storageTotal;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getStorageTotal() {
		return storageTotal;
	}

	public void setStorageTotal(int storageTotal) {
		this.storageTotal = storageTotal;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public Bitmap getBmp() {
		return bmp;
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public String getDescString() {
		return descString;
	}

	public void setDescString(String desc) {
		this.descString = desc;
	}
}
