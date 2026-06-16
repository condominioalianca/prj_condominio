import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  FaUsers, 
  FaBuilding, 
  FaKey, 
  FaCog, 
  FaFileInvoiceDollar, 
  FaExchangeAlt, 
  FaCogs, 
  FaHome 
} from 'react-icons/fa';

interface SidebarProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
}

const Sidebar: React.FC<SidebarProps> = ({ sidebarOpen, setSidebarOpen }) => {
  const { hasRole, isAdminOrSindico } = useAuth();

  const handleLinkClick = (): void => {
    if (window.innerWidth < 992) {
      setSidebarOpen(false);
    }
  };

  return (
    <div className={`sidebar-wrapper ${sidebarOpen ? 'mobile-show' : 'collapsed'}`}>
      <div className="sidebar-brand">
        <h4>Nova Aliança</h4>
      </div>
      
      <div className="sidebar-menu">
        <div className="sidebar-menu-title">Menu</div>
        <ul className="sidebar-menu-list">
          <li className="sidebar-menu-item">
            <NavLink 
              to="/dashboard" 
              className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
              onClick={handleLinkClick}
            >
              <FaHome />
              <span>Dashboard</span>
            </NavLink>
          </li>
        </ul>

        {isAdminOrSindico() && (
          <>
            <div className="sidebar-menu-title">Administração</div>
            <ul className="sidebar-menu-list">
              <li className="sidebar-menu-item">
                <NavLink 
                  to="/admin/usuarios" 
                  className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                  onClick={handleLinkClick}
                >
                  <FaUsers />
                  <span>Usuários</span>
                </NavLink>
              </li>
              <li className="sidebar-menu-item">
                <NavLink 
                  to="/admin/unidades" 
                  className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                  onClick={handleLinkClick}
                >
                  <FaBuilding />
                  <span>Unidades</span>
                </NavLink>
              </li>
              <li className="sidebar-menu-item">
                <NavLink 
                  to="/admin/empresas" 
                  className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                  onClick={handleLinkClick}
                >
                  <FaBuilding />
                  <span>Empresas</span>
                </NavLink>
              </li>
              <li className="sidebar-menu-item">
                <NavLink 
                  to="/admin/conciliacao" 
                  className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                  onClick={handleLinkClick}
                >
                  <FaExchangeAlt />
                  <span>Conciliação</span>
                </NavLink>
              </li>
              <li className="sidebar-menu-item">
                <NavLink 
                  to="/admin/cobranca-extra" 
                  className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                  onClick={handleLinkClick}
                >
                  <FaFileInvoiceDollar />
                  <span>Cobrança Extra</span>
                </NavLink>
              </li>
              
              {hasRole('ADMINISTRADOR') && (
                <>
                  <li className="sidebar-menu-item">
                    <NavLink 
                      to="/admin/parametros-sistema" 
                      className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                      onClick={handleLinkClick}
                    >
                      <FaCogs />
                      <span>Parâmetros Sistema</span>
                    </NavLink>
                  </li>
                  <li className="sidebar-menu-item">
                    <NavLink 
                      to="/admin/parametros-perfis" 
                      className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
                      onClick={handleLinkClick}
                    >
                      <FaKey />
                      <span>Parâmetros Perfis</span>
                    </NavLink>
                  </li>
                </>
              )}
            </ul>
          </>
        )}

        <div className="sidebar-menu-title">Configurações</div>
        <ul className="sidebar-menu-list">
          <li className="sidebar-menu-item">
            <NavLink 
              to="/configuracoes" 
              className={({ isActive }) => `sidebar-menu-link ${isActive ? 'active' : ''}`}
              onClick={handleLinkClick}
            >
              <FaCog />
              <span>Minha Conta</span>
            </NavLink>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;
