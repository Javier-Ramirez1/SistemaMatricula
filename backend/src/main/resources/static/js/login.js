/**
 * login.js — valida el formulario y llama a POST /api/auth/login.
 * Si ya hay una sesión activa, redirige directo al dashboard que
 * corresponda (evita que alguien vea el login estando ya logueado).
 */
(() => {
  const session = SM.getSession();
  if (session?.token) {
    window.location.href = SM.rutaDashboardPorRol(session);
    return;
  }

  const form = document.getElementById("loginForm");
  const btn = document.getElementById("btnLogin");
  const alertBox = document.getElementById("loginAlert");
  const fieldUsername = document.getElementById("fieldUsername");
  const fieldPassword = document.getElementById("fieldPassword");
  const inputUsername = document.getElementById("username");
  const inputPassword = document.getElementById("password");

  function mostrarError(mensaje) {
    alertBox.textContent = mensaje;
    alertBox.style.display = "flex";
  }

  function ocultarError() {
    alertBox.style.display = "none";
  }

  function marcarCampo(fieldEl, valido) {
    fieldEl.classList.toggle("has-error", !valido);
  }

  function validar() {
    const usernameOk = inputUsername.value.trim().length > 0;
    const passwordOk = inputPassword.value.length > 0;
    marcarCampo(fieldUsername, usernameOk);
    marcarCampo(fieldPassword, passwordOk);
    return usernameOk && passwordOk;
  }

  [inputUsername, inputPassword].forEach(el => {
    el.addEventListener("input", () => {
      ocultarError();
      el.closest(".field").classList.remove("has-error");
    });
  });

  form.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    ocultarError();

    if (!validar()) return;

    btn.disabled = true;
    btn.classList.add("is-loading");

    try {
      const data = await SM.api("/auth/login", {
        method: "POST",
        body: {
          username: inputUsername.value.trim(),
          password: inputPassword.value,
        },
      });

      SM.setSession(data);
      window.location.href = SM.rutaDashboardPorRol(data);
    } catch (err) {
      mostrarError(err.message || "Usuario o contraseña incorrectos.");
      btn.disabled = false;
      btn.classList.remove("is-loading");
    }
  });
})();
