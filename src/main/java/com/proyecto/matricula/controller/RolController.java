package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Rol;
import com.proyecto.matricula.entity.RolFuncionalidad;
import com.proyecto.matricula.service.RolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/roles")
@PreAuthorize("@securityService.tienePermiso('Usuarios', 'VER')")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public String listarRoles(Model model) {
        List<Rol> roles = rolService.listarActivos();
        model.addAttribute("roles", roles);
        return "roles/lista";
    }

    @PostMapping("/nuevo")
    public String registrarRol(@RequestParam("nombreRol") String nombreRol,
                               @RequestParam("descripcion") String descripcion,
                               RedirectAttributes redirectAttributes) {
        try {
            rolService.crearRol(nombreRol, descripcion);
            redirectAttributes.addFlashAttribute("success", "El rol '" + nombreRol.toUpperCase() + "' ha sido creado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/roles";
    }

    @GetMapping("/editar/{id}")
    public String editarRolForm(@PathVariable("id") Integer id, Model model) {
        Rol rol = rolService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));
        List<RolFuncionalidad> permisos = rolService.obtenerPermisosRol(id);
        
        model.addAttribute("rol", rol);
        model.addAttribute("permisos", permisos);
        return "roles/editar";
    }

    @PostMapping("/editar/{id}")
    public String guardarRol(@PathVariable("id") Integer id,
                             @RequestParam("nombreRol") String nombreRol,
                             @RequestParam("descripcion") String descripcion,
                             @RequestParam(value = "verIds", required = false) List<Integer> verIds,
                             @RequestParam(value = "crearIds", required = false) List<Integer> crearIds,
                             @RequestParam(value = "editarIds", required = false) List<Integer> editarIds,
                             @RequestParam(value = "eliminarIds", required = false) List<Integer> eliminarIds,
                             @RequestParam(value = "imprimirIds", required = false) List<Integer> imprimirIds,
                             RedirectAttributes redirectAttributes) {
        try {
            List<RolFuncionalidad> permisosModificados = new ArrayList<>();
            List<RolFuncionalidad> permisosActuales = rolService.obtenerPermisosRol(id);

            for (RolFuncionalidad rf : permisosActuales) {
                RolFuncionalidad rfEditado = new RolFuncionalidad();
                rfEditado.setIdRolFuncionalidad(rf.getIdRolFuncionalidad());
                
                rfEditado.setVer(verIds != null && verIds.contains(rf.getIdRolFuncionalidad()));
                rfEditado.setCrear(crearIds != null && crearIds.contains(rf.getIdRolFuncionalidad()));
                rfEditado.setEditar(editarIds != null && editarIds.contains(rf.getIdRolFuncionalidad()));
                rfEditado.setEliminar(eliminarIds != null && eliminarIds.contains(rf.getIdRolFuncionalidad()));
                rfEditado.setImprimir(imprimirIds != null && imprimirIds.contains(rf.getIdRolFuncionalidad()));
                
                permisosModificados.add(rfEditado);
            }

            rolService.actualizarRol(id, nombreRol, descripcion, permisosModificados);
            redirectAttributes.addFlashAttribute("success", "El rol '" + nombreRol.toUpperCase() + "' y sus permisos han sido actualizados.");
            return "redirect:/roles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/roles/editar/" + id;
        }
    }

    @PostMapping("/eliminar/{id}")
    @ResponseBody
    public String eliminarRol(@PathVariable("id") Integer id) {
        try {
            rolService.eliminarRol(id);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
