import Navbar from "./Navbar";

import "./styles.css"
import {Route, Switch} from "react-router-dom";

import Usuarios from "./Usuarios";
import UsuariosEdit from "./Usuarios/UsuariosEdit/indesx";
import Unidades from "./Unidades";
import UnidadeEdit from "./Unidades/UnidadeEdit";
import PrivateRoute from "../../components/PrivateRoute";
import Boletos from "./Boletos";


const Admin = () => {
  return (
    <div className={"admin-container"}>
       <Navbar/>
        <div className={"admin-content"}>
            <Switch>
                {/*Rotas de Para Listagem e Cadastro de Unidade*/}

                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/unidade"} exact={true}>
                    <Unidades/>
                </PrivateRoute>
                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']}  path={"/admin/unidade/:idUnidade"} exact={true}>
                    <UnidadeEdit/>
                </PrivateRoute>

                {/*Rotas de Para Listagem e Cadastro de Contratos*/}

                <Route path={"/admin/Contratos"}>
                    <h1>Contratos Crud</h1>
                </Route>

                {/*Rotas de Para Listagem e Cadastro de Usuarios*/}
                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/users"} exact={true}>
                    <Usuarios/>
                </PrivateRoute>
                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/users/:idUsuario"} exact={true}>
                    <UsuariosEdit/>
                </PrivateRoute>

                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/boletos"} exact={true}>
                    <Boletos/>
                </PrivateRoute>

            </Switch>
        </div>
    </div>
  );
}

export default Admin;