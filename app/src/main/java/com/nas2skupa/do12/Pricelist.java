package com.nas2skupa.do12;


public class Pricelist {
	public String ID;
	public String service;
	public String price;
	public int action;
    public Pricelist(){
        super();
    }
   
    public Pricelist(String ID, String service, String price, int action) {
        super();
        this.ID = ID;
        this.service = service;
        this.price = price;
        this.action = action;
    }
}