package com.cudos.server;

class StudentLoginData extends Student{
  String password;
	public String getTypedPassword(){return password;}

  public StudentLoginData(String forename, String middleInitials,
                          String surname, String sch) {
    super(forename, middleInitials, surname, sch);
  }
}
