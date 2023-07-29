import {NavLink} from "react-router-dom";
import "./styles.css"
import {hasAnyRoles} from "../../../utils/auth";

const Navbar = () => {
    return (
        <nav className={"admin-nav-container"}>
            <ul className={"admin-nav-list"} style={{flexDirection: "column"}}>


                {hasAnyRoles(['ADMINISTRADOR', 'SINDICO']) && (
                    <li>
                        <NavLink to={"/admin/unidade"} className={"admin-nav-item"}>
                            <p>Unidades</p>
                        </NavLink>
                    </li>)
                }
                {hasAnyRoles(['ADMINISTRADOR', 'SINDICO']) && (
                    <li>
                        <NavLink to={"/admin/users"} className={"admin-nav-item"}>
                            <p>Usuário</p>
                        </NavLink>
                    </li>)
                }
                {hasAnyRoles(['USUARIO', 'SINDICO']) && (
                    <li>
                        <NavLink to={"/admin/boletos"} className={"admin-nav-item"}>
                            <p>Boleto</p>
                        </NavLink>
                    </li>)
                }


                <li>
                    <NavLink to={"/admin/financeiro"} className={"admin-nav-item"}>
                        <p>Financeiro</p>
                    </NavLink>
                </li>

                <li>
                    <NavLink to={"/admin/contrato"} className={"admin-nav-item"}>
                        <p>Contratos</p>
                    </NavLink>
                </li>

            </ul>
        </nav>
    );
}
export default Navbar;