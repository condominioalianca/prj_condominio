package com.condominio.novaalianca.cobranca.builder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import com.condominio.novaalianca.builder.UnidadeBuilder;
import com.condominio.novaalianca.builder.UsuarioBuilder;
import com.condominio.novaalianca.cobranca.models.dto.BoletoDTO;
import com.condominio.novaalianca.cobranca.models.dto.ResponseCobrancaDTO;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.inter.cobranca.Boleto;
import com.condominio.novaalianca.dto.inter.cobranca.Desconto;
import com.condominio.novaalianca.dto.inter.cobranca.EmissaoBoletoResponseDTO;
import com.condominio.novaalianca.dto.inter.cobranca.Mensagem;
import com.condominio.novaalianca.dto.inter.cobranca.Mora;
import com.condominio.novaalianca.dto.inter.cobranca.Multa;
import com.condominio.novaalianca.dto.inter.cobranca.Pessoa;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.entities.CobrancaExtra;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.enums.OrigemBoleto;
import com.condominio.novaalianca.enums.ParametrosSistema;
import com.condominio.novaalianca.enums.inter.SituacaoBoleto;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.CobrancaExtraService;
import com.condominio.novaalianca.util.Feriados;
import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoDesconto;
import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoMora;
import com.condominio.novaalianca.dto.inter.cobranca.enums.CodigoMulta;
import com.condominio.novaalianca.dto.inter.cobranca.enums.TipoPessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BoletoBuilder {

	@Autowired
	Feriados feriados;

	@Autowired
	private UnidadeBuilder unidadeBuilder;

	@Autowired
	private UsuarioBuilder usuarioBuilder;
	
	@Autowired
	ParametrosSistemaRepository parametrosSistemaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CobrancaExtraService cobrancaExtraService;

	@Autowired
	private NovaAliancaProperties properties;

	Locale BRASILLOCALE = new Locale("pt","BR");


	public Boleto boletoInter (Usuario usuario) throws ParseException {
		Locale ptBr = new Locale("pt", "BR");
		DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
		Double valorCondominio = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_CONDOMINIO.toString() +"_"+ LocalDate.now().format(formatterYear))));
		Double valorTaxaMinAgua = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_TAXA_MIN_AGUA.toString())));
		Double valorMulta = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_MULTA.toString())));
		Double valorMora = (Double.valueOf(parametrosSistemaRepository.findValorParametro(ParametrosSistema.VALOR_MORA.toString())));
		int diaVencimento = Integer.parseInt(parametrosSistemaRepository.findValorParametro(ParametrosSistema.DIA_DE_VENCIMENTO_BOLETO.toString()));
		double valorTaxaAguaAcrescimoSetentaPorCento = valorTaxaMinAgua * 0.7;
		double valorCondominio1Morador = valorCondominio+valorTaxaMinAgua;
		double valoraguaMaisMorador = valorTaxaMinAgua+valorTaxaAguaAcrescimoSetentaPorCento;
		double valorCondominioMaisMorador = valorCondominio+valoraguaMaisMorador;
		DateTimeFormatter formatterSeuNumer = DateTimeFormatter.ofPattern("MMyyyy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		CobrancaExtra cobrancaExtra = cobrancaExtraService.getCobrancaExtraByIdUnidadeAndMesReferencia(usuario.getUnidade(),LocalDate.now().getMonth().getValue());


		Mensagem mensagem = new Mensagem();
		mensagem.setLinha1("TAXA CONDOMINAL REFERENTE AO MÊS " + LocalDate.now().format(formatterSeuNumer));
		if(cobrancaExtra != null && cobrancaExtra.getValorCobranca()>0){
			String linha_2 = "TAXA CONDOMINNIO = " + NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio);
			linha_2 = linha_2 + " + "+ cobrancaExtra.getDescricao()+ " " + NumberFormat.getCurrencyInstance(ptBr).format(cobrancaExtra.getValorCobranca());
			mensagem.setLinha2(linha_2);

			valorCondominioMaisMorador = valorCondominioMaisMorador+cobrancaExtra.getValorCobranca();
			valorCondominio1Morador = valorCondominio1Morador+cobrancaExtra.getValorCobranca();

		}else {
			mensagem.setLinha2("TAXA CONDOMINNIO = " + NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio));
		}
		mensagem.setLinha3("TAXA MIN AGUA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaMinAgua));


		Boleto boletoInter = new Boleto();
		boletoInter.setSeuNumero(LocalDate.now().format(formatterSeuNumer) + usuario.getUnidade().getNumeroUnidade());
		boletoInter.setDataVencimento(this.verificaFeriado(diaVencimento).toString());
		boletoInter.setNumDiasAgenda(30);

		if(usuario.getUnidade().getQtMorador()>1){
			mensagem.setLinha4("ACRESCIMO 70% DA TAXA MIN (UNIDADE COM MAIS DE 1 MORADOR) = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorTaxaAguaAcrescimoSetentaPorCento));
			mensagem.setLinha5("VALOR TOTAL DA COBRANÇA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorCondominioMaisMorador));
			boletoInter.setValorNominal(BigDecimal.valueOf(valorCondominioMaisMorador).setScale(2,BigDecimal.ROUND_HALF_EVEN));

		}else{
			mensagem.setLinha4("VALOR TOTAL DA COBRANÇA = "+ NumberFormat.getCurrencyInstance(ptBr).format(valorCondominio1Morador));
			boletoInter.setValorNominal(BigDecimal.valueOf(valorCondominio1Morador).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}

		boletoInter.setMensagem(mensagem);
		Pessoa pagador = new Pessoa();
		/* Preenchendo Dados Pagador */
		pagador.setCpfCnpj(Objects.isNull(usuario.getNrDocumentoCpf() )? usuario.getNrDocumentoCnpj() : usuario.getNrDocumentoCpf());
		pagador.setNome(usuario.getNomeUsuario());
		pagador.setEmail(usuario.getTxEmail());
		pagador.setTelefone(Objects.isNull(usuario.getNrCelular() ) ? "" : usuario.getNrCelular());
		pagador.setEndereco(usuario.getEndereco().getTxEndereco());
		pagador.setNumero(usuario.getEndereco().getTxEnderecoNumero());
		pagador.setComplemento(usuario.getEndereco().getTxEnderecoComplemento());
		pagador.setBairro(usuario.getEndereco().getTxBairro());
		pagador.setCidade(usuario.getEndereco().getTxCidade());
		pagador.setUf(usuario.getEndereco().getTxUf());
		pagador.setCep(usuario.getEndereco().getTxCep());
		pagador.setDdd(usuario.getNrCelularDdd() == null ? "" : usuario.getNrCelularDdd());
		pagador.setTipoPessoa(TipoPessoa.FISICA);

		boletoInter.setPagador(pagador);




		Desconto desconto = new Desconto();
		/* Preenchendo Desconto 1 */
		desconto.setCodigoDesconto(CodigoDesconto.NAOTEMDESCONTO);
		desconto.setTaxa(BigDecimal.ZERO);
		desconto.setValor(BigDecimal.ZERO);
		desconto.setData("");

		/* Preenchendo Desconto 2 */
		boletoInter.setDesconto1(desconto);
		boletoInter.setDesconto2(desconto);
		boletoInter.setDesconto3(desconto);


		Multa multa = new Multa();
		boletoInter.setMulta(multa);

		/* Preenchendo Multa */
		boletoInter.getMulta().setCodigo(CodigoMulta.VALORFIXO);
		boletoInter.getMulta().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boletoInter.getMulta().setValor(BigDecimal.valueOf(valorMulta));
		boletoInter.getMulta().setTaxa(BigDecimal.ZERO);
		Mora mora = new Mora();
		boletoInter.setMora(mora);

		/* Preenchendo Mora */
		boletoInter.getMora().setCodigo(CodigoMora.TAXAMENSAL);
		boletoInter.getMora().setData(this.verificaFeriado(diaVencimento).plusDays(1).format(formatter));
		boletoInter.getMora().setTaxa(BigDecimal.valueOf(valorMora));
		boletoInter.getMora().setValor(BigDecimal.ZERO);

		Pessoa beneficiarioFinal = new Pessoa();
		boletoInter.setBeneficiarioFinal(beneficiarioFinal);
		boletoInter.getBeneficiarioFinal().setNome("Condominio Nova Alianca");
		boletoInter.getBeneficiarioFinal().setCpfCnpj(properties.getCnpjCpfBenificiario());
		boletoInter.getBeneficiarioFinal().setTipoPessoa(TipoPessoa.JURIDICA);
		boletoInter.getBeneficiarioFinal().setCep("09894205");
		boletoInter.getBeneficiarioFinal().setEndereco("RUA ARNALDO MARGONARI");
		boletoInter.getBeneficiarioFinal().setBairro("JORDANOPOLIS");
		boletoInter.getBeneficiarioFinal().setCidade("SAO BERNARDO DO CAMPO");
		boletoInter.getBeneficiarioFinal().setUf("SP");

		return boletoInter;
	}

	public LocalDate verificaFeriado(Integer diaVencimento) throws ParseException {

		LocalDate dataVencimento = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
				diaVencimento);
		while (feriados.verificaFeriado(dataVencimento) || dataVencimento.getDayOfWeek().getValue() > 5) {
			dataVencimento = dataVencimento.plusDays(1);
		}
		return dataVencimento;

	}


public BoletoDTO entityToBoletoDTO(BoletoNovaAlianca ent){
		return BoletoDTO.builder()
				.nossoNumero(ent.getNossoNumero())
				.codigoBarras(ent.getTxCodBarras())
				.linhaDigitavel(ent.getTxLinhaDigitavel())
				.build();
}
//	public BoletoNovaAlianca entityInterToEntityNovaAlianca(BoletoDetalhado boleto) {
//		DateTimeFormatter formatterLocalDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//		DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		Usuario usuario = usuarioRepository.findByTxEmail(boleto.getPagador().getEmail());
//
//
//		return BoletoNovaAlianca.builder()
//				.nossoNumero(boleto.getNossoNumero())
//				.seuNumero(boleto.getSeuNumero())
//				.txCancelamento(boleto.getMotivoCancelamento())
//				.txSituacao(boleto.getSituacao())
//				.dhSituacao(LocalDateTime.parse(boleto.getDataHoraSituacao(), formatterLocalDateTime))
//				.dtVencimento(LocalDate.parse(boleto.getDataVencimento(),formatterLocalDate))
//				.valor(boleto.getValorNominal().doubleValue())
//				.dtEmissao(LocalDate.parse(boleto.getDataEmissao(),formatterLocalDate))
//				.dtLimitePagamento(LocalDate.parse(boleto.getDataLimite(),formatterLocalDate))
//				.txEspecie(boleto.getCodigoEspecie())
//				.txCodBarras(boleto.getCodigoBarras())
//				.txLinhaDigitavel(boleto.getLinhaDigitavel())
//				.txOrigem(boleto.getOrigem())
//				//.empresa()
//				.usuario(usuario)
//				//.valorPagamento(boleto.get)
//				//.motivoBaixa()
//				//.dtBaixa()
//				//.dtPagamento()
//				.dtEnvio(LocalDate.now())
//				.idUnidade(usuario.getUnidade())
//				//.arquivopdf()
//				.ativo(Objects.isNull(boleto.getMotivoCancelamento()) ? Boolean.TRUE : Boolean.FALSE)
//				.build();
//	}

	public BoletoNovaAlianca updateBoletoCarga(BoletoNovaAlianca boleto, ResponseCobrancaDTO dto){
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		boleto.setDhSituacao(LocalDate.parse(dto.getCobranca().getDataSituacao(),formatterData).atStartOfDay());
		boleto.setDtPagamento(LocalDate.parse(dto.getCobranca().getDataSituacao(),formatterData));
//		boleto.setTxEspecie(dto.getCobranca().);
		boleto.setCodSolicitacao(dto.getCobranca().getCodigoSolicitacao());
		boleto.setTxOrigem(dto.getCobranca().getOrigemRecebimento());
		boleto.setTxSituacao(dto.getCobranca().getSituacao());
		boleto.setValor(Double.valueOf(dto.getCobranca().getValorNominal()));
		boleto.setValorPagamento(Objects.isNull(dto.getCobranca().getValorTotalRecebido())? 0 : Double.valueOf(dto.getCobranca().getValorTotalRecebido()));
		boleto.setAtivo(!dto.getCobranca().getSituacao().equals("CANCELADO") ? Boolean.TRUE : Boolean.FALSE);
		return boleto;
	}

	public BoletoNovaAlianca newBoletoCarga (ResponseCobrancaDTO dto){
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		DateTimeFormatter formatterData1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		Usuario usuario = usuarioBuilder.byCPF(dto.getCobranca().getPagador().getCpfCnpj());
		BoletoNovaAlianca boletoNovaAlianca = new BoletoNovaAlianca();
		boletoNovaAlianca.setDhSituacao(LocalDate.parse(dto.getCobranca().getDataSituacao(),formatterData1).atStartOfDay());
		boletoNovaAlianca.setDtBaixa(null);
		boletoNovaAlianca.setDtEmissao(LocalDate.parse(dto.getCobranca().getDataEmissao(),formatterData1));
		boletoNovaAlianca.setDtEnvio(null);
//		boletoNovaAlianca.setDtLimitePagamento(dto.getDataLimite());
//		boletoNovaAlianca.setDtPagamento(LocalDate.parse(dto.getDataHoraSituacao(), formatterDataHora1));
		boletoNovaAlianca.setDtVencimento(LocalDate.parse(dto.getCobranca().getDataVencimento(),formatterData1));
		boletoNovaAlianca.setMotivoBaixa(null);
		boletoNovaAlianca.setNossoNumero(dto.getBoleto().getNossoNumero());
		boletoNovaAlianca.setSeuNumero(dto.getCobranca().getSeuNumero());
		boletoNovaAlianca.setTxCancelamento(null);
		boletoNovaAlianca.setTxCodBarras(dto.getBoleto().getCodigoBarras());
//		boletoNovaAlianca.setTxEspecie(dto.getBoleto().getTxEspecie());
		boletoNovaAlianca.setTxLinhaDigitavel(dto.getBoleto().getLinhaDigitavel());
//		boletoNovaAlianca.setTxOrigem(dto.getBoleto().getTxOrigem());
		boletoNovaAlianca.setTxSituacao(dto.getCobranca().getSituacao());
		boletoNovaAlianca.setValor(Double.valueOf(dto.getCobranca().getValorNominal()));
		boletoNovaAlianca.setValorPagamento(Objects.isNull(dto.getCobranca().getValorTotalRecebido())? 0 : Double.valueOf(dto.getCobranca().getValorTotalRecebido()));
		boletoNovaAlianca.setEmpresa(null);
		boletoNovaAlianca.setIdUnidade(usuario.getUnidade());
		boletoNovaAlianca.setUsuario(usuario);
		boletoNovaAlianca.setAtivo(!dto.getCobranca().getSituacao().equals("CANCELADO") ? Boolean.TRUE : Boolean.FALSE);
		boletoNovaAlianca.setEmailEnviado(Boolean.FALSE);
		boletoNovaAlianca.setCodSolicitacao(dto.getCobranca().getCodigoSolicitacao());


		return boletoNovaAlianca;
	}

	public BoletoNovaAlianca newBoletoCargaV2 (Boleto boleto, EmissaoBoletoResponseDTO dto, OrigemBoleto origem){
		DateTimeFormatter formatterDataHora1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		DateTimeFormatter formatterData1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		Usuario usuario = usuarioBuilder.byCPF(boleto.getPagador().getCpfCnpj());
		BoletoNovaAlianca boletoNovaAlianca = new BoletoNovaAlianca();
		boletoNovaAlianca.setDhSituacao(LocalDateTime.now());
		boletoNovaAlianca.setDtBaixa(null);
		boletoNovaAlianca.setDtEmissao(LocalDate.now());
		boletoNovaAlianca.setDtEnvio(null);
		boletoNovaAlianca.setDtLimitePagamento(boletoNovaAlianca.getDtEmissao().plusDays(30));
//		boletoNovaAlianca.setDtPagamento(LocalDate.parse(dto.getDataHoraSituacao(), formatterDataHora1));
		boletoNovaAlianca.setDtVencimento(LocalDate.parse(boleto.getDataVencimento(),formatterData1));
		boletoNovaAlianca.setMotivoBaixa(null);
//		boletoNovaAlianca.setNossoNumero(boleto.getBoleto().getNossoNumero());
		boletoNovaAlianca.setSeuNumero(boleto.getSeuNumero());
		boletoNovaAlianca.setTxCancelamento(null);
//		boletoNovaAlianca.setTxCodBarras(boleto.getBoleto().getCodigoBarras());
//		boletoNovaAlianca.setTxEspecie(dto.getBoleto().getTxEspecie());
//		boletoNovaAlianca.setTxLinhaDigitavel(boleto.getBoleto().getLinhaDigitavel());
		boletoNovaAlianca.setTxOrigem(origem != null ? origem.name() : null);
		boletoNovaAlianca.setTxSituacao(SituacaoBoleto.A_RECEBER.getValue());
		boletoNovaAlianca.setValor(Double.valueOf(String.valueOf(boleto.getValorNominal())));
//		boletoNovaAlianca.setValorPagamento(Objects.isNull(boleto.getCobranca().getValorTotalRecebido())? 0 : Double.valueOf(boleto.getCobranca().getValorTotalRecebido()));
		boletoNovaAlianca.setEmpresa(null);
		boletoNovaAlianca.setIdUnidade(usuario.getUnidade());
		boletoNovaAlianca.setUsuario(usuario);
		boletoNovaAlianca.setAtivo(Boolean.TRUE);
		boletoNovaAlianca.setEmailEnviado(Boolean.FALSE);
		boletoNovaAlianca.setCodSolicitacao(dto.getCodigoSolicitacao());


		return boletoNovaAlianca;
	}



}
