package com.knightlore.network;


public class TimeoutException extends Exception{

	/**
	 *  maybe this will be used in the future
	 */
	private static final long serialVersionUID = 1L;
	
	public TimeoutException(){
		super();
	}
	
	public TimeoutException(String message){
		super(message);
	}
	
}
