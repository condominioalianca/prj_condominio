package com.condominio.novaalianca.job.entities;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.CascadeType;
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
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tb_usuario")
public class Usuario {



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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDADE")
    private Unidade unidade;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_perfil",
            joinColumns = @JoinColumn(name = "ID_USUARIO"),
            inverseJoinColumns = @JoinColumn(name = "ID_PERFIL"))
    @Setter(AccessLevel.NONE)
    private Set<Perfil> listPerfis = new HashSet<>();

    public Usuario(Long idUsuario, String nomeUsuario, String txEmail, String nrTelefoneDdd, String nrTelefone, String nrCelularDdd, String nrCelular, String nrDocumentoCpf, String nrDocumentoCnpj, String txTipoPessoa, boolean enviaBoleto, boolean enviaSms, boolean ativo, Unidade unidade, Endereco endereco) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.txEmail = txEmail;
        this.nrTelefoneDdd = nrTelefoneDdd;
        this.nrTelefone = nrTelefone;
        this.nrCelularDdd = nrCelularDdd;
        this.nrCelular = nrCelular;
        this.nrDocumentoCpf = nrDocumentoCpf;
        this.nrDocumentoCnpj = nrDocumentoCnpj;
        this.txTipoPessoa = txTipoPessoa;
        this.enviaBoleto = enviaBoleto;
        this.enviaSms = enviaSms;
        this.ativo = ativo;
        this.unidade = unidade;
        this.endereco = endereco;
    }

    public Usuario() {
    }
}
