package com.certificate.controller;



import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
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
	public ResponseEntity<?> generateRootCertificate(@RequestBody CertificateData certData) throws CertificateParsingException{
		KeyStore keyStore=(KeyStore) session.getAttribute("store");
		if(keyStore==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		X500Name x500name=generateName(certData);
	    
	    KeyPair newKeyPair = keyPairService.generateKeyPair(certData.getKeySize());
	    
	    Calendar cal = Calendar.getInstance();
	    Date startDate = new Date();
	    cal.add(Calendar.DATE, certData.getNumberOfDays());
	    Date endDate = cal.getTime();
	    
		IssuerData issuer = new IssuerData(newKeyPair.getPrivate(), x500name);
		
		try {
			SubjectData subject = new SubjectData(newKeyPair.getPublic(), x500name, ""+keyStore.size(), startDate, endDate);
			X509Certificate cert = certGen.generateCertificate(subject, issuer, true);
			keyStoreService.write(keyStore,null,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (CertIOException | KeyStoreException e) {
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
	    
	    try {
	    	SubjectData subject = new SubjectData(newKeyPair.getPublic(), subjectData, ""+keyStore.size(), startDate, endDate);
	    	IssuerData issuer=keyStoreService.validateCa(keyStore, parentAlias, parentPassword.toCharArray());
			X509Certificate cert = certGen.generateCertificate(subject, issuer, certData.isCa());
			keyStoreService.write(keyStore,parentAlias,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (KeyStoreException | CertIOException | UnrecoverableKeyException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/getExisting/{certificateID}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<X509Certificate> getExistingCertificate(@PathVariable("certificateID") String certificateID){
		KeyStore keyStore=(KeyStore)session.getAttribute("store");
		try {
			X509Certificate certificate=keyStoreService.getSertificateBySerialNumber(keyStore, certificateID);
			System.out.println(certificate);
			return new ResponseEntity<X509Certificate>(certificate, HttpStatus.OK);
		} catch (NullPointerException | KeyStoreException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/revoke/{certificateID}",
			method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<X509Certificate> revokeExistingCertificate(@PathVariable("certificateID") String certificateID){
		KeyStore keyStore=(KeyStore)session.getAttribute("store");
		try {
			keyStoreService.revokeCertificate(keyStore, certificateID);
		} catch (NullPointerException  | KeyStoreException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
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
