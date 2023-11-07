package com.condominio.novaalianca.controller;


import com.condominio.novaalianca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoEmissaoDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.EmailService;
import com.condominio.novaalianca.dto.boleto.FiltroListagemBoletoDTO;
import com.condominio.novaalianca.dto.boleto.BoletoTESTEOLDDTO;
import com.condominio.novaalianca.dto.boleto.ResponseBoletoDTO;
import com.condominio.novaalianca.services.boleto.BoletoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private BoletoService boletoService;

//    @GetMapping("/token")
//    public TokenResponseDTO testeTokenClient() {
//        return BoletoService.getInstance().devolvetoken(builder.requestBoleto("boleto-cobranca.read"));
//    }


    @GetMapping("/geraboleto")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> geraBoleto() throws Exception {
        List<Usuario> listUsuarios  = usuarioRepository.listUsuariosGeraBoleto();
        List<ResponseBoletoDTO> listResponse = new ArrayList<>();

        for (Usuario usuario: listUsuarios ) {
            BoletoEmissaoDTO boletoDTO = boletoBuilder.carregaDadosEmissao(usuario);
            ResponseBoletoDTO responseBoletoDTO = boletoService.geraBoleto(builder.requestBoleto("boleto-cobranca.write"), boletoDTO);
            listResponse.add(responseBoletoDTO);
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setNumeroUnidade(usuario.getUnidade().getNumeroUnidade());
            emailDTO.setTo(usuario.getTxEmail());
            emailDTO.setNossoNumero(responseBoletoDTO.getNossoNumero());
            emailService.sendMail(emailDTO);

        }

        return ResponseEntity.ok().body( listResponse);
    }


    @GetMapping("/listaBoletos")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> listBoletos(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.listaBoletos(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/boletoDetalhe")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> boletoDetalhe(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.boletoDetalhado(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/downloadPDF")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> downloadPdf(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.downloadPDF(filtro.getNossoNumero(), builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/cancelaBoleto")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> cancelaBoleto(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.cancelaBoleto(filtro, builder.requestBoleto("boleto-cobranca.write")));
    }


    @GetMapping("/cargabanco/{dataInicio}/{datafim}")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> cargaBanco(@PathVariable String dataInicio , @PathVariable String datafim ) throws Exception {

        return ResponseEntity.ok().body( boletoService.cargaBoleo(dataInicio, datafim, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/enviaEmail")
    // @Scheduled(cron="* */2 * * * *")
    public ResponseEntity<?> enviaEmail(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setNossoNumero(filtro.getNossoNumero());
        BoletoDTO boletoDTO = boletoService.findByNossoNumero (filtro.getNossoNumero());

        emailDTO.setNumeroUnidade(boletoDTO.getUnidade().getNumeroUnidade());
        emailDTO.setTo(boletoDTO.getUsuario().getTxEmail());


        return ResponseEntity.ok().body(emailService.sendMail(emailDTO));
    }



}
