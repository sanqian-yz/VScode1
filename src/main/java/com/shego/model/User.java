package com.shego.model;
import java.io.Serializable;
public class User implements Serializable { private int id; private String username,phone,role; public int getId(){return id;} public void setId(int v){id=v;} public String getUsername(){return username;} public void setUsername(String v){username=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getRole(){return role;} public void setRole(String v){role=v;} }
