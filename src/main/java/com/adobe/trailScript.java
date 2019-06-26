package com.adobe;

public class trailScript {

	
	public static void main(String[] args) {		
		String csvFile = System.getenv("FILE_PATH");
		String email = System.getenv("EMAIL");
		System.out.println(email);	
		System.out.println("First Jenkins Job");
	}

}
