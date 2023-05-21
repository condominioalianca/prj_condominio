import Navbar from "./Navbar";

import "./styles.css"
import {Route, Switch} from "react-router-dom";

import Usuarios from "./Usuarios";


const Admin = () => {
  return (
    <div className={"admin-container"}>
       <Navbar/>
        <div className={"admin-content"}>
            <Switch>
                <Route path={"/admin/Unidades"}>
                    <h1>Unidades Crud</h1>
                </Route>
                <Route path={"/admin/Contratos"}>
                    <h1>Contratos Crud</h1>
                </Route>
                <Route path={"/admin/users"}>
                    <Usuarios/>
                </Route>
            </Switch>
        </div>
    </div>
  );
}

export default Admin;