import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { ICobrancaExtra, IUnidade, ISprungPage } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner } from 'react-icons/fa';

const CobrancaExtra: React.FC = () => {
  const [cobrancas, setCobrancas] = useState<ICobrancaExtra[]>([]);
  const [unidades, setUnidades] = useState<IUnidade[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingCobranca, setEditingCobranca] = useState<ICobrancaExtra | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  // Campos de Formulário
  const [valorCobranca, setValorCobranca] = useState<number>(0);
  const [mesReferencia, setMesReferencia] = useState<number>(1);
  const [descricao, setDescricao] = useState<string>('');
  const [selectedUnidadeId, setSelectedUnidadeId] = useState<number>(-1);

  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      const [resCobrancas, resUnidades] = await Promise.all([
        backEndService.get<ICobrancaExtra[]>('/cobrancas-extras'),
        backEndService.get<ISprungPage<IUnidade>>('/unidade?size=1000'),
      ]);
      setCobrancas(resCobrancas);
      setUnidades(resUnidades.content);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = (): void => {
    setEditingCobranca(null);
    setValorCobranca(0);
    setMesReferencia(new Date().getMonth() + 1);
    setDescricao('');
    setSelectedUnidadeId(-1);
    setModalOpen(true);
  };

  const openEditModal = (cobranca: ICobrancaExtra): void => {
    setEditingCobranca(cobranca);
    setValorCobranca(cobranca.valorCobranca);
    setMesReferencia(cobranca.mesReferencia);
    setDescricao(cobranca.descricao);
    setSelectedUnidadeId(cobranca.idUnidade);
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    if (selectedUnidadeId === -1) {
      alert('Por favor, selecione uma unidade.');
      return;
    }
    setSubmitting(true);
    try {
      const payload: ICobrancaExtra = {
        idCobrancaExtra: editingCobranca ? editingCobranca.idCobrancaExtra : null,
        valorCobranca,
        dtInclusao: editingCobranca ? editingCobranca.dtInclusao : new Date().toISOString().split('T')[0],
        mesReferencia,
        descricao,
        idUnidade: selectedUnidadeId,
      };

      if (editingCobranca) {
        await backEndService.put('/cobrancas-extras/update', payload);
      } else {
        await backEndService.post('/cobrancas-extras/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar cobrança extra.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir esta cobrança extra?')) {
      try {
        await backEndService.delete(`/cobrancas-extras/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir cobrança extra.');
      }
    }
  };

  // Encontra o número da unidade correspondente ao ID
  const getNumeroUnidade = (idUnidade: number): string => {
    const u = unidades.find((item) => item.idUnidade === idUnidade);
    return u ? `Unidade ${u.numeroUnidade}` : `Unidade ID ${idUnidade}`;
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Cobranças Extras</h2>
          <p className="text-muted small">Crie cobranças avulsas associadas às unidades de moradores.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Nova Cobrança Extra</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando cobranças...</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Unidade</th>
                    <th>Mês de Referência</th>
                    <th>Descrição</th>
                    <th>Valor</th>
                    <th>Inclusão</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {cobrancas.length > 0 ? (
                    cobrancas.map((cob) => (
                      <tr key={cob.idCobrancaExtra}>
                        <td>{cob.idCobrancaExtra}</td>
                        <td className="fw-semibold">{getNumeroUnidade(cob.idUnidade)}</td>
                        <td>Mês {cob.mesReferencia}</td>
                        <td>{cob.descricao}</td>
                        <td className="fw-bold text-danger">
                          R$ {cob.valorCobranca.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                        </td>
                        <td>
                          {cob.dtInclusao 
                            ? new Date(cob.dtInclusao).toLocaleDateString('pt-BR') 
                            : 'N/A'}
                        </td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(cob)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => cob.idCobrancaExtra && handleDelete(cob.idCobrancaExtra)}
                            title="Excluir"
                          >
                            <FaTrash />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={7} className="text-center py-4 text-muted">
                        Nenhuma cobrança extra lançada.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {modalOpen && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex={-1}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSave}>
                <div className="modal-header modal-header-custom justify-content-between">
                  <h5 className="modal-title">{editingCobranca ? 'Editar Cobrança' : 'Nova Cobrança Extra'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Unidade Associada</label>
                    <select 
                      className="form-select form-control-custom"
                      value={selectedUnidadeId}
                      onChange={(e) => setSelectedUnidadeId(Number(e.target.value))}
                      required
                    >
                      <option value={-1}>Selecione uma unidade...</option>
                      {unidades.map((u) => (
                        <option key={u.idUnidade} value={u.idUnidade}>
                          Unidade {u.numeroUnidade}
                        </option>
                      ))}
                    </select>
                  </div>
                  
                  <div className="row g-2 mb-3">
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Valor da Cobrança (R$)</label>
                      <input 
                        type="number" 
                        step="0.01"
                        className="form-control form-control-custom" 
                        value={valorCobranca} 
                        onChange={(e) => setValorCobranca(Number(e.target.value))} 
                        required 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Mês de Referência</label>
                      <select 
                        className="form-select form-control-custom"
                        value={mesReferencia}
                        onChange={(e) => setMesReferencia(Number(e.target.value))}
                      >
                        {Array.from({ length: 12 }, (_, i) => (
                          <option key={i + 1} value={i + 1}>
                            Mês {i + 1}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label form-label-custom">Descrição da Cobrança</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      placeholder="Ex: Conserto do Portão, Manutenção Piscina"
                      value={descricao} 
                      onChange={(e) => setDescricao(e.target.value)} 
                      required 
                    />
                  </div>
                </div>
                <div className="modal-footer modal-footer-custom justify-content-end gap-2">
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setModalOpen(false)}>Cancelar</button>
                  <button type="submit" className="btn btn-primary btn-primary-custom" disabled={submitting}>
                    {submitting ? 'Salvando...' : 'Salvar'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CobrancaExtra;
