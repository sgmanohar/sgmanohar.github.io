package com.cudos.server;

import com.cudos.server.client.*;

import java.io.*;
import java.rmi.server.*;
import java.rmi.*;
import java.util.*;

/**
 * The actual implementation of the CUDOS server.
 */

public class CudosServer extends UnicastRemoteObject implements Server{

	/**
	 * Create a server, and bind it to the Cudos server location.
	 */
	public static void main(String[] argv){
		try{
			CudosServer s = new CudosServer();
			Naming.rebind(Server.location, s);
		}catch(Exception e){
			System.out.println("Server not initialised:");
			e.printStackTrace();
		}
	}


	public CudosServer() throws RemoteException{
		super();
		try{	    //load student list
			students = (StudentList) new ObjectInputStream(new FileInputStream(studentFile)).readObject();
		}catch(Exception e){
			System.out.println("Student file could not be loaded:");
			e.printStackTrace();
		}
	}

	private String studentFile = "E://students.ser";
	private StudentList students;
	/** Mappings from current ClientHandles to Students */
	private Hashtable connectionTable = new Hashtable();


	/**
	 * Create a random unique user ID for the given student
	 */
	private void createID(Student s){
		String id = "";
		do{
			id = s.firstName.substring(0,3) + s.lastName.substring(0,3) +
				String.valueOf((int)(Math.random()*1000));
		}while(students.getStudentByUserID(id)!=null);
		s.userID = id;
	}






	///   Implementation of Interface : com.cudos.server.client.Server


	public ClientHandle login(String userName, String password) throws LoginError{
		Student s = students.getStudentByUserID(userName);
		if(s==null) throw new LoginError("No such student '"+userName+"'");
		if(!s.checkPassword(password)) throw new LoginError("Incorrect password");
		ClientHandle handle = new ClientHandle();
		connectionTable.put(handle, s);
		s.addLoginDate(); //log the time in the student's record
		return handle;
	}

	public void logout(ClientHandle h){
		connectionTable.remove(h);
	}

	public String[] getFilenames(ClientHandle h){
		return (String[])  ((Student)connectionTable.get(h)) . fileNames.toArray(new String[0]);
	}

	public void saveFile(ClientHandle h, String filename, Object file){
		Student s = (Student)connectionTable.get(h);
		for(int i=0;i<s.fileNames.size();i++){
			if( filename.equalsIgnoreCase( (String)s.fileNames.get(i) ) ){
				s.files.set(i, file); return;
			}
		}
		s.fileNames.add(filename);
		s.files.add(file);
	}

	public void deleteFile(ClientHandle h, String filename){
		Student s = (Student)connectionTable.get(h);
		for(int i=0;i<s.fileNames.size();i++){
			if(filename.equalsIgnoreCase( (String)s.fileNames.get(i)) ){
				s.files.remove(i);
				s.fileNames.remove(i);
				break;
			}
		}
	}

	public Object getFile(ClientHandle h, String filename){
		Student s = (Student)connectionTable.get(h);
		for(int i=0;i<s.fileNames.size();i++){
			if(filename.equalsIgnoreCase( (String)s.fileNames.get(i)) ){
				return s.files.get(i);
			}
		}
		return null;
	}

	public void addTestResult(ClientHandle h, TestResult r){
		((Student)connectionTable.get(h)).addTestResult(r);
	}

	public Vector getTestResults(ClientHandle h){
		return ((Student)connectionTable.get(h)).testResults;
	}

	public ClientHandle createUser(String surname, String forename, String school,
				Calendar date) throws LoginError {
		Student s = new Student(forename, "", surname, school);
		s.dob = date.getTime();
		createID(s);
		return login( s.userID, "" );
	}

	public void changePassword(ClientHandle h, String oldPassword, String newPassword)
				throws LoginError{
		((Student)connectionTable.get(h)).changePassword(oldPassword,newPassword);
	}

	public String getUserID(ClientHandle handle){
		return ((Student)connectionTable.get(handle)).getUserID();
	}

}