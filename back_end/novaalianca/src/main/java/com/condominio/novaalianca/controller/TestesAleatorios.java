package com.condominio.novaalianca.controller;


import com.condominio.novaalianca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.EmailService;
import com.inter.boletos.client.dto.FiltroListagemBoletoDTO;
import com.inter.boletos.client.dto.boleto.BoletoDTO;
import com.inter.boletos.client.dto.boleto.ResponseBoletoDTO;
import com.inter.boletos.client.dto.token.TokenResponseDTO;
import com.inter.boletos.client.service.BoletoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(value = "/testes")
public class TestesAleatorios {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestesAleatorios.class);

    @Autowired
    private RequestBoletoBuilder builder;

    @Autowired
    private BoletoBuilder boletoBuilder;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/token")
    public TokenResponseDTO testeTokenClient() {
        return BoletoService.getInstance().devolvetoken(builder.requestBoleto("boleto-cobranca.read"));
    }


    @GetMapping("/geraboleto")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> geraBoleto() throws Exception {
        List<Usuario> listUsuarios  = usuarioRepository.listUsuariosGeraBoleto();
        List<ResponseBoletoDTO> listResponse = new ArrayList<>();

        for (Usuario usuario: listUsuarios ) {
            BoletoDTO boletoDTO = boletoBuilder.carregaDadosEmissao(usuario);
            listResponse.add(BoletoService.getInstance().geraBoleto(builder.requestBoleto("boleto-cobranca.write"), boletoDTO));
        }

        return ResponseEntity.ok().body( listResponse);
    }


    @GetMapping("/listaBoletos")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> listBoletos(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( BoletoService.getInstance().listaBoletos(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/boletoDetalhe")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> boletoDetalhe(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( BoletoService.getInstance().boletoDetalhado(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/downloadPDF")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> downloadPdf(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( BoletoService.getInstance().downloadPDF(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/cancelaBoleto")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> cancelaBoleto(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( BoletoService.getInstance().cancelaBoleto(filtro, builder.requestBoleto("boleto-cobranca.write")));
    }

    @GetMapping("/enviaEmail")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> enviaEmail(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        EmailDTO emailDTO = new EmailDTO();

        byte[] decoder = Base64.getDecoder().decode(BoletoService.getInstance().downloadPDF(filtro, builder.requestBoleto("boleto-cobranca.read")).getPdf());


        DateTimeFormatter formatterReferencia = DateTimeFormatter.ofPattern("MM-yyyy");

        emailDTO.setAnexo(decoder);
        emailDTO.setNumeroUnidade("05");
        emailDTO.setTo("patrickmoura@gmail.com");
        emailDTO.setFrom("condominionovaaliancasbc@gmail.com");
        emailDTO.setSubject("Cobrança Condomínio - " + LocalDate.now().format(formatterReferencia).toString());
        emailDTO.setValorBoleto("3522");
        emailDTO.setDtVencimento("25*/05/2014");
        emailDTO.setBody("TESTE EMAIL");
        emailDTO.setContent("teste");
        emailService.sendMailWithAttachment(emailDTO);

        return ResponseEntity.ok().body( "TESTE");
    }



}
