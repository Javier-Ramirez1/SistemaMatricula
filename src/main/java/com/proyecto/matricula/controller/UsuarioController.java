package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Rol;
import com.proyecto.matricula.entity.Usuario;
import com.proyecto.matricula.repository.RolRepository;
import com.proyecto.matricula.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.proyecto.matricula.config.security.CustomUserDetails;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("@securityService.tienePermiso('Usuarios', 'VER')") // Dinamico por base de datos
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.proyecto.matricula.repository.RolFuncionalidadRepository rolFuncionalidadRepository;
    private final com.proyecto.matricula.repository.PersonaRepository personaRepository;
    private final com.proyecto.matricula.repository.TipoDocumentoRepository tipoDocumentoRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, 
                             RolRepository rolRepository, 
                             PasswordEncoder passwordEncoder,
                             com.proyecto.matricula.repository.RolFuncionalidadRepository rolFuncionalidadRepository,
                             com.proyecto.matricula.repository.PersonaRepository personaRepository,
                             com.proyecto.matricula.repository.TipoDocumentoRepository tipoDocumentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolRepository.findByEstadoTrue());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm() {
        return "redirect:/usuarios";
    }

    @PostMapping("/nuevo")
    public String registrarUsuario(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("idRol") Integer idRol,
                                   @RequestParam("nombres") String nombres,
                                   @RequestParam("apellidoPaterno") String apellidoPaterno,
                                   @RequestParam("apellidoMaterno") String apellidoMaterno,
                                   @RequestParam("numeroDocumento") String numeroDocumento,
                                   @RequestParam(value = "correoElectronico", required = false) String correoElectronico,
                                   @RequestParam(value = "celular", required = false) String celular,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            if (usuarioRepository.findByUsernameAndEstadoTrue(username).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya existe en el sistema.");
            }

            // Validar si la persona ya existe con ese documento
            if (personaRepository.findByTipoDocumentoCodTipoDocumentoAndNumeroDocumentoAndEstadoTrue(1, numeroDocumento).isPresent()) {
                throw new IllegalArgumentException("Ya existe una persona registrada con ese número de documento.");
            }

            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new IllegalArgumentException("El Rol seleccionado no existe."));

            // Crear y guardar Persona
            com.proyecto.matricula.entity.Persona persona = new com.proyecto.matricula.entity.Persona();
            persona.setTipoDocumento(tipoDocumentoRepository.findById(1).orElseThrow()); // DNI por defecto
            persona.setNumeroDocumento(numeroDocumento);
            persona.setNombres(nombres);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1)); // Fecha ficticia para usuarios
            persona.setCorreoElectronico(correoElectronico);
            persona.setCelular(celular);
            persona.setEstado(true);
            persona = personaRepository.save(persona);

            // Crear y guardar Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setPersona(persona);
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setRol(rol);
            nuevoUsuario.setEstado(true);

            usuarioRepository.save(nuevoUsuario);
            redirectAttributes.addFlashAttribute("success", "El usuario ha sido registrado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolRepository.findAll());
        
        // Cargar permisos del Rol asociado al usuario para la pestaña 2
        List<com.proyecto.matricula.entity.RolFuncionalidad> permisos = rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol());
        model.addAttribute("permisos", permisos);
        return "usuarios/editar";
    }

    @PostMapping("/editar/{id}")
    public String actualizarUsuario(@PathVariable("id") Integer id,
                                    @RequestParam("username") String username,
                                    @RequestParam(value = "password", required = false) String password,
                                    @RequestParam("idRol") Integer idRol,
                                    @RequestParam("nombres") String nombres,
                                    @RequestParam("apellidoPaterno") String apellidoPaterno,
                                    @RequestParam("apellidoMaterno") String apellidoMaterno,
                                    @RequestParam("numeroDocumento") String numeroDocumento,
                                    @RequestParam(value = "correoElectronico", required = false) String correoElectronico,
                                    @RequestParam(value = "celular", required = false) String celular,
                                    Model model) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new IllegalArgumentException("El Rol seleccionado no existe."));

            if (!usuario.getUsername().equals(username) && usuarioRepository.findByUsernameAndEstadoTrue(username).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya existe en el sistema.");
            }

            // Actualizar datos de Persona asociada
            com.proyecto.matricula.entity.Persona persona = usuario.getPersona();
            persona.setNombres(nombres);
            persona.setApellidoPaterno(apellidoPaterno);
            persona.setApellidoMaterno(apellidoMaterno);
            persona.setNumeroDocumento(numeroDocumento);
            persona.setCorreoElectronico(correoElectronico);
            persona.setCelular(celular);
            personaRepository.save(persona);

            usuario.setUsername(username);
            usuario.setRol(rol);

            if (password != null && !password.trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }

            usuarioRepository.save(usuario);

            // Sincronizar sesión activa si el usuario editado es el actual
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName().equals(usuario.getUsername())) {
                CustomUserDetails newUserDetails = new CustomUserDetails(usuario);
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        newUserDetails, auth.getCredentials(), newUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

            return "redirect:/usuarios?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuario", usuarioRepository.findById(id).orElse(null));
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/editar";
        }
    }

    @PostMapping("/guardar-permisos")
    public String guardarPermisos(@RequestParam("idRol") Integer idRol,
                                  @RequestParam(value = "verIds", required = false) List<Integer> verIds,
                                  @RequestParam(value = "crearIds", required = false) List<Integer> crearIds,
                                  @RequestParam(value = "editarIds", required = false) List<Integer> editarIds,
                                  @RequestParam(value = "eliminarIds", required = false) List<Integer> eliminarIds,
                                  @RequestParam(value = "imprimirIds", required = false) List<Integer> imprimirIds) {
        
        List<com.proyecto.matricula.entity.RolFuncionalidad> permisos = rolFuncionalidadRepository.findByRolIdRol(idRol);
        for (com.proyecto.matricula.entity.RolFuncionalidad p : permisos) {
            p.setVer(verIds != null && verIds.contains(p.getIdRolFuncionalidad()));
            p.setCrear(crearIds != null && crearIds.contains(p.getIdRolFuncionalidad()));
            p.setEditar(editarIds != null && editarIds.contains(p.getIdRolFuncionalidad()));
            p.setEliminar(eliminarIds != null && eliminarIds.contains(p.getIdRolFuncionalidad()));
            p.setImprimir(imprimirIds != null && imprimirIds.contains(p.getIdRolFuncionalidad()));
            rolFuncionalidadRepository.save(p);
        }
        return "redirect:/usuarios?success=true";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id) {
        if (id == 1) {
            return "redirect:/usuarios?error=El+Superusuario+semilla+no+puede+eliminarse.";
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
        
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
        return "redirect:/usuarios?success=true";
    }
}
