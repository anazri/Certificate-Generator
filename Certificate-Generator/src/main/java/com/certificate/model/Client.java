package com.certificate.model;

import javax.persistence.Entity;

@Entity
public class Client extends User{

	private static final long serialVersionUID = 3869686168651898625L;

	private String certificateAlias;
	
	public Client() {
	}

	public String getCertificateAlias() {
		return certificateAlias;
	}

	public void setCertificateAlias(String certificateAlias) {
		this.certificateAlias = certificateAlias;
	}
	
}
