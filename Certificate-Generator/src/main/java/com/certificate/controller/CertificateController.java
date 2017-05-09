package com.certificate.controller;



import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.certificate.service.KeyStoreService;

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
	private KeyStoreService keyStoreService;
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping(value="/generateRoot", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateRootCertificate(@RequestBody CertificateData certData){
	    X500Name x500name=generateName(certData);
	    
	    KeyPair newKeyPair = keyPairService.generateKeyPair(certData.getKeySize());
	    
	    Calendar cal = Calendar.getInstance();
	    Date startDate = new Date();
	    cal.add(Calendar.DATE, certData.getNumberOfDays());
	    Date endDate = cal.getTime();
	    
		IssuerData issuer = new IssuerData(newKeyPair.getPrivate(), x500name);
		SubjectData subject = new SubjectData(newKeyPair.getPublic(), x500name, "1", startDate, endDate);
		
		try {
			X509Certificate cert = certGen.generateCertificate(subject, issuer, true);
			KeyStore keyStore=(KeyStore) session.getAttribute("store");
			if(keyStore==null){
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			keyStoreService.write(keyStore,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (CertIOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/generateCertificate/{parentAlias}/{parentPassword}",
					method=RequestMethod.POST,
					consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateCertificate(@RequestBody CertificateData certData, @PathVariable("parentAlias")String parentAlias, @PathVariable("parentPassword")String parentPassword){
		KeyStore keyStore=(KeyStore)session.getAttribute("store");
		X500Name subjectData=generateName(certData);
		
		KeyPair newKeyPair = keyPairService.generateKeyPair(certData.getKeySize());
		
		Calendar cal = Calendar.getInstance();
	    Date startDate = new Date();
	    cal.add(Calendar.DATE, certData.getNumberOfDays());
	    Date endDate = cal.getTime();
	    SubjectData subject = new SubjectData(newKeyPair.getPublic(), subjectData, "1", startDate, endDate);
	    
	    try {
	    	IssuerData issuer=keyStoreService.validateCa(keyStore, parentAlias, parentPassword.toCharArray());
			X509Certificate cert = certGen.generateCertificate(subject, issuer, certData.isCa());
			keyStoreService.write(keyStore,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (KeyStoreException | CertIOException | UnrecoverableKeyException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/getExisting/{certificateID}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateExistingCertificate(@PathVariable("certificateID") String certificateID){
		return null;
	}

	private X500Name generateName(CertificateData certData){
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
	    builder.addRDN(BCStyle.CN, certData.getCn());
	    builder.addRDN(BCStyle.SURNAME, certData.getSurname());
	    builder.addRDN(BCStyle.GIVENNAME, certData.getGivenName());
	    builder.addRDN(BCStyle.O, certData.getO());
	    builder.addRDN(BCStyle.OU, certData.getOu());
	    builder.addRDN(BCStyle.C, certData.getC());
	    builder.addRDN(BCStyle.E, certData.getE());
	    builder.addRDN(BCStyle.UID, "654321");
		return builder.build();
	}
	
}
