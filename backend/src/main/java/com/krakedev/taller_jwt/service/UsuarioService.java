package com.krakedev.taller_jwt.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.taller_jwt.entity.Usuario;
import com.krakedev.taller_jwt.repository.UsuarioRepository;

// Logica de negocio de los usuarios
@Service
public class UsuarioService {

	// Repositorio para guardar y buscar usuarios
	private final UsuarioRepository usuarioRepository;

	// Spring entrega el repositorio solo
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	// Cifra la contrasena y guarda el usuario
	public Usuario guardar(Usuario usuario) {

		// BCrypt: hash de un solo sentido con salt aleatorio
		String constrasenaEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

		// Reemplazo la clave en texto plano por el hash
		usuario.setPassword(constrasenaEncriptada);

		return usuarioRepository.save(usuario);
	}

	// Devuelve true si el usuario existe y la contrasena coincide
	public boolean autenticar(String username, String password) {
		// Busco al usuario; Optional evita errores si no existe
		Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();

			// checkpw compara la clave escrita con el hash (nunca se usa equals)
			if (BCrypt.checkpw(password, usuario.getPassword())) {
				return true;
			}
		}
		return false;

	}

}
