package com.krakedev.taller_jwt.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.taller_jwt.JwtUtil;
import com.krakedev.taller_jwt.service.TokenBlackListService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Filtro que revisa el token JWT en cada peticion, antes de llegar al controlador
@Component
public class JwtAutenticationFilter extends OncePerRequestFilter {

	// Lista negra de tokens de sesiones cerradas
	private final TokenBlackListService blackListService;

	// Spring entrega la lista negra solo
	public JwtAutenticationFilter(TokenBlackListService blackListService) {
		super();
		this.blackListService = blackListService;
	}

	// OncePerRequestFilter: este metodo se ejecuta una sola vez por peticion
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		// Leo el encabezado Authorization
		String authHeader = request.getHeader("Authorization");

		// Sin token: dejo pasar la peticion sin autenticar (Spring decide si la ruta es publica)
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// Quito "Bearer " (7 caracteres)
		String token = authHeader.substring(7);
		// Si el token es de una sesion cerrada, respondo 401 y corto aqui
		if (blackListService.estaInvalidado(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			response.getWriter().write("Acceso denegado: Sesion cerrada");
			return;
		}

		// Valido la firma y la expiracion del token
		DecodedJWT datosToken = JwtUtil.validarToker(token);

		// Token valido: saco el usuario y el rol
		if (datosToken != null) {
			String username = datosToken.getSubject();
			String rolOriginal = datosToken.getClaim("rol").asString();

			// Spring exige el prefijo ROLE_ en los roles
			String rolSpring = "ROLE_" + rolOriginal;

			// Permiso (rol) del usuario
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);

			// Objeto que representa al usuario ya autenticado
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
					Collections.singleton(authority));

			// Lo guardo en el contexto: asi Spring sabe quien hace la peticion
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// Continuo con el siguiente filtro o con el controlador
		filterChain.doFilter(request, response);
	}

}
