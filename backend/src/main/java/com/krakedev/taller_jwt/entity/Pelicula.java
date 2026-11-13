package com.krakedev.taller_jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Esta clase representa la tabla "peliculas" de la base de datos
@Entity
@Table(name = "peliculas")
public class Pelicula {
	// Llave primaria: identifica cada pelicula; la base de datos la genera sola y la va aumentando
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	// Titulo de la pelicula, no puede quedar vacio
	@Column(nullable = false)
	private String titulo;
	
	// Director de la pelicula, no puede quedar vacio
	@Column(nullable = false)
	private String director;
	
	// Tipo de archivo de la foto (por ejemplo image/png), sirve para mostrarla bien despues
	@Column(name = "mime_type")
	private String mimeType;
	
	// La foto guardada como bytes; bytea es el tipo de PostgreSQL para archivos binarios
	@Column(name = "foto", columnDefinition = "bytea")
	private byte[] foto;

	// Constructor vacio: JPA lo necesita para crear objetos al leer la base de datos
	public Pelicula() {
		super();
	}

	// Constructor para crear una pelicula con todos sus datos de una vez
	public Pelicula(String titulo, String director, String mimeType, byte[] foto) {
		super();
		this.titulo = titulo;
		this.director = director;
		this.mimeType = mimeType;
		this.foto = foto;
	}

	// Getters y setters: sirven para leer y cambiar cada atributo
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	
	
	
	
	
}
