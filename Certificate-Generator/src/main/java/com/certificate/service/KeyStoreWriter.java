package com.certificate.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class KeyStoreWriter {
	private KeyStore keyStore;

	public KeyStoreWriter() {
		try {
			keyStore = KeyStore.getInstance("JKS", "SUN");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	public void loadKeyStore(String fileName, char[] password) {
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
	}

	public void saveKeyStore(String fileName, char[] password) {
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

	public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {

			/*
			 * X500Name x500name = new
			 * JcaX509CertificateHolder((X509Certificate)
			 * certificate).getIssuer(); RDN cn =
			 * x500name.getRDNs(BCStyle.CN)[0]; String parentAlias =
			 * IETFUtils.valueToString(cn.getFirst().getValue()); Certificate[]
			 * certificates= keyStore.getCertificateChain(parentAlias);
			 */

			Certificate[] certificates = keyStore
					.getCertificateChain(((X509Certificate) certificate).getIssuerX500Principal().getName());

			if (certificates != null && certificates.length != 0) {

				ArrayList<Certificate> temp = (ArrayList<Certificate>) Arrays.asList(certificates);
				temp.add(certificate);

				keyStore.setKeyEntry(alias, privateKey, password, (Certificate[]) temp.toArray());
			} else {
				keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] { certificate });
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
}
