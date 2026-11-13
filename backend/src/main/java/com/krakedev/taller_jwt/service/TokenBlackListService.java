package com.krakedev.taller_jwt.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

// Guarda los tokens de sesiones cerradas (lista negra)
@Service
public class TokenBlackListService {

	// Set seguro para varios hilos; vive en memoria, se pierde al reiniciar la app
	private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

	// Agrega el token a la lista negra (se usa en el logout)
	public void invalidarToken(String token) {
		blacklist.add(token);
	}

	// Dice si el token ya fue cerrado
	public boolean estaInvalidado(String token) {
		return blacklist.contains(token);
	}

}
