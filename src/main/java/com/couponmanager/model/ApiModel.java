package com.couponmanager.model;

public class ApiModel {
	int result;
	String message;
	
	public ApiModel(){
		
	}
	
	public ApiModel(int result){
		this.result = result;
	}
	
	public ApiModel(int result, String message){
		this.result = result;
		this.message = message;
	}
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
