package com.condominio.novaalianca.banking.services;

import com.condominio.novaalianca.banking.models.entities.Extrato;
import com.condominio.novaalianca.banking.repositories.ExtratoRepository;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.cobranca.repositories.BoletoRepository;
import com.condominio.novaalianca.services.InterSDKService;
import com.google.gson.Gson;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.banking.model.TransacaoEnriquecida;
import inter.exceptions.SdkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Slf4j

@Service
public class ExtratoService {

    @Autowired
    InterSDKService interSDKService;

    @Autowired
    private ExtratoRepository extratoRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private NovaAliancaProperties properties;

    @Autowired
    private Gson gson;

    public List<Extrato> getExtratoEnriquecido(String dataInicio, String dataFim, FiltroConsultarExtratoEnriquecido filtro) throws SdkException {
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Extrato> listExtrato = new ArrayList<>();

        List<TransacaoEnriquecida> listTransacoes = interSDKService.banking(dataInicio, dataFim, filtro.builder().build());
        if(!listTransacoes.isEmpty()){
            listTransacoes.forEach(listTransacao -> {
                Extrato extrato = new Extrato();
                extrato.setIdTransacao(listTransacao.getIdTransacao());
                extrato.setDtInclusao(LocalDate.parse(listTransacao.getDataTransacao(),formatterLocalDate));
                extrato.setTipoTransacao(listTransacao.getTipoTransacao());
                extrato.setTipoOperacao(listTransacao.getTipoOperacao());
                extrato.setTituloTransacao(listTransacao.getTitulo());
                extrato.setValorTransacao(Double.valueOf(listTransacao.getValor()));
                log.debug("Transação: {}", listTransacao);
                if(Objects.nonNull(listTransacao.getDetalhes())){
                    log.debug("Campos Adcionais {}",listTransacao.getDetalhes().getCamposAdicionais().toString());
                    extrato.setNomeRecebedor(listTransacao.getDetalhes().getCamposAdicionais().get("nomeRecebedor") == null ? "": listTransacao.getDetalhes().getCamposAdicionais().get("nomeRecebedor").toString());
                    extrato.setDocumenteRecebedor(listTransacao.getDetalhes().getCamposAdicionais().get("cpfCnpjRecebedor") == null ? "": listTransacao.getDetalhes().getCamposAdicionais().get("cpfCnpjRecebedor").toString());
                    extrato.setNomePagador(listTransacao.getDetalhes().getCamposAdicionais().get("nomePagador") == null ? "": listTransacao.getDetalhes().getCamposAdicionais().get("nomePagador").toString());
                    extrato.setDocumentePagador(listTransacao.getDetalhes().getCamposAdicionais().get("cpfCnpjPagador") == null ? "": listTransacao.getDetalhes().getCamposAdicionais().get("cpfCnpjPagador").toString());
                }
                    if(listTransacao.getTipoTransacao().equals("BOLETO_COBRANCA")){
                        log.debug("NOSSO NUMERO BOLETO {}", listTransacao.getDetalhes().getCamposAdicionais().get("nossoNumero"));
                        BoletoNovaAlianca boleto = boletoRepository.findByNossoNumero(listTransacao.getDetalhes().getCamposAdicionais().get("nossoNumero").toString());
                        log.debug("ID_BOLETO NOVAALIANCA {}",boleto.getId());
                        extrato.setNomePagador(boleto.getUsuario().getNomeUsuario());
                        extrato.setDocumentePagador(boleto.getUsuario().getNrDocumentoCpf());
                        extrato.setNomeRecebedor("Condominio Nova Aliança");
                        extrato.setDocumenteRecebedor(properties.getCnpjCpfBenificiario());
                        extrato.setIdBoleto(boleto.getId());
                    }
                    log.debug("Entrou para Salvar ID {}", listTransacao.getIdTransacao());

            if(listTransacao.getTipoTransacao().equals("DEBITO_AUTOMATICO")
                    || listTransacao.getTipoTransacao().equals("PIX")
                    || listTransacao.getTipoTransacao().equals("OUTROS")
                    || listTransacao.getTipoTransacao().equals("PAGAMENTO")){
                log.debug("ListTransacao ID {}", new Gson().toJson(listTransacao));
            }
            if(!extratoRepository.getbyIdTransacao(listTransacao.getIdTransacao()).isPresent()){
                extratoRepository.save(extrato);
            }
                listExtrato.add(extrato);
            });
        }
        return listExtrato;
    }
}
