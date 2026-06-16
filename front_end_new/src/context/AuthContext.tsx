import React, { createContext, useContext, useState, useEffect } from 'react';
import { backEndService } from '../services/api';
import type { IUserSession } from '../types';

interface AuthContextType {
  user: IUserSession | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  hasRole: (role: string) => boolean;
  isAdminOrSindico: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<IUserSession | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

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
    } catch (error) {
      logout();
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const logout = (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const hasRole = (role: string): boolean => {
    if (!user) return false;
    // O backend retorna as roles como "ADMINISTRADOR", "SINDICO", "USUARIO"
    return user.roles.some((r) => r.toUpperCase() === role.toUpperCase() || r.toUpperCase() === `ROLE_${role.toUpperCase()}`);
  };

  const isAdminOrSindico = (): boolean => {
    return hasRole('ADMINISTRADOR') || hasRole('SINDICO');
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
      }}
    >
      {children}
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
