import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaBars, FaSignOutAlt, FaUserCog, FaChevronDown } from 'react-icons/fa';

interface HeaderProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
}

const Header: React.FC<HeaderProps> = ({ sidebarOpen, setSidebarOpen }) => {
  const { user, logout } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent): void => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleLogout = (): void => {
    logout();
    navigate('/login');
  };

  // Obtém as iniciais do nome do usuário
  const getInitials = (name: string): string => {
    if (!name) return 'U';
    const parts = name.split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return parts[0][0].toUpperCase();
  };

  // Formata a exibição do papel (ex: ADMINISTRADOR -> Administrador)
  const formatRole = (roles: string[]): string => {
    if (!roles || roles.length === 0) return 'Morador';
    const mainRole = roles[0];
    if (mainRole === 'ADMINISTRADOR') return 'Administrador';
    if (mainRole === 'SINDICO') return 'Síndico';
    return 'Morador';
  };

  return (
    <header className="header-wrapper">
      <button 
        className="header-toggle-btn" 
        onClick={() => setSidebarOpen(!sidebarOpen)}
        title="Toggle Menu"
      >
        <FaBars />
      </button>

      <div className="header-right">
        {user && (
          <div className="header-profile-dropdown" ref={dropdownRef}>
            <button 
              className="header-profile-btn" 
              onClick={() => setDropdownOpen(!dropdownOpen)}
            >
              <div className="d-none d-md-block text-end">
                <p className="header-profile-name">{user.userName}</p>
                <p className="header-profile-role">{formatRole(user.roles)}</p>
              </div>
              <div className="header-profile-avatar">
                {getInitials(user.userName)}
              </div>
              <FaChevronDown style={{ fontSize: '0.8rem', color: '#64748b' }} />
            </button>

            {dropdownOpen && (
              <div className="dropdown-menu show position-absolute end-0 mt-2 shadow-sm border border-light" style={{ width: '200px' }}>
                <Link 
                  to="/configuracoes" 
                  className="dropdown-item d-flex align-items-center py-2"
                  onClick={() => setDropdownOpen(false)}
                >
                  <FaUserCog className="me-2 text-muted" />
                  <span>Minha Conta</span>
                </Link>
                <div className="dropdown-divider"></div>
                <button 
                  className="dropdown-item d-flex align-items-center text-danger py-2" 
                  onClick={handleLogout}
                >
                  <FaSignOutAlt className="me-2" />
                  <span>Sair</span>
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
