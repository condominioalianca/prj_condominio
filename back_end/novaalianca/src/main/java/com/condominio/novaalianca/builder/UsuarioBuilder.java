package com.condominio.novaalianca.builder;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.entities.Endereco;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioBuilder {

    private final UnidadeBuilder unidadeBuilder;

    private final EnderecoBuilder enderecoBuilder;

    private final PerfilBuilder perfilBuilder;

    private final UsuarioRepository repository;

//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsuarioDTO entityToDto (Usuario usuario){

        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nomeUsuario(usuario.getNomeUsuario())
                .nrCelularDdd(usuario.getNrCelularDdd())
                .nrCelular(usuario.getNrCelular())
                .nrTelefoneDdd(usuario.getNrTelefoneDdd())
                .nrTelefone(usuario.getNrTelefone())
                .nrDocumentoCpf(usuario.getNrDocumentoCpf())
                .nrDocumentoCnpj(usuario.getNrDocumentoCnpj())
                .txEmail(usuario.getTxEmail())
                .txTipoPessoa(usuario.getTxTipoPessoa())
                .ativo(usuario.isAtivo())
                .enviaBoleto(usuario.isEnviaBoleto())
                .enviaSms(usuario.isEnviaSms())
                .unidadeDTO(unidadeBuilder.entityToDto(usuario.getUnidade()))
                .enderecoDTO(enderecoBuilder.entityToDto(usuario.getEndereco()))
                .listPerfis(Objects.isNull(usuario.getListPerfis()) ? null : usuario.getListPerfis()
                        .stream()
                        .map(perfil -> perfilBuilder.entityToDto(perfil))
                        .collect(Collectors.toSet()))
                .build();
    }

    public Usuario dtoToEntity (UsuarioDTO dto){

        return Usuario.builder()
                .idUsuario(dto.getIdUsuario())
                .nomeUsuario(dto.getNomeUsuario())
                .txEmail(dto.getTxEmail())
                .nrCelularDdd(dto.getNrCelularDdd())
                .nrCelular(dto.getNrCelular())
                .nrTelefoneDdd(dto.getNrTelefoneDdd())
                .nrTelefone(dto.getNrTelefone())
                .nrDocumentoCpf(dto.getNrDocumentoCpf())
                .nrDocumentoCnpj(dto.getNrDocumentoCnpj())
                .txTipoPessoa(dto.getTxTipoPessoa())
                .ativo(dto.isAtivo())
                .enviaBoleto(dto.isEnviaBoleto())
                .enviaSms(dto.isEnviaSms())
                .unidade(unidadeBuilder.dtoToEntity(dto.getUnidadeDTO()))
                .endereco(enderecoBuilder.dtoToEntity(dto.getEnderecoDTO()))
                .listPerfis((Objects.isNull(dto.getListPerfis()) ? null : dto.getListPerfis()
                        .stream()
                        .map(perfil -> perfilBuilder.dtoToEntity(perfil))
                        .collect(Collectors.toSet())))
                .build();
    }

    public Usuario byCPF (String cpf){

        return repository.findByNrDocumentoCpf(cpf);
    }

//    public Usuario dtoToEntityNew(UsuarioDTO dto, Endereco endereco, Unidade unidade) {
//
//        return Usuario.builder()
//                .idUsuario(dto.getIdUsuario())
//                .nomeUsuario(dto.getNomeUsuario())
//                .txEmail(dto.getTxEmail())
//                .nrCelularDdd(dto.getNrCelularDdd())
//                .nrCelular(dto.getNrCelular())
//                .nrTelefoneDdd(dto.getNrTelefoneDdd())
//                .nrTelefone(dto.getNrTelefone())
//                .nrDocumentoCpf(dto.getNrDocumentoCpf())
//                .nrDocumentoCnpj(dto.getNrDocumentoCnpj())
//                .txTipoPessoa(dto.getTxTipoPessoa())
//                .ativo(dto.isAtivo())
//                .enviaBoleto(dto.isEnviaBoleto())
//                .enviaSms(dto.isEnviaSms())
//                .unidade(unidade)
//                .endereco(endereco)
//                .listPerfis((Objects.isNull(dto.getListPerfis()) ? null : dto.getListPerfis()
//                        .stream()
//                        .map(perfil -> perfilBuilder.dtoToEntity(perfil))
//                        .collect(Collectors.toSet())))
//                .build();
//    }

}