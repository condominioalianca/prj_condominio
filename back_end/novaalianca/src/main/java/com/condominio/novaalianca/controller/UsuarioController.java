package com.condominio.novaalianca.controller;

import com.condominio.novaalianca.entities.EnderecoUsuario;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.EnderecoService;
import com.condominio.novaalianca.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping("/save")
    public Usuario usuarioSave(){

        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("TESTE");
        usuario.setTxEmail("TESTE");
        usuario.setNrTelefoneDdd("11");
        usuario.setNrTelefone("99858744");
        usuario.setNrCelularDdd("11");
        usuario.setNrCelular("998587401");
        usuario.setNrDocumentoCpf("21958651800");
        usuario.setTxTipoPessoa("F");
        usuario.setEnviaBoleto(true);
        usuario.setEnviaSms(true);
        usuario.setAtivo(true);
        usuario = usuarioService.usuarioSave(usuario);

        EnderecoUsuario enderecoUsuario = new EnderecoUsuario();
        enderecoUsuario.setTxEndereco("TESTE");
        enderecoUsuario.setUsuario(usuario);
        enderecoUsuario.setTxBairro("TESTE");
        enderecoUsuario.setTxCidade("teste");
        enderecoUsuario.setTxEnderecoComplemento("teste");
        enderecoUsuario.setTxTipoPessoa("F");
        enderecoUsuario.setTxUf("SP");
        enderecoService.salvarEndereco(enderecoUsuario);


        return usuarioService.findById(usuario.getIdUsuario());
    }
}
