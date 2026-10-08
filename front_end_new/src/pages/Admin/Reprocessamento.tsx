import React, { useState } from 'react';
import { backEndService } from '../../services/api';
import { FaFileInvoice, FaSearchDollar, FaPaperPlane, FaSpinner, FaCheckCircle, FaExclamationTriangle } from 'react-icons/fa';

const Reprocessamento: React.FC = () => {
  const [loadingGerar, setLoadingGerar] = useState<boolean>(false);
  const [loadingRecuperar, setLoadingRecuperar] = useState<boolean>(false);
  const [loadingEnviar, setLoadingEnviar] = useState<boolean>(false);

  const [message, setMessage] = useState<{ type: 'success' | 'danger'; text: string } | null>(null);

  const handleGerarBoleto = async (): Promise<void> => {
    try {
      setLoadingGerar(true);
      setMessage(null);
      const res = await backEndService.post<string>('/reprocessamento/gerar-boleto');
      setMessage({ type: 'success', text: res || 'Processo de geração de boleto executado com sucesso!' });
    } catch (err: any) {
      console.error(err);
      setMessage({ 
        type: 'danger', 
        text: err.response?.data || 'Erro ao executar a geração de boleto.' 
      });
    } finally {
      setLoadingGerar(false);
    }
  };

  const handleRecuperarBoleto = async (): Promise<void> => {
    try {
      setLoadingRecuperar(true);
      setMessage(null);
      const res = await backEndService.post<string>('/reprocessamento/recupera-boleto-detalhado');
      setMessage({ type: 'success', text: res || 'Processo de recuperação de boleto detalhado executado com sucesso!' });
    } catch (err: any) {
      console.error(err);
      setMessage({ 
        type: 'danger', 
        text: err.response?.data || 'Erro ao executar a recuperação detalhada de boletos.' 
      });
    } finally {
      setLoadingRecuperar(false);
    }
  };

  const handleEnviarBoleto = async (): Promise<void> => {
    try {
      setLoadingEnviar(true);
      setMessage(null);
      const res = await backEndService.post<string>('/reprocessamento/envia-boleto');
      setMessage({ type: 'success', text: res || 'Processo de envio de boletos executado com sucesso!' });
    } catch (err: any) {
      console.error(err);
      setMessage({ 
        type: 'danger', 
        text: err.response?.data || 'Erro ao executar o envio de e-mails de boletos.' 
      });
    } finally {
      setLoadingEnviar(false);
    }
  };

  return (
    <div className="container-fluid">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="h4 mb-1">Reprocessamento de Boletos</h2>
          <p className="text-muted small mb-0">
            Disparo manual e forçado das rotinas agendadas de emissão, consulta e envio de boletos.
          </p>
        </div>
      </div>

      {message && (
        <div className={`alert alert-${message.type} alert-dismissible fade show d-flex align-items-center gap-2 mb-4`} role="alert">
          {message.type === 'success' ? <FaCheckCircle /> : <FaExclamationTriangle />}
          <div>{message.text}</div>
          <button type="button" className="btn-close" onClick={() => setMessage(null)} aria-label="Close"></button>
        </div>
      )}

      <div className="row g-4">
        {/* Card 1: Gerar Boleto */}
        <div className="col-md-4">
          <div className="card h-100 border-0 shadow-sm">
            <div className="card-body p-4 d-flex flex-column justify-content-between">
              <div>
                <div className="d-flex align-items-center gap-3 mb-3">
                  <div className="p-3 bg-primary bg-opacity-10 text-primary rounded-3">
                    <FaFileInvoice size={24} />
                  </div>
                  <h5 className="card-title mb-0">Gerar Boleto</h5>
                </div>
                <p className="card-text text-muted small">
                  Força a execução imediata da rotina <code>validaEnviodDeBoletos()</code>. Identifica a próxima unidade ativa sem cobrança no mês e emite o boleto no Banco Inter.
                </p>
              </div>
              <button
                type="button"
                className="btn btn-primary-custom w-100 d-flex align-items-center justify-content-center gap-2 mt-3"
                onClick={handleGerarBoleto}
                disabled={loadingGerar || loadingRecuperar || loadingEnviar}
              >
                {loadingGerar ? (
                  <>
                    <FaSpinner className="spinner-border spinner-border-sm me-1" />
                    <span>Gerando...</span>
                  </>
                ) : (
                  <>
                    <FaFileInvoice />
                    <span>Gerar Boleto</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Card 2: Recupera Boleto Detalhado */}
        <div className="col-md-4">
          <div className="card h-100 border-0 shadow-sm">
            <div className="card-body p-4 d-flex flex-column justify-content-between">
              <div>
                <div className="d-flex align-items-center gap-3 mb-3">
                  <div className="p-3 bg-info bg-opacity-10 text-info rounded-3">
                    <FaSearchDollar size={24} />
                  </div>
                  <h5 className="card-title mb-0">Recupera Boleto Detalhado</h5>
                </div>
                <p className="card-text text-muted small">
                  Força a execução imediata da rotina <code>recuperaBoletoDetalhado()</code>. Consulta na API do Banco Inter o status e baixa os arquivos PDF pendentes dos boletos.
                </p>
              </div>
              <button
                type="button"
                className="btn btn-info text-white w-100 d-flex align-items-center justify-content-center gap-2 mt-3"
                onClick={handleRecuperarBoleto}
                disabled={loadingGerar || loadingRecuperar || loadingEnviar}
              >
                {loadingRecuperar ? (
                  <>
                    <FaSpinner className="spinner-border spinner-border-sm me-1" />
                    <span>Recuperando...</span>
                  </>
                ) : (
                  <>
                    <FaSearchDollar />
                    <span>Recuperar Boleto Detalhado</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>

        {/* Card 3: Envia Boleto */}
        <div className="col-md-4">
          <div className="card h-100 border-0 shadow-sm">
            <div className="card-body p-4 d-flex flex-column justify-content-between">
              <div>
                <div className="d-flex align-items-center gap-3 mb-3">
                  <div className="p-3 bg-success bg-opacity-10 text-success rounded-3">
                    <FaPaperPlane size={24} />
                  </div>
                  <h5 className="card-title mb-0">Envia Boleto</h5>
                </div>
                <p className="card-text text-muted small">
                  Força a execução imediata da rotina <code>enviaEmail()</code>. Mescla o boleto com o demonstrativo de taxas e a conciliação do mês anterior (se BATIDO) e dispara por e-mail.
                </p>
              </div>
              <button
                type="button"
                className="btn btn-success text-white w-100 d-flex align-items-center justify-content-center gap-2 mt-3"
                onClick={handleEnviarBoleto}
                disabled={loadingGerar || loadingRecuperar || loadingEnviar}
              >
                {loadingEnviar ? (
                  <>
                    <FaSpinner className="spinner-border spinner-border-sm me-1" />
                    <span>Enviando...</span>
                  </>
                ) : (
                  <>
                    <FaPaperPlane />
                    <span>Enviar Boleto</span>
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Reprocessamento;
