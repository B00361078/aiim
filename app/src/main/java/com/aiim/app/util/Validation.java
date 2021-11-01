package com.aiim.app.util;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Alert;

/* Main validation class handles any validation required and makes use of DataHandler where needed.
 * Neil Campbell 07/05/2021, B00361078
 */

public class Validation {
	
	
	
	

	
	public boolean stringValidator(String string1, String string2) {
		if (string1.contentEquals(string2)) {
			return true;
		}
		else
		return false;
	}
	
	public boolean isAlpha(String name) {
	    char[] chars = name.toCharArray();

	    for (char c : chars) {
	        if(!Character.isLetter(c)) {
	            return false;
	        }
	    }
	    return true;
	}

	public boolean isDigit(String num) {
		char[] chars = num.toCharArray();
		for (char c : chars) {
	        if(!Character.isDigit(c)) {
	            return false;
	        }
	    }
		return true;
	}
	public boolean isPostcode(String postcode) {
		String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(postcode);
		if (matcher.matches() == false) {
			return false;
		}
		return true;	
	}
	public static boolean isHPW(String hpw) {
		char[] chars = hpw.toCharArray();
		for (char c : chars) {
	        if(!Character.isDigit(c) || hpw.length() > 2) {
	            return false;
	        }
	    }
		int hpwInt = Integer.parseInt(hpw);
		if (hpwInt > 35) {
			return false;
		}
		return true;
	}
	public boolean passwordCriteria(String password) {
		
		boolean hasDigit = false;
	    boolean hasUpper = false;
	    
		char[] chars = password.toCharArray();
		for (char c : chars) {
	        if(Character.isDigit(c)) {
	            hasDigit=true;
	        }
	        if((Character.isUpperCase(c))) {
	        	hasUpper = true;
	        }
	    }
		if (password.length() >= 8 && hasDigit == true && hasUpper == true) {
			return true;
		}
		else 
			return false;
	}
	
	
	
}
