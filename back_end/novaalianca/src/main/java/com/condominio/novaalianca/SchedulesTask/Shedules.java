package com.condominio.novaalianca.SchedulesTask;

import com.condominio.novaalianca.banking.services.ExtratoService;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.InterSDKService;
import com.condominio.novaalianca.services.UsuarioService;
import com.condominio.novaalianca.cobranca.services.BoletoService;
import com.condominio.novaalianca.util.DateUtils;
import inter.cobranca.model.Boleto;
import inter.exceptions.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class Shedules {
    private static final Logger log = LoggerFactory.getLogger(Shedules.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private RequestBoletoBuilder requestBoletoBuilder;

    @Autowired
    private InterSDKService interSDKService;

    @Autowired
    private ExtratoService extratoService;
    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "0 */6 19 09 * *")
    public void validaEnviodDeBoletos() throws SdkException, ParseException {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate dtfim = LocalDate.now();

        Usuario usuario = usuarioService.findFirstByAtivosAndEnviaBoletoAndSemBoleto(dtInicio, dtfim);

        if (!Objects.isNull(usuario)) {
            log.info("Boleto sendo Emitido para{}", usuario.getNomeUsuario());
            Boleto boleto = boletoService.builderBoletoInter(usuario);
            interSDKService.emitirBoleto(boleto);

        }
    }

    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "10 */4 19 09 * *")
    public void recuperaBoletoDetalhado() throws Exception {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1).minusMonths(2);
        boletoService.cargaBoleo(dtInicio.toString(), dateUtils.ultimoDiaMes(), requestBoletoBuilder.requestBoleto("boleto-cobranca.read"));

    }

//    CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "10 */2 19 09 * *")
    public void enviaEmail() throws Exception {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate dtfim = LocalDate.now();
        boletoService.enviaBoletosPorEmail(dtInicio, dtfim);

    }

    //CRON = (SEGUNDO MINUTO HORA DIA MES DIAS_DA_SEMANA
    @Scheduled(cron = "0 */2 * * * *")
    public void extrato() throws SdkException {
        extratoService.getExtratoEnriquecido(dateUtils.localDateToStringYYYYMMDD(LocalDate.now().minusDays(80L)),dateUtils.localDateToStringYYYYMMDD(LocalDate.now()));

    }

}
