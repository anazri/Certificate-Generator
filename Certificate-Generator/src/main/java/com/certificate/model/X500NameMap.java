package com.certificate.model;

import java.io.Serializable;

public class X500NameMap implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8067874383632836659L;
 
	private String cn;
	private String surname;
	private String givenName;
	private String o;
	private String ou;
	private String c;
	private String e;
	
	
	
	public X500NameMap(String cn, String surname, String givenName, String o, String ou, String c, String e) {
		super();
		this.cn = cn;
		this.surname = surname;
		this.givenName = givenName;
		this.o = o;
		this.ou = ou;
		this.c = c;
		this.e = e;
	}
	public X500NameMap() {
	}
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getO() {
		return o;
	}
	public void setO(String o) {
		this.o = o;
	}
	public String getOu() {
		return ou;
	}
	public void setOu(String ou) {
		this.ou = ou;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	
	
	
}
