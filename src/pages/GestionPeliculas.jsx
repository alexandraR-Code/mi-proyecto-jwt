// useState guarda datos que cambian; useEffect ejecuta codigo cuando la pantalla se abre
import { useEffect, useState } from 'react'
// useNavigate sirve para cambiar de ruta desde el codigo
import { useNavigate } from 'react-router-dom'
// URL del backend, definida en un solo lugar
import { API_BASE_URL } from '../config/apiConfig'
import ListaPeliculas from '../components/ListaPeliculas'

function GestionPeliculas() {
    // Lista de peliculas que llegan del backend (empieza vacia)
    const [peliculas, setPeliculas] = useState([])
    // Datos que se escriben en el formulario
    const [titulo, setTitulo] = useState('')
    const [director, setDirector] = useState('')
    // Archivo de imagen elegido (empieza sin nada)
    const [archivo, setArchivo] = useState(null)
    // Mensaje para avisar si salio bien o mal, y su tipo (exito o error)
    const [mensaje, setMensaje] = useState('')
    const [tipo, setTipo] = useState('')
    const navigate = useNavigate()

    // Leo el token que se guardo al hacer login
    const token = localStorage.getItem('token')

    // Pide la lista de peliculas al backend
    const cargarPeliculas = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/peliculas`, {
                // El token va en el encabezado para que el backend sepa quien soy
                headers: { Authorization: `Bearer ${token}` },
            })

            if (response.ok) {
                // Convierto la respuesta en un arreglo y lo guardo en el estado
                setPeliculas(await response.json())
            } else {
                // Si el token vencio o no es valido, lo borro y vuelvo al login
                localStorage.removeItem('token')
                navigate('/login')
            }
        } catch {
            // Entra aqui si el backend esta apagado o no hay red
            setTipo('error')
            setMensaje('No se pudo conectar con el servidor')
        }
    }

    // Se ejecuta una vez al abrir la pantalla
    useEffect(() => {
        // Sin token no hay sesion: mando a la persona al login
        if (!token) {
            navigate('/login')
            return
        }
        cargarPeliculas()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    // Guarda la imagen elegida en el campo de archivo
    const handleArchivo = (e) => {
        // files[0] es el primer archivo seleccionado
        setArchivo(e.target.files[0])
    }

    // Se ejecuta al enviar el formulario
    const handleSubmit = async (e) => {
        // Evita que el navegador recargue la pagina
        e.preventDefault()
        // Guardo el formulario para poder limpiarlo despues
        const formulario = e.target
        setMensaje('')

        // La foto es obligatoria al registrar
        if (!archivo) {
            setTipo('error')
            setMensaje('Debes seleccionar una foto')
            return
        }

        // FormData permite enviar texto y archivo juntos (multipart/form-data)
        const formData = new FormData()
        // Estos nombres deben ser iguales a los @RequestParam del backend
        formData.append('file', archivo)
        formData.append('titulo', titulo)
        formData.append('director', director)

        try {
            const response = await fetch(`${API_BASE_URL}/auth/peliculas`, {
                method: 'POST',
                // Solo Authorization: el navegador pone solo el Content-Type del FormData
                headers: { Authorization: `Bearer ${token}` },
                body: formData,
            })

            if (response.ok) {
                setTipo('exito')
                setMensaje('Pelicula registrada con exito')
                // Limpio los campos y el selector de archivo
                setTitulo('')
                setDirector('')
                setArchivo(null)
                formulario.reset()
                // Vuelvo a pedir la lista para ver la pelicula nueva
                cargarPeliculas()
            } else {
                // Muestro el mensaje de error que devuelve el backend
                setTipo('error')
                setMensaje(await response.text())
            }
        } catch {
            setTipo('error')
            setMensaje('No se pudo conectar con el servidor')
        }
    }
    // Elimina una pelicula y vuelve a cargar la lista
    const handleEliminar = async (id) => {
        // Pido confirmacion antes de borrar
        if (!window.confirm('Seguro que deseas eliminar esta pelicula?')) return

        try {
            const response = await fetch(`${API_BASE_URL}/auth/peliculas/${id}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${token}` },
            })

            if (response.ok) {
                setTipo('exito')
                setMensaje('Pelicula eliminada con exito')
                // Recargo la lista para que la card desaparezca
                cargarPeliculas()
            } else {
                setTipo('error')
                setMensaje(await response.text())
            }
        } catch {
            setTipo('error')
            setMensaje('No se pudo conectar con el servidor')
        }
    }

    return (
        <div className="gestion-container">
            <h2>Gestion de peliculas</h2>

            {/* onSubmit llama a handleSubmit al pulsar el boton */}
            <form className="gestion-form" onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Titulo"
                    value={titulo}
                    onChange={(e) => setTitulo(e.target.value)}
                    required
                />

                <input
                    type="text"
                    placeholder="Director"
                    value={director}
                    onChange={(e) => setDirector(e.target.value)}
                    required
                />

                {/* accept="image/*" solo deja elegir imagenes */}
                <input type="file" accept="image/*" onChange={handleArchivo} />

                {/* La clase cambia segun el tipo: "mensaje exito" o "mensaje error" */}
                {mensaje && <p className={`mensaje ${tipo}`}>{mensaje}</p>}

                <button type="submit">Registrar pelicula</button>
            </form>

            <h3>Peliculas registradas</h3>

            {/* Si no hay peliculas muestro un aviso; si hay, las recorro con map */}
            {peliculas.length === 0 ? (
                <p>No hay peliculas registradas</p>
            ) : (
                <ListaPeliculas
                    peliculas={peliculas}
                    token={token}
                    onEliminar={handleEliminar}
                />
            )}
        </div>
    )
}

// Lo exporto para usarlo en App.jsx
export default GestionPeliculas