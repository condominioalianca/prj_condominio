import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { IEmpresa } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSpinner } from 'react-icons/fa';

const Empresas: React.FC = () => {
  const [empresas, setEmpresas] = useState<IEmpresa[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingEmpresa, setEditingEmpresa] = useState<IEmpresa | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  // Campos de Formulário
  const [nomeEmpresa, setNomeEmpresa] = useState<string>('');
  const [nrDocumento, setNrDocumento] = useState<string>('');
  const [nrCelular, setNrCelular] = useState<string>('');
  const [nrTelefone, setNrTelefone] = useState<string>('');
  const [txEndereco, setTxEndereco] = useState<string>('');
  const [txEnderecoComplemento, setTxEnderecoComplemento] = useState<string>('');
  const [txEnderecoNumero, setTxEnderecoNumero] = useState<string>('');
  const [txCep, setTxCep] = useState<string>('');
  const [txBairro, setTxBairro] = useState<string>('');
  const [txEmail, setTxEmail] = useState<string>('');

  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      const res = await backEndService.get<IEmpresa[]>('/empresas');
      setEmpresas(res);
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
    setEditingEmpresa(null);
    setNomeEmpresa('');
    setNrDocumento('');
    setNrCelular('');
    setNrTelefone('');
    setTxEndereco('');
    setTxEnderecoComplemento('');
    setTxEnderecoNumero('');
    setTxCep('');
    setTxBairro('');
    setTxEmail('');
    setModalOpen(true);
  };

  const openEditModal = (empresa: IEmpresa): void => {
    setEditingEmpresa(empresa);
    setNomeEmpresa(empresa.nomeEmpresa);
    setNrDocumento(empresa.nrDocumento);
    setNrCelular(empresa.nrCelular || '');
    setNrTelefone(empresa.nrTelefone || '');
    setTxEndereco(empresa.txEndereco || '');
    setTxEnderecoComplemento(empresa.txEnderecoComplemento || '');
    setTxEnderecoNumero(empresa.txEnderecoNumero || '');
    setTxCep(empresa.txCep || '');
    setTxBairro(empresa.txBairro || '');
    setTxEmail(empresa.txEmail || '');
    setModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload: IEmpresa = {
        id: editingEmpresa ? editingEmpresa.id : null,
        nomeEmpresa,
        nrDocumento,
        nrCelular: nrCelular || null,
        nrTelefone: nrTelefone || null,
        txEndereco: txEndereco || null,
        txEnderecoComplemento: txEnderecoComplemento || null,
        txEnderecoNumero: txEnderecoNumero || null,
        txCep: txCep || null,
        txBairro: txBairro || null,
        txEmail: txEmail || null,
      };

      if (editingEmpresa) {
        await backEndService.put('/empresas/update', payload);
      } else {
        await backEndService.post('/empresas/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar empresa.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Deseja realmente excluir esta empresa?')) {
      try {
        await backEndService.delete(`/empresas/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir empresa.');
      }
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Administração de Empresas</h2>
          <p className="text-muted small">Gerencie as empresas e prestadores de serviços parceiros.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Nova Empresa</span>
        </button>
      </div>

      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando empresas...</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th>Nome Empresa / Razão Social</th>
                    <th>CNPJ / CPF</th>
                    <th>E-mail</th>
                    <th>Celular / Telefone</th>
                    <th>Cidade/Bairro</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {empresas.length > 0 ? (
                    empresas.map((empresa) => (
                      <tr key={empresa.id}>
                        <td className="fw-semibold text-nowrap">{empresa.nomeEmpresa}</td>
                        <td>{empresa.nrDocumento}</td>
                        <td>{empresa.txEmail || 'N/A'}</td>
                        <td>{empresa.nrCelular || empresa.nrTelefone || 'N/A'}</td>
                        <td>{empresa.txBairro ? `${empresa.txBairro}` : 'N/A'}</td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(empresa)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => empresa.id && handleDelete(empresa.id)}
                            title="Excluir"
                          >
                            <FaTrash />
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan={6} className="text-center py-4 text-muted">
                        Nenhuma empresa cadastrada.
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
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', overflowY: 'auto' }} tabIndex={-1}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <form onSubmit={handleSave}>
                <div className="modal-header modal-header-custom justify-content-between">
                  <h5 className="modal-title">{editingEmpresa ? 'Editar Empresa' : 'Nova Empresa'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                <div className="modal-body p-4">
                  <div className="row g-3">
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Nome da Empresa</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nomeEmpresa} 
                        onChange={(e) => setNomeEmpresa(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">CNPJ / CPF</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nrDocumento} 
                        onChange={(e) => setNrDocumento(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">E-mail</label>
                      <input 
                        type="email" 
                        className="form-control form-control-custom" 
                        value={txEmail} 
                        onChange={(e) => setTxEmail(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Celular</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nrCelular} 
                        onChange={(e) => setNrCelular(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Telefone</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nrTelefone} 
                        onChange={(e) => setNrTelefone(e.target.value)} 
                      />
                    </div>

                    <h6 className="text-primary border-bottom pb-1 mt-4 mb-2">Endereço da Empresa</h6>
                    <div className="col-md-9">
                      <label className="form-label form-label-custom">Logradouro</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txEndereco} 
                        onChange={(e) => setTxEndereco(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Número</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txEnderecoNumero} 
                        onChange={(e) => setTxEnderecoNumero(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Complemento</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txEnderecoComplemento} 
                        onChange={(e) => setTxEnderecoComplemento(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Bairro</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txBairro} 
                        onChange={(e) => setTxBairro(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">CEP</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txCep} 
                        onChange={(e) => setTxCep(e.target.value)} 
                      />
                    </div>
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

export default Empresas;
