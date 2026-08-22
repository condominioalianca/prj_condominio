package com.condominio.novaalianca.controller;


import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.cobranca.builder.BoletoBuilder;
import com.condominio.novaalianca.builder.RequestBoletoBuilder;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import com.condominio.novaalianca.services.EmailService;
import com.condominio.novaalianca.dto.boleto.FiltroListagemBoletoDTO;
import com.condominio.novaalianca.services.inter.InterService;
import com.condominio.novaalianca.cobranca.services.BoletoService;
import com.condominio.novaalianca.dto.inter.cobranca.Boleto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/testes")
@RequiredArgsConstructor
public class TestesAleatoriosController {

    private static final Logger log = LoggerFactory.getLogger(TestesAleatoriosController.class);

    private final RequestBoletoBuilder builder;

    private final BoletoBuilder boletoBuilder;
    private final UsuarioRepository usuarioRepository;

    private final EmailService emailService;

    private final BoletoService boletoService;

    private final InterService interService;

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
//            if (!Objects.isNull(usuario) && usuario.getIdUsuario().equals(5L)){
//                log.info("Usuarios sem Boleto Enviado {}", usuario.getNomeUsuario());
//                interService.emitirBoleto(boleto);
//            }

        }

        return ResponseEntity.ok().body( listResponse);
    }


    @GetMapping("/listaBoletos")
    public ResponseEntity<?> listBoletos(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {

        return ResponseEntity.ok().body( boletoService.listaBoletos(filtro.getDataFinal(), filtro.getDataFinal(), builder.requestBoleto("boleto-cobranca.read")));
    }

//    @GetMapping("/boletoDetalhe")
//    public ResponseEntity<?> boletoDetalhe(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {
//
//        return ResponseEntity.ok().body( boletoService.boletoDetalhado(filtro, builder.requestBoleto("boleto-cobranca.read")));
//    }
//
//    @GetMapping("/downloadPDF")
//    public ResponseEntity<?> downloadPdf(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {
//
//        return ResponseEntity.ok().body( boletoService.downloadPDF(filtro.getNossoNumero(), builder.requestBoleto("boleto-cobranca.read")));
//    }
//
//    @GetMapping("/cancelaBoleto")
//    public ResponseEntity<?> cancelaBoleto(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {
//
//        return ResponseEntity.ok().body( boletoService.cancelaBoleto(filtro, builder.requestBoleto("boleto-cobranca.write")));
//    }
//
//
    @GetMapping("/cargabanco/{dataInicio}/{datafim}")
    public ResponseEntity<?> cargaBanco(@PathVariable String dataInicio , @PathVariable String datafim ) throws Exception {

        return ResponseEntity.ok().body( boletoService.cargaBoleo(dataInicio, datafim, builder.requestBoleto("boleto-cobranca.read")));
    }
//
//    @GetMapping("/enviaEmail")
//    public ResponseEntity<?> enviaEmail(@RequestBody FiltroListagemBoletoDTO filtro) throws Exception {
//
//        EmailDTO emailDTO = new EmailDTO();
//        emailDTO.setNossoNumero(filtro.getNossoNumero());
//        BoletoDTO boletoDTO = boletoService.findByNossoNumero (filtro.getNossoNumero());
//
//        emailDTO.setNumeroUnidade(boletoDTO.getUnidade().getNumeroUnidade());
//        emailDTO.setTo(boletoDTO.getUsuario().getTxEmail());
//
//
//        return ResponseEntity.ok().body(emailService.sendMail(emailDTO));
//    }

}