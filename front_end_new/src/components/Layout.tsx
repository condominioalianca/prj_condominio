import React, { useState } from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Sidebar from './Sidebar';
import Header from './Header';

const Layout: React.FC = () => {
  const { isAuthenticated, loading } = useAuth();
  // Em mobile (< 992px) o sidebar começa fechado
  const [sidebarOpen, setSidebarOpen] = useState<boolean>(window.innerWidth >= 992);

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center w-100 vh-100 bg-light">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Carregando...</span>
        </div>
      </div>
    );
  }

  // Se não estiver autenticado, redireciona imediatamente para a tela de Login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      {/* Overlay escuro — visível apenas no mobile quando o sidebar está aberto */}
      <div
        className={`sidebar-overlay ${sidebarOpen ? 'active' : ''}`}
        onClick={() => setSidebarOpen(false)}
        aria-hidden="true"
      />
      <Sidebar sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
      <div className="main-layout">
        <Header sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
        <main className="page-container">
          <Outlet />
        </main>
      </div>
    </>
  );
};

export default Layout;
