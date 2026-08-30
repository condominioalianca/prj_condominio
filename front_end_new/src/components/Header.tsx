import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaBars, FaSignOutAlt, FaUserCog, FaChevronDown } from 'react-icons/fa';
import { getEnvironment, backEndService } from '../services/api';
import type { IUsuario } from '../types';

interface HeaderProps {
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
}

const Header: React.FC<HeaderProps> = ({ sidebarOpen, setSidebarOpen }) => {
  const { user, logout, isAdminOrSindico } = useAuth();
  const [dropdownOpen, setDropdownOpen] = useState<boolean>(false);
  const [hasUnidade, setHasUnidade] = useState<boolean>(true);
  const [showEnvBadge, setShowEnvBadge] = useState<boolean>(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const env = getEnvironment();

  useEffect(() => {
    if (user && user.userId) {
      backEndService.get<IUsuario>(`/usuarios/${user.userId}`)
        .then(userData => {
          if (userData && userData.cpf === '21958651800') {
            setShowEnvBadge(true);
          } else {
            setShowEnvBadge(false);
          }

          if (!isAdminOrSindico()) {
            if (userData && !userData.unidade) {
              setHasUnidade(false);
            } else {
              setHasUnidade(true);
            }
          } else {
            setHasUnidade(true);
          }
        })
        .catch(err => {
          console.error('Erro ao buscar usuário', err);
          setShowEnvBadge(false);
          setHasUnidade(true);
        });
    } else {
      setShowEnvBadge(false);
      setHasUnidade(true);
    }
  }, [user, isAdminOrSindico]);

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
    <header className="header-wrapper d-flex align-items-center justify-content-between">
      <div className="d-flex align-items-center">
        <button 
          className="header-toggle-btn" 
          onClick={() => setSidebarOpen(!sidebarOpen)}
          title="Toggle Menu"
        >
          <FaBars />
        </button>

        {/* Indicador de Ambiente */}
        {showEnvBadge && (
          <div className="ms-3">
            <span 
            style={{
              backgroundColor: env.color + '12',
              color: env.color,
              border: `1px solid ${env.color}25`,
              padding: '4px 10px',
              borderRadius: '20px',
              fontSize: '0.7rem',
              fontWeight: '700',
              letterSpacing: '0.3px',
              display: 'inline-flex',
              alignItems: 'center',
              boxShadow: `0 2px 6px ${env.color}08`
            }}
          >
            <span 
              style={{
                width: '6px',
                height: '6px',
                borderRadius: '50%',
                backgroundColor: env.color,
                marginRight: '6px',
                display: 'inline-block'
              }}
            />
            {env.label}
          </span>
        </div>
        )}
      </div>

      <div className="header-right">
        {user && (
          <div className="header-profile-dropdown" ref={dropdownRef}>
            <button 
              className="header-profile-btn" 
              onClick={() => setDropdownOpen(!dropdownOpen)}
            >
              <div className="d-none d-md-block text-end">
                <p className="header-profile-name">{user.userName}</p>
                {hasUnidade && <p className="header-profile-role">{formatRole(user.roles)}</p>}
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
