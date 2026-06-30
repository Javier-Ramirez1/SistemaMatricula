package pe.edu.unjfsc.sistemamatricula.service.financiero;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ClonarConceptosRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ConceptoRequest;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ConceptoResponse;
import pe.edu.unjfsc.sistemamatricula.entity.academico.AnioAcademico;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Concepto;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.TipoConcepto;
import pe.edu.unjfsc.sistemamatricula.exception.BusinessException;
import pe.edu.unjfsc.sistemamatricula.exception.ResourceNotFoundException;
import pe.edu.unjfsc.sistemamatricula.mapper.ConceptoMapper;
import pe.edu.unjfsc.sistemamatricula.repository.AnioAcademicoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.ConceptoRepository;
import pe.edu.unjfsc.sistemamatricula.repository.TipoConceptoRepository;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tarifario por año académico. Incluye la "herramienta para clonar
 * por año" mencionada en los apuntes: copia todos los conceptos
 * es_default=true (obligatorios para todos los años) de un año
 * origen a un año destino, evitando recapturarlos manualmente
 * cada vez que se crea un nuevo año académico.
 */
@Service
@RequiredArgsConstructor
public class ConceptoService {

    private final ConceptoRepository conceptoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final TipoConceptoRepository tipoConceptoRepository;
    private final ConceptoMapper conceptoMapper;

    @Transactional(readOnly = true)
    public List<ConceptoResponse> listarPorAnio(Long idAnioAcademico) {
        return conceptoRepository.findByAnioAcademicoIdAndEstadoOrderByOrdenCobro(idAnioAcademico, Constantes.ACTIVO)
                .stream()
                .map(conceptoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Concepto obtenerPorId(Long id) {
        return conceptoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto", id));
    }

    @Transactional
    public ConceptoResponse crear(ConceptoRequest request) {
        AnioAcademico anio = anioAcademicoRepository.findById(request.getIdAnioAcademico())
                .orElseThrow(() -> new ResourceNotFoundException("AnioAcademico", request.getIdAnioAcademico()));
        TipoConcepto tipoConcepto = tipoConceptoRepository.findById(request.getIdTipoConcepto())
                .orElseThrow(() -> new ResourceNotFoundException("TipoConcepto", request.getIdTipoConcepto()));

        Concepto concepto = new Concepto();
        concepto.setAnioAcademico(anio);
        concepto.setTipoConcepto(tipoConcepto);
        concepto.setNombre(request.getNombre());
        concepto.setMonto(request.getMonto());
        concepto.setOrdenCobro(request.getOrdenCobro());
        concepto.setEsDefault((byte) (request.isEsDefault() ? 1 : 0));
        concepto.setEstado(Constantes.ACTIVO);

        return conceptoMapper.toResponse(conceptoRepository.save(concepto));
    }

    /**
     * Actualiza un concepto existente. Como Concepto tiene @Version,
     * si otra secretaria editó el monto entre la carga de pantalla y
     * el guardado, JPA lanza OptimisticLockException automáticamente
     * (capturado por GlobalExceptionHandler) — este es el escenario
     * de optimistic locking explicado en la sustentación técnica.
     */
    @Transactional
    public ConceptoResponse actualizar(Long id, ConceptoRequest request) {
        Concepto concepto = obtenerPorId(id);
        concepto.setNombre(request.getNombre());
        concepto.setMonto(request.getMonto());
        concepto.setOrdenCobro(request.getOrdenCobro());
        concepto.setEsDefault((byte) (request.isEsDefault() ? 1 : 0));
        return conceptoMapper.toResponse(conceptoRepository.save(concepto));
    }

    @Transactional
    public void eliminarLogico(Long id) {
        Concepto concepto = obtenerPorId(id);
        concepto.setEstado(Constantes.INACTIVO);
        conceptoRepository.save(concepto);
    }

    /** Clona el tarifario de un año a otro (solo los conceptos marcados es_default). */
    @Transactional
    public List<ConceptoResponse> clonarConceptos(ClonarConceptosRequest request) {
        if (request.getIdAnioAcademicoOrigen().equals(request.getIdAnioAcademicoDestino())) {
            throw new BusinessException("El año de origen y destino no pueden ser el mismo.");
        }

        AnioAcademico destino = anioAcademicoRepository.findById(request.getIdAnioAcademicoDestino())
                .orElseThrow(() -> new ResourceNotFoundException("AnioAcademico", request.getIdAnioAcademicoDestino()));

        List<Concepto> origenes = conceptoRepository.findByAnioAcademicoIdAndEsDefaultAndEstado(
                request.getIdAnioAcademicoOrigen(), (byte) 1, Constantes.ACTIVO);

        if (origenes.isEmpty()) {
            throw new BusinessException("El año de origen no tiene conceptos por defecto para clonar.");
        }

        List<Concepto> clonados = origenes.stream().map(original -> {
            Concepto clon = new Concepto();
            clon.setAnioAcademico(destino);
            clon.setTipoConcepto(original.getTipoConcepto());
            clon.setNombre(original.getNombre());
            clon.setMonto(original.getMonto());
            clon.setOrdenCobro(original.getOrdenCobro());
            clon.setEsDefault((byte) 1);
            clon.setEstado(Constantes.ACTIVO);
            return clon;
        }).collect(Collectors.toList());

        return conceptoRepository.saveAll(clonados).stream()
                .map(conceptoMapper::toResponse)
                .collect(Collectors.toList());
    }
}