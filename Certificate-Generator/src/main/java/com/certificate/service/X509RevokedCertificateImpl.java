package com.certificate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.certificate.model.X509RevokedCertificate;
import com.certificate.repository.X509RevokedCertificateRepository;

@Service
public class X509RevokedCertificateImpl implements X509RevokedCertificateService {
	
	@Autowired
	X509RevokedCertificateRepository x509Cert;

	@Override
	public void save(X509RevokedCertificate cert) {
		// TODO Auto-generated method stub
		x509Cert.save(cert);
	}

}
