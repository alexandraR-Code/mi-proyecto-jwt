// useEffect ejecuta codigo cuando cambia la lista; useState guarda las fotos
import { useEffect, useState } from 'react'
// URL del backend, definida en un solo lugar
import { API_BASE_URL } from '../config/apiConfig'

// Recibe la lista de peliculas, el token y la funcion que elimina
function ListaPeliculas({ peliculas, token, onEliminar, onEditar }) {
    // Guarda la direccion temporal (Object URL) de la foto de cada pelicula
    // Ejemplo: { 1: "blob:http://...", 2: "blob:http://..." }
    const [fotos, setFotos] = useState({})

    useEffect(() => {
        // Aqui guardo las URLs que se crean para liberarlas despues
        const urlsCreadas = []
        // Bandera para no actualizar el estado si el componente ya se cerro
        let activo = true

        const descargarFotos = async () => {
            const nuevas = {}
            for (const p of peliculas) {
                try {
                    // La foto esta protegida, por eso tambien lleva el token
                    const res = await fetch(`${API_BASE_URL}/auth/peliculas/${p.id}/foto`, {
                        headers: { Authorization: `Bearer ${token}` },
                    })
                    if (res.ok) {
                        // blob = archivo binario (la imagen)
                        const blob = await res.blob()
                        // createObjectURL crea una direccion temporal para usarla en <img>
                        const url = URL.createObjectURL(blob)
                        urlsCreadas.push(url)
                        nuevas[p.id] = url
                    }
                } catch (error) {
                    console.log('No se pudo descargar la foto', error)
                }
            }
            if (activo) setFotos(nuevas)
        }

        descargarFotos()

        // Limpieza: se ejecuta antes de repetir el efecto o al salir de la pantalla
        return () => {
            activo = false
            // revokeObjectURL libera la memoria de las fotos anteriores
            urlsCreadas.forEach((url) => URL.revokeObjectURL(url))
        }
    }, [peliculas, token])

    return (
        <div className="grid-peliculas">
            {/* Por cada pelicula dibujo una card */}
            {peliculas.map((p) => (
                <div className="card-pelicula" key={p.id}>
                    {/* Si la foto ya llego la muestro; si no, un aviso */}
                    {fotos[p.id] ? (
                        <img src={fotos[p.id]} alt={p.titulo} />
                    ) : (
                        <div className="foto-vacia">Cargando foto...</div>
                    )}

                    <div className="card-info">
                        <h3>{p.titulo}</h3>
                        <p>Director: {p.director}</p>
                    </div>

                    <div className="card-acciones">
                        {/* onClick le pasa al padre la pelicula completa para editarla */}
                        <button className="btn-editar" onClick={() => onEditar(p)}>
                            Editar
                        </button>
                        {/* onClick avisa al padre cual pelicula se quiere eliminar */}
                        <button className="btn-eliminar" onClick={() => onEliminar(p.id)}>
                            Eliminar
                        </button>
                    </div>
                </div>
            ))}

        </div>
    )
}

// Lo exporto para usarlo en GestionPeliculas.jsx
export default ListaPeliculas