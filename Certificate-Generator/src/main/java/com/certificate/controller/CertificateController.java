package com.certificate.controller;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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

import com.certificate.model.CertificateRequest;
import com.certificate.model.CertificateResponse;
import com.certificate.model.IssuerData;
import com.certificate.model.SubjectData;
import com.certificate.model.X509RevokedCertificate;
import com.certificate.service.CertificateService;
import com.certificate.service.KeyPairService;
import com.certificate.service.KeyStoreService;
import com.certificate.service.X509RevokedCertificateImpl;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
	
	@Autowired
	private X509RevokedCertificateImpl x509RevokedCert;
	
	@Autowired
	private CertificateService certGen;
	
	@Autowired
	private KeyPairService keyPairService;
	
	@Autowired
	private KeyStoreService keyStoreService;
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping(value="/getCertificates", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getCertificates() throws KeyStoreException{
		KeyStore keyStore=(KeyStore) session.getAttribute("store");
		if(keyStore==null){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<CertificateResponse> list = certGen.getSertificates(keyStore);
		
		
		return new ResponseEntity<ArrayList<CertificateResponse>>(list,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getVerification/{email}", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getVerification(@PathVariable("email") String email) throws KeyStoreException{

		Boolean b = certGen.getVerification(email);
		
		
		return new ResponseEntity<Boolean>(b,HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/generateRoot", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateRootCertificate(@RequestBody CertificateRequest certData) throws CertificateParsingException{
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
			SubjectData subject = new SubjectData(newKeyPair.getPublic(), x500name, ""+certData.getSerialNumber(), startDate, endDate);
			X509Certificate cert = certGen.generateCertificate(subject, issuer, true);
			CertificateResponse cr = certGen.map(cert);
			keyStoreService.write(keyStore,null,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			return new ResponseEntity<CertificateResponse>(cr,HttpStatus.OK);
		} catch (CertIOException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/generateCertificate/{parentAlias}/{parentPassword}",
					method=RequestMethod.POST,
					consumes=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> generateCertificate(@RequestBody CertificateRequest certData, @PathVariable("parentAlias")String parentAlias, @PathVariable("parentPassword")String parentPassword){
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
			CertificateResponse cr = certGen.map(cert);
			keyStoreService.write(keyStore,parentAlias,certData.getAlias(), newKeyPair.getPrivate(), certData.getPassword().toCharArray(),(Certificate) cert);
			System.out.println(cert);
			return new ResponseEntity<CertificateResponse>(cr,HttpStatus.OK);
		} catch (KeyStoreException | CertIOException | UnrecoverableKeyException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/getExisting/{certificateID}",
			method=RequestMethod.GET,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getExistingCertificate(@PathVariable("certificateID") String certificateID){
		KeyStore keyStore=(KeyStore)session.getAttribute("store");
		try {
			X509Certificate certificate=keyStoreService.getSertificateBySerialNumber(keyStore, certificateID);
			CertificateResponse cr = certGen.map(certificate);
			
			return new ResponseEntity<CertificateResponse>(cr, HttpStatus.OK);
		} catch (NullPointerException | KeyStoreException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value="/getStatus/{serialNumber}",
			method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> getRevocationStatus(@PathVariable("serialNumber") String certificateID){
		System.out.println(certificateID);
			if(x509RevokedCert.findBySerialNumber(Long.parseLong(certificateID)) != null){
				return new ResponseEntity<Boolean>(new Boolean(false), HttpStatus.OK);
			} 
			return new ResponseEntity<Boolean>(new Boolean(true), HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/revoke/{certificateID}",
			method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Iterable<CertificateResponse>> revokeExistingCertificate(@PathVariable("certificateID") String certificateID){
		KeyStore keyStore=(KeyStore)session.getAttribute("store");
		try {
			Iterable<X509Certificate> temp=keyStoreService.revokeCertificate(keyStore, certificateID);
			ArrayList<CertificateResponse> deleted=new ArrayList<>();
			for(X509Certificate cert : temp){
				deleted.add(certGen.map(cert));
				try {
					X509RevokedCertificate tmp = new X509RevokedCertificate();
					tmp.setX509Bytes(cert.getEncoded());
					boolean ca = cert.getKeyUsage() != null ? true : false;
					tmp.setCa(ca);
					tmp.setSerialNumber(cert.getSerialNumber().longValue());
					x509RevokedCert.save(tmp);
				} catch (CertificateEncodingException e) {
					e.printStackTrace();
				}
			}
			return new ResponseEntity<Iterable<CertificateResponse>>(deleted, HttpStatus.OK);
		} catch (NullPointerException  | KeyStoreException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	private X500Name generateName(CertificateRequest certData){
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
	    builder.addRDN(BCStyle.CN, certData.getAlias());
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
