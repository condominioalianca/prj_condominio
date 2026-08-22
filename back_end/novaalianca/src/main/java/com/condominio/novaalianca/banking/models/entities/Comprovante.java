package com.condominio.novaalianca.banking.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_comprovante", indexes = {
        @Index(name = "idx_comprovante_nome", columnList = "NOME_ARQUIVO")
})
public class Comprovante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMPROVANTE")
    private Long id;

    @Column(name = "NOME_ARQUIVO", nullable = false)
    private String nomeArquivo;

    @Column(name = "TIPO_ARQUIVO")
    private String tipoArquivo;

    @org.hibernate.annotations.Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "DADOS", nullable = false)
    private byte[] dados;
}
