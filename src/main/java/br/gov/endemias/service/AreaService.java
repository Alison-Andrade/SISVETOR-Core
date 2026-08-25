package br.gov.endemias.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.gov.endemias.domain.entity.Agente;
import br.gov.endemias.domain.entity.Area;
import br.gov.endemias.dto.AreaRequest;
import br.gov.endemias.dto.AreaResponse;
import br.gov.endemias.exception.ResourceNotFoundException;
import br.gov.endemias.repository.AreaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AreaService {
    
    private final AreaRepository areaRepository;
    private final AgenteService agenteService;

    public AreaResponse cadastrar(AreaRequest request) {
        Area area = request.toEntity();

        if (request.agenteId() != null) {
            Agente agenteResponsavel = agenteService.buscarEntityPorId(request.agenteId());
            area.setAgenteResponsavel(agenteResponsavel);
        }

        Area areaSalva = areaRepository.save(area);
        return AreaResponse.fromEntity(areaSalva);
    }

    public AreaResponse buscarPorId(Long id) {
        return AreaResponse.fromEntity(areaRepository.findById(id).get());
    }

    public List<AreaResponse> listar() {
        List<AreaResponse> areas = areaRepository
                .findAll()
                .stream()
                .map(AreaResponse::fromEntity)
                .toList();
        return areas;
    }

    public AreaResponse atualizar(Long id, AreaRequest request) {
        Area area = buscarEntityPorId(id);

        area.setNumArea(request.numArea() != null ? request.numArea() : area.getNumArea());
        
        if (request.agenteId() != null) {
            Agente agenteResponsavel = agenteService.buscarEntityPorId(request.agenteId());
            area.setAgenteResponsavel(agenteResponsavel);
        } else {
            area.setAgenteResponsavel(area.getAgenteResponsavel());
        }

        Area areaAtualizada = areaRepository.save(area);
        return AreaResponse.fromEntity(areaAtualizada);
    }

    public void deletar(Long id) {
        areaRepository.deleteById(id);
    }

    public Area buscarEntityPorId(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area não encontrada com id: " + id));
    }

}
