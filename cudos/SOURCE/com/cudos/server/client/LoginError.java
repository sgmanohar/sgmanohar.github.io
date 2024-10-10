package com.cudos.server.client;

/**
 * Login errors
 */

public class LoginError extends Exception {
	public LoginError() {  }
	public LoginError(String s) {
		super(s);
	}
}