import './styles.css'
import {AxiosRequestConfig} from 'axios';
import {useEffect, useState} from 'react';

import {SpringPage} from 'types/vendor/spring';


import {requestBackend} from "../../../utils/requests";
import { AiFillEdit , AiOutlineClose } from "react-icons/ai";
import {Unidade} from "../../../types/unidade";
import {Link} from "react-router-dom";


const Unidades = () => {

    const [pageTitle] = useState('Unidades');

    useEffect(() => {
        document.title = pageTitle;
    }, [pageTitle]);


    const [pageListUnidades, setPageListUnidades] = useState<SpringPage<Unidade>>();
    useEffect(() => {
        const params: AxiosRequestConfig = {
            url: '/unidade',

        };
        requestBackend(params).then((response) => {
            setPageListUnidades(response.data);
            console.log(pageListUnidades)
        });
    }, []);


    return (


        <div>
           <div className={"list-crud-card-buttons-search"}>
               <Link to={"/admin/unidade/create"}>
                   <button className = {"btn btn-primary text-white crud-btn-add"}>ADICIONAR</button>
               </Link>
               <div className={ "base-card list-bar-container"}>Serch Bar</div>
           </div>
           <div className={"base-card"}>
               <table className=" base-card table table-hover admin-container-table">
                   <thead>
                   <tr>
                       <th scope="col">Id</th>
                       <th scope="col">Num Unidade</th>
                       <th scope="col">Andar</th>
                       <th scope="col">Ação</th>
                   </tr>
                   </thead>
                   <tbody>
                   {pageListUnidades?.content.map((unidade, key) => {
                       return (
                           <tr>
                               <td>{unidade.idUnidade}</td>
                               <td>{unidade.numeroUnidade}</td>
                               <td>{unidade.andarUnidade}</td>

                               <td >
                                   <ul className={"user-action"} >
                                       <li>
                                           <a href={"/admin/unidade/"+unidade.idUnidade} className="btn btn-info btn-round btn-just-icon btn-sm">
                                               <i className={"material-icons"}><AiFillEdit/></i>
                                           </a>
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
}


export default Unidades;
