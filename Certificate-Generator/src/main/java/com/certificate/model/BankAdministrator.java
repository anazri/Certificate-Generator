package com.certificate.model;

import javax.persistence.Entity;

@Entity
public class BankAdministrator extends User {

	private static final long serialVersionUID = -5458546902527103269L;

	private String organizationName;
	
	public BankAdministrator() {
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
}
