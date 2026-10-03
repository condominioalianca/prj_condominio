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

    public CobrancaExtra getCobrancaExtraByIdUnidadeAndMesReferencia (Unidade unidade, int mesReferencia, int anoReferencia){
        LOGGER.info("Mes/Ano de Referencia Cobranca Extra: {}/{}", mesReferencia, anoReferencia);
        return cobrancaExtraRepository.findByidUnidadeAndMesReferenciaAndAnoReferencia(unidade, (long) mesReferencia, (long) anoReferencia);
    }

    public List<CobrancaExtra> getCobrancasExtrasParaUnidadeEMes(Unidade unidade, int mesReferencia, int anoReferencia) {
        LOGGER.info("Buscando Cobrancas Extras para Unidade: {}, Mes/Ano: {}/{}", unidade != null ? unidade.getIdUnidade() : null, mesReferencia, anoReferencia);
        return cobrancaExtraRepository.findCobrancasExtrasParaUnidadeEMes(unidade, (long) mesReferencia, (long) anoReferencia);
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
        String tipoAbrangencia = dto.getTipoAbrangencia() != null ? dto.getTipoAbrangencia() : "UNIDADE";
        if ("UNIDADE".equals(tipoAbrangencia) && dto.getIdUnidade() != null && dto.getIdUnidade() > 0) {
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
        
        String tipoAbrangencia = dto.getTipoAbrangencia() != null ? dto.getTipoAbrangencia() : "UNIDADE";
        Unidade unidade = null;
        if ("UNIDADE".equals(tipoAbrangencia) && dto.getIdUnidade() != null && dto.getIdUnidade() > 0) {
            unidade = unidadeRepository.findById(dto.getIdUnidade())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade nao encontrada para o ID: " + dto.getIdUnidade()));
        }
        
        entity.setValorCobranca(dto.getValorCobranca());
        entity.setDtInclusao(dto.getDtInclusao());
        entity.setMesReferencia(dto.getMesReferencia());
        entity.setAnoReferencia(dto.getAnoReferencia());
        entity.setDescricao(dto.getDescricao());
        entity.setTipoOperacao(dto.getTipoOperacao() != null ? dto.getTipoOperacao() : "ACRESCIMO");
        entity.setRecorrente(dto.getRecorrente() != null ? dto.getRecorrente() : false);
        entity.setTipoAbrangencia(tipoAbrangencia);
        entity.setUnidade("GERAL".equals(tipoAbrangencia) ? null : unidade);
        
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