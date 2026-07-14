package com.proyecto.matricula.service;

import com.proyecto.matricula.entity.AnioAcademico;
import com.proyecto.matricula.entity.Concepto;
import com.proyecto.matricula.repository.AnioAcademicoRepository;
import com.proyecto.matricula.repository.ConceptoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConceptoService {

    private final ConceptoRepository conceptoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final AuditoriaService auditoriaService;

    public ConceptoService(ConceptoRepository conceptoRepository,
                           AnioAcademicoRepository anioAcademicoRepository,
                           AuditoriaService auditoriaService) {
        this.conceptoRepository = conceptoRepository;
        this.anioAcademicoRepository = anioAcademicoRepository;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public void clonarConceptos(Integer anioOrigenId, Integer anioDestinoId) {
        AnioAcademico anioDestino = anioAcademicoRepository.findById(anioDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Año académico de destino no existe."));

        List<Concepto> conceptosOrigen = conceptoRepository.findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(anioOrigenId);
        if (conceptosOrigen.isEmpty()) {
            throw new IllegalStateException("No hay conceptos activos en el año origen para clonar.");
        }

        for (Concepto cOrigen : conceptosOrigen) {
            // Validar que no exista el concepto duplicado en el año destino
            boolean existe = conceptoRepository.existsByAnioAcademicoCodAnioAcademicoAndNombreConceptoAndEstadoTrue(
                    anioDestinoId, cOrigen.getNombreConcepto());

            if (!existe) {
                Concepto cClon = new Concepto();
                cClon.setAnioAcademico(anioDestino);
                cClon.setTipoConcepto(cOrigen.getTipoConcepto());
                cClon.setNombreConcepto(cOrigen.getNombreConcepto());
                cClon.setMonto(cOrigen.getMonto());
                cClon.setOrdenPago(cOrigen.getOrdenPago());
                cClon.setObligatorio(cOrigen.getObligatorio());
                cClon.setEstado(true);

                conceptoRepository.save(cClon);

                auditoriaService.registrar(
                        "CONFIGURACIÓN ACADÉMICA",
                        "concepto",
                        "CLONE",
                        cClon.getCodConcepto(),
                        null,
                        "Concepto clonado desde id: " + cOrigen.getCodConcepto()
                );
            }
        }
    }
}
