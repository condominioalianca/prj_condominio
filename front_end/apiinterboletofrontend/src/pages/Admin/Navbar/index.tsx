import { NavLink } from "react-router-dom";
import "./styles.css"

const Navbar = () => {
    return (
        <nav className={"admin-nav-container"}>
            <ul className={"admin-nav-list"} style={{flexDirection: "column"}} >
                <li>
                    <NavLink to={"/admin/Unidades"} className={"admin-nav-item"}>
                        <p>Unidades</p>
                    </NavLink>
                </li>
                <li>
                    <NavLink to={"/admin/Contratos"} className={"admin-nav-item"}>
                        <p>Contratos</p>
                    </NavLink>
                </li>
                <li>
                    <NavLink to={"/admin/users"} className={"admin-nav-item"}>
                        <p>Usuário</p>
                    </NavLink>
                </li>
            </ul>
        </nav>
    );
}
export default Navbar;