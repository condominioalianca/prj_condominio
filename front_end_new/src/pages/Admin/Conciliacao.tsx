import React, { useEffect, useState } from 'react';
import { FaArrowRight, FaSpinner, FaExchangeAlt } from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import { getConciliacoes } from '../../services/conciliacaoService';
import type { ConciliacaoResponseDTO } from '../../types/conciliacao';

const Conciliacao: React.FC = () => {
  const [conciliacoes, setConciliacoes] = useState<ConciliacaoResponseDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    carregarConciliacoes();
  }, []);

  const carregarConciliacoes = async (): Promise<void> => {
    try {
      setLoading(true);
      const data = await getConciliacoes();
      setConciliacoes(data);
    } catch (error) {
      console.error('Erro ao carregar conciliações', error);
      // Aqui pode entrar um toast de erro
    } finally {
      setLoading(false);
    }
  };

  const statusBadge = (status: string) => {
    switch (status) {
      case 'BATIDO':
        return <span className="badge bg-success">Batido</span>;
      case 'PENDENTE':
        return <span className="badge bg-warning text-dark">Pendente</span>;
      default:
        return <span className="badge bg-secondary">{status}</span>;
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0 d-flex align-items-center gap-2">
            <FaExchangeAlt className="text-primary" />
            Conciliação Bancária
          </h2>
          <p className="text-muted small mb-0">Alinhamento de lançamentos do extrato com registros locais.</p>
        </div>
      </div>

      <div className="card shadow-sm border-0">
        <div className="card-body p-0">
          {loading ? (
            <div className="p-5 text-center">
              <FaSpinner className="fa-spin text-primary fs-3" />
              <p className="mt-2 text-muted">Carregando conciliações...</p>
            </div>
          ) : conciliacoes.length === 0 ? (
            <div className="p-5 text-center text-muted">
              Nenhuma conciliação encontrada.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Descrição</th>
                    <th className="text-center">Total Registros</th>
                    <th className="text-center">Qtd. Divergente</th>
                    <th className="text-center">Qtd. Batido</th>
                    <th className="text-center">Status</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {conciliacoes.map((c) => (
                    <tr key={c.id}>
                      <td>
                        <strong>{c.descricao}</strong>
                      </td>
                      <td className="text-center fw-bold">{c.qtdTotal}</td>
                      <td className="text-center text-danger fw-bold">{c.qtdDivergente}</td>
                      <td className="text-center text-success fw-bold">{c.qtdBatido}</td>
                      <td className="text-center">{statusBadge(c.status)}</td>
                      <td className="text-end">
                        <button
                          className="btn btn-sm btn-outline-primary rounded-circle"
                          onClick={() => navigate(`/admin/conciliacao/${c.id}`)}
                          title="Ver Extratos"
                        >
                          <FaArrowRight />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Conciliacao;
