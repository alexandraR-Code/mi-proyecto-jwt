package com.krakedev.taller_jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Esta clase representa la tabla "usuarios" de la base de datos
@Entity
@Table(name = "usuarios")
public class Usuario {

	// Llave primaria autoincremental
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Nombre de usuario: obligatorio y no se puede repetir
	@Column(nullable = false, unique = true)
	private String username;

	// Contrasena: obligatoria (se guarda cifrada con BCrypt)
	@Column(nullable = false)
	private String password;

	// Rol del usuario, por ejemplo USER o ADMIN
	private String rol;

	// Constructor vacio que necesita JPA
	public Usuario() {
	}

	// Constructor con los datos del usuario
	public Usuario(String username, String password, String rol) {
		this.username = username;
		this.password = password;
		this.rol = rol;
	}

	// Getters y setters de cada atributo
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

}
