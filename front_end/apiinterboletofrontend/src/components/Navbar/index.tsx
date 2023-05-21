import './styles.css';
import 'bootstrap/js/src/collapse.js'
import {Link,NavLink} from 'react-router-dom'

function Navbar() {
    return (
        <nav className="navbar navbar-expand-md navbar-dark bg-primary main-nav">
            <div className="container-fluid">
                <Link to="/" className="nav-log-text">
                    <h4>Condominio Nova Alianca</h4>
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
                            <NavLink to="/boletos" activeClassName={"active"}> BOLETOS </NavLink>
                        </li>
                        <li>
                            <NavLink to="/admin" activeClassName={"active"}>ADMIN</NavLink>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
