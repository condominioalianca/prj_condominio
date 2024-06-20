package com.condominio.novaalianca.SchedulesTask;

import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.config.NovaAliancaProperties;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.entities.BoletoNovaAlianca;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.services.EmailService;
import com.condominio.novaalianca.services.UsuarioService;
import com.condominio.novaalianca.services.boleto.BoletoService;
import com.condominio.novaalianca.util.CaminhoArquivosUtil;
import com.condominio.novaalianca.util.DateUtils;
import inter.InterSdk;
import inter.banking.model.Saldo;
import inter.cobranca.model.Boleto;
import inter.cobranca.model.BoletoDetalhado;
import inter.cobranca.model.FiltroRecuperarBoletos;
import inter.cobranca.model.Ordenacao;
import inter.cobranca.model.enums.OrdenadoPor;
import inter.cobranca.model.enums.TipoOrdenacao;
import inter.exceptions.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class Shedules {
    private static final Logger log = LoggerFactory.getLogger(Shedules.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private NovaAliancaProperties properties;

    @Autowired
    private CaminhoArquivosUtil caminhoArquivosUtil;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private RequestBoletoBuilder requestBoletoBuilder;





    @Scheduled(cron = "50 */4 21,21 * * *")
    public void validaEnviodDeBoletos() throws SdkException, ParseException {
    LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
    LocalDate dtfim = LocalDate.now();

        Usuario usuario = usuarioService.findFirstByAtivosAndEnviaBoletoAndSemBoleto(dtInicio,dtfim);

        if (!Objects.isNull(usuario)){
            log.info("Usuarios sem Boleto Enviado {}", usuario.getNomeUsuario());
            Boleto boleto = boletoService.builderBoletoInter (usuario);
            this.interSdk().cobranca().emitirBoleto(boleto);
        }


    }


    @Scheduled(cron = "10 */2 20,21 * * *")
    public void recuperaBoletoDetalhado() throws Exception {
        boletoService.cargaBoleo(dateUtils.primeiroDiaMes(), dateUtils.ultimoDiaMes(), requestBoletoBuilder.requestBoleto("boleto-cobranca.read"));

    }

    @Scheduled(cron = "20 */2 20,21 * * *")
    public void enviaEmail() throws Exception {
        LocalDate dtInicio = LocalDate.now().withDayOfMonth(1);
        LocalDate dtfim = LocalDate.now();
        boletoService.enviaBoletosPorEmail(dtInicio, dtfim);

    }



    //@Scheduled(cron = "0 28 23 * * *")
    public void saldo() throws SdkException {
        InterSdk interSdk = new InterSdk(properties.getClientId(), properties.getClientSecret(), caminhoArquivosUtil.caminhoCertificado(), properties.getSenhaCertificado());
        interSdk.setDebug(true);
        interSdk.setContaCorrente(properties.getNumeroContaCorrente());
        Saldo saldo = interSdk.banking().consultarSaldo();

        log.info("The time is now {}", dateFormat.format(new Date()));
        System.out.println("Rodou" + saldo.toString());
        log.info("Saldo {}", saldo.toString());
    }

    private InterSdk interSdk() throws SdkException {
        InterSdk interSdk = new InterSdk(properties.getClientId(), properties.getClientSecret(), caminhoArquivosUtil.caminhoCertificado(), properties.getSenhaCertificado());
        return interSdk;
    }




}
