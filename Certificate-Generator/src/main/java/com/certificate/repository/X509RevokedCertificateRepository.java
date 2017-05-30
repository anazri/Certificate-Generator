package com.certificate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.certificate.model.X509RevokedCertificate;

public interface X509RevokedCertificateRepository extends JpaRepository<X509RevokedCertificate, Long> {
	
	@Query("select cert from X509RevokedCertificate as cert where cert.serialNumber = ?1")
	X509RevokedCertificate findBySerialNumber(Long serialNumber);
	
}
