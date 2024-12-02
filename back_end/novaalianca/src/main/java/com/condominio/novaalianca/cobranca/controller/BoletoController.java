package com.condominio.novaalianca.cobranca.controller;

import com.condominio.novaalianca.builder.PageableBuilder;
import com.condominio.novaalianca.dto.boleto.BoletoDTO;
import com.condominio.novaalianca.cobranca.services.BoletoService;
import com.condominio.novaalianca.dto.pageable.PageableResponseDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/boleto")
public class BoletoController {

    private static final String ID_UNIDADE = "idUnidade";
    private static final String DT_EMISSAO = "dtEmissao";

    @Autowired
    private BoletoService boletoService;

    @GetMapping
    public ResponseEntity<PageableResponseDTO<BoletoDTO>> findAll(@RequestParam(required = false) Integer pageNumber,
                                                                  @RequestParam(required = false) Integer pageSize,
                                                                  @RequestParam(required = false) String orderBy,
                                                                  @RequestParam(required = false) String order){


        final Pageable pageable = PageableBuilder.from(pageNumber, pageSize, orderBy, order);
        final PageableResponseDTO<BoletoDTO> responseDTO = boletoService.findAllPaged(pageable);

        PageableResponseDTO<BoletoDTO> list = boletoService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<PageableResponseDTO<BoletoDTO>> findAllbyId(@RequestParam(required = false) Integer pageNumber,
                                                                      @RequestParam(required = false) Integer pageSize,
                                                                      @RequestParam(required = false) String orderBy,
                                                                      @RequestParam(required = false) String order, @PathVariable Long idUsuario){

        final Pageable pageable = !Strings.isEmpty(order) ? PageableBuilder.from(pageNumber, pageSize, orderBy, order) : PageableBuilder.fromMultipleColumns(pageNumber, pageSize,
                Sort.Order.desc(DT_EMISSAO),Sort.Order.asc(ID_UNIDADE));
        final PageableResponseDTO<BoletoDTO> responseDTO = boletoService.findAllPagedByIdUsuario(pageable,idUsuario);
        return ResponseEntity.ok().body(responseDTO);
    }

   // @PostMapping("/emitir/{id}")
    
}
