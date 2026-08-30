import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FaUser, FaEnvelope, FaPhone, FaIdCard, FaLock, FaBuilding } from 'react-icons/fa';
import { backEndService } from '../services/api';

const Cadastro: React.FC = () => {
  const [nomeUsuario, setNomeUsuario] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [ddd, setDdd] = useState<string>('');
  const [celular, setCelular] = useState<string>('');
  const [cpf, setCpf] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleCpfChange = (value: string) => {
    // Remove tudo o que não for dígito
    const cleanValue = value.replace(/\D/g, '');
    
    // Limita a 11 caracteres
    const truncatedValue = cleanValue.slice(0, 11);
    
    // Aplica a máscara CPF: 000.000.000-00
    let maskedValue = truncatedValue;
    if (truncatedValue.length > 9) {
      maskedValue = `${truncatedValue.slice(0, 3)}.${truncatedValue.slice(3, 6)}.${truncatedValue.slice(6, 9)}-${truncatedValue.slice(9)}`;
    } else if (truncatedValue.length > 6) {
      maskedValue = `${truncatedValue.slice(0, 3)}.${truncatedValue.slice(3, 6)}.${truncatedValue.slice(6)}`;
    } else if (truncatedValue.length > 3) {
      maskedValue = `${truncatedValue.slice(0, 3)}.${truncatedValue.slice(3)}`;
    }
    
    setCpf(maskedValue);
  };

  const handleCelularChange = (value: string) => {
    // Remove tudo o que não for dígito
    const cleanValue = value.replace(/\D/g, '');
    
    // Limita a 9 caracteres (celular brasileiro)
    const truncatedValue = cleanValue.slice(0, 9);
    
    // Aplica a máscara Celular: 90000-0000
    let maskedValue = truncatedValue;
    if (truncatedValue.length > 5) {
      maskedValue = `${truncatedValue.slice(0, 5)}-${truncatedValue.slice(5)}`;
    }
    
    setCelular(maskedValue);
  };

  const handleDddChange = (value: string) => {
    // Remove tudo o que não for dígito e limita a 2 dígitos
    const cleanValue = value.replace(/\D/g, '').slice(0, 2);
    setDdd(cleanValue);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError(null);

    // Validações básicas
    if (!nomeUsuario || !email || !ddd || !celular || !cpf || !password || !confirmPassword) {
      setError('Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    if (ddd.length !== 2) {
      setError('O DDD deve conter exatamente 2 dígitos.');
      return;
    }

    const cleanCpf = cpf.replace(/\D/g, '');
    if (cleanCpf.length !== 11) {
      setError('O CPF deve conter exatamente 11 dígitos.');
      return;
    }

    if (password !== confirmPassword) {
      setError('As senhas informadas não coincidem.');
      return;
    }

    if (password.length < 6) {
      setError('A senha deve conter no mínimo 6 caracteres.');
      return;
    }

    setSubmitting(true);
    try {
      await backEndService.post('/usuarios/cadastrar', {
        nomeUsuario,
        txEmail: email,
        nrCelularDdd: ddd,
        nrCelular: celular.replace(/\D/g, ''),
        nrDocumentoCpf: cleanCpf,
        password,
      });

      setSuccess(true);
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else if (err.response && err.response.data && typeof err.response.data === 'string') {
        setError(err.response.data);
      } else {
        setError('Erro ao realizar o cadastro. Tente novamente mais tarde.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center w-100 vh-100 bg-light px-3 overflow-y-auto py-5">
      <div className="card shadow border-0 my-auto" style={{ width: '500px', borderRadius: '12px' }}>
        <div className="card-body p-5">
          <div className="text-center mb-4">
            <div className="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3" style={{ width: '60px', height: '60px', fontSize: '1.5rem' }}>
              <FaBuilding />
            </div>
            <h3 className="mb-1 fw-bold" style={{ fontFamily: 'var(--font-heading)' }}>Criar Conta</h3>
            <p className="text-muted small">Preencha o formulário para se cadastrar</p>
          </div>

          {error && (
            <div className="alert alert-danger py-2 px-3 small border-0 mb-4" role="alert">
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 px-3 small border-0 mb-4" role="alert">
              Cadastro realizado com sucesso! Redirecionando para a tela de login...
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label small fw-semibold text-dark">Nome Completo</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaUser />
                </span>
                <input
                  type="text"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="Nome Completo"
                  value={nomeUsuario}
                  onChange={(e) => setNomeUsuario(e.target.value)}
                  disabled={submitting || success}
                  required
                />
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label small fw-semibold text-dark">E-mail</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaEnvelope />
                </span>
                <input
                  type="email"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="exemplo@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={submitting || success}
                  required
                />
              </div>
            </div>

            <div className="row mb-3">
              <div className="col-4">
                <label className="form-label small fw-semibold text-dark">DDD</label>
                <div className="input-group">
                  <span className="input-group-text bg-white border-end-0 text-muted px-2.5">
                    <FaPhone />
                  </span>
                  <input
                    type="text"
                    className="form-control border-start-0 ps-0 form-control-custom text-center"
                    placeholder="11"
                    value={ddd}
                    onChange={(e) => handleDddChange(e.target.value)}
                    disabled={submitting || success}
                    required
                  />
                </div>
              </div>
              <div className="col-8">
                <label className="form-label small fw-semibold text-dark">Celular</label>
                <div className="input-group">
                  <span className="input-group-text bg-white border-end-0 text-muted">
                    <FaPhone />
                  </span>
                  <input
                    type="text"
                    className="form-control border-start-0 ps-0 form-control-custom"
                    placeholder="99999-9999"
                    value={celular}
                    onChange={(e) => handleCelularChange(e.target.value)}
                    disabled={submitting || success}
                    required
                  />
                </div>
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label small fw-semibold text-dark">CPF</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaIdCard />
                </span>
                <input
                  type="text"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="000.000.000-00"
                  value={cpf}
                  onChange={(e) => handleCpfChange(e.target.value)}
                  disabled={submitting || success}
                  required
                />
              </div>
            </div>

            <div className="mb-3">
              <label className="form-label small fw-semibold text-dark">Senha</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaLock />
                </span>
                <input
                  type="password"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="No mínimo 6 caracteres"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={submitting || success}
                  required
                />
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label small fw-semibold text-dark">Confirmar Senha</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaLock />
                </span>
                <input
                  type="password"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="Repita sua senha"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  disabled={submitting || success}
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-primary-custom w-100 py-2.5 fw-semibold d-flex justify-content-center align-items-center mb-3"
              disabled={submitting || success}
            >
              {submitting ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  <span>Cadastrando...</span>
                </>
              ) : (
                <span>Cadastrar</span>
              )}
            </button>

            <div className="text-center">
              <span className="text-muted small">Já possui uma conta? </span>
              <Link to="/login" className="text-primary small fw-semibold text-decoration-none">
                Entrar
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Cadastro;
