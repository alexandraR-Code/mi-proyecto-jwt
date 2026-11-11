import { useState } from "react";
// Link para volver a login, useNavigate para redirigir desde el codigo
import { Link, useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../config/apiConfig";

function Registrar() {
    //Un estado por cda campo del formulario
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    // El rol inicia en USER para que el select ya tenga una opcion elegida
    const [rol, setRol] = useState('USER')

    // mensaje guarda el texto a mostrar y tipo dice si es deexito o error
    // Con el tipo decide el color verde o rojo
    const [mensaje, setMensaje] = useState('')
    const [tipo, setTipo] = useState('')

    //Cargando evita que se pulse el boton dos veces mientras espera el aservidor
    const [cargando, setCargando] = useState(false)

    const navigate = useNavigate()

    const handleSubmit = async (e) => {
        // evita que el navedor recargue la pagina al enviar el formulario
        e.preventDefault()
        setMensaje('')
        setCargando(true)

        try {
            //Manda los 33 datos a backend igual que Login
            //Los nombres username, password y rol deben coincidir con la entidad Usuario.java
            const response = await fetch(`${API_BASE_URL}/auth/registrar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password, rol }),
            })
            //El backend responde 201 create cuando el usuario se guarda correctae=mente
            if (response.status === 201) {
                setTipo('exito')
                setMensaje('Usuario registrado con éxito. Redirigiendo al login...')

                //setTimeout ejecuta la funcion despues de cierto tiempo
                setTimeout(() => navigate('/login'), 2000)
            } else {
                //si el servidor falla por un usuario repetido muestra esto 
                setTipo('error')
                setMensaje(await response.text())
            }
        } catch {
            //Entra aqui si el backend esta apagado o no hay red
            setTipo('error')
            setMensaje('No se puede conectar con el servidor')
        } finally {
            //finally se ejecuta siempre haya salido bien o mal 
            //aqui se activa de nuevo el boton
            setCargando(false)
        }
    }

    return (
        <div className="auth-container">
            <form className="auth-card" onSubmit={handleSubmit}>
                <h2>Crear cuenta</h2>

                <input
                    type="text"
                    placeholder="Nombre de usuario"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />

                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                {/* select es la caja de selección, su value viene del estado rol */}
                {/* Al elegir otra opción, onChange guarda el nuevo valor */}
                <select value={rol} onChange={(e) => setRol(e.target.value)}>
                    {/* El value es lo que se envía al backend, el texto es lo que ve la persona */}
                    <option value="USER">Usuario (USER)</option>
                    <option value="ADMIN">Administrador (ADMIN)</option>
                </select>

                {/* La clase cambia según el tipo: "mensaje exito" o "mensaje error" */}
                {mensaje && <p className={`mensaje ${tipo}`}>{mensaje}</p>}

                {/* disabled desactiva el botón mientras se envía */}
                <button type="submit" disabled={cargando}>
                    {cargando ? 'Registrando...' : 'Registrarme'}
                </button>

                <p className="auth-link">
                    ¿Ya tienes cuenta? <Link to="/login">Inicia sesión</Link>
                </p>
            </form>
        </div>
    )
}
export default Registrar
