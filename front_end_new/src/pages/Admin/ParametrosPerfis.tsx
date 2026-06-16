import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { IPerfil } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner } from 'react-icons/fa';

const ParametrosPerfis: React.FC = () => {
  const [perfis, setPerfis] = useState<IPerfil[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingPerfil, setEditingPerfil] = useState<IPerfil | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const [nomePerfil, setNomePerfil] = useState<string>('');

  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      setError(null);
      const res = await backEndService.get<IPerfil[]>('/perfis');
      setPerfis(res);
    } catch (err: any) {
      console.error(err);
      setError('Ocorreu um erro ao carregar os perfis de acesso. Verifique a conexão com o backend.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = (): void => {
    setEditingPerfil(null);
    setNomePerfil('');
    setModalOpen(true);
  };

  const openEditModal = (perfil: IPerfil): void => {
    setEditingPerfil(perfil);
    setNomePerfil(perfil.nomePerfil);
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload = {
        id: editingPerfil ? editingPerfil.id : null,
        nomePerfil,
      };

      if (editingPerfil) {
        await backEndService.put('/perfis/update', payload);
      } else {
        await backEndService.post('/perfis/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar perfil.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir este perfil?')) {
      try {
        await backEndService.delete(`/perfis/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir perfil.');
      }
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Gerenciamento de Perfis de Acesso</h2>
          <p className="text-muted small">Gerencie as funções (roles) disponíveis no condomínio.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Novo Perfil</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando perfis...</p>
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
                    <th>Nome do Perfil (Role)</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {perfis.length > 0 ? (
                    perfis.map((perfil) => (
                      <tr key={perfil.id}>
                        <td>{perfil.id}</td>
                        <td className="fw-semibold text-primary text-nowrap">{perfil.nomePerfil}</td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(perfil)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleDelete(perfil.id)}
                            title="Excluir"
                          >
                            <FaTrash />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={3} className="text-center py-4 text-muted">
                        Nenhum perfil cadastrado.
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
                  <h5 className="modal-title">{editingPerfil ? 'Editar Perfil' : 'Novo Perfil'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Nome do Perfil (recomenda-se maiúsculo)</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      placeholder="Ex: VISITANTE, DIRETORIA"
                      value={nomePerfil} 
                      onChange={(e) => setNomePerfil(e.target.value.toUpperCase())} 
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

export default ParametrosPerfis;
