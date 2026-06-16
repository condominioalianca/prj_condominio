import React from 'react';
import { FaExchangeAlt, FaCheckCircle, FaExclamationTriangle, FaListAlt } from 'react-icons/fa';

const Conciliacao: React.FC = () => {
  return (
    <div>
      <div className="mb-4">
        <h2 className="mb-0">Conciliação Bancária</h2>
        <p className="text-muted small">Alinhamento de lançamentos do extrato com boletos emitidos.</p>
      </div>

      <div className="card-content p-5 text-center shadow-sm">
        <div className="d-inline-flex align-items-center justify-content-center bg-primary-light text-primary rounded-circle mb-4 p-3" style={{ fontSize: '3rem', backgroundColor: 'rgba(60, 80, 224, 0.1)' }}>
          <FaExchangeAlt />
        </div>
        <h3 className="mb-3">Módulo de Conciliação em Breve</h3>
        <p className="text-muted mx-auto mb-5" style={{ maxWidth: '600px' }}>
          Este módulo está sendo preparado. Ele fará a conciliação automática das movimentações financeiras da conta do Banco Inter com os boletos gerados e recebidos no banco de dados local.
        </p>

        <div className="row g-4 justify-content-center text-start" style={{ maxWidth: '800px', margin: '0 auto' }}>
          <div className="col-md-4">
            <div className="p-3 border rounded h-100">
              <h5 className="d-flex align-items-center text-success gap-2 mb-3">
                <FaCheckCircle />
                <span>Casamento Automático</span>
              </h5>
              <p className="text-muted small mb-0">Cruza valores recebidos no extrato via ID de boleto ou PIX com a base local para dar baixa automática.</p>
            </div>
          </div>
          <div className="col-md-4">
            <div className="p-3 border rounded h-100">
              <h5 className="d-flex align-items-center text-warning gap-2 mb-3">
                <FaExclamationTriangle />
                <span>Tratamento de Divergências</span>
              </h5>
              <p className="text-muted small mb-0">Alerta os administradores sobre valores pagos divergentes ou datas inválidas de pagamento.</p>
            </div>
          </div>
          <div className="col-md-4">
            <div className="p-3 border rounded h-100">
              <h5 className="d-flex align-items-center text-primary gap-2 mb-3">
                <FaListAlt />
                <span>Relatório Consolidado</span>
              </h5>
              <p className="text-muted small mb-0">Relatórios mensais de conciliação para prestação de contas aos condôminos.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Conciliacao;
