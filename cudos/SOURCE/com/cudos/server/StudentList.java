package com.cudos.server;

import java.util.*;

public class StudentList{
	Vector students;

	public void addStudent(Student s){
		students.add(s);
	}
	public Student getStudentByUserID(String userID){
		for(int i=0;i<students.size();i++){
			Student s = (Student)students.get(i);
			if(s.getUserID().equalsIgnoreCase(userID)) return s;
		}
		return null;
	}
}