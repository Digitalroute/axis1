package com.bookshop.soap;
/*This class is automatically generated by 
*axis_lsf client service generator
*The client side Locator of the web service. 
*/

public class AmazonSearchPortLocator  {

	private com.bookshop.soap.AmazonSearchPort ws;
	public AmazonSearchPortLocator(){
	}
	public AmazonSearchPortLocator(java.lang.String enduri){
	this.ws = new com.bookshop.soap.AmazonSearchPortStub(enduri);
	}

	public com.bookshop.soap.AmazonSearchPort getStub(){
		return this.ws;
	}
}
