package com.krakedev.taller_jwt.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.krakedev.taller_jwt.entity.Pelicula;
import com.krakedev.taller_jwt.repository.PeliculaRepository;


// Esta clase atiende peticiones HTTP y responde datos (JSON), no paginas
@RestController
// Ruta base: todos los endpoints empiezan con /auth/peliculas
@RequestMapping("auth/peliculas")
// Permite que el frontend de React (puerto 5173) llame a este backend
@CrossOrigin(origins = "http://localhost:5173")
public class PeliculaController {

	// Repositorio para guardar y consultar pelicula
	private final PeliculaRepository peliculaRepository;

	// Spring entrega el repositorio solo (inyeccion de dependencias)
	public PeliculaController(PeliculaRepository peliculaRepository) {
		super();
		this.peliculaRepository = peliculaRepository;
	}

	// POST /auth/pelicula/registrar: guarda un pelicula nuevo con su foto
	// Los datos llegan como formulario con archivo (multipart/form-data)
	@PostMapping
	public ResponseEntity<?> registrarPelicula(@RequestParam("file") MultipartFile file,
			@RequestParam("titulo") String titulo, @RequestParam("director") String director) {

		try {
			// Armo el pelicula con los datos que llegaron
			Pelicula pelicula = new Pelicula();
			pelicula.setTitulo(titulo);
			pelicula.setDirector(director);
			// Guardo el tipo de archivo de la foto
			pelicula.setMimeType(file.getContentType());
			// Convierto la foto en bytes para guardarla en la base de datos
			pelicula.setFoto(file.getBytes());

			// Guarda el pelicula en la base de datos
			peliculaRepository.save(pelicula);

			// 201 Created: el pelicula se registro correctamente
			return ResponseEntity.status(HttpStatus.CREATED).body("Pelicula registrada con exito");

		} catch (Exception e) {
			// Si algo falla, responde 500 con el mensaje del error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al procesar el archivo" + e.getMessage());

		}

	}

	@GetMapping()
	// Devuelve todos los peliculas registrados
	public ResponseEntity<?> listarPeliculas() {
		List<Pelicula> lista = peliculaRepository.findAll();

		for (Pelicula p : lista) {
			// Quito los bytes para que la lista sea liviana; la foto se pide aparte con
			// /{id}/foto
			p.setFoto(null);
		}

		return ResponseEntity.ok(lista);
	}

	// GET /auth/pelicula/{id}/foto: devuelve solo la imagen de un vehiculo
	@GetMapping("/{id}/foto")
	public ResponseEntity<byte[]> obtenerFotoPelicula(@PathVariable Integer id) {
		// Busco el pelicula por id; si no existe lanza un error
		Pelicula pelicula = peliculaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pelicula no encontrado"));

		String mimeType = pelicula.getMimeType();
		HttpHeaders headers = new HttpHeaders();
		// Le aviso al navegador que tipo de imagen es (png, jpg...)
		headers.setContentType(MediaType.parseMediaType(mimeType));
		// Devuelve los bytes de la foto con estado 200 OK
		return new ResponseEntity<>(pelicula.getFoto(), headers, HttpStatus.OK);

	}

	// DELETE /auth/pelicula/{id}: elimina un pelicula por su id
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminarPelicula(@PathVariable Integer id) {
		// Reviso si existe antes de borrar
		if (!peliculaRepository.existsById(id)) {
			// 404 Not Found: no hay un pelicula con ese id
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pelicula no encontrada");
		}

		// Lo elimino de la base de datos
		peliculaRepository.deleteById(id);

		// 200 OK: se elimino correctamente
		return ResponseEntity.ok("Pelicula eliminada con exito");
	}

	// PUT /auth/pelicula/{id}: edita titulo y director; la foto es opcional
	@PutMapping("/{id}")
	public ResponseEntity<?> editarPelicula(@PathVariable Integer id, @RequestParam("titulo") String titulo,
			@RequestParam("director") String director, @RequestParam(value = "file", required = false) MultipartFile file) {

		// Busco el pelicula actual; Optional viene vacio si no existe
		Optional<Pelicula> peliculaOpt = peliculaRepository.findById(id);

		if (peliculaOpt.isEmpty()) {
			// 404 Not Found: no hay un pelicula con ese id
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pelicula no encontrado");
		}

		try {
			Pelicula  pelicula = peliculaOpt.get();

			// Actualizo los textos
			pelicula.setTitulo(titulo);
			pelicula.setDirector(director);

			// Solo si llego una foto nueva cambio la imagen
			// Si no llego, no toco la foto y se conservan los bytes que ya estaban
			if (file != null && !file.isEmpty()) {
				pelicula.setMimeType(file.getContentType());
				pelicula.setFoto(file.getBytes());
			}

			// Guardo los cambios (como el pelicula ya tiene id, actualiza y no crea otro)
			peliculaRepository.save(pelicula);

			// 200 OK: se edito correctamente
			return ResponseEntity.ok("Pelicula actualizada con exito");

		} catch (Exception e) {
			// Si algo falla (por ejemplo al leer el archivo), responde 500
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al actualizar el pelicula: " + e.getMessage());
		}
	}

}
