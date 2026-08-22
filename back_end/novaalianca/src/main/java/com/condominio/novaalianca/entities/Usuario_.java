package com.condominio.novaalianca.entities;



import org.hibernate.metamodel.model.domain.internal.SetAttributeImpl;

import jakarta.annotation.Generated;

import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.util.Set;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Usuario.class)
public abstract class Usuario_ {

    public static volatile SingularAttribute<Usuario, Long> idUsuario;
    public static volatile SingularAttribute<Usuario, String> nomeUsuario;
    public static volatile SingularAttribute<Usuario, String> txEmail;
    public static volatile SingularAttribute<Usuario, String> nrTelefoneDdd;
    public static volatile SingularAttribute<Usuario, String> nrTelefone;
    public static volatile SingularAttribute<Usuario, String> nrCelularDdd;
    public static volatile SingularAttribute<Usuario, String> nrCelular;
    public static volatile SingularAttribute<Usuario, String> nrDocumentoCpf;
    public static volatile SingularAttribute<Usuario, String> nrDocumentoCnpj;
    public static volatile SingularAttribute<Usuario, String> txTipoPessoa;
    public static volatile SingularAttribute<Usuario, Boolean> enviaBoleto;
    public static volatile SingularAttribute<Usuario, Boolean> enviaSms;
    public static volatile SingularAttribute<Usuario, Boolean> ativo;
    public static volatile SingularAttribute<Usuario, String> password;
    public static volatile SingularAttribute<Usuario, Unidade> unidade;
    public static volatile SingularAttribute<Usuario, Endereco> endereco;
    public static volatile SetAttributeImpl<Usuario, Set<Perfil>> listPerfis;

    public static final String ID_USUARIO = "idUsuario";

}
