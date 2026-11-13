package com.krakedev.taller_jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krakedev.taller_jwt.entity.Pelicula;

// Repositorio: se encarga de hablar con la base de datos
// JpaRepository ya trae save, findAll, findById y deleteById sin escribirlos
// <Pelicula, Integer> = la entidad que maneja y el tipo de su id
public interface PeliculaRepository  extends JpaRepository<Pelicula, Integer>{

}
