package com.certificate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable{

	private static final long serialVersionUID = -4183179901882702679L;

	@Id
	@SequenceGenerator(name = "USER_ID_GEN", allocationSize = 10)
	@GeneratedValue(generator = "USER_ID_GEN")
	private Long id;
	
	@Column
	@NotEmpty
	private String name;
	
	@Column
	@NotEmpty
	private String surname;
	
	@Column
	@NotEmpty
	private String email;
	
	@Column
	@NotEmpty
	private String password;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
	
}
