package com.proyecto.matricula.controller;

import com.proyecto.matricula.entity.Concepto;
import com.proyecto.matricula.entity.TipoConcepto;
import com.proyecto.matricula.repository.AnioAcademicoRepository;
import com.proyecto.matricula.repository.ConceptoRepository;
import com.proyecto.matricula.repository.TipoConceptoRepository;
import com.proyecto.matricula.service.ConceptoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/conceptos")
public class ConceptoController {

    private final ConceptoRepository conceptoRepository;
    private final ConceptoService conceptoService;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final TipoConceptoRepository tipoConceptoRepository;

    public ConceptoController(ConceptoRepository conceptoRepository, ConceptoService conceptoService,
                              AnioAcademicoRepository anioAcademicoRepository, TipoConceptoRepository tipoConceptoRepository) {
        this.conceptoRepository = conceptoRepository;
        this.conceptoService = conceptoService;
        this.anioAcademicoRepository = anioAcademicoRepository;
        this.tipoConceptoRepository = tipoConceptoRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'DIRECTOR', 'SECRETARIA')")
    public String listarConceptos(Model model) {
        List<Concepto> conceptos = conceptoRepository.findAll();
        model.addAttribute("conceptos", conceptos);
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        return "conceptos/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String nuevoConceptoForm(Model model) {
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        model.addAttribute("tiposConcepto", tipoConceptoRepository.findAll());
        model.addAttribute("concepto", new Concepto());
        return "conceptos/nuevo";
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String registrarConcepto(@RequestParam("codAnioAcademico") Integer codAnioAcademico,
                                    @RequestParam("codTipoConcepto") Integer codTipoConcepto,
                                    @RequestParam("nombreConcepto") String nombreConcepto,
                                    @RequestParam("monto") BigDecimal monto,
                                    @RequestParam("ordenPago") Short ordenPago,
                                    @RequestParam(value = "obligatorio", required = false) Boolean obligatorio,
                                    Model model) {
        try {
            if (conceptoRepository.existsByAnioAcademicoCodAnioAcademicoAndNombreConceptoAndEstadoTrue(codAnioAcademico, nombreConcepto)) {
                throw new IllegalArgumentException("Ya existe un concepto registrado con ese nombre para el año académico.");
            }

            Concepto concepto = new Concepto();
            concepto.setAnioAcademico(anioAcademicoRepository.findById(codAnioAcademico).orElseThrow());
            concepto.setTipoConcepto(tipoConceptoRepository.findById(codTipoConcepto).orElseThrow());
            concepto.setNombreConcepto(nombreConcepto);
            concepto.setMonto(monto);
            concepto.setOrdenPago(ordenPago);
            concepto.setObligatorio(obligatorio != null && obligatorio);
            concepto.setEstado(true);

            conceptoRepository.save(concepto);
            return "redirect:/conceptos?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("tiposConcepto", tipoConceptoRepository.findAll());
            return "conceptos/nuevo";
        }
    }

    @PostMapping("/clonar")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String clonarConceptos(@RequestParam("anioOrigen") Integer anioOrigen,
                                  @RequestParam("anioDestino") Integer anioDestino,
                                  Model model) {
        try {
            conceptoService.clonarConceptos(anioOrigen, anioDestino);
            return "redirect:/conceptos?success=Conceptos+clonados+correctamente.";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("conceptos", conceptoRepository.findAll());
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            return "conceptos/lista";
        }
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String eliminarConcepto(@PathVariable("id") Integer id) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concepto no encontrado."));
        concepto.setEstado(false);
        conceptoRepository.save(concepto);
        return "redirect:/conceptos?success=true";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String editarConceptoForm(@PathVariable("id") Integer id, Model model) {
        Concepto concepto = conceptoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Concepto no encontrado."));
        model.addAttribute("concepto", concepto);
        model.addAttribute("anios", anioAcademicoRepository.findAll());
        model.addAttribute("tiposConcepto", tipoConceptoRepository.findAll());
        return "conceptos/editar";
    }

    @PostMapping("/editar")
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public String actualizarConcepto(@ModelAttribute("concepto") Concepto conceptoForm,
                                     @RequestParam("codAnioAcademico") Integer codAnioAcademico,
                                     @RequestParam("codTipoConcepto") Integer codTipoConcepto,
                                     Model model) {
        try {
            Concepto conceptoDb = conceptoRepository.findById(conceptoForm.getCodConcepto())
                    .orElseThrow(() -> new IllegalArgumentException("Concepto no encontrado."));

            // Asignar versión recibida para forzar la validación de Optimistic Lock
            conceptoDb.setVersion(conceptoForm.getVersion());

            conceptoDb.setAnioAcademico(anioAcademicoRepository.findById(codAnioAcademico).orElseThrow());
            conceptoDb.setTipoConcepto(tipoConceptoRepository.findById(codTipoConcepto).orElseThrow());
            conceptoDb.setNombreConcepto(conceptoForm.getNombreConcepto());
            conceptoDb.setMonto(conceptoForm.getMonto());
            conceptoDb.setOrdenPago(conceptoForm.getOrdenPago());
            conceptoDb.setObligatorio(conceptoForm.getObligatorio() != null && conceptoForm.getObligatorio());

            conceptoRepository.save(conceptoDb);
            return "redirect:/conceptos?success=Concepto+actualizado+correctamente.";
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException | jakarta.persistence.OptimisticLockException ex) {
            model.addAttribute("error", "Error de Concurrencia: El Concepto fue modificado por otro usuario en otra pestaña. Por favor, cancela y actualiza la página.");
            model.addAttribute("concepto", conceptoForm);
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("tiposConcepto", tipoConceptoRepository.findAll());
            return "conceptos/editar";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("concepto", conceptoForm);
            model.addAttribute("anios", anioAcademicoRepository.findAll());
            model.addAttribute("tiposConcepto", tipoConceptoRepository.findAll());
            return "conceptos/editar";
        }
    }

    @PostMapping("/api/tipos")
    @ResponseBody
    @PreAuthorize("hasAnyRole('SUPERUSUARIO', 'SECRETARIA')")
    public TipoConcepto crearTipoConcepto(@RequestBody java.util.Map<String, String> body) {
        String descripcion = body.get("descripcion");
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        TipoConcepto tc = new TipoConcepto();
        tc.setDescripcion(descripcion.trim());
        tc.setEstado(true);
        return tipoConceptoRepository.save(tc);
    }
}
