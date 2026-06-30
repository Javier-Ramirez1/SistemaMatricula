/**
 * shell.js — inicializa la parte común de cualquier dashboard:
 * valida sesión+rol, pinta el nombre/avatar del usuario en el
 * topbar y conecta el botón de cerrar sesión.
 *
 * Cada página de dashboard solo necesita:
 *   <script src="/js/api.js"></script>
 *   <script src="/js/shell.js"></script>
 *   <script>SM_initShell(["SUPERUSUARIO"]);</script>
 */
function SM_initShell(rolesPermitidos) {
  const session = SM.exigirSesion(rolesPermitidos);
  if (!session) return null;

  const nameEl = document.getElementById("topbarUserName");
  const avatarEl = document.getElementById("topbarAvatar");
  const logoutBtn = document.getElementById("btnLogout");

  if (nameEl) {
    nameEl.innerHTML = `${session.nombreCompleto} <small>@${session.username}</small>`;
  }
  if (avatarEl) {
    avatarEl.textContent = SM.iniciales(session.nombreCompleto);
  }
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => SM.cerrarSesion());
  }

  return session;
}
