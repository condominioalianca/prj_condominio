import {BrowserRouter, Redirect, Route, Switch} from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Boletos from "./pages/Admin/Boletos";
import Admin from "./pages/Admin";
import Auth from "./pages/Admin/Auth";
import BoletoHome from "./pages/Home/BoletoHome";

const Routes = () => {
    return (
        <BrowserRouter>

            <Navbar/>
            <Switch>
                <Route path={"/"} exact>
                    <Home/>
                </Route>
                <Route path={"/boleto"} exact={true}>
                    <BoletoHome/>
                </Route>


                <Route path={"/admin/auth"}>
                    <Auth/>
                </Route>
                <Redirect from={"/admin"} to={"/admin/products"} exact />
                <Route path={"/admin"}>
                    <Admin/>
                </Route>

            </Switch>
        </BrowserRouter>
    );
}

export default Routes;