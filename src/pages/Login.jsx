// useState sirve para guardar datos que cambian (lo que se escribe en los campos)
import { useState } from 'react'
// useNavigate me deja cambiar de página desde el código
import { useNavigate } from 'react-router-dom'
// Traigo la URL del backend desde un solo lugar
import { API_BASE_URL } from '../config/apiConfig'

function Login() {
    // Cada useState devuelve [valor actual, función para cambiarlo]
    // Aquí guardo lo que la persona escribe en el formulario
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    // Aquí guardo el mensaje de error, vacío significa que no hay error
    const [error, setError] = useState('')
    // navigate es la función que uso para ir a otra ruta
    const navigate = useNavigate()

    // handleSubmit se ejecuta cuando se envía el formulario
    // async porque dentro espero la respuesta del servidor con await
    const handleSubmit = async (e) => {
        // preventDefault evita que el navegador recargue la página al enviar
        e.preventDefault()
        // Limpio el error anterior antes de intentar de nuevo
        setError('')

        // try/catch me permite controlar los errores de red sin que se rompa la app
        try {
            // fetch hace la petición HTTP, await espera a que el servidor responda
            // Mando las credenciales al backend
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                // Le aviso al servidor que le estoy mandando JSON
                headers: {
                    'Content-Type': 'application/json'
                },
                // JSON.stringify convierte el objeto en texto JSON
                body: JSON.stringify({ username, password }),
            })

            // response.ok es true cuando el estado es 200-299
            if (response.ok) {
                // Convierto la respuesta a objeto y saco el token
                const data = await response.json()
                // Guardo el token para usarlo en las rutas protegidas
                localStorage.setItem('token', data.token)
                // Redirijo a la página del perfil
                navigate('/perfil')
            } else {
                // Muestro el mensaje de error que devuelve el servidor
                setError(await response.text())
            }
        } catch {
            // Se ejecuta si el backend está apagado o no hay red
            setError('No se pudo conectar con el servidor')
        }
    }

    return (
        <div className="auth-container">
            {/* onSubmit llama a handleSubmit cuando se pulsa el botón */}
            <form className="auth-card" onSubmit={handleSubmit}>
                <h2>Iniciar sesión</h2>

                {/* value + onChange: el campo siempre muestra lo que hay en el estado */}
                {/* required: el navegador no deja enviar si está vacío */}
                <input
                    type="text"
                    placeholder="Usuario"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />

                {/*type="password" oculta lo que se escribe*/}
                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                {/* El párrafo solo aparece cuando error no está vacío */}
                {error && <p className="mensaje error">{error}</p>}

                {/* type="submit" dispara el onSubmit del formulario */}
                <button type="submit">Ingresar</button>
            </form>
        </div>
    )
}

// Lo exporto para poder usarlo en App.jsx
export default Login
