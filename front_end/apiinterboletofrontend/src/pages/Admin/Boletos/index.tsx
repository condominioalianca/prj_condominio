import './style.css'
import {useEffect, useState} from "react";
import {SpringPage} from "../../../types/vendor/spring";
import {Boleto} from "../../../types/Boleto/boleto";
import {AxiosRequestConfig} from "axios/index";
import {requestBackend} from "../../../utils/requests";
import {AiFillEdit} from "react-icons/ai";

const Boletos = () => {

    const [pageListBoletos, setPageListBoletos] = useState<SpringPage<Boleto>>();

    useEffect(() => {
        const params: AxiosRequestConfig = {
            url: "/boleto",

        };
        requestBackend(params).then((response) => {
            setPageListBoletos(response.data);
            console.log(pageListBoletos)
        });
    }, []);

    return (

        <div className={"list-container"}>
            <div className={"list-crud-card-buttons-search"}>
                <div className={ "base-card list-bar-container"}>Serch Bar</div>
            </div>
            <div >
                <table className="base-card table table-hover admin-container-table">
                    <thead>
                    <tr>
                        <th scope="col">Situacao</th>
                        <th scope="col">Mes</th>
                        <th scope="col">Valor Devido</th>
                        <th scope="col">Valor Pago</th>
                        <th scope="col">Acao</th>

                    </tr>
                    </thead>
                    <tbody>
                    {pageListBoletos?.content.map((boleto, key) => {
                        return (
                            <tr>
                                <td>{boleto.txSituacao}</td>
                                <td>{boleto.mesReferencia}</td>
                                <td>{boleto.valor.toFixed(2)}</td>
                                <td>{boleto.valorPagamento.toFixed(2)}</td>
                                <td >
                                    <ul className={"user-action"} >
                                        <li>
                                            <a href={'#teste'} className="btn btn-info btn-round btn-just-icon btn-sm">
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


export default Boletos;