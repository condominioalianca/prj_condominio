import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { ICobrancaExtra, IUnidade, ISprungPage } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner, FaSyncAlt, FaBuilding, FaUser } from 'react-icons/fa';

const CobrancaExtra: React.FC = () => {
  const [cobrancas, setCobrancas] = useState<ICobrancaExtra[]>([]);
  const [unidades, setUnidades] = useState<IUnidade[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingCobranca, setEditingCobranca] = useState<ICobrancaExtra | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  // Campos de Formulário
  const [tipoOperacao, setTipoOperacao] = useState<'ACRESCIMO' | 'DESCONTO'>('ACRESCIMO');
  const [tipoAbrangencia, setTipoAbrangencia] = useState<'GERAL' | 'UNIDADE'>('UNIDADE');
  const [recorrente, setRecorrente] = useState<boolean>(false);
  const [valorCobranca, setValorCobranca] = useState<number>(0);
  const [mesReferencia, setMesReferencia] = useState<number>(1);
  const [anoReferencia, setAnoReferencia] = useState<number>(new Date().getFullYear());
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
    setTipoOperacao('ACRESCIMO');
    setTipoAbrangencia('UNIDADE');
    setRecorrente(false);
    setValorCobranca(0);
    setMesReferencia(new Date().getMonth() + 1);
    setAnoReferencia(new Date().getFullYear());
    setDescricao('');
    setSelectedUnidadeId(-1);
    setModalOpen(true);
  };

  const openEditModal = (cobranca: ICobrancaExtra): void => {
    setEditingCobranca(cobranca);
    setTipoOperacao(cobranca.tipoOperacao || 'ACRESCIMO');
    setTipoAbrangencia(cobranca.tipoAbrangencia || (cobranca.idUnidade ? 'UNIDADE' : 'GERAL'));
    setRecorrente(cobranca.recorrente ?? false);
    setValorCobranca(cobranca.valorCobranca);
    setMesReferencia(cobranca.mesReferencia);
    setAnoReferencia(cobranca.anoReferencia || new Date().getFullYear());
    setDescricao(cobranca.descricao);
    setSelectedUnidadeId(cobranca.idUnidade ?? -1);
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    if (tipoAbrangencia === 'UNIDADE' && selectedUnidadeId === -1) {
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
        anoReferencia,
        descricao,
        idUnidade: tipoAbrangencia === 'GERAL' ? null : selectedUnidadeId,
        tipoOperacao,
        recorrente,
        tipoAbrangencia,
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
      alert('Erro ao salvar cobrança/desconto.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir este lançamento?')) {
      try {
        await backEndService.delete(`/cobrancas-extras/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir lançamento.');
      }
    }
  };

  const getAbrangenciaBadge = (cob: ICobrancaExtra) => {
    const isGeral = cob.tipoAbrangencia === 'GERAL' || !cob.idUnidade;
    if (isGeral) {
      return (
        <span className="badge bg-secondary d-inline-flex align-items-center gap-1">
          <FaBuilding className="small" /> Geral (Todas Unidades)
        </span>
      );
    }
    const u = unidades.find((item) => item.idUnidade === cob.idUnidade);
    return (
      <span className="badge bg-light text-dark border d-inline-flex align-items-center gap-1">
        <FaUser className="small text-muted" /> {u ? `Unidade ${u.numeroUnidade}` : `Unidade ID ${cob.idUnidade}`}
      </span>
    );
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Cobranças & Descontos Extras</h2>
          <p className="text-muted small">Gerencie cobranças avulsas, descontos, lançamentos gerais e recorrentes.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Novo Lançamento Extra</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando lançamentos...</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom align-middle">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Tipo</th>
                    <th>Abrangência</th>
                    <th>Recorrente</th>
                    <th>Mês/Ano Ref.</th>
                    <th>Descrição</th>
                    <th>Valor</th>
                    <th>Inclusão</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {cobrancas.length > 0 ? (
                    cobrancas.map((cob) => {
                      const isDesconto = cob.tipoOperacao === 'DESCONTO';
                      return (
                        <tr key={cob.idCobrancaExtra}>
                          <td>{cob.idCobrancaExtra}</td>
                          <td>
                            {isDesconto ? (
                              <span className="badge bg-success">Desconto (-)</span>
                            ) : (
                              <span className="badge bg-danger">Acréscimo (+)</span>
                            )}
                          </td>
                          <td className="fw-semibold">{getAbrangenciaBadge(cob)}</td>
                          <td>
                            {cob.recorrente ? (
                              <span className="badge bg-info text-dark d-inline-flex align-items-center gap-1">
                                <FaSyncAlt className="small" /> Sim
                              </span>
                            ) : (
                              <span className="badge bg-light text-muted border">Não</span>
                            )}
                          </td>
                          <td>{String(cob.mesReferencia).padStart(2, '0')}/{cob.anoReferencia || new Date().getFullYear()}</td>
                          <td>{cob.descricao}</td>
                          <td className={`fw-bold ${isDesconto ? 'text-success' : 'text-danger'}`}>
                            {isDesconto ? '-' : '+'} R$ {cob.valorCobranca.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
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
                      );
                    })
                  ) : (
                    <tr>
                      <td colSpan={9} className="text-center py-4 text-muted">
                        Nenhum lançamento extra cadastrado.
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
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <form onSubmit={handleSave}>
                <div className="modal-header modal-header-custom justify-content-between">
                  <h5 className="modal-title">
                    {editingCobranca ? 'Editar Lançamento Extra' : 'Novo Lançamento Extra'}
                  </h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="row g-3 mb-3">
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Tipo de Lançamento</label>
                      <select
                        className="form-select form-control-custom"
                        value={tipoOperacao}
                        onChange={(e) => setTipoOperacao(e.target.value as 'ACRESCIMO' | 'DESCONTO')}
                      >
                        <option value="ACRESCIMO">Acréscimo (+) [Cobrança Extra]</option>
                        <option value="DESCONTO">Desconto (-) [Abatimento Extra]</option>
                      </select>
                    </div>

                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Abrangência</label>
                      <select
                        className="form-select form-control-custom"
                        value={tipoAbrangencia}
                        onChange={(e) => {
                          const val = e.target.value as 'GERAL' | 'UNIDADE';
                          setTipoAbrangencia(val);
                          if (val === 'GERAL') setSelectedUnidadeId(-1);
                        }}
                      >
                        <option value="UNIDADE">Por Condômino (Unidade Específica)</option>
                        <option value="GERAL">Cobrança Geral (Todas as Unidades)</option>
                      </select>
                    </div>
                  </div>

                  {tipoAbrangencia === 'UNIDADE' && (
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
                  )}

                  <div className="row g-2 mb-3">
                    <div className="col-md-4">
                      <label className="form-label form-label-custom">Valor (R$)</label>
                      <input 
                        type="number" 
                        step="0.01"
                        className="form-control form-control-custom" 
                        value={valorCobranca} 
                        onChange={(e) => setValorCobranca(Number(e.target.value))} 
                        required 
                      />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label form-label-custom">Mês Ref.</label>
                      <select 
                        className="form-select form-control-custom"
                        value={mesReferencia}
                        onChange={(e) => setMesReferencia(Number(e.target.value))}
                      >
                        {Array.from({ length: 12 }, (_, i) => (
                          <option key={i + 1} value={i + 1}>
                            {String(i + 1).padStart(2, '0')}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="col-md-4">
                      <label className="form-label form-label-custom">Ano Ref.</label>
                      <input 
                        type="number" 
                        className="form-control form-control-custom" 
                        value={anoReferencia} 
                        onChange={(e) => setAnoReferencia(Number(e.target.value))} 
                        required 
                      />
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label form-label-custom">Descrição</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      placeholder="Ex: Conserto do Portão, Fundo de Reserva, Desconto Antecipação"
                      value={descricao} 
                      onChange={(e) => setDescricao(e.target.value)} 
                      required 
                    />
                  </div>

                  <div className="form-check form-switch mt-3">
                    <input
                      className="form-check-input"
                      type="checkbox"
                      id="recorrenteCheck"
                      checked={recorrente}
                      onChange={(e) => setRecorrente(e.target.checked)}
                    />
                    <label className="form-check-label fw-semibold" htmlFor="recorrenteCheck">
                      Cobrança Recorrente (Aplica-se mensalmente aos boletos)
                    </label>
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
