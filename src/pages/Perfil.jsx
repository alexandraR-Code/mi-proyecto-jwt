// useEffect sirve para ejecutar código en momentos concretos,
// por ejemplo cuando la pantalla se abre por primera vez
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { API_BASE_URL } from '../config/apiConfig'

function Perfil() {
  // Empieza en null porque al abrir la página todavía no tengo los datos
  // Cuando el backend responda, aquí guardo el objeto con la información
  const [perfil, setPerfil] = useState(null)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  // useEffect recibe dos cosas: la función a ejecutar y una lista de dependencias
  // Con la lista, React vuelve a ejecutarla solo si algo de esa lista cambia
  useEffect(() => {
    // getItem lee lo que guardé antes con setItem
    // Si no existe, devuelve null
    const token = localStorage.getItem('token')

    // Un null o vacío cuenta como false, así que entra aquí cuando no hay token
    // Sin token no hay sesión, entonces mando a la persona al login
    if (!token) {
      navigate('/login')
      // El return corta la función para que no siga ejecutando lo de abajo
      return
    }

    // Creo una función aparte porque la que va dentro de useEffect
    // no puede ser async, así que uso esta para poder usar await
    const cargarPerfil = async () => {
      try {
        // En una ruta protegida el backend pide saber quién soy
        // Para eso mando el token en el encabezado Authorization
        // "Bearer" es la palabra estándar que va antes de un token JWT
        const response = await fetch(`${API_BASE_URL}/auth/perfil`, {
          headers: { Authorization: `Bearer ${token}` },
        })

        if (response.ok) {
          // json() lee el cuerpo de la respuesta y lo convierte en un objeto
          // Al guardarlo en el estado, React vuelve a pintar la pantalla
          setPerfil(await response.json())
        } else {
          // Si el token venció o no es válido, el backend responde con error
          // removeItem borra el token guardado para no reutilizar uno malo
          localStorage.removeItem('token')
          navigate('/login')
        }
      } catch {
        // Entra aquí si no hay conexión con el backend
        setError('No se pudo conectar con el servidor')
      }
    }

    // Una función creada no hace nada hasta que se llama, por eso la llamo aquí
    cargarPerfil()

    // [navigate] es la lista de dependencias
    // Si estuviera vacía [], se ejecutaría solo una vez al abrir la página
    // navigate no cambia, así que en la práctica sigue corriendo una sola vez
    // ESLint pide ponerlo porque lo uso dentro del efecto
  }, [navigate])

  const handleLogout = async () => {
    const token = localStorage.getItem('token')

    try {
      // El logout del backend mete el token en una lista negra
      // así nadie más puede usarlo aunque todavía no haya vencido
      await fetch(`${API_BASE_URL}/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      })
    } catch {
      // Si falla la red no me detengo: igual cierro la sesión en el navegador
    }

    // Borro el token del navegador y regreso al login
    localStorage.removeItem('token')
    navigate('/login')
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Mi perfil</h2>

        {/* Si error tiene texto lo muestro, si está vacío no pinta nada */}
        {error && <p className="mensaje error">{error}</p>}

        {/* perfil empieza en null, así que esto no se pinta hasta que lleguen los datos */}
        {/* Sin esta condición, perfil.Usuario daría error al abrir la página */}
        {perfil && (
          <div className="perfil-datos">
            {/* Estos nombres (Usuario, rol_detectado, Mensaje) son los que */}
            {/* devuelve el backend, tienen que escribirse igual */}
            <p><strong>Usuario:</strong> {perfil.Usuario}</p>
            <p><strong>Rol:</strong> {perfil.rol_detectado}</p>
            <p>{perfil.Mensaje}</p>
          </div>
        )}

        {/* onClick ejecuta la función cuando se hace clic */}
        <button onClick={handleLogout}>Cerrar sesión</button>
      </div>
    </div>
  )
}

export default Perfil
