import './style.css'
import {useEffect, useState} from "react";
import {SpringPage} from "../../../types/vendor/spring";
import {Boleto} from "../../../types/Boleto/boleto";
import {AxiosRequestConfig} from "axios/index";
import {requestBackend} from "../../../utils/requests";

const Financeiro = () => {
    return(
        <div className={"list-container"}>
            <h1>Teste Financeiro</h1>
        </div>
    )
}

export default Financeiro;