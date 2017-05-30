package com.certificate.service;

import com.certificate.model.X509RevokedCertificate;

public interface X509RevokedCertificateService {

	public void save(X509RevokedCertificate cert);
	
	public X509RevokedCertificate findBySerialNumber(Long serialNumber);
}
