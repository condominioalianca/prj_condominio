package com.condominio.novaalianca.SchedulesTask;

import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.banking.services.ExtratoService;
import com.condominio.novaalianca.banking.services.SaldoService;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.inter.InterService;
import com.condominio.novaalianca.services.UsuarioService;
import com.condominio.novaalianca.cobranca.services.BoletoService;
import com.condominio.novaalianca.util.DateUtils;
import com.condominio.novaalianca.dto.inter.cobranca.Boleto;
import com.condominio.novaalianca.dto.inter.cobranca.EmissaoBoletoResponseDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.cobranca.repositories.BoletoRepository;
import com.condominio.novaalianca.cobranca.builder.BoletoBuilder;
import com.condominio.novaalianca.enums.OrigemBoleto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Shedules {
    private static final Logger log = LoggerFactory.getLogger(Shedules.class);

    private final UsuarioService usuarioService;

    private final BoletoService boletoService;

    private final DateUtils dateUtils;

    private final RequestBoletoBuilder requestBoletoBuilder;

    private final InterService interService;

    private final ExtratoService extratoService;

    private final SaldoService saldoService;

    private final BoletoRepository boletoRepository;

    private final BoletoBuilder boletoBuilder;
    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "${cron.schedule.valida-envio:0 */4 10 1 * *}")
    public void validaEnviodDeBoletos() throws ParseException {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate dtfim = LocalDate.now();
//        Usuario usuario = usuarioService.findByIDEntity(5L);
        Usuario usuario = usuarioService.findFirstByAtivosAndEnviaBoletoAndSemBoleto(dtInicio, dtfim);

        if (!Objects.isNull(usuario)) {
            log.info("Boleto sendo Emitido para{}", usuario.getNomeUsuario());
            Boleto boleto = boletoService.builderBoletoInter(usuario);
            // Emite o boleto e grava na base de dados local utilizando o builder
            EmissaoBoletoResponseDTO response = interService.emitirBoleto(boleto, null, "PRODUCAO");
            if (response != null && response.getCodigoSolicitacao() != null) {
                BoletoNovaAlianca boletoNovaAlianca = boletoBuilder.newBoletoCargaV2(boleto, response, OrigemBoleto.SCHEDULE);
                boletoRepository.save(boletoNovaAlianca);
                log.info("Boleto emitido e salvo com sucesso na base de dados para: {}", usuario.getNomeUsuario());
            } else {
                log.error("Erro ao emitir boleto para {}. Resposta do Banco Inter inválida.", usuario.getNomeUsuario());
            }
        }
    }

    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "${cron.schedule.recupera-boleto:0 */1 10 1 * *}")
    public void recuperaBoletoDetalhado() throws Exception {
        LocalDate dataCorte = LocalDate.now().minusDays(120);
        List<BoletoNovaAlianca> boletosPendentes = boletoRepository.findBoletosParaEnriquecer(dataCorte);

        if (boletosPendentes != null && !boletosPendentes.isEmpty()) {
            log.info("Iniciando lote de enriquecimento de {} boletos emitidos nos últimos 60 dias.", boletosPendentes.size());
            
            DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            int chamadasNoMinuto = 0;
            long tempoInicioMinuto = System.currentTimeMillis();
            
            for (BoletoNovaAlianca boleto : boletosPendentes) {
                try {
                    // Cada enriquecimento faz 2 requisições à API do Inter (detalhe + PDF).
                    // Se as próximas 2 requisições excederem o limite de 5 por minuto, aguarda o reinício do minuto.
                    if (chamadasNoMinuto + 2 > 5) {
                        long tempoDecorrido = System.currentTimeMillis() - tempoInicioMinuto;
                        long tempoRestante = 60000 - tempoDecorrido;
                        if (tempoRestante > 0) {
                            log.info("Atingido o limite de chamadas permitidas por minuto (5). Aguardando {} ms para reiniciar o limite.", tempoRestante);
                            Thread.sleep(tempoRestante);
                        }
                        chamadasNoMinuto = 0;
                        tempoInicioMinuto = System.currentTimeMillis();
                    }

                    String username = boleto.getUsuario() != null ? boleto.getUsuario().getNomeUsuario() : "N/A";
                    String formattedDate = boleto.getDtEmissao() != null ? boleto.getDtEmissao().format(logFormatter) : "N/A";
                    
                    log.info("Enriquecendo boleto do usuário: {}, Data de Emissão: {}, ID: {} com Código de Solicitação: {}", 
                            username, formattedDate, boleto.getId(), boleto.getCodSolicitacao());
                    
                    boletoService.enriquecerBoleto(boleto.getCodSolicitacao(), "PRODUCAO");
                    
                    chamadasNoMinuto += 2;
                    
                    // Pequeno delay de segurança de 1 segundo entre boletos
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    log.error("Loop de enriquecimento interrompido: {}", ie.getMessage());
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Erro ao enriquecer o boleto ID: {} - {}", boleto.getId(), e.getMessage(), e);
                }
            }
        } else {
            log.info("Nenhum boleto emitido nos últimos 60 dias pendente de enriquecimento foi encontrado.");
        }
    }

//    CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
@Scheduled(cron = "${cron.schedule.envia-email:10 */2 10 1 * *}")
    public void enviaEmail() throws Exception {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate dtfim = LocalDate.now();
        boletoService.enviaBoletosPorEmail(dtInicio, dtfim);

    }

    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "${cron.schedule.extrato:0 0 */1 * * *}")
    public void extrato() {
        extratoService.getExtratoEnriquecido(
                dateUtils.localDateToStringYYYYMMDD(LocalDate.now().minusDays(80L)),
                dateUtils.localDateToStringYYYYMMDD(LocalDate.now()),
                "PRODUCAO");
        try {
            saldoService.atualizarSaldo("PRODUCAO");
        } catch (Exception e) {
            log.error("Erro ao rodar schedule de atualizacao de saldo: {}", e.getMessage(), e);
        }

    }

}