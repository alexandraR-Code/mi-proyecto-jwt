package com.krakedev.taller_jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Date;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

// Clase de utilidad para crear y validar tokens JWT
public class JwtUtil {
	// Logger para mostrar errores en la consola
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	// Clave secreta con la que se firma el token (no debe compartirse)
	private static final String CLAVE_SECRETA = "EsttaEsUnaClaveSecretaMuyLarga123456789";
	// Emisor del token
	private static final String EMISOR = "KarkedevBackend";

	// Tiempo de vida del token en milisegundos
	private static final long TIEMPO_EXPIRACION = 1800000;

	// Crea el token JWT con usuario, rol, fecha de creacion y de vencimiento
	public static String generarToken(String username, String rol) {

		// Algoritmo de firma HMAC256 con la clave secreta
		Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

		// Calculo cuando vence el token
		long tiempoActual = System.currentTimeMillis();
		Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

		// Armo el token: emisor, usuario, fechas y el rol como dato extra
		String tokenGenerado = JWT.create().withIssuer(EMISOR).withSubject(username)
				.withIssuedAt(new Date(tiempoActual)).withExpiresAt(fechaExpiracion).withClaim("rol", rol)
				.sign(algoritmo);

		return tokenGenerado;
	}

	// Revisa firma, emisor y vencimiento; si es valido devuelve sus datos, si no devuelve null
	public static DecodedJWT validarToker(String token) {
		try {
			Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

			// Verificador que exige la misma clave y el mismo emisor
			JWTVerifier verificador = JWT.require(algoritmo).withIssuer(EMISOR).build();

			// Si el token es falso o ya vencio, aqui se lanza un error
			DecodedJWT tokenDecodificado = verificador.verify(token);

			return tokenDecodificado;

		} catch (Exception e) {

			// Si el token no es valido registro el error y devuelvo null
			logger.error("Error de validacion del token: " + e.getMessage());

			return null;

		}
	}

}
