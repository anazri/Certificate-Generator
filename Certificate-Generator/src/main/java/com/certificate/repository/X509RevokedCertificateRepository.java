package com.certificate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.certificate.model.X509RevokedCertificate;
import com.certificate.service.X509RevokedCertificateService;

public interface X509RevokedCertificateRepository extends JpaRepository<X509RevokedCertificate, Long> {


}
