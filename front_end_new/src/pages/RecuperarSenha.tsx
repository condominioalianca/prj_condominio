import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FaEnvelope, FaLock, FaBuilding, FaKey, FaArrowLeft } from 'react-icons/fa';
import { backEndService } from '../services/api';

const RecuperarSenha: React.FC = () => {
  const [step, setStep] = useState<1 | 2>(1);
  const [email, setEmail] = useState<string>('');
  const [code, setCode] = useState<string>('');
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleRequestCode = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!email) {
      setError('Por favor, informe seu e-mail.');
      return;
    }

    setSubmitting(true);
    try {
      await backEndService.post('/auth/password-reset/request', { email });
      setSuccess('Código de 8 dígitos enviado para o seu e-mail!');
      setStep(2);
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Erro ao enviar código de segurança. Verifique se o e-mail está cadastrado.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleConfirmReset = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!code || !newPassword || !confirmPassword) {
      setError('Por favor, preencha todos os campos.');
      return;
    }

    if (code.length !== 8) {
      setError('O código de recuperação deve ter exatamente 8 dígitos.');
      return;
    }

    if (newPassword.length < 6) {
      setError('A nova senha deve ter no mínimo 6 caracteres.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('A nova senha e a confirmação não conferem.');
      return;
    }

    setSubmitting(true);
    try {
      await backEndService.post('/auth/password-reset/confirm', {
        email,
        code,
        newPassword,
      });
      setSuccess('Senha redefinida com sucesso!');
      setTimeout(() => {
        navigate('/login');
      }, 3000);
    } catch (err: any) {
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Não foi possível redefinir a senha. Código inválido ou expirado.');
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
            <h3 className="mb-1 fw-bold" style={{ fontFamily: 'var(--font-heading)' }}>Recuperar Senha</h3>
            <p className="text-muted small">
              {step === 1 
                ? 'Insira seu e-mail para receber o código de acesso' 
                : 'Insira o código de 8 dígitos e defina sua nova senha'
              }
            </p>
          </div>

          {error && (
            <div className="alert alert-danger py-2 px-3 small border-0 mb-4" role="alert">
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 px-3 small border-0 mb-4" role="alert">
              {success}
            </div>
          )}

          {step === 1 ? (
            <form onSubmit={handleRequestCode}>
              <div className="mb-4">
                <label className="form-label small fw-semibold text-dark">E-mail Cadastrado</label>
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

              <button
                type="submit"
                className="btn btn-primary btn-primary-custom w-100 py-2.5 fw-semibold d-flex justify-content-center align-items-center mb-3"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    <span>Enviando...</span>
                  </>
                ) : (
                  <span>Enviar Código de Segurança</span>
                )}
              </button>

              <div className="text-center">
                <Link to="/login" className="text-decoration-none small text-secondary fw-semibold d-inline-flex align-items-center">
                  <FaArrowLeft className="me-2" /> Voltar ao Login
                </Link>
              </div>
            </form>
          ) : (
            <form onSubmit={handleConfirmReset}>
              <div className="mb-3">
                <label className="form-label small fw-semibold text-dark">Código de 8 Dígitos</label>
                <div className="input-group">
                  <span className="input-group-text bg-white border-end-0 text-muted">
                    <FaKey />
                  </span>
                  <input
                    type="text"
                    maxLength={8}
                    className="form-control border-start-0 ps-0 form-control-custom text-center fw-bold"
                    placeholder="00000000"
                    value={code}
                    onChange={(e) => setCode(e.target.value.replace(/\D/g, ''))}
                    disabled={submitting}
                    required
                  />
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label small fw-semibold text-dark">Nova Senha</label>
                <div className="input-group">
                  <span className="input-group-text bg-white border-end-0 text-muted">
                    <FaLock />
                  </span>
                  <input
                    type="password"
                    className="form-control border-start-0 ps-0 form-control-custom"
                    placeholder="Mínimo de 6 caracteres"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    disabled={submitting}
                    required
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="form-label small fw-semibold text-dark">Confirmar Nova Senha</label>
                <div className="input-group">
                  <span className="input-group-text bg-white border-end-0 text-muted">
                    <FaLock />
                  </span>
                  <input
                    type="password"
                    className="form-control border-start-0 ps-0 form-control-custom"
                    placeholder="Repita a nova senha"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    disabled={submitting}
                    required
                  />
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-primary btn-primary-custom w-100 py-2.5 fw-semibold d-flex justify-content-center align-items-center mb-3"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    <span>Redefinindo Senha...</span>
                  </>
                ) : (
                  <span>Redefinir Senha</span>
                )}
              </button>

              <div className="text-center">
                <button
                  type="button"
                  onClick={() => setStep(1)}
                  className="btn btn-link text-decoration-none small text-secondary fw-semibold d-inline-flex align-items-center p-0"
                  disabled={submitting}
                >
                  <FaArrowLeft className="me-2" /> Solicitar novo código
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default RecuperarSenha;
