package com.krakedev.taller_jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.krakedev.taller_jwt.entity.Usuario;

// Repositorio de usuarios: consulta la tabla usuarios
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	// Busca un usuario por su nombre (Spring arma la consulta con el nombre del metodo)
	Optional<Usuario> findByUsername(String username);

}
