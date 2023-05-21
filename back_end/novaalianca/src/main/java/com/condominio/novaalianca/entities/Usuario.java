package com.condominio.novaalianca.entities;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NM_USUARIO")
    private String nomeUsuario;

    @Column(name = "TX_EMAIL", unique = true)
    private String txEmail;

    @Column(name = "NR_TELEFONE_DDD")
    private String nrTelefoneDdd;

    @Column(name = "NR_TELEFONE")
    private String nrTelefone;

    @Column(name = "NR_CELULAR_DDD")
    private String nrCelularDdd;

    @Column(name = "NR_CELULAR")
    private String nrCelular;

    @Column(name = "NR_DOCUMENTO_CPF")
    private String nrDocumentoCpf;

    @Column(name = "NR_DOCUMENTO_CNPJ")
    private String nrDocumentoCnpj;

    @Column(name = "TX_TIPO_PESSOA")
    private String txTipoPessoa;

    @Column(name = "FL_ENVIA_BOLETO")
    private boolean enviaBoleto;

    @Column(name = "FL_ENVIA_SMS")
    private boolean enviaSms;

    @Column(name = "FL_ATIVO")
    private boolean ativo;

    @Column(name = "TX_PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE")
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_perfil",
            joinColumns = @JoinColumn(name = "ID_USUARIO"),
            inverseJoinColumns = @JoinColumn(name = "ID_PERFIL"))
    @Setter(AccessLevel.NONE)
    private Set<Perfil> listPerfis = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return listPerfis.stream().map(perfil -> new SimpleGrantedAuthority(perfil.getNomePerfil())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return txEmail;
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
