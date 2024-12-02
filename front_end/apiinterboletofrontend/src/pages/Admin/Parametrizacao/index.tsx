import './style.css'
import {useEffect, useState} from "react";
import {SpringPage} from "../../../types/vendor/spring";
import {Boleto} from "../../../types/Boleto/boleto";
import {AxiosRequestConfig} from "axios/index";
import {requestBackend} from "../../../utils/requests";

const Parametrizacao = () => {
    return(
        <div className={"list-container"}>
            <h1>Teste Parametrizacao</h1>
        </div>
    )
}

export default Parametrizacao;