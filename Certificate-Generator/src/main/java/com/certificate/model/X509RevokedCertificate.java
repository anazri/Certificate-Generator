package com.certificate.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class X509RevokedCertificate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7497032872676052467L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Lob @Basic(fetch = FetchType.LAZY)
	@Column(nullable = false, length=4147000)
	private byte[] x509Bytes;

	@Column(nullable = false)
	private long serialNumber;
	
	@Column(nullable = false)
	private boolean ca;
	
	public X509RevokedCertificate() {
		super();
	}
	
	public long getSerialNumber() {
		return serialNumber;
	}



	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}



	public boolean isCa() {
		return ca;
	}



	public void setCa(boolean ca) {
		this.ca = ca;
	}



	public Long getId() {
		return id;
	}

	public byte[] getX509Bytes() {
		return x509Bytes;
	}

	public void setX509Bytes(byte[] x509Bytes) {
		this.x509Bytes = x509Bytes;
	}
	
	
	
}
