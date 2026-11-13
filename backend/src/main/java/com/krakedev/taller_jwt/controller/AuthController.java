package com.krakedev.taller_jwt.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.taller_jwt.JwtUtil;
import com.krakedev.taller_jwt.entity.Usuario;
import com.krakedev.taller_jwt.repository.UsuarioRepository;
import com.krakedev.taller_jwt.service.TokenBlackListService;
import com.krakedev.taller_jwt.service.UsuarioService;

// Controlador de autenticacion: registro, login, perfil y logout
@RestController
// Ruta base: los endpoints empiezan con /auth
@RequestMapping("/auth")
public class AuthController {

	// Servicios y repositorio que usa este controlador
	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;
	private final TokenBlackListService blackListService;

	// Spring los entrega solo (inyeccion de dependencias)
	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
			TokenBlackListService blackListService) {
		super();
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
		this.blackListService = blackListService;
	}

	// POST /auth/registrar: crea un usuario nuevo (la clave se guarda cifrada)
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			// El servicio cifra la contrasena con BCrypt y guarda el usuario
			Usuario usuarioNuevo = usuarioService.guardar(usuario);

			// 201 Created: usuario registrado
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);

		} catch (Exception e) {

			// Si falla, responde 500 con el mensaje del error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el usuario: " + e.getMessage());
		}
	}

	// POST /auth/login: valida usuario y contrasena y devuelve el token JWT
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {

		// Saco el usuario y la contrasena del JSON recibido
		String username = credenciales.get("username");
		String password = credenciales.get("password");

		// Compara la contrasena escrita con el hash guardado (BCrypt)
		boolean autenticado = usuarioService.autenticar(username, password);

		if (autenticado) {

			// Busco al usuario para conocer su rol
			Usuario usuario = usuarioRepository.findByUsername(username).get();

			// Genero el token JWT con el usuario y el rol
			String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

			// 200 OK con el token
			return ResponseEntity.ok(Map.of("token", token));

		} else {
			// 401: usuario o contrasena incorrectos
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contrasena incorrecta");
		}

	}

	// GET /auth/perfil: muestra quien esta autenticado (requiere token)
	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil() {

		// SecurityContextHolder guarda al usuario que el filtro JWT autentico
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String usuario = auth.getName();
		// Rol del usuario (viene con el prefijo ROLE_)
		String rol = auth.getAuthorities().iterator().next().getAuthority();

		return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido al sistema protegido por Spring Security", "Usuario",
				usuario, "rol_detectado", rol, "status", "Autenticado existosamente"));

	}

	// POST /auth/logout: cierra la sesion metiendo el token en la lista negra
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authheader) {
		if (authheader != null && authheader.startsWith("Bearer ")) {

			// Quito "Bearer " (7 caracteres) para quedarme solo con el token
			String token = authheader.substring(7);
			// Desde aqui el token queda invalido aunque no haya vencido
			blackListService.invalidarToken(token);

			return ResponseEntity.ok(Map.of("Mensaje", "Sesion cerrada exitosamente. Token invalidado"));
		} else {
			// 400: no enviaron el token
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado: ");
		}
	}

	// GET /auth/dashboard: solo entra un usuario con rol ADMIN
	@GetMapping("/dashboard")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> adminDashboard() {
		String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
		
		return ResponseEntity.ok(Map.of("Mensaje", "Bienvenido al panel secreto del administrador", "admin", usuario));

	}

}
