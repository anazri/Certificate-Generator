package com.certificate.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CertificateResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -752681635695312182L;

	private X500NameMap subjectData;
	private X500NameMap issuerData;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
	private Date startDate;
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
	private Date endDate;
	private String serialNumber;
	private String alias;
	private String algorithm;
	private int keySize;
	
	public CertificateResponse(X500NameMap subjectData, X500NameMap issuerData, Date startDate, Date endDate,
			String serialNumber, String alias, String algorithm,int keySize) {
		super();
		this.subjectData = subjectData;
		this.issuerData = issuerData;
		this.startDate = startDate;
		this.endDate = endDate;
		this.serialNumber = serialNumber;
		this.alias = alias;
		this.algorithm = algorithm;
		this.setKeySize(keySize);
	}
	
	public CertificateResponse(){
		this.algorithm = "SHA256withRSA";
		this.subjectData = new X500NameMap();
		this.issuerData = new X500NameMap();
	}
	public X500NameMap getSubjectData() {
		return subjectData;
	}
	public void setSubjectData(X500NameMap subjectData) {
		this.subjectData = subjectData;
	}
	public X500NameMap getIssuerData() {
		return issuerData;
	}
	public void setIssuerData(X500NameMap issuerData) {
		this.issuerData = issuerData;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	

}
