package com.certificate.controller;



import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.certificate.model.CertificateData;
import com.certificate.model.IssuerData;
import com.certificate.model.SubjectData;
import com.certificate.service.CertificateGenerator;
import com.certificate.service.CertificateReader;
import com.certificate.service.KeyPairService;
import com.certificate.service.KeyStoreWriter;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
	
	@Autowired
	private CertificateGenerator certGen;
	
	@Autowired
	private CertificateReader certReader;
	
	@Autowired
	private KeyPairService keyPairService;
	
	@Autowired
	private KeyStoreWriter keyStoreWriter;
	
	@RequestMapping(value="/generate", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateCertificate(@RequestBody CertificateData certData){
		
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
	    builder.addRDN(BCStyle.CN, certData.getCn());
	    builder.addRDN(BCStyle.SURNAME, certData.getSurname());
	    builder.addRDN(BCStyle.GIVENNAME, certData.getGivenName());
	    builder.addRDN(BCStyle.O, certData.getO());
	    builder.addRDN(BCStyle.OU, certData.getOu());
	    builder.addRDN(BCStyle.C, certData.getC());
	    builder.addRDN(BCStyle.E, certData.getE());
	    builder.addRDN(BCStyle.UID, "654321");
	    
	    X500Name x500name = builder.build();
	    
	    KeyPair newKeyPair = keyPairService.generateKeyPair(certData.getKeySize());
	    
	    Calendar cal = Calendar.getInstance();
	    Date startDate = new Date();
	    cal.add(Calendar.DATE, certData.getNumberOfDays());
	    Date endDate = cal.getTime();
	    
		IssuerData issuer = new IssuerData(newKeyPair.getPrivate(), x500name);
		SubjectData subject = new SubjectData(newKeyPair.getPublic(), x500name, "1", startDate, endDate);
		
		try {
			Certificate cert = (Certificate) certGen.generateCertificate(subject, issuer);
			
			keyStoreWriter.write(((X509Certificate) cert).getSubjectX500Principal().getName(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(), cert);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (CertIOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
