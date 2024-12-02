import Navbar from "./Navbar";

import "./styles.css"
import {Route, Switch} from "react-router-dom";

import Usuarios from "./Usuarios";
import UsuariosEdit from "./Usuarios/UsuariosEdit/indesx";
import Unidades from "./Unidades";
import UnidadeEdit from "./Unidades/UnidadeEdit";
import PrivateRoute from "../../components/PrivateRoute";
import Boletos from "./Boletos";
import Financeiro from "./Financeiro";
import Contratos from "./Contratos";
import Parametrizacao from "./Parametrizacao";


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
                    {/*Rotas de Para Listagem e Cadastro de Usuarios*/}
                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/users"} exact={true}>
                    <Usuarios/>
                </PrivateRoute>
                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/users/:idUsuario"} exact={true}>
                    <UsuariosEdit/>
                </PrivateRoute>

                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/boleto"} exact={true}>
                    <Boletos/>
                </PrivateRoute>

                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/financeiro"} exact={true}>
                    <Financeiro/>
                </PrivateRoute>

                <PrivateRoute roles={ ['ADMINISTRADOR', 'SINDICO']} path={"/admin/contratos"} exact={true}>
                    <Contratos/>
                </PrivateRoute>

                <PrivateRoute roles={ ['ADMINISTRADOR','SINDICO']} path={"/admin/parametrizacao"} exact={true}>
                    <Parametrizacao/>
                </PrivateRoute>

            </Switch>
        </div>
    </div>
  );
}

export default Admin;