/**
 * api.js — capa compartida de sesión + acceso a la API REST.
 * La cargan TODAS las páginas (login y los 3 dashboards).
 *
 * Sesión guardada en localStorage bajo la clave SM.session:
 *   { token, username, nombreCompleto, roles: [...], esSuperusuario }
 *
 * Nota de arquitectura: la seguridad real vive en el backend
 * (cada endpoint exige JWT + rol/permiso vía Spring Security).
 * Este archivo solo evita que el navegador muestre pantallas que
 * de todas formas no podrían cargar datos sin el token correcto.
 */
const SM = (() => {
  const STORAGE_KEY = "sm.session";
  const API_BASE = "/api";

  function getSession() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  function setSession(session) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  }

  function clearSession() {
    localStorage.removeItem(STORAGE_KEY);
  }

  function isLoggedIn() {
    return !!getSession()?.token;
  }

  function hasRole(roleName) {
    const session = getSession();
    return !!session?.roles?.includes(roleName);
  }

  /** Ruta del dashboard que corresponde al primer rol relevante del usuario. */
  function rutaDashboardPorRol(session) {
    if (!session) return "/login.html";
    if (session.esSuperusuario || session.roles.includes("SUPERUSUARIO")) return "/superusuario/index.html";
    if (session.roles.includes("DIRECTOR")) return "/director/index.html";
    if (session.roles.includes("SECRETARIA")) return "/secretaria/index.html";
    return "/login.html";
  }

  /**
   * Protege una página: si no hay sesión, redirige al login.
   * Si rolesPermitidos se especifica y el usuario no tiene ninguno
   * de esos roles, lo redirige a SU dashboard correcto (no al login)
   * para que no quede en bucle al entrar a una URL ajena.
   */
  function exigirSesion(rolesPermitidos) {
    const session = getSession();
    if (!session?.token) {
      window.location.href = "/login.html";
      return null;
    }
    if (rolesPermitidos && !rolesPermitidos.some(r => session.roles.includes(r))) {
      window.location.href = rutaDashboardPorRol(session);
      return null;
    }
    return session;
  }

  function cerrarSesion() {
    clearSession();
    window.location.href = "/login.html";
  }

  /**
   * Wrapper de fetch: agrega el header Authorization, parsea JSON,
   * y ante un 401 limpia la sesión y manda al login (token vencido
   * o inválido).
   */
  async function api(path, { method = "GET", body, headers } = {}) {
    const session = getSession();
    const res = await fetch(API_BASE + path, {
      method,
      headers: {
        "Content-Type": "application/json",
        ...(session?.token ? { Authorization: "Bearer " + session.token } : {}),
        ...headers,
      },
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    if (res.status === 401) {
      clearSession();
      window.location.href = "/login.html";
      throw new Error("Sesión expirada");
    }

    const isJson = res.headers.get("content-type")?.includes("application/json");
    const data = isJson ? await res.json() : null;

    if (!res.ok) {
      const mensaje = data?.error || data?.detalles
        ? (data.error || Object.values(data.detalles || {}).join(", "))
        : "Ocurrió un error al comunicarse con el servidor.";
      throw new Error(mensaje);
    }

    return data;
  }

  function iniciales(nombreCompleto) {
    if (!nombreCompleto) return "?";
    return nombreCompleto
      .trim()
      .split(/\s+/)
      .slice(0, 2)
      .map(p => p[0]?.toUpperCase())
      .join("");
  }

  return {
    getSession, setSession, clearSession, isLoggedIn, hasRole,
    rutaDashboardPorRol, exigirSesion, cerrarSesion, api, iniciales,
  };
})();
