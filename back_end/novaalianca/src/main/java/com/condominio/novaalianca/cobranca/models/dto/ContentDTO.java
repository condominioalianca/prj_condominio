package com.condominio.novaalianca.cobranca.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ContentDTO {

    private CobrancaDTO cobranca;
    private BoletoDTO boleto;
    private PixDTO pix;

//	private String cnpjCpfBeneficiario;
//	private String nomeBeneficiario;
//	private String contaCorrente;
//	private String nossoNumero;
//	private String seuNumero;
//	private PagadorDTO pagador;
//	private String situacao;
//	private String dataHoraSituacao;
//	private LocalDate dataVencimento;
//	private Float valorNominal;
//	private Float valorTotalRecebimento;
//	private LocalDate dataEmissao;
//	private LocalDate dataLimite;
//	private String codigoEspecie;
//	private String codigoBarras;
//	private String linhaDigitavel;
//	private String origem;
//	private MensagemDTO mensagemDTO;
//	private DescontoDTO desconto1;
//	private DescontoDTO desconto2;
//	private DescontoDTO desconto3;
//	private MultaDTO multa;
//	private MoraDTO mora;

//	public String getDataHoraSituacao() {
//		DateTimeFormatter formatterDataHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
//		return LocalDateTime.parse(dataHoraSituacao,formatterDataHora).format(formatterDataHora1);
//	}
}
