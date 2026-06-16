import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { backEndService } from '../services/api';
import type { IUsuario } from '../types';
import { FaSpinner, FaUser, FaLock, FaCheckCircle } from 'react-icons/fa';

const Configuracoes: React.FC = () => {
  const { user } = useAuth();
  
  const [loading, setLoading] = useState<boolean>(true);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Dados do Usuário do Backend
  const [rawUser, setRawUser] = useState<IUsuario | null>(null);

  // Campos do Formulário
  const [nomeUsuario, setNomeUsuario] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [nrTelefoneDdd, setNrTelefoneDdd] = useState<string>('');
  const [nrTelefone, setNrTelefone] = useState<string>('');
  const [nrCelularDdd, setNrCelularDdd] = useState<string>('');
  const [nrCelular, setNrCelular] = useState<string>('');
  const [cpf, setCpf] = useState<string>('');
  const [cnpj, setCnpj] = useState<string>('');
  const [tipoPessoa, setTipoPessoa] = useState<string>('F');

  // Campos de Troca de Senha
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');

  const loadUserData = async (): Promise<void> => {
    if (!user) return;
    try {
      setLoading(true);
      const res = await backEndService.get<IUsuario>(`/usuarios/${user.userId}`);
      setRawUser(res);

      setNomeUsuario(res.nomeUsuario);
      setEmail(res.email);
      setNrTelefoneDdd(res.nrTelefoneDdd || '');
      setNrTelefone(res.nrTelefone || '');
      setNrCelularDdd(res.nrCelularDdd || '');
      setNrCelular(res.nrCelular || '');
      setCpf(res.cpf || '');
      setCnpj(res.nrDocumentoCnpj || '');
      setTipoPessoa(res.tipoPessoa || 'F');
    } catch (err) {
      console.error(err);
      setError('Erro ao carregar dados da conta.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUserData();
  }, [user]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validação de senha
    if (newPassword) {
      if (newPassword.length < 4) {
        setError('A nova senha deve ter pelo menos 4 caracteres.');
        return;
      }
      if (newPassword !== confirmPassword) {
        setError('A confirmação de senha não confere com a nova senha.');
        return;
      }
    }

    setSubmitting(true);
    try {
      if (!rawUser) return;

      const payload = {
        id: rawUser.id,
        nomeUsuario,
        email,
        password: newPassword || undefined,
        nrTelefoneDdd: nrTelefoneDdd || null,
        nrTelefone: nrTelefone || null,
        nrCelularDdd: nrCelularDdd || null,
        nrCelular: nrCelular || null,
        cpf: tipoPessoa === 'F' ? cpf : null,
        nrDocumentoCnpj: tipoPessoa === 'J' ? cnpj : null,
        tipoPessoa,
        ativo: rawUser.ativo,
        enviaBoleto: rawUser.enviaBoleto,
        enviaSms: rawUser.enviaSms,
        unidadeDTO: rawUser.unidade ? {
          idUnidade: rawUser.unidade.idUnidade,
          numeroUnidade: rawUser.unidade.numeroUnidade,
          andarUnidade: rawUser.unidade.andarUnidade,
        } : null,
        endereco: rawUser.endereco ? {
          idEndereco: rawUser.endereco.idEndereco,
          txEndereco: rawUser.endereco.txEndereco,
          txEnderecoNumero: rawUser.endereco.txEnderecoNumero,
          txEnderecoComplemento: rawUser.endereco.txEnderecoComplemento,
          txBairro: rawUser.endereco.txBairro,
          txCidade: rawUser.endereco.txCidade,
          txUf: rawUser.endereco.txUf,
          txCep: rawUser.endereco.txCep,
        } : null,
        listPerfis: rawUser.listPerfis.map((p) => ({
          id: p.id,
          nomePerfil: p.nomePerfil,
        })),
      };

      await backEndService.put('/usuarios/update', payload);
      
      // Atualizar o nome exibido localmente se houver mudança
      if (user) {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const parsed = JSON.parse(storedUser);
          parsed.userName = nomeUsuario;
          localStorage.setItem('user', JSON.stringify(parsed));
          user.userName = nomeUsuario;
        }
      }

      setSuccess('Dados cadastrais atualizados com sucesso!');
      setNewPassword('');
      setConfirmPassword('');
      loadUserData();
    } catch (err) {
      console.error(err);
      setError('Erro ao atualizar dados cadastrais.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="py-5 text-center">
        <FaSpinner className="spin text-primary fs-3 mb-2" />
        <p className="text-muted">Carregando configurações da conta...</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="mb-4">
        <h2 className="mb-0">Configurações da Conta</h2>
        <p className="text-muted small">Gerencie suas informações cadastrais e altere sua senha de acesso.</p>
      </div>

      {success && (
        <div className="alert alert-success border-0 shadow-sm d-flex align-items-center gap-2 mb-4" role="alert">
          <FaCheckCircle />
          <span>{success}</span>
        </div>
      )}

      {error && (
        <div className="alert alert-danger border-0 shadow-sm mb-4" role="alert">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="row g-4">
          <div className="col-12">
            <div className="card-content">
              <div className="card-content-header">
                <h5 className="card-content-title d-flex align-items-center gap-2">
                  <FaUser className="text-primary" />
                  <span>Meus Dados Cadastrais</span>
                </h5>
              </div>
              <div className="card-content-body">
                <div className="row g-3">
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
                  <div className="col-md-3">
                    <label className="form-label form-label-custom">Tipo Pessoa</label>
                    <select 
                      className="form-select form-control-custom" 
                      value={tipoPessoa} 
                      onChange={(e) => setTipoPessoa(e.target.value)}
                    >
                      <option value="F">Física</option>
                      <option value="J">Jurídica</option>
                    </select>
                  </div>
                  <div className="col-md-5">
                    <label className="form-label form-label-custom">CPF / CNPJ</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom"
                      value={tipoPessoa === 'F' ? cpf : cnpj}
                      onChange={(e) => tipoPessoa === 'F' ? setCpf(e.target.value) : setCnpj(e.target.value)}
                    />
                  </div>

                  <div className="col-md-2">
                    <label className="form-label form-label-custom">DDD Celular</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom"
                      maxLength={2}
                      value={nrCelularDdd}
                      onChange={(e) => setNrCelularDdd(e.target.value)}
                    />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label form-label-custom">Celular</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom"
                      value={nrCelular}
                      onChange={(e) => setNrCelular(e.target.value)}
                    />
                  </div>
                  
                  <div className="col-md-2">
                    <label className="form-label form-label-custom">DDD Fixo</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom"
                      maxLength={2}
                      value={nrTelefoneDdd}
                      onChange={(e) => setNrTelefoneDdd(e.target.value)}
                    />
                  </div>
                  <div className="col-md-4">
                    <label className="form-label form-label-custom">Telefone Fixo</label>
                    <input 
                      type="text" 
                      className="form-control form-control-custom"
                      value={nrTelefone}
                      onChange={(e) => setNrTelefone(e.target.value)}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-12">
            <div className="card-content">
              <div className="card-content-header">
                <h5 className="card-content-title d-flex align-items-center gap-2">
                  <FaLock className="text-primary" />
                  <span>Segurança e Acesso</span>
                </h5>
              </div>
              <div className="card-content-body">
                <div className="row g-3">
                  <div className="col-md-6">
                    <label className="form-label form-label-custom">Nova Senha (deixe vazio se não for alterar)</label>
                    <input 
                      type="password" 
                      className="form-control form-control-custom"
                      placeholder="Mínimo 4 caracteres"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                    />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label form-label-custom">Confirmar Nova Senha</label>
                    <input 
                      type="password" 
                      className="form-control form-control-custom"
                      placeholder="Repita a nova senha"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="col-12 d-flex justify-content-end mb-5">
            <button 
              type="submit" 
              className="btn btn-primary btn-primary-custom px-4 py-2.5" 
              disabled={submitting}
            >
              {submitting ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default Configuracoes;
