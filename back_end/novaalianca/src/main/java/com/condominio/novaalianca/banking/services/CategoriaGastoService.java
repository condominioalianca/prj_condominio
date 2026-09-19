package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import com.condominio.novaalianca.banking.repositories.CategoriaGastoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaGastoService {

    private final CategoriaGastoRepository categoriaGastoRepository;

    public List<CategoriaGasto> listarAtivas() {
        List<CategoriaGasto> lista = categoriaGastoRepository.findByAtivoTrue();
        for (CategoriaGasto cat : lista) {
            if (cat.getTipo() == null || cat.getTipo().trim().isEmpty()) {
                String desc = cat.getDescricao() != null ? cat.getDescricao().toLowerCase() : "";
                if (desc.contains("condominial") || desc.contains("multa") || desc.contains("juro")
                        || desc.contains("rendimento") || desc.contains("receita") || desc.contains("fundo de reserva")
                        || desc.contains("mudan") || desc.contains("espa")) {
                    cat.setTipo("C");
                } else {
                    cat.setTipo("D");
                }
            }
        }
        return lista;
    }

    public CategoriaGasto salvar(CategoriaGasto categoriaGasto) {
        return categoriaGastoRepository.save(categoriaGasto);
    }

    @Transactional
    public CategoriaGasto obterOuCriarCategoriaBoletoCondominial() {
        // Tenta buscar por "Taxa Condominial"
        Optional<CategoriaGasto> catOpt = categoriaGastoRepository.findFirstByDescricaoIgnoreCase("Taxa Condominial");
        if (catOpt.isPresent()) {
            CategoriaGasto cat = catOpt.get();
            if (!"C".equalsIgnoreCase(cat.getTipo())) {
                cat.setTipo("C");
                return categoriaGastoRepository.save(cat);
            }
            return cat;
        }

        // Fallback: tenta buscar qualquer categoria ativa de crédito com 'Condominial'
        catOpt = categoriaGastoRepository.findFirstByTipoAndDescricaoContainingIgnoreCase("C", "Condominial");
        if (catOpt.isPresent()) {
            return catOpt.get();
        }

        // Se não existir, cadastra automaticamente a categoria de crédito padrão
        log.info("Criando categoria padrão para Boleto Condominial: Taxa Condominial (tipo C)");
        CategoriaGasto novaCategoria = CategoriaGasto.builder()
                .descricao("Taxa Condominial")
                .tipo("C")
                .ativo(true)
                .build();
        return categoriaGastoRepository.save(novaCategoria);
    }
}
