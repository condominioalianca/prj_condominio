import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Configuracoes from './pages/Configuracoes';

// Páginas de Administração
import Usuarios from './pages/Admin/Usuarios';
import Unidades from './pages/Admin/Unidades';
import Empresas from './pages/Admin/Empresas';
import Conciliacao from './pages/Admin/Conciliacao';
import ParametrosSistema from './pages/Admin/ParametrosSistema';
import ParametrosPerfis from './pages/Admin/ParametrosPerfis';
import CobrancaExtra from './pages/Admin/CobrancaExtra';

// Componente para proteger rotas baseadas em Papel/Role
interface RoleRouteProps {
  requiredRole?: string;
  adminOrSindicoRequired?: boolean;
  children: React.ReactElement;
}

const RoleRoute: React.FC<RoleRouteProps> = ({ 
  requiredRole, 
  adminOrSindicoRequired, 
  children 
}) => {
  const { isAuthenticated, hasRole, isAdminOrSindico, loading } = useAuth();

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center w-100 vh-100 bg-light">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Carregando...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (adminOrSindicoRequired && !isAdminOrSindico()) {
    return <Navigate to="/dashboard" replace />;
  }

  if (requiredRole && !hasRole(requiredRole)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Rota Pública */}
          <Route path="/login" element={<Login />} />

          {/* Rotas Protegidas de Layout */}
          <Route path="/" element={<Layout />}>
            {/* Rota Padrão */}
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            
            {/* Rotas de Administração Gerais (Sindico ou Admin) */}
            <Route 
              path="admin/usuarios" 
              element={
                <RoleRoute adminOrSindicoRequired>
                  <Usuarios />
                </RoleRoute>
              } 
            />
            <Route 
              path="admin/unidades" 
              element={
                <RoleRoute adminOrSindicoRequired>
                  <Unidades />
                </RoleRoute>
              } 
            />
            <Route 
              path="admin/empresas" 
              element={
                <RoleRoute adminOrSindicoRequired>
                  <Empresas />
                </RoleRoute>
              } 
            />
            <Route 
              path="admin/conciliacao" 
              element={
                <RoleRoute adminOrSindicoRequired>
                  <Conciliacao />
                </RoleRoute>
              } 
            />
            <Route 
              path="admin/cobranca-extra" 
              element={
                <RoleRoute adminOrSindicoRequired>
                  <CobrancaExtra />
                </RoleRoute>
              } 
            />

            {/* Rotas de Administração Restritas (Apenas Administrador) */}
            <Route 
              path="admin/parametros-sistema" 
              element={
                <RoleRoute requiredRole="ADMINISTRADOR">
                  <ParametrosSistema />
                </RoleRoute>
              } 
            />
            <Route 
              path="admin/parametros-perfis" 
              element={
                <RoleRoute requiredRole="ADMINISTRADOR">
                  <ParametrosPerfis />
                </RoleRoute>
              } 
            />

            {/* Rota de Configurações Geral */}
            <Route path="configuracoes" element={<Configuracoes />} />
          </Route>

          {/* Rota de Fallback */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
