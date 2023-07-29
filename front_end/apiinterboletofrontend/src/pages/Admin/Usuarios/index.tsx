import "./styles.css"
import {AxiosRequestConfig} from 'axios';
import {useEffect, useState} from 'react';
import {SpringPage} from 'types/vendor/spring';
import {Usuario} from "../../../types/usuario";
import {requestBackend} from "../../../utils/requests";
import { AiFillEdit , AiOutlineClose } from "react-icons/ai";
import {useForm} from "react-hook-form";
import {Unidade} from "../../../types/unidade";
import {Link} from "react-router-dom";


type FormData = {
    nomeCompleto: string;
    celular: number;
    ddd: number
    email: string;
    senha: string;
    cep: number;
    endereco: string;
    complemento : string;
    cidade: string
    estado: string;
    unidade : number;
    ativo : boolean;


}

const Usuarios = () => {


    const [pageTableUsuario, setPageUsuarioTable] = useState<SpringPage<Usuario>>();


    useEffect(() => {
        const paramsUsuarios: AxiosRequestConfig = {
            url: '/usuarios',
        };

        requestBackend(paramsUsuarios).then((response) => {
            setPageUsuarioTable(response.data);
        });



    }, []);

    // const onSubmit = (usuario: Usuario) => {
    //     const config : AxiosRequestConfig ={
    //         method : 'POST',
    //         url : '/usuarios/save',
    //         data: usuario,
    //         withCredentials: true
    //     }
    //
    //     requestBackend(config)
    //         .then(response => {
    //
    //             console.log('SUCESSO', response);
    //         })
    //         .catch(error => {
    //             setHasError(true);
    //             console.log('ERRO', error)
    //         })
    //
    //     console.log(usuario)
    //
    // }

    return (

        <div className={"list-container"}>
            <div className={"list-crud-card-buttons-search"}>
                <Link to={"/admin/users/create"}>
                    <button className = {"btn btn-primary text-white crud-btn-add"}>ADICIONAR</button>
                </Link>
                <div className={ "base-card list-bar-container"}>Serch Bar</div>
            </div>

            <div >
                <table className="table table-hover admin-container-table">
                    <thead>
                    <tr>
                        <th scope="col">Id Usuario</th>
                        <th scope="col">Nome Usuario</th>
                        <th scope="col">CPF</th>
                        <th scope="col">Email</th>
                        <th scope="col">Celular</th>
                        <th scope="col">Ação</th>
                    </tr>
                    </thead>
                    <tbody>
                    {pageTableUsuario?.content.map((usuario, key) => {
                        return (
                            <tr>
                                <td>{usuario.id}</td>
                                <td>{usuario.nomeUsuario}</td>
                                <td>{usuario.cpf}</td>
                                <td>{usuario.email}</td>
                                <td>{usuario.nrCelularDdd+ "-" + usuario.nrCelular}</td>
                                <td >
                                    <ul className={"user-action"} >
                                        <li>
                                            <a href={"/admin/users/"+ usuario.id} className="btn btn-info btn-round btn-just-icon btn-sm"><i className={"material-icons"}><AiFillEdit/></i></a>
                                        </li>
                                    </ul>
                                </td>

                            </tr>
                        );

                    })}
                    </tbody>
                </table>
            </div>



        </div>


    );
};

export default Usuarios;
