package com.certificate.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Service;

import com.certificate.model.CertificateResponse;
import com.certificate.model.IssuerData;
import com.certificate.model.SubjectData;
import com.certificate.model.X500NameMap;
import com.certificate.user.GetUserRequest;
import com.certificate.user.GetUserResponse;

import certificate.com.webservice.client.BankClientUser;




@Service
public class CertificateService {
	
	
	public CertificateService() {}
	
	@Autowired
	private ApplicationContext con;
	
	public ArrayList<CertificateResponse> getSertificates(KeyStore keyStore) throws KeyStoreException{
		ArrayList<CertificateResponse> list = new ArrayList<CertificateResponse>();
		Enumeration<String> aliases=keyStore.aliases();
		while(aliases.hasMoreElements()){
			String alias=aliases.nextElement();
			X509Certificate temp=(X509Certificate) keyStore.getCertificate(alias);
			list.add(this.map(temp));
		}
		return list;
	}
	
	public CertificateResponse map(X509Certificate cert){
		CertificateResponse response = new CertificateResponse();
		this.setX500Name(new X500Name(cert.getSubjectX500Principal().getName(X500Principal.RFC1779)),response.getSubjectData());
		this.setX500Name(new X500Name(cert.getIssuerX500Principal().getName(X500Principal.RFC1779)),response.getIssuerData());
		response.setStartDate(cert.getNotBefore());
		response.setEndDate(cert.getNotAfter());
		response.setSerialNumber(""+cert.getSerialNumber());
		response.setAlias(response.getSubjectData().getCn());
		response.setKeySize(((RSAPublicKey)cert.getPublicKey()).getModulus().bitLength());
		return response;
	}
	
	public boolean getVerification(String email){
		BankClientUser bcu = (BankClientUser) con.getBean(BankClientUser.class);
		GetUserRequest request = new GetUserRequest();
		request.setEmail(email);
		GetUserResponse response = new GetUserResponse();
		
		try {
			response = bcu.getUserResponse(request);
		} catch (XmlMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response.isVerified();
		
	}
	
	
	private void setX500Name(X500Name nameData,X500NameMap nameMapped){
		nameMapped.setC(IETFUtils.valueToString((nameData.getRDNs(BCStyle.C)[0]).getFirst().getValue()));
		nameMapped.setCn(IETFUtils.valueToString((nameData.getRDNs(BCStyle.CN)[0]).getFirst().getValue()));
		nameMapped.setE(IETFUtils.valueToString((nameData.getRDNs(BCStyle.E)[0]).getFirst().getValue()));
		nameMapped.setGivenName(IETFUtils.valueToString((nameData.getRDNs(BCStyle.GIVENNAME)[0]).getFirst().getValue()));
		nameMapped.setO(IETFUtils.valueToString((nameData.getRDNs(BCStyle.O)[0]).getFirst().getValue()));
		nameMapped.setOu(IETFUtils.valueToString((nameData.getRDNs(BCStyle.OU)[0]).getFirst().getValue()));
		nameMapped.setSurname(IETFUtils.valueToString((nameData.getRDNs(BCStyle.SURNAME)[0]).getFirst().getValue()));
	}
	
	public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData,boolean ca) throws CertIOException {
		try {
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			
			BouncyCastleProvider bcp = new BouncyCastleProvider();
			builder = builder.setProvider(bcp);

			ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

			X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					new BigInteger(subjectData.getSerialNumber()),
					subjectData.getStartDate(),
					subjectData.getEndDate(),
					subjectData.getX500name(),
					subjectData.getPublicKey());
			if(ca)
				certGen.addExtension(Extension.keyUsage,true, new KeyUsage(KeyUsage.keyCertSign));

			
			X509CertificateHolder certHolder = certGen.build(contentSigner);

			JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			certConverter = certConverter.setProvider(bcp);

			return certConverter.getCertificate(certHolder);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (OperatorCreationException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}
}
