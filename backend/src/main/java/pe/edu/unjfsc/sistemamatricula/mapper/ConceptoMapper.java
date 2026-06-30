package pe.edu.unjfsc.sistemamatricula.mapper;

import org.springframework.stereotype.Component;
import pe.edu.unjfsc.sistemamatricula.dto.financiero.ConceptoResponse;
import pe.edu.unjfsc.sistemamatricula.entity.financiero.Concepto;
import pe.edu.unjfsc.sistemamatricula.util.Constantes;

@Component
public class ConceptoMapper {

    public ConceptoResponse toResponse(Concepto concepto) {
        if (concepto == null) return null;
        return new ConceptoResponse(
                concepto.getId(),
                concepto.getAnioAcademico().getAnio(),
                concepto.getTipoConcepto().getNombre(),
                concepto.getNombre(),
                concepto.getMonto(),
                concepto.getOrdenCobro(),
                concepto.getEsDefault() != null && concepto.getEsDefault() == 1,
                concepto.getEstado(),
                concepto.getVersion()
        );
    }
}