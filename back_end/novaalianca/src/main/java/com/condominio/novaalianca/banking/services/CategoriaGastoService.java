package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.CategoriaGasto;
import com.condominio.novaalianca.banking.repositories.CategoriaGastoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaGastoService {

    private final CategoriaGastoRepository categoriaGastoRepository;

    public List<CategoriaGasto> listarAtivas() {
        return categoriaGastoRepository.findByAtivoTrue();
    }

    public CategoriaGasto salvar(CategoriaGasto categoriaGasto) {
        return categoriaGastoRepository.save(categoriaGasto);
    }
}
