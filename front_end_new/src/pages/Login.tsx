import React, { useState } from 'react';
import { useNavigate, Navigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaEnvelope, FaLock, FaBuilding } from 'react-icons/fa';

const Login: React.FC = () => {
  const { login, isAuthenticated } = useAuth();
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const navigate = useNavigate();

  // Se já estiver logado, redireciona direto
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError(null);
    
    if (!email || !password) {
      setError('Por favor, informe seu e-mail e senha.');
      return;
    }

    setSubmitting(true);
    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.status === 400) {
        setError('E-mail ou senha incorretos.');
      } else {
        setError('Não foi possível conectar ao servidor. Verifique o backend.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center w-100 vh-100 bg-light px-3">
      <div className="card shadow border-0" style={{ width: '450px', borderRadius: '12px' }}>
        <div className="card-body p-5">
          <div className="text-center mb-4">
            <div className="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-circle mb-3" style={{ width: '60px', height: '60px', fontSize: '1.5rem' }}>
              <FaBuilding />
            </div>
            <h3 className="mb-1 fw-bold" style={{ fontFamily: 'var(--font-heading)' }}>Condomínio Nova Aliança</h3>
            <p className="text-muted small">Insira suas credenciais para acessar o painel</p>
          </div>

          {error && (
            <div className="alert alert-danger py-2 px-3 small border-0 mb-4" role="alert">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
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
                  disabled={submitting}
                  required
                />
              </div>
            </div>

            <div className="mb-4">
              <label className="form-label small fw-semibold text-dark">Senha</label>
              <div className="input-group">
                <span className="input-group-text bg-white border-end-0 text-muted">
                  <FaLock />
                </span>
                <input
                  type="password"
                  className="form-control border-start-0 ps-0 form-control-custom"
                  placeholder="Sua senha"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={submitting}
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-primary-custom w-100 py-2.5 fw-semibold d-flex justify-content-center align-items-center"
              disabled={submitting}
            >
              {submitting ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  <span>Autenticando...</span>
                </>
              ) : (
                <span>Entrar</span>
              )}
            </button>

            <div className="text-center mt-3">
              <span className="text-muted small">Não tem uma conta? </span>
              <Link to="/cadastro" className="text-primary small fw-semibold text-decoration-none">
                Cadastre-se
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
