package com.certificate.controller;

import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.certificate.service.KeyStoreService;

@Controller
@RestController
@RequestMapping("/keystores")
public class KeystoreController {
	
	@Autowired
	private KeyStoreService keyStoreService;
	
	@Autowired
	private HttpSession session;
	
	@RequestMapping(value="/create",
					method=RequestMethod.GET)
	public ResponseEntity<?> createKeyStore(){
		try {
			KeyStore keyStore=keyStoreService.loadKeyStore(null, "".toCharArray());
			session.setAttribute("store", keyStore);
		} catch (KeyStoreException | NoSuchProviderException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/save/{file}/{password}",
			method=RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveKeyStore(@PathVariable("file") String file, @PathVariable("password") String password){
		KeyStore keyStore=(KeyStore) session.getAttribute("store");
		if(keyStore==null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		keyStoreService.saveKeyStore(keyStore, file, password.toCharArray());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/load/{password}",
			method=RequestMethod.POST,
			consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> loadKeyStore(@PathVariable("password")String password, @RequestParam(value = "file", required = false) MultipartFile fileMultipart	) throws IllegalStateException, IOException{
		try {
			if(fileMultipart != null){
				String orgName = fileMultipart.getOriginalFilename();
		        String filePath = "C:\\TempUploads\\" + orgName;
		        
				File fileObj = new File(filePath);
				fileMultipart.transferTo(fileObj);
				
				KeyStore keyStore=keyStoreService.loadKeyStore(fileObj, password.toCharArray());
				session.setAttribute("store", keyStore);
			}
		} catch (KeyStoreException | NoSuchProviderException | IOException e) {
			return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
