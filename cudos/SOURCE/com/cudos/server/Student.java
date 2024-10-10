package com.cudos.server;

import java.util.*;
import java.net.*;
import com.cudos.server.client.*;
import java.io.*;

public class Student {
		//personal fields
	String firstName;
	String middleInitials;
	String lastName;
	String school;
	String userID;
	Date dob;

		//last login
	Vector loginDates;
	InetAddress lastLoginAddress;

		//private fields
	private String password;

	Object details;

	Vector files = new Vector();
	Vector fileNames = new Vector();
	Vector testResults = new Vector();

	int sid;
	static int serialid=0;

	public Student(String forename,String middleInitials,String surname, String sch){
		firstName=forename;
		this.middleInitials=middleInitials;
		lastName=surname;
		school=sch;
		sid=serialid++;
	}
	public Student(String forename,String surname){
		this(forename,"",surname,"");
	}
	public String getName(){
		return firstName+" "+middleInitials+". "+lastName;
	}
	public String getUserID(){return userID;}
	public void setDob(int year,int month,int date){
		Calendar c=Calendar.getInstance();
		c.set(year,month,date);
		dob=c.getTime();	//translate y/m/d into universal time
	}
	public void addTestResult(TestResult t){
		testResults.add(t);
	}
	public Object getSavedFile(String name){
		for(int i=0;i<fileNames.size();i++){
			if(((String)fileNames.get(i)).equalsIgnoreCase(name)){
				return files.get(i);
			}
		}
		return null;
	}

	public boolean checkPassword(String pass){
		return pass.equalsIgnoreCase(this.password);
	}

	public Date getLastLoginDate(){
		if(loginDates.size()==0)return null;
		return (Date)loginDates.get(loginDates.size()-1);
	}
	public void addLoginDate(){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		loginDates.add(c.getTime());
	}

	protected void dump(PrintStream o){
		o.println("Name: "+firstName+" "+lastName); o.println("DOB: "+dob);
		o.println("School: "+school);		o.println("UserID: "+userID);
		o.print("Logins: ");	for(int i=0;i<loginDates.size();i++){
			if(i>0)o.print(", ");o.print(loginDates.get(i));  }		o.println();
		o.print("Files: ");   for(int i=0;i<fileNames.size();i++){
			if(i>0)o.print(", ");o.print(fileNames.get(i)); }     o.println();
		o.print("Test results: "); for(int i=0;i<testResults.size();i++){
			if(i>0)o.print(", ");o.print(testResults.get(i)); }   o.println();
	}

	public void changePassword(String oldPassword, String newPassword) throws LoginError{
		if(!checkPassword(oldPassword)) throw new LoginError("Incorrect password. Password not changed.");
		password = newPassword;
	}
}