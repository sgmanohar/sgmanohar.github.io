package com.cudos.server.client;

import java.rmi.*;
import java.util.*;
/**
 * This is the client-side remote interface for communicating with the server.
 * The client should access Server objects with Naming.lookup(), e.g.
 * Server s = (Server)Naming.lookup("http://www.cudos.ac.uk/CudosServer");
 *
 * The implementation is in cudos.server.CudosServer
 */

public interface Server extends Remote{
	static String location = "http:///www.cudos.ac.uk/CudosServer.obj";

	/**
	 * Log into the server.
	 */
	ClientHandle login(String userName, String password) throws LoginError;

	/**
	 * Read the file names from the user's record
	 */
	String[] getFilenames(ClientHandle handle);

	/**
	 * Write a file to the user's record
	 */
	void saveFile(ClientHandle handle, String filename, Object file);

	/**
	 * Read a file from the user's record
	 */
	Object getFile(ClientHandle handle, String filename);

	/**
	 * Delete a file
	 */
	void deleteFile(ClientHandle handle, String filename);

	/**
	 * Stores a test result item
	 */
	void addTestResult(ClientHandle handle, TestResult result);

	/**
	 * Retrieves the test restults stored so far
	 */
	Vector getTestResults(ClientHandle handle);

	/**
	 * Return the user ID of a person, given their details.
	 */
	ClientHandle createUser(String surname, String forename, String school, Calendar date)
		throws LoginError;

	/**
	 * Change the user's password
	 */
	void changePassword(ClientHandle handle, String oldPassword, String newPassword)
		throws LoginError;

	/**
	 * Return the current UserID.
	 */
	String getUserID(ClientHandle handle);

	/**
	 * Log out the user.
	 */
	void logout(ClientHandle handle);
}