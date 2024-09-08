package com.condominio.novaalianca.controller;


import com.condominio.novaalianca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.dto.EmailDTO;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.EmailService;
import com.condominio.novaalianca.dto.boleto.FiltroListagemBoletoDTO;
import com.condominio.novaalianca.services.InterSDKService;
import com.condominio.novaalianca.services.boleto.BoletoService;
import inter.cobranca.model.Boleto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/testes")
public class TestesAleatoriosController {

    private static final Logger log = LoggerFactory.getLogger(TestesAleatoriosController.class);

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

    @Autowired
    private InterSDKService interSDKService;

    @GetMapping("/geraboleto")
 //   @Scheduled(cron="1 22 13 * * *")    //Segundo Minuto Hora dia-do-mes mes  dia-da-semana
    public ResponseEntity<?> geraBoleto() throws Exception {
        List<Usuario> listUsuarios  = usuarioRepository.listUsuariosGeraBoleto();
        List<Boleto> listResponse = new ArrayList<>();

        for (Usuario usuario: listUsuarios ) {
            Boleto boleto = boletoBuilder.boletoInter(usuario);
//            ResponseBoletoDTO responseBoletoDTO = boletoService.geraBoleto(builder.requestBoleto("boleto-cobranca.write"), boletoDTO);
            listResponse.add(boleto);
//            EmailDTO emailDTO = new EmailDTO();
//            emailDTO.setNumeroUnidade(usuario.getUnidade().getNumeroUnidade());
//            emailDTO.setTo(usuario.getTxEmail());
//            emailDTO.setNossoNumero(responseBoletoDTO.getNossoNumero());
//            emailService.sendMail(emailDTO);
            if (!Objects.isNull(usuario) && usuario.getIdUsuario().equals(5L)){
                log.info("Usuarios sem Boleto Enviado {}", usuario.getNomeUsuario());
                interSDKService.emitirBoleto(boleto);
            }

        }

        return ResponseEntity.ok().body( listResponse);
    }


    @GetMapping("/listaBoletos")
    public ResponseEntity<?> listBoletos(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.listaBoletos(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/boletoDetalhe")
    public ResponseEntity<?> boletoDetalhe(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.boletoDetalhado(filtro, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/downloadPDF")
    public ResponseEntity<?> downloadPdf(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.downloadPDF(filtro.getNossoNumero(), builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/cancelaBoleto")
    public ResponseEntity<?> cancelaBoleto(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.cancelaBoleto(filtro, builder.requestBoleto("boleto-cobranca.write")));
    }


    @GetMapping("/cargabanco/{dataInicio}/{datafim}")
    public ResponseEntity<?> cargaBanco(@PathVariable String dataInicio , @PathVariable String datafim ) throws Exception {

        return ResponseEntity.ok().body( boletoService.cargaBoleo(dataInicio, datafim, builder.requestBoleto("boleto-cobranca.read")));
    }

    @GetMapping("/enviaEmail")
    public ResponseEntity<?> enviaEmail(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setNossoNumero(filtro.getNossoNumero());
        BoletoDTO boletoDTO = boletoService.findByNossoNumero (filtro.getNossoNumero());

        emailDTO.setNumeroUnidade(boletoDTO.getUnidade().getNumeroUnidade());
        emailDTO.setTo(boletoDTO.getUsuario().getTxEmail());


        return ResponseEntity.ok().body(emailService.sendMail(emailDTO));
    }

}
