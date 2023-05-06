package com.condominio.novaalianca.services;

import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.dto.UsuarioDTO;
import com.condominio.novaalianca.dto.UsuarioInsertDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.EnderecoRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service

public class UsuarioService //implements UserDetailsService
{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UsuarioBuilder usuarioBuilder;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;


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

    @Transactional
    public UsuarioDTO usuarioSave(UsuarioInsertDTO dto) {

        Usuario usuario = usuarioBuilder.dtoToEntity(dto);
        //usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario = usuarioRepository.save(usuario);

        return usuarioBuilder.entityToDto(usuario);
    }

    @Transactional
    public void deletById(Long idUsuario) {

        usuarioRepository.deleteById(idUsuario);
    }


//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Usuario usuario = usuarioRepository.findByTxEmail(username);
//        if (Objects.isNull(usuario.getTxEmail())) {
//            throw new UsernameNotFoundException("Email não Encontrado");
//        }
//        return usuario;
//    }
}
