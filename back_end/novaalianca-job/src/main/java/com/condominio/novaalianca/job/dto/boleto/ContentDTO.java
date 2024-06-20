package com.condominio.novaalianca.job.dto.boleto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class ContentDTO {

	private String cnpjCpfBeneficiario;
	private String nomeBeneficiario;
	private String contaCorrente;
	private String nossoNumero;
	private String seuNumero;
	private PagadorDTO pagador;
	private String situacao;
	private String dataHoraSituacao;
	private LocalDate dataVencimento;
	private Float valorNominal;
	private Float valorTotalRecebimento;
	private LocalDate dataEmissao;
	private LocalDate dataLimite;
	private String codigoEspecie;
	private String codigoBarras;
	private String linhaDigitavel;
	private String origem;
	private MensagemDTO mensagemDTO;
	private DescontoDTO desconto1;
	private DescontoDTO desconto2;
	private DescontoDTO desconto3;
	private MultaDTO multa;
	private MoraDTO mora;

	public String getDataHoraSituacao() {
		DateTimeFormatter formatterDataHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return LocalDateTime.parse(dataHoraSituacao,formatterDataHora).format(formatterDataHora1);
	}
}
