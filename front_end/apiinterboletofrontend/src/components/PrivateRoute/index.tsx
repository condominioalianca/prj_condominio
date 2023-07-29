import {Redirect, Route} from 'react-router-dom';
import React from "react";
import {hasAnyRoles, isAuthenticated, Role} from "../../utils/auth";


type Props = {
    children: React.ReactNode;
    path?: string;
    roles?: Role[];

    exact?: boolean
};

const PrivateRoute = ({children, path, roles = [], exact}: Props) => {

    return (
        <Route exact={exact}
            path={path}
            render={({location}) =>


                !isAuthenticated() ?
                    //Se não tiver authenticado Manda para o Login
                    <Redirect to={{
                        pathname: "/admin/auth/login",
                        state: {from: location}
                    }}/> : (
                        //ESTA AUTENTICADO MAS NÂO TEM PERMISSAO ENVIA PARA PRODUCTS
                        !hasAnyRoles(roles) ? <Redirect to="/"/> :

                        //SE TIVER AUTENTICADO E TIVER PERMISAO REINDERIZA A ROTA SOLICITADA
                            <> {children}</>
                    )
            }
        />
    );
};

export default PrivateRoute;

