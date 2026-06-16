import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { IUnidade, ISprungPage } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner } from 'react-icons/fa';

const Unidades: React.FC = () => {
  const [unidadesPage, setUnidadesPage] = useState<ISprungPage<IUnidade> | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0);
  const [size] = useState<number>(10);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingUnidade, setEditingUnidade] = useState<IUnidade | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const [numeroUnidade, setNumeroUnidade] = useState<string>('');
  const [andarUnidade, setAndarUnidade] = useState<string>('');

  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      const res = await backEndService.get<ISprungPage<IUnidade>>(`/unidade?page=${page}&size=${size}&sort=numeroUnidade,asc`);
      setUnidadesPage(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, size]);

  const openCreateModal = (): void => {
    setEditingUnidade(null);
    setNumeroUnidade('');
    setAndarUnidade('');
    setModalOpen(true);
  };

  const openEditModal = (unidade: IUnidade): void => {
    setEditingUnidade(unidade);
    setNumeroUnidade(unidade.numeroUnidade);
    setAndarUnidade(unidade.andarUnidade);
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload = {
        idUnidade: editingUnidade ? editingUnidade.idUnidade : null,
        numeroUnidade,
        andarUnidade,
      };

      if (editingUnidade) {
        await backEndService.put('/unidade/update', payload);
      } else {
        await backEndService.post('/unidade/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar unidade.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir esta unidade?')) {
      try {
        await backEndService.delete(`/unidade/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir unidade.');
      }
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Administração de Unidades</h2>
          <p className="text-muted small">Gerencie as unidades e andares do condomínio.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Nova Unidade</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando unidades...</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th>ID Unidade</th>
                    <th>Número da Unidade</th>
                    <th>Andar</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {unidadesPage && unidadesPage.content.length > 0 ? (
                    unidadesPage.content.map((unidade) => (
                      <tr key={unidade.idUnidade}>
                        <td>{unidade.idUnidade}</td>
                        <td className="fw-semibold">Unidade {unidade.numeroUnidade}</td>
                        <td>{unidade.andarUnidade}º Andar</td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(unidade)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleDelete(unidade.idUnidade)}
                            title="Excluir"
                          >
                            <FaTrash />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={4} className="text-center py-4 text-muted">
                        Nenhuma unidade cadastrada.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {unidadesPage && unidadesPage.totalPages > 1 && (
          <div className="card-content-header justify-content-end border-top">
            <nav>
              <ul className="pagination pagination-sm mb-0">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(page - 1)}>Anterior</button>
                </li>
                {Array.from({ length: unidadesPage.totalPages }, (_, i) => (
                  <li key={i} className={`page-item ${page === i ? 'active' : ''}`}>
                    <button className="page-link" onClick={() => setPage(i)}>{i + 1}</button>
                  </li>
                ))}
                <li className={`page-item ${page === unidadesPage.totalPages - 1 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(page + 1)}>Próximo</button>
                </li>
              </ul>
            </nav>
          </div>
        )}
      </div>

      {modalOpen && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }} tabIndex={-1}>
          <div className="modal-dialog">
            <div className="modal-content">
              <form onSubmit={handleSave}>
                <div className="modal-header modal-header-custom justify-content-between">
                  <h5 className="modal-title">{editingUnidade ? 'Editar Unidade' : 'Nova Unidade'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Número da Unidade (ex: 101, 12-B)</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      value={numeroUnidade} 
                      onChange={(e) => setNumeroUnidade(e.target.value)} 
                      required 
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label form-label-custom">Andar (ex: 1, 2, Térreo)</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom" 
                      value={andarUnidade} 
                      onChange={(e) => setAndarUnidade(e.target.value)} 
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

export default Unidades;
