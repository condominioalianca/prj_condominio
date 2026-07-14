package com.condominio.novaalianca.banking.models.entities;

import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusGeral;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_conciliacao", indexes = {
        @Index(name = "idx_conciliacao_status", columnList = "STATUS"),
        @Index(name = "idx_conciliacao_data", columnList = "DT_REFERENCIA")
})
public class Conciliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONCILIACAO")
    private Long id;

    @Column(name = "DT_REFERENCIA")
    private LocalDate dataReferencia;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS")
    private StatusConciliacao status;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "STATUS_GERAL")
    private StatusGeral statusGeral;

    @Column(name = "DT_CRIACAO")
    private LocalDateTime dataCriacao;

    @Column(name = "USUARIO_CRIACAO")
    private String usuarioCriacao;

    @Column(name = "DT_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @Column(name = "USUARIO_ATUALIZACAO")
    private String usuarioAtualizacao;

    @OneToMany(mappedBy = "conciliacao", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Extrato> extratos;

    @Column(name = "ARQUIVO_PDF")
    @org.hibernate.annotations.Type(type = "org.hibernate.type.BinaryType")
    private byte[] arquivoPdf;
}
