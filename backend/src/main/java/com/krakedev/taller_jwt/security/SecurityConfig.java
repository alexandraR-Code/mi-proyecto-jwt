package com.krakedev.taller_jwt.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Clase de configuracion de seguridad
@Configuration
// Activa @PreAuthorize para proteger metodos por rol
@EnableMethodSecurity
public class SecurityConfig {

	// Mi filtro JWT
	private final JwtAutenticationFilter jwtAutenticationFilter;

	// Spring entrega el filtro solo
	public SecurityConfig(JwtAutenticationFilter jwtAutenticationFilter) {
		super();
		this.jwtAutenticationFilter = jwtAutenticationFilter;
	}

	// Define las reglas de seguridad de la API
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {

		// cors: permite las llamadas desde React; csrf desactivado porque la API no usa sesiones con cookies
		// STATELESS: no se guarda sesion, cada peticion trae su token
		// /auth/login y /auth/registrar son publicas; todo lo demas exige token
		// Mi filtro JWT corre antes del filtro de usuario y contrasena de Spring
		return http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/login", "/auth/registrar").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAutenticationFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	// Define que origen y metodos puede usar el frontend (CORS)
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		// Solo el frontend de React (puerto 5173) puede llamar a la API
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		// Metodos HTTP permitidos
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		// Encabezados permitidos (el token va en Authorization)
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);

		// Aplica esta configuracion a todas las rutas
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;

	}

}
