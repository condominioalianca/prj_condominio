package com.condominio.novaalianca.services;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.builder.CobrancaExtraBuilder;
import com.condominio.novaalianca.dto.CobrancaExtraDTO;
import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.repositories.CobrancaExtraRepository;
import com.condominio.novaalianca.repositories.UnidadeRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CobrancaExtraService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CobrancaExtraService.class);

    private final CobrancaExtraRepository cobrancaExtraRepository;

    private final UnidadeRepository unidadeRepository;

    private final CobrancaExtraBuilder builder;

    public CobrancaExtra getCobrancaExtraByIdUnidadeAndMesReferencia (Unidade unidade, int mesReferencia){
        LOGGER.info("Mes de Referencia Cobranca Extra: {}",mesReferencia);
        return cobrancaExtraRepository.findByidUnidadeAndMesReferencia(unidade, (long) mesReferencia);
    }

    @Transactional(readOnly = true)
    public List<CobrancaExtraDTO> findAll() {
        List<CobrancaExtra> list = cobrancaExtraRepository.findAll();
        return list.stream().map(builder::entityToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CobrancaExtraDTO findById(Long id) {
        CobrancaExtra entity = cobrancaExtraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cobranca Extra nao encontrada para o ID: " + id));
        return builder.entityToDto(entity);
    }

    @Transactional
    public CobrancaExtraDTO save(CobrancaExtraDTO dto) {
        Unidade unidade = null;
        if (dto.getIdUnidade() != null) {
            unidade = unidadeRepository.findById(dto.getIdUnidade())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada para o ID: " + dto.getIdUnidade()));
        }
        CobrancaExtra entity = builder.dtoToEntity(dto, unidade);
        entity = cobrancaExtraRepository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public CobrancaExtraDTO update(CobrancaExtraDTO dto) {
        CobrancaExtra entity = cobrancaExtraRepository.findById(dto.getIdCobrancaExtra())
                .orElseThrow(() -> new ResourceNotFoundException("Cobranca Extra nao encontrada para o ID: " + dto.getIdCobrancaExtra()));
        
        Unidade unidade = null;
        if (dto.getIdUnidade() != null) {
            unidade = unidadeRepository.findById(dto.getIdUnidade())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada para o ID: " + dto.getIdUnidade()));
        }
        
        entity.setValorCobranca(dto.getValorCobranca());
        entity.setDtInclusao(dto.getDtInclusao());
        entity.setMesReferencia(dto.getMesReferencia());
        entity.setDescricao(dto.getDescricao());
        entity.setUnidade(unidade);
        
        entity = cobrancaExtraRepository.save(entity);
        return builder.entityToDto(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!cobrancaExtraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cobranca Extra nao encontrada para o ID: " + id);
        }
        cobrancaExtraRepository.deleteById(id);
    }
}