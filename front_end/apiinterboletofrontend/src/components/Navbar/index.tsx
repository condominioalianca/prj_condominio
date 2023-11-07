import './styles.css';
import 'bootstrap/js/src/collapse.js'
import {Link, NavLink, useHistory} from 'react-router-dom'
import {getTokenData, isAuthenticated, TokenData} from "../../utils/auth";
import {useContext, useEffect} from "react";
import {removeAuthData} from "../../utils/storage";
import {AuthContext} from "../../AuthContext";

type AuthData = {
    authenticated: boolean,
    tokenData?: TokenData
}

const Navbar = () => {
    const history = useHistory();
    const {authContextData, setAuthContextData} = useContext(AuthContext)
    useEffect(() => {
        if (isAuthenticated()) {
            setAuthContextData({
                authenticated: isAuthenticated(),
                tokenData: getTokenData()
            })
        } else (
            setAuthContextData({
                authenticated: isAuthenticated(),

            })
        )
    }, [setAuthContextData]);

    const handleLogoutClick = (event: React.MouseEvent<HTMLAnchorElement>) => {
        event.preventDefault();
        removeAuthData();
        setAuthContextData({
            authenticated: false,
        });
        history.replace('/')
    }


    return (
        <nav className="navbar navbar-expand-md navbar-dark bg-primary main-nav">
            <div className="container-fluid">
                <Link to="/" className="nav-log-text">
                    <h4>DEPOIS TROCAR</h4>
                </Link>
                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#api-inter-navbar"
                    aria-controls="api-inter-navbar"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="api-inter-navbar">
                    <ul className="navbar-nav offset-md-2 main-menu">
                        <li>
                            <NavLink to="/" activeClassName={"active"} exact>HOME</NavLink>
                        </li>
                        <li>
                            <NavLink to="/boleto" activeClassName={"active"}> BOLETOS </NavLink>
                        </li>
                        <li>
                            <NavLink to="/admin" activeClassName={"active"}>ADMIN</NavLink>
                        </li>
                    </ul>
                </div>
                <div className={"nav-login-logout"}>
                    {authContextData.authenticated ? (<><span
                            className={"nav-user-name"}>{authContextData.tokenData?.user_name}</span><Link
                            to={"/admin/auth"} onClick={handleLogoutClick}>Logout</Link></>)
                        : (<Link to={"/admin/auth/login"}>Login</Link>)}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
