package com.condominio.novaalianca.cobranca.services;

import com.condominio.novaalianca.banking.models.entities.Conciliacao;
import com.condominio.novaalianca.banking.models.enums.StatusConciliacao;
import com.condominio.novaalianca.banking.repositories.ConciliacaoRepository;
import com.condominio.novaalianca.banking.services.RelatorioConciliacaoService;
import com.condominio.novaalianca.entities.Unidade;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.enums.ParametrosSistema;
import com.condominio.novaalianca.repositories.ParametrosSistemaRepository;
import com.condominio.novaalianca.services.CobrancaExtraService;
import com.condominio.novaalianca.services.exceptions.ConciliacaoPendenteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoletoPdfComposerServiceTest {

    @Mock
    private ConciliacaoRepository conciliacaoRepository;

    @Mock
    private RelatorioConciliacaoService relatorioConciliacaoService;

    @Mock
    private ParametrosSistemaRepository parametrosSistemaRepository;

    @Mock
    private CobrancaExtraService cobrancaExtraService;

    @InjectMocks
    private BoletoPdfComposerService composerService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        Unidade unidade = new Unidade();
        unidade.setNumeroUnidade("101");
        unidade.setQtMorador(2L);

        usuarioMock = new Usuario();
        usuarioMock.setNomeUsuario("João Silva");
        usuarioMock.setUnidade(unidade);
    }

    @Test
    void deveLancarExcecaoQuandoConciliacaoMesAnteriorNaoEstiverBatida() {
        LocalDate dataRef = LocalDate.of(2026, 10, 1);
        LocalDate dataMesAnterior = LocalDate.of(2026, 9, 1);

        Conciliacao conciliacaoPendente = new Conciliacao();
        conciliacaoPendente.setStatus(StatusConciliacao.PENDENTE);

        when(conciliacaoRepository.findByDataReferencia(dataMesAnterior))
                .thenReturn(Optional.of(conciliacaoPendente));

        assertThrows(ConciliacaoPendenteException.class, () ->
                composerService.comporPdfCompleto(usuarioMock, null, new byte[]{1, 2, 3}, dataRef)
        );
    }

    @Test
    void deveGerarDescritivoTaxasPdfComSucesso() {
        LocalDate dataRef = LocalDate.of(2026, 10, 1);

        when(parametrosSistemaRepository.findValorParametro(anyString())).thenReturn("250.00");
        when(cobrancaExtraService.getCobrancasExtrasParaUnidadeEMes(any(), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.emptyList());

        byte[] result = composerService.gerarDescritivoTaxasPdf(usuarioMock, dataRef);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
