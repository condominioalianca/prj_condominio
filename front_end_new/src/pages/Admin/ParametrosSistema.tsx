import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { IParametro } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner } from 'react-icons/fa';

const ParametrosSistema: React.FC = () => {
  const [parametros, setParametros] = useState<IParametro[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingParametro, setEditingParametro] = useState<IParametro | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const [descParametro, setDescParametro] = useState<string>('');
  const [valorParametro, setValorParametro] = useState<string>('');
  const [ativo, setAtivo] = useState<boolean>(true);

  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      // O endpoint findAll é GET /parametros/ com barra final conforme Controller
      const res = await backEndService.get<IParametro[]>('/parametros/');
      setParametros(res);
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.status === 403) {
        setError('Acesso Negado (403 Forbidden). O backend local possui a correção de segurança aplicada, mas verifique se o servidor backend em execução (192.168.15.10) foi recompilado e reiniciado para ler a nova configuração.');
      } else {
        setError('Ocorreu um erro ao carregar os parâmetros de sistema. Verifique a conexão com o backend.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = (): void => {
    setEditingParametro(null);
    setDescParametro('');
    setValorParametro('');
    setAtivo(true);
    setModalOpen(true);
  };

  const openEditModal = (param: IParametro): void => {
    setEditingParametro(param);
    setDescParametro(param.descParametro);
    setValorParametro(param.valorParametro);
    setAtivo(param.ativo);
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload = {
        idParametro: editingParametro ? editingParametro.idParametro : null,
        descParametro,
        valorParametro,
        ativo,
      };

      if (editingParametro) {
        await backEndService.put('/parametros/update', payload);
      } else {
        await backEndService.post('/parametros/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar parâmetro do sistema.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir este parâmetro?')) {
      try {
        await backEndService.delete(`/parametros/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir parâmetro.');
      }
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Parâmetros do Sistema</h2>
          <p className="text-muted small">Gerencie as chaves, caminhos e tokens de integração.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Novo Parâmetro</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando parâmetros...</p>
            </div>
          ) : error ? (
            <div className="p-4">
              <div className="alert alert-danger mb-0" role="alert">
                <h5 className="alert-heading fw-bold mb-2">Erro de Integração</h5>
                <p className="mb-0 small">{error}</p>
                <hr />
                <button type="button" className="btn btn-sm btn-outline-danger" onClick={loadData}>Tentar Novamente</button>
              </div>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Chave / Descrição</th>
                    <th>Valor do Parâmetro</th>
                    <th>Status</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {parametros.length > 0 ? (
                    parametros.map((param) => (
                      <tr key={param.idParametro}>
                        <td>{param.idParametro}</td>
                        <td className="fw-semibold text-nowrap">{param.descParametro}</td>
                        <td>
                          <code className="text-dark" style={{ wordBreak: 'break-all' }}>
                            {param.valorParametro}
                          </code>
                        </td>
                        <td>
                          <span className={`badge-custom ${param.ativo ? 'pago' : 'vencido'}`}>
                            {param.ativo ? 'Ativo' : 'Inativo'}
                          </span>
                        </td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(param)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => param.idParametro && handleDelete(param.idParametro)}
                            title="Excluir"
                          >
                            <FaTrash />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={5} className="text-center py-4 text-muted">
                        Nenhum parâmetro cadastrado.
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
                  <h5 className="modal-title">{editingParametro ? 'Editar Parâmetro' : 'Novo Parâmetro'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Chave / Descrição do Parâmetro</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      placeholder="Ex: BANCO_INTER_CLIENT_ID"
                      value={descParametro} 
                      onChange={(e) => setDescParametro(e.target.value)} 
                      required 
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Valor do Parâmetro</label>
                    <textarea 
                      className="form-control form-control-custom" 
                      rows={4}
                      placeholder="Insira o valor ou token correspondente..."
                      value={valorParametro} 
                      onChange={(e) => setValorParametro(e.target.value)} 
                      required 
                    />
                  </div>
                  <div className="form-check form-switch mb-2">
                    <input 
                      className="form-check-input" 
                      type="checkbox" 
                      id="ativo-param" 
                      checked={ativo} 
                      onChange={(e) => setAtivo(e.target.checked)} 
                    />
                    <label className="form-check-label small" htmlFor="ativo-param">Ativo</label>
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

export default ParametrosSistema;
