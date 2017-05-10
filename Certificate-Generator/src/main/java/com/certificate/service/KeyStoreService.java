package com.certificate.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import com.certificate.model.IssuerData;

@Service
public class KeyStoreService {
	
	public KeyStoreService() {
		
	}
	/**
	 * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
	 * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
	 * 
	 * @param keyStoreFile - datoteka odakle se citaju podaci
	 * @param alias - alias putem kog se identifikuje sertifikat izdavaoca
	 * @param password - lozinka koja je neophodna da se otvori key store
	 * @param keyPass - lozinka koja je neophodna da se izvuce privatni kljuc
	 * @return - podatke o izdavaocu i odgovarajuci privatni kljuc
	 * @throws UnrecoverableKeyException 
	 */
	public IssuerData readIssuerFromStore(KeyStore keyStore, String alias, char[] keyPass) throws UnrecoverableKeyException {
		try {
			//Datoteka se ucitava
			//BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			//keyStore.load(in, password);
			//Iscitava se sertifikat koji ima dati alias
			Certificate cert = keyStore.getCertificate(alias);
			//Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
			PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

			X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
			return new IssuerData(privKey, issuerName);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Ucitava sertifikat is KS fajla
	 */
    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			ks.load(in, keyStorePass.toCharArray());
			
			if(ks.isKeyEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				return cert;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Ucitava privatni kljuc is KS fajla
	 */
	public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			ks.load(in, keyStorePass.toCharArray());
			
			if(ks.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
				return pk;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public KeyStore loadKeyStore(String fileName, char[] password) throws KeyStoreException, NoSuchProviderException {
		KeyStore keyStore = this.createNewKeyStore();
		try {
			if (fileName != null) {
				keyStore.load(new FileInputStream(fileName), password);
			} else {
				keyStore.load(null, password);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keyStore;
	}

	public void saveKeyStore(KeyStore keyStore,String fileName, char[] password) {
		try {
			keyStore.store(new FileOutputStream(fileName), password);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(KeyStore keyStore,String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			X500Name x500name = new JcaX509CertificateHolder((X509Certificate)certificate).getIssuer(); 
			RDN cn =x500name.getRDNs(BCStyle.CN)[0]; 
			String parentAlias = IETFUtils.valueToString(cn.getFirst().getValue()); 
			Certificate[] certificates=null;
			if(keyStore.containsAlias(parentAlias)){
				 certificates = keyStore.getCertificateChain(parentAlias);
			}
			if (certificates != null && certificates.length != 0) {
				ArrayList<Certificate> temp = (ArrayList<Certificate>) Arrays.asList(certificates);
				temp.add(certificate);
				keyStore.setKeyEntry(alias, privateKey, password, (Certificate[]) temp.toArray());
			} else {
				keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] { certificate });
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public KeyStore createNewKeyStore() throws KeyStoreException, NoSuchProviderException{
		return KeyStore.getInstance("JKS", "SUN");
	}
	
	public IssuerData validateCa(KeyStore keyStore, String alias, char[] keyPass) throws KeyStoreException, UnrecoverableKeyException{
		X509Certificate cert=(X509Certificate) keyStore.getCertificate(alias);
		IssuerData id=null;
		boolean validDate=true;
		try{
			cert.checkValidity(new Date());
		}catch (CertificateExpiredException | CertificateNotYetValidException e) {
			validDate=false;
		}
		if(validDate && cert.getKeyUsage()[5])
			id=this.readIssuerFromStore(keyStore, alias, keyPass);
		return id;
	}
	
	public X509Certificate getSertificateBySerialNumber(KeyStore keyStore,String certificateId) throws KeyStoreException{
		Enumeration<String> aliases=keyStore.aliases();
		while(aliases.hasMoreElements()){
			String alias=aliases.nextElement();
			X509Certificate temp=(X509Certificate) keyStore.getCertificate(alias);
			if(temp.getSerialNumber().toString().equals(certificateId))
				return temp;
		}
		return null;
	}
	
	
	
}
