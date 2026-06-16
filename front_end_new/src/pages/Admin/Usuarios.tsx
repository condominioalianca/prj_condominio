import React, { useEffect, useState } from 'react';
import { backEndService } from '../../services/api';
import type { IUsuario, IUnidade, IPerfil, ISprungPage } from '../../types';
import { FaPlus, FaEdit, FaTrash, FaSearch, FaSpinner } from 'react-icons/fa';

const Usuarios: React.FC = () => {
  // Estados para listagem
  const [usuariosPage, setUsuariosPage] = useState<ISprungPage<IUsuario> | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [page, setPage] = useState<number>(0);
  const [size] = useState<number>(10);

  // Estados dos Modais
  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [editingUser, setEditingUser] = useState<IUsuario | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  // Dados auxiliares de formulário
  const [unidades, setUnidades] = useState<IUnidade[]>([]);
  const [perfis, setPerfis] = useState<IPerfil[]>([]);

  // Formulário
  const [nomeUsuario, setNomeUsuario] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [nrTelefoneDdd, setNrTelefoneDdd] = useState<string>('');
  const [nrTelefone, setNrTelefone] = useState<string>('');
  const [nrCelularDdd, setNrCelularDdd] = useState<string>('');
  const [nrCelular, setNrCelular] = useState<string>('');
  const [cpf, setCpf] = useState<string>('');
  const [cnpj, setCnpj] = useState<string>('');
  const [tipoPessoa, setTipoPessoa] = useState<string>('F');
  const [enviaBoleto, setEnviaBoleto] = useState<boolean>(true);
  const [enviaSms, setEnviaSms] = useState<boolean>(true);
  const [ativo, setAtivo] = useState<boolean>(true);
  const [selectedUnidadeId, setSelectedUnidadeId] = useState<number>(-1);
  const [selectedPerfilIds, setSelectedPerfilIds] = useState<number[]>([]);

  // Campos de Endereço
  const [txEndereco, setTxEndereco] = useState<string>('');
  const [txEnderecoNumero, setTxEnderecoNumero] = useState<string>('');
  const [txEnderecoComplemento, setTxEnderecoComplemento] = useState<string>('');
  const [txBairro, setTxBairro] = useState<string>('');
  const [txCidade, setTxCidade] = useState<string>('');
  const [txUf, setTxUf] = useState<string>('');
  const [txCep, setTxCep] = useState<string>('');

  // Carregar dados de usuários, unidades e perfis
  const loadData = async (): Promise<void> => {
    try {
      setLoading(true);
      const url = searchTerm 
        ? `/usuarios?page=${page}&size=${size}&sort=nomeUsuario,asc` // endpoint padrão
        : `/usuarios?page=${page}&size=${size}&sort=nomeUsuario,asc`;
      
      const res = await backEndService.get<ISprungPage<IUsuario>>(url);
      setUsuariosPage(res);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page, size]);

  // Carregar listas auxiliares ao abrir o formulário
  const loadFormHelpers = async (): Promise<void> => {
    try {
      // Carrega unidades (limite de 1000 para dropdown)
      const resUnidades = await backEndService.get<ISprungPage<IUnidade>>('/unidade?size=1000');
      setUnidades(resUnidades.content);

      // Carrega perfis
      const resPerfis = await backEndService.get<IPerfil[]>('/perfis');
      setPerfis(resPerfis);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSearchSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    if (searchTerm.trim() === '') {
      loadData();
      return;
    }
    try {
      setLoading(true);
      // O backend tem um endpoint de busca por CPF/CNPJ ou Email
      const res = await backEndService.get<IUsuario>(`/usuarios/search?value=${searchTerm}`);
      setUsuariosPage({
        content: [res],
        pageable: {
          sort: { sorted: true, unsorted: false, empty: false },
          pageNumber: 0,
          pageSize: 10,
          offset: 0,
          paged: true,
          unpaged: false,
        },
        totalPages: 1,
        totalElements: 1,
        last: true,
        numberOfElements: 1,
        size: 10,
        number: 0,
        sort: { sorted: true, unsorted: false, empty: false },
        first: true,
        empty: false,
      });
    } catch (err) {
      console.error(err);
      alert('Usuário não encontrado.');
      loadData();
    } finally {
      setLoading(false);
    }
  };

  const openCreateModal = (): void => {
    setEditingUser(null);
    setNomeUsuario('');
    setEmail('');
    setPassword('');
    setNrTelefoneDdd('');
    setNrTelefone('');
    setNrCelularDdd('');
    setNrCelular('');
    setCpf('');
    setCnpj('');
    setTipoPessoa('F');
    setEnviaBoleto(true);
    setEnviaSms(true);
    setAtivo(true);
    setSelectedUnidadeId(-1);
    setSelectedPerfilIds([]);
    
    // Zera endereço
    setTxEndereco('');
    setTxEnderecoNumero('');
    setTxEnderecoComplemento('');
    setTxBairro('');
    setTxCidade('');
    setTxUf('');
    setTxCep('');

    loadFormHelpers();
    setModalOpen(true);
  };

  const openEditModal = (usuario: IUsuario): void => {
    setEditingUser(usuario);
    setNomeUsuario(usuario.nomeUsuario);
    setEmail(usuario.email);
    setPassword(''); // nunca exibe a senha atual por segurança
    setNrTelefoneDdd(usuario.nrTelefoneDdd || '');
    setNrTelefone(usuario.nrTelefone || '');
    setNrCelularDdd(usuario.nrCelularDdd || '');
    setNrCelular(usuario.nrCelular || '');
    setCpf(usuario.cpf || '');
    setCnpj(usuario.nrDocumentoCnpj || '');
    setTipoPessoa(usuario.tipoPessoa || 'F');
    setEnviaBoleto(usuario.enviaBoleto);
    setEnviaSms(usuario.enviaSms);
    setAtivo(usuario.ativo);
    setSelectedUnidadeId(usuario.unidade ? usuario.unidade.idUnidade : -1);
    setSelectedPerfilIds(usuario.listPerfis.map((p) => p.id));

    // Endereço
    if (usuario.endereco) {
      setTxEndereco(usuario.endereco.txEndereco);
      setTxEnderecoNumero(usuario.endereco.txEnderecoNumero);
      setTxEnderecoComplemento(usuario.endereco.txEnderecoComplemento || '');
      setTxBairro(usuario.endereco.txBairro);
      setTxCidade(usuario.endereco.txCidade);
      setTxUf(usuario.endereco.txUf);
      setTxCep(usuario.endereco.txCep);
    } else {
      setTxEndereco('');
      setTxEnderecoNumero('');
      setTxEnderecoComplemento('');
      setTxBairro('');
      setTxCidade('');
      setTxUf('');
      setTxCep('');
    }

    loadFormHelpers();
    setModalOpen(true);
  };

  const handleCheckboxPerfilChange = (id: number): void => {
    if (selectedPerfilIds.includes(id)) {
      setSelectedPerfilIds(selectedPerfilIds.filter((pId) => pId !== id));
    } else {
      setSelectedPerfilIds([...selectedPerfilIds, id]);
    }
  };

  const handleSave = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const selectedUnidade = unidades.find((u) => u.idUnidade === selectedUnidadeId) || null;
      const mappedPerfis = perfis.filter((p) => selectedPerfilIds.includes(p.id));

      const enderecoData = {
        idEndereco: editingUser?.endereco ? editingUser.endereco.idEndereco : null,
        txEndereco,
        txEnderecoNumero,
        txEnderecoComplemento,
        txBairro,
        txCidade,
        txUf,
        txCep,
      };

      const payload = {
        id: editingUser ? editingUser.id : null,
        nomeUsuario,
        email,
        password: password || undefined,
        nrTelefoneDdd: nrTelefoneDdd || null,
        nrTelefone: nrTelefone || null,
        nrCelularDdd: nrCelularDdd || null,
        nrCelular: nrCelular || null,
        cpf: cpf || null,
        nrDocumentoCnpj: cnpj || null,
        tipoPessoa,
        enviaBoleto,
        enviaSms,
        ativo,
        unidadeDTO: selectedUnidade ? {
          idUnidade: selectedUnidade.idUnidade,
          numeroUnidade: selectedUnidade.numeroUnidade,
          andarUnidade: selectedUnidade.andarUnidade,
        } : null,
        endereco: enderecoData,
        listPerfis: mappedPerfis.map((p) => ({
          id: p.id,
          nomePerfil: p.nomePerfil,
        })),
      };

      if (editingUser) {
        await backEndService.put('/usuarios/update', payload);
      } else {
        await backEndService.post('/usuarios/save', payload);
      }

      setModalOpen(false);
      loadData();
    } catch (err) {
      console.error(err);
      alert('Erro ao salvar dados do usuário.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Tem certeza de que deseja deletar este usuário?')) {
      try {
        await backEndService.delete(`/usuarios/delet/${id}`);
        loadData();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir usuário.');
      }
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="mb-0">Administração de Usuários</h2>
          <p className="text-muted small">Crie, altere ou remova moradores e administradores.</p>
        </div>
        <button className="btn btn-primary btn-primary-custom d-flex align-items-center gap-2" onClick={openCreateModal}>
          <FaPlus />
          <span>Novo Usuário</span>
        </button>
      </div>

      {/* Barra de Filtro e Busca */}
      <div className="card-content mb-4">
        <div className="card-content-body py-3">
          <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
            <div className="col-auto">
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaSearch />
                </span>
                <input
                  type="text"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="Buscar CPF, CNPJ ou E-mail"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>
            <div className="col-auto">
              <button type="submit" className="btn btn-primary btn-primary-custom py-2">Buscar</button>
            </div>
            {searchTerm && (
              <div className="col-auto">
                <button 
                  type="button" 
                  className="btn btn-outline-secondary py-2"
                  onClick={() => {
                    setSearchTerm('');
                    setPage(0);
                    loadData();
                  }}
                >
                  Limpar
                </button>
              </div>
            )}
          </form>
        </div>
      </div>

      {/* Grid de Dados */}
      <div className="card-content">
        <div className="card-content-body p-0">
          {loading ? (
            <div className="py-5 text-center">
              <FaSpinner className="spin text-primary fs-3 mb-2" />
              <p className="text-muted mb-0">Carregando usuários...</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-custom">
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>E-mail</th>
                    <th>CPF/CNPJ</th>
                    <th>Unidade</th>
                    <th>Perfis</th>
                    <th>Status</th>
                    <th className="text-end">Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {usuariosPage && usuariosPage.content.length > 0 ? (
                    usuariosPage.content.map((usuario) => (
                      <tr key={usuario.id}>
                        <td className="fw-semibold text-nowrap">{usuario.nomeUsuario}</td>
                        <td>{usuario.email}</td>
                        <td>{usuario.cpf || usuario.nrDocumentoCnpj || 'N/A'}</td>
                        <td>{usuario.unidade ? `AP ${usuario.unidade.numeroUnidade}` : 'Sem Unidade'}</td>
                        <td>
                          {usuario.listPerfis.map((p) => p.nomePerfil).join(', ')}
                        </td>
                        <td>
                          <span className={`badge-custom ${usuario.ativo ? 'pago' : 'vencido'}`}>
                            {usuario.ativo ? 'Ativo' : 'Inativo'}
                          </span>
                        </td>
                        <td className="text-end text-nowrap">
                          <button 
                            className="btn btn-outline-primary btn-sm me-2"
                            onClick={() => openEditModal(usuario)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button 
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => usuario.id && handleDelete(usuario.id)}
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
                        Nenhum usuário cadastrado.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Paginação */}
        {usuariosPage && usuariosPage.totalPages > 1 && (
          <div className="card-content-header justify-content-end border-top">
            <nav>
              <ul className="pagination pagination-sm mb-0">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(page - 1)}>Anterior</button>
                </li>
                {Array.from({ length: usuariosPage.totalPages }, (_, i) => (
                  <li key={i} className={`page-item ${page === i ? 'active' : ''}`}>
                    <button className="page-link" onClick={() => setPage(i)}>{i + 1}</button>
                  </li>
                ))}
                <li className={`page-item ${page === usuariosPage.totalPages - 1 ? 'disabled' : ''}`}>
                  <button className="page-link" onClick={() => setPage(page + 1)}>Próximo</button>
                </li>
              </ul>
            </nav>
          </div>
        )}
      </div>

      {/* Modal de Cadastro / Edição */}
      {modalOpen && (
        <div className="modal fade show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)', overflowY: 'auto' }} tabIndex={-1}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <form onSubmit={handleSave}>
                <div className="modal-header modal-header-custom justify-content-between">
                  <h5 className="modal-title">{editingUser ? 'Editar Usuário' : 'Novo Usuário'}</h5>
                  <button type="button" className="btn-close" onClick={() => setModalOpen(false)}></button>
                </div>
                
                <div className="modal-body p-4">
                  <h6 className="text-primary border-bottom pb-2 mb-3">Informações Pessoais</h6>
                  <div className="row g-3 mb-4">
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Nome Completo</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nomeUsuario} 
                        onChange={(e) => setNomeUsuario(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">E-mail</label>
                      <input 
                        type="email" 
                        className="form-control form-control-custom" 
                        value={email} 
                        onChange={(e) => setEmail(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Senha {editingUser && '(deixe em branco para manter a atual)'}</label>
                      <input 
                        type="password" 
                        className="form-control form-control-custom" 
                        value={password} 
                        onChange={(e) => setPassword(e.target.value)} 
                        required={!editingUser} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Tipo Pessoa</label>
                      <select className="form-select form-control-custom" value={tipoPessoa} onChange={(e) => setTipoPessoa(e.target.value)}>
                        <option value="F">Física</option>
                        <option value="J">Jurídica</option>
                      </select>
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">CPF / CNPJ</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={tipoPessoa === 'F' ? cpf : cnpj} 
                        onChange={(e) => tipoPessoa === 'F' ? setCpf(e.target.value) : setCnpj(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">DDD Fixo</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        maxLength={2}
                        value={nrTelefoneDdd} 
                        onChange={(e) => setNrTelefoneDdd(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Telefone Fixo</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={nrTelefone} 
                        onChange={(e) => setNrTelefone(e.target.value)} 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">DDD Celular</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        maxLength={2}
                        value={nrCelularDdd} 
                        onChange={(e) => setNrCelularDdd(e.target.value)} 
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
                  </div>

                  <h6 className="text-primary border-bottom pb-2 mb-3">Parâmetros e Perfis</h6>
                  <div className="row g-3 mb-4">
                    <div className="col-md-6">
                      <label className="form-label form-label-custom">Unidade (Apartamento)</label>
                      <select 
                        className="form-select form-control-custom" 
                        value={selectedUnidadeId} 
                        onChange={(e) => setSelectedUnidadeId(Number(e.target.value))}
                      >
                        <option value={-1}>Selecione uma unidade...</option>
                        {unidades.map((u) => (
                          <option key={u.idUnidade} value={u.idUnidade}>
                            Unidade {u.numeroUnidade} (Andar {u.andarUnidade})
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="col-md-6">
                      <label className="form-label form-label-custom d-block">Perfil de Acesso</label>
                      <div className="d-flex flex-wrap gap-3 mt-2">
                        {perfis.map((p) => (
                          <div key={p.id} className="form-check">
                            <input
                              className="form-check-input"
                              type="checkbox"
                              id={`perfil-${p.id}`}
                              checked={selectedPerfilIds.includes(p.id)}
                              onChange={() => handleCheckboxPerfilChange(p.id)}
                            />
                            <label className="form-check-label small" htmlFor={`perfil-${p.id}`}>
                              {p.nomePerfil}
                            </label>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div className="col-12 d-flex gap-4">
                      <div className="form-check form-switch">
                        <input 
                          className="form-check-input" 
                          type="checkbox" 
                          id="ativo" 
                          checked={ativo} 
                          onChange={(e) => setAtivo(e.target.checked)} 
                        />
                        <label className="form-check-label small" htmlFor="ativo">Usuário Ativo</label>
                      </div>
                      <div className="form-check form-switch">
                        <input 
                          className="form-check-input" 
                          type="checkbox" 
                          id="enviaBoleto" 
                          checked={enviaBoleto} 
                          onChange={(e) => setEnviaBoleto(e.target.checked)} 
                        />
                        <label className="form-check-label small" htmlFor="enviaBoleto">Envia Boleto por E-mail</label>
                      </div>
                      <div className="form-check form-switch">
                        <input 
                          className="form-check-input" 
                          type="checkbox" 
                          id="enviaSms" 
                          checked={enviaSms} 
                          onChange={(e) => setEnviaSms(e.target.checked)} 
                        />
                        <label className="form-check-label small" htmlFor="enviaSms">Envia SMS</label>
                      </div>
                    </div>
                  </div>

                  <h6 className="text-primary border-bottom pb-2 mb-3">Endereço</h6>
                  <div className="row g-3">
                    <div className="col-md-9">
                      <label className="form-label form-label-custom">Logradouro (Rua/Avenida)</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txEndereco} 
                        onChange={(e) => setTxEndereco(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">Número</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txEnderecoNumero} 
                        onChange={(e) => setTxEnderecoNumero(e.target.value)} 
                        required 
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
                        required 
                      />
                    </div>
                    <div className="col-md-5">
                      <label className="form-label form-label-custom">Cidade</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txCidade} 
                        onChange={(e) => setTxCidade(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-3">
                      <label className="form-label form-label-custom">UF (Estado)</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        maxLength={2}
                        value={txUf} 
                        onChange={(e) => setTxUf(e.target.value)} 
                        required 
                      />
                    </div>
                    <div className="col-md-4">
                      <label className="form-label form-label-custom">CEP</label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        value={txCep} 
                        onChange={(e) => setTxCep(e.target.value)} 
                        required 
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

export default Usuarios;
