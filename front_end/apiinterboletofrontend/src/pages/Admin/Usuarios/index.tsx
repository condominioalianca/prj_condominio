import {AxiosRequestConfig} from 'axios';
import {useEffect, useState} from 'react';

import {SpringPage} from 'types/vendor/spring';

import {Usuario} from "../../../types/usuario";
import {requestBackend} from "../../../utils/requests";
import { AiFillEdit , AiOutlineClose } from "react-icons/ai";


const Usuarios = () => {
    const [page, setPage] = useState<SpringPage<Usuario>>();
    //const [page, setPage] = useState<[Usuario]>();

    useEffect(() => {
        const params: AxiosRequestConfig = {
            url: '/usuarios',

        };

        requestBackend(params).then((response) => {
            setPage(response.data);
        });
    }, []);

    return (

        <div className={"admin-container"}>
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
                {page?.content.map((usuario, key) => {
                    return (
                        <tr>
                            <td>{usuario.idUsuario}</td>
                            <td>{usuario.nomeUsuario}</td>
                            <td>{usuario.nrDocumentoCpf}</td>
                            <td>{usuario.txEmail}</td>
                            <td>{usuario.nrCelularDdd+ "-" + usuario.nrCelular}</td>
                            <td >
                                <ul className={"user-action"} >
                                    <li><a href="#" className="btn btn-info btn-round btn-just-icon btn-sm"><i className={"material-icons"}><AiFillEdit/></i></a></li>
                                    <li><a href="#" className="btn btn-danger btn-round btn-just-icon btn-sm"><i className={"material-icons"}><AiOutlineClose/></i></a></li>

                                </ul>
                            </td>

                        </tr>
                    );

                })}
                </tbody>
            </table>
        </div>


    );
};

export default Usuarios;
