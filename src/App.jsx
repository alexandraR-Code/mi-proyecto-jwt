// Routes agrupa todas las rutas, Route define cada una
// Navigate sirve para redirigir a otra ruta automaticamente
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Perfil from "./pages/Perfil";
import Registrar from "./pages/Registrar";


function App() {
  return (
    // React Router mira la URL y muestra solo la ruta que coincide
    <Routes>
      {/* Si entran a la raiz "/", los mando directo al login */}
      {/* replace evita que "/" quede en el historial del navegador */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Rutas publicas: se pueden abrir sin token */}
      <Route path="/login" element={<Login />} />

      {/* Ruta pública para crear una cuenta nueva */}
      <Route path="/registrar" element={<Registrar />} />

      {/* Esta pantalla revisa por dentro si hay token, si no, vuelve al login */}
      <Route path="/perfil" element={<Perfil />} />
    </Routes>
  )
}

export default App