import './styles.css'
import {ReactComponent as AuthImage} from 'assets/images/auth-image.svg';
import {Route, Switch} from "react-router-dom";
import Login from "./Login/index";
const Auth =() =>{
    return (
        <div className={"auth-container"}>
            <div className={"auth-banner-container"}>
                <h1>Acompanhe as Novidades do Condominio</h1>
                <p>Recupere Boleos, valide o financeiro do Condominio e da sua unidade</p>
                <AuthImage/>
            </div>
            <div className={"auth-form-container"}>
                <Switch>
                    <Route path={"/admin/auth/login"}>
                        <Login/>
                    </Route>
                    <Route path={"/admin/auth/signup"}>
                        <h1>Card de signup</h1>
                    </Route>
                    <Route path={"/admin/auth/recover"}>
                        <h1>Card de recover</h1>
                    </Route>
                </Switch>

            </div>
        </div>


    );
}

export default Auth;