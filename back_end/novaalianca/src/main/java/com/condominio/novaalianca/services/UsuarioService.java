package com.condominio.novaalianca.services;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.entities.Endereco;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.repositories.UsuarioSpecification;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    private final EnderecoRepository enderecoRepository;

    private final UsuarioBuilder usuarioBuilder;

    private final EnderecoService enderecoService;

    private final UnidadeService unidadeService;

    private final BCryptPasswordEncoder passwordEncoder;


    @Transactional
    public Page<UsuarioDTO> findAllPaged(Pageable pageable) {
        Page<Usuario> list = usuarioRepository.findAll(pageable);
        return list.map(x -> usuarioBuilder.entityToDto(x));
    }

    public UsuarioDTO findByID(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario entity = usuario.orElseThrow(() -> new ResourceNotFoundException("Usuario Não Encontrado"));
        return usuarioBuilder.entityToDto(entity);
    }

    public Usuario findByIDEntity(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        Usuario entity = usuario.orElseThrow(() -> new ResourceNotFoundException("Usuario Não Encontrado"));
        return entity;
    }

    @Transactional
    public UsuarioDTO usuarioSave(UsuarioDTO dto) {
        LOGGER.info("dto = {}", dto);

        Usuario usuario = usuarioBuilder.dtoToEntity(dto);
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        usuario = usuarioRepository.save(usuario);

        return usuarioBuilder.entityToDto(usuario);
    }

    @Transactional
    public void deletById(Long idUsuario) {

        usuarioRepository.deleteById(idUsuario);
    }




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByTxEmail(username);
        if (Objects.isNull(usuario.getTxEmail())) {
            throw new UsernameNotFoundException("Email não Encontrado");
        }
        return usuario;
    }

    @Transactional
    public void usuarioUpdate(UsuarioDTO usuarioDTO) {
        String existingPassword = usuarioRepository.findById(usuarioDTO.getIdUsuario())
                .map(Usuario::getPassword)
                .orElse(null);

        Usuario usuario = usuarioBuilder.dtoToEntity(usuarioDTO);

        if (usuarioDTO.getPassword() != null && !usuarioDTO.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        } else {
            usuario.setPassword(existingPassword);
        }

        usuarioRepository.save(usuario);
    }

    public Usuario findFirstByAtivosAndEnviaBoletoAndSemBoleto(LocalDate dtInicio, LocalDate dtFim) {
        List<Usuario> list = usuarioRepository.findFirstByAtivosAndEnviaBoletoAndSemBoleto(dtInicio, dtFim );
        return list.isEmpty() ? null : list.get(0);
    }

    public Usuario findByIdSpecification (Long idUnidade){
        Specification specification = UsuarioSpecification.findByIdUnidade(idUnidade);
        List<Usuario> usuarios = usuarioRepository.findAll(specification);
        return usuarios.get(0);
    }

    @Transactional
    public UsuarioDTO findByDocumentoOrEmail(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("O valor de busca nao pode ser nulo ou vazio");
        }
        Usuario entity;
        if (value.contains("@")) {
            entity = usuarioRepository.findByTxEmail(value);
        } else {
            entity = usuarioRepository.findByNrDocumentoCpf(value);
            if (entity == null) {
                entity = usuarioRepository.findByNrDocumentoCnpj(value);
            }
        }
        if (entity == null) {
            throw new ResourceNotFoundException("Usuario nao encontrado para: " + value);
        }
        return usuarioBuilder.entityToDto(entity);
    }

}