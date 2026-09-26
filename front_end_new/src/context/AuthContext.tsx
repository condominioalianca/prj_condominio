import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { backEndService } from '../services/api';
import type { IUserSession } from '../types';
import { useInactivityTimeout } from '../hooks/useInactivityTimeout';

// Sessão expira após 10 minutos de inatividade
const INACTIVITY_TIMEOUT_MS = 10 * 60 * 1000;
// Aviso aparece 2 minutos antes do logout
const INACTIVITY_WARNING_MS = 2 * 60 * 1000;

interface AuthContextType {
  user: IUserSession | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
  isAdminOrSindico: () => boolean;
  hasPerfilAtrelado: () => boolean;
  hasAnyRole: (roles: string[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<IUserSession | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [showTimeoutWarning, setShowTimeoutWarning] = useState<boolean>(false);
  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUserJson = localStorage.getItem('user');

    if (storedToken && storedUserJson) {
      try {
        const storedUser = JSON.parse(storedUserJson) as Omit<IUserSession, 'token'>;
        setUser({
          ...storedUser,
          token: storedToken,
        });
      } catch (e) {
        // Limpar em caso de erro de parsing
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const logout = useCallback((): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setShowTimeoutWarning(false);
    navigate('/login', { replace: true });
  }, [navigate]);

  const login = async (email: string, password: string): Promise<void> => {
    setLoading(true);
    try {
      const data = await backEndService.login(email, password);

      const sessionUser: IUserSession = {
        userId: data.userId,
        userName: data.userName,
        email: email,
        roles: data.roles,
        token: data.access_token,
      };

      localStorage.setItem('token', data.access_token);
      localStorage.setItem('user', JSON.stringify({
        userId: data.userId,
        userName: data.userName,
        email: email,
        roles: data.roles,
      }));

      setUser(sessionUser);
      setShowTimeoutWarning(false);
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Hook de inatividade — só ativo quando autenticado
  useInactivityTimeout({
    timeoutMs: INACTIVITY_TIMEOUT_MS,
    warningMs: INACTIVITY_WARNING_MS,
    enabled: !!user,
    onWarning: () => setShowTimeoutWarning(true),
    onTimeout: () => {
      setShowTimeoutWarning(false);
      logout();
    },
  });

  const hasRole = (role: string): boolean => {
    if (!user) return false;
    // O backend retorna as roles como "ADMINISTRADOR", "SINDICO", "USUARIO"
    return user.roles.some((r) => r.toUpperCase() === role.toUpperCase() || r.toUpperCase() === `ROLE_${role.toUpperCase()}`);
  };

  const isAdminOrSindico = (): boolean => {
    return hasRole('ADMINISTRADOR') || hasRole('SINDICO');
  };

  const hasPerfilAtrelado = (): boolean => {
    return !!user && Array.isArray(user.roles) && user.roles.length > 0;
  };

  const hasAnyRole = (roles: string[]): boolean => {
    if (!user || !user.roles || user.roles.length === 0) return false;
    return roles.some((r) => hasRole(r));
  };

  const isAuthenticated = !!user;

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        loading,
        login,
        logout,
        hasRole,
        isAdminOrSindico,
        hasPerfilAtrelado,
        hasAnyRole,
      }}
    >
      {children}

      {/* Toast de aviso de sessão — aparece 2 min antes do logout automático */}
      {showTimeoutWarning && isAuthenticated && (
        <div className="session-warning-toast" role="alert">
          <span>⚠️ Sua sessão expira em <strong>2 minutos</strong> por inatividade.</span>
          <button
            onClick={() => setShowTimeoutWarning(false)}
            aria-label="Manter sessão ativa"
          >
            Continuar
          </button>
        </div>
      )}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth deve ser utilizado dentro de um AuthProvider');
  }
  return context;
};
