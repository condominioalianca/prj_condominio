package com.condominio.novaalianca.entities;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "NM_USUARIO")
    private String nomeUsuario;

    @Column(name = "TX_EMAIL")
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

    @ManyToOne
    @JoinColumn(name = "ID_UNIDADE")
    private Unidade unidade;

    @ManyToOne
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;

        if (getIdUsuario() != null ? !getIdUsuario().equals(usuario.getIdUsuario()) : usuario.getIdUsuario() != null)
            return false;
        if (getNrDocumentoCpf() != null ? !getNrDocumentoCpf().equals(usuario.getNrDocumentoCpf()) : usuario.getNrDocumentoCpf() != null)
            return false;
        return getNrDocumentoCnpj() != null ? getNrDocumentoCnpj().equals(usuario.getNrDocumentoCnpj()) : usuario.getNrDocumentoCnpj() == null;
    }

    @Override
    public int hashCode() {
        int result = getIdUsuario() != null ? getIdUsuario().hashCode() : 0;
        result = 31 * result + (getNrDocumentoCpf() != null ? getNrDocumentoCpf().hashCode() : 0);
        result = 31 * result + (getNrDocumentoCnpj() != null ? getNrDocumentoCnpj().hashCode() : 0);
        return result;
    }
}
