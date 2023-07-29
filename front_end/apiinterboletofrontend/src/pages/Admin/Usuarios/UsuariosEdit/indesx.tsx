import './styles.css'

import {Usuario} from "../../../../types/usuario";
import {AxiosRequestConfig} from "axios";
import {requestBackend} from "../../../../utils/requests";
import {useEffect, useState} from "react";
import {useForm} from "react-hook-form";
import {SpringPage} from "../../../../types/vendor/spring";
import {Unidade} from "../../../../types/unidade";
import {Link, useHistory, useParams} from "react-router-dom";


type UrlParams = {
    idUsuario : string
}
const UsuariosEdit = () => {

    const {from} =  {from : {pathname: '/admin/users'}};
    const history = useHistory();
    const [pageTitle] = useState('Cadastro Usuario');
    const {idUsuario} = useParams<UrlParams>();
    const [hasError, setHasError] = useState(false);
    const {register, handleSubmit, formState: {errors}, setValue} = useForm<Usuario>();
    const [pageListUnidades, setPageListUnidades] = useState<SpringPage<Unidade>>();
    const  isEditing = idUsuario !== 'create'
    const paramsUnidade: AxiosRequestConfig = {url: '/unidade'};



    useEffect(() => {
        document.title = pageTitle;
    }, [pageTitle]);


    useEffect(() => {
        if (isEditing){
            requestBackend({url:`/usuarios/${idUsuario}`,method: "GET"})
                .then(response => {

                    const usuario = response.data as Usuario;
                    setValue("nomeUsuario", usuario.nomeUsuario);
                    setValue("id", usuario.id);
                    setValue("cpf", usuario.cpf);
                    setValue("tipoPessoa", usuario.tipoPessoa);
                    setValue("email", usuario.email);
                    setValue("nrCelular", usuario.nrCelular);
                    setValue("nrCelularDdd", usuario.nrCelularDdd);
                    setValue("unidade", usuario.unidade);
                    setValue("endereco", usuario.endereco);
                    setValue("ativo", usuario.ativo);
                    setValue("enviaBoleto", usuario.enviaBoleto);
                    setValue("enviaSms", usuario.enviaSms);

                    console.log('SUCESSO response', response);
                })
                .catch(error => {
                    setHasError(true);
                    console.log('ERRO', error)
                })
        }



        requestBackend(paramsUnidade).then((response) => {
            setPageListUnidades(response.data);
        });


    }, [isEditing,idUsuario,setValue])

    const onSubmit = (usuario: Usuario) => {
        const config : AxiosRequestConfig ={
            method : isEditing ? 'PUT':'POST',
            url : isEditing ? `'/usuarios/${idUsuario}'`: '/usuarios/save',
            data: usuario,
            withCredentials: true
        }

        requestBackend(config)
            .then(response => {
                history.replace(from)
                console.log('SUCESSO response post', response);
            })
            .catch(error => {
                setHasError(true);
                console.log('ERRO', error)
            })

        console.log(usuario)

    }


    return (
        <div className={"crud-form"}>
        <div className={"usuario-container"}>
            <form onSubmit={handleSubmit(onSubmit)}>
                <div className="form-row group-senha-email">
                    <div className="form-group col-sm-6 inputs">
                        <label htmlFor="inputNomeCompleto">Nome Completo</label>
                        <input {...register("nomeUsuario",
                            {
                                required: 'Campo Obrigatorio'
                            })}
                               type="text" className={`form-control ${errors.nomeUsuario ? 'is-invalid' : ''}`}
                               id="inputNomeCompleto" placeholder="Nome" name={"nomeUsuario"}/>
                        <div className={"invalid-feedback d-block"}>{errors.nomeUsuario?.message}</div>

                    </div>
                    <div className="form-group col-sm-1 inputs">
                        <label htmlFor="inputDDD">DDD</label>
                        <input
                            {...register("nrCelularDdd",
                                {
                                    required: 'Campo Obrigatorio'
                                })}
                            type="text" className={`form-control ${errors.nrCelularDdd ? 'is-invalid' : ''}`}
                            id="inputDDD" placeholder="DDD" name={"nrCelularDdd"}/>
                        <div className={"invalid-feedback d-block"}>{errors.nrCelularDdd?.message}</div>

                    </div>
                    <div className="form-group col-sm-2 inputs">
                        <label htmlFor="inputCelular">Celular</label>
                        <input
                            {...register("nrCelular",
                                {
                                    required: 'Campo Obrigatorio'
                                })}
                            type="text" className={`form-control ${errors.nrCelular ? 'is-invalid' : ''}`}
                            id="inputCelular" placeholder="Celular" name={"nrCelular"}/>
                        <div className={"invalid-feedback d-block"}>{errors.nrCelular?.message}</div>

                    </div>
                </div>

                <div className="form-row group-senha-email">
                    <div className="form-group col-md-3 inputs">
                        <label htmlFor="inputEmail4">CPF</label>
                        <input
                            {...register("cpf", //o register esta vindo do useForm  , o nome do campo é de acordo com o type FormData declarado
                                {
                                    required: 'Campo Obrigatorio', //mensagem no campo requerido que não está preenchido

                                })}

                            type="text" className={`form-control ${errors.cpf ? 'is-invalid' : ''}`} id="inputCpf"
                            placeholder="CPF" name={"cpf"}/>
                        <div className={"invalid-feedback d-block"}>{errors.cpf?.message}</div>

                    </div>
                    <div className="form-group col-md-5 inputs">
                        <label htmlFor="inputEmail4">Email</label>
                        <input
                            {...register("email", //o register esta vindo do useForm  , o nome do campo é de acordo com o type FormData declarado
                                {
                                    required: 'Campo Obrigatorio', //mensagem no campo requerido que não está preenchido
                                    pattern: {
                                        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i, //partener de validação de email
                                        message: 'Email Inválido' //mensagem para caso oq esta sendo digitado no input esteja errado
                                    }
                                })}

                            type="text" className={`form-control ${errors.email ? 'is-invalid' : ''}`} id="inputEmail4"
                            placeholder="Email" name={"email"}/>
                        <div className={"invalid-feedback d-block"}>{errors.email?.message}</div>

                    </div>
                    <div className="form-group col-md-1 inputs">
                        <label htmlFor="inputEstado">Tipo Pessoa</label>
                        <select
                            {...register("tipoPessoa",
                                {
                                    required: 'Campo Obrigatorio'
                                })}

                            id="inputEstado" className={`form-control ${errors.tipoPessoa ? 'is-invalid' : ''}`}
                            name={"tipoPessoa"}>
                            <option selected>Escolher...</option>
                            <option>F</option>
                            <option>J</option>
                        </select>
                        <div className={"invalid-feedback d-block"}>{errors.tipoPessoa?.message}</div>

                    </div>

                </div>

                <div className={"group-endereco"}>
                    <div className="col-md-2 inputs">
                        <label htmlFor="inputCEP">CEP</label>
                        <input
                            {...register("endereco.txCep"
                            )}
                            type="text" className={`form-control ${errors.endereco?.txCep ? 'is-invalid' : ''}`}
                            id="inputCEP" name={"endereco.txCep"}/>
                        <div className={"invalid-feedback d-block"}>{errors.endereco?.txCep?.message}</div>

                    </div>
                    <div className=" col-md-4 inputs">
                        <label htmlFor="inputAddress">Endereço</label>
                        <input
                            {...register("endereco.txEndereco",
                                {
                                    required: 'Campo Obrigatorio'
                                })}
                            type="text" className={`form-control ${errors.endereco?.txEndereco ? 'is-invalid' : ''}`}
                            id="inputAddress" placeholder="Endereço Completo" name={"endereco.txEndereco"}/>
                        <div className={"invalid-feedback d-block"}>{errors.endereco?.txEndereco?.message}</div>

                    </div>
                    <div className=" col-md-4 inputs">
                        <label htmlFor="inputAddress2">Complemento</label>
                        <input
                            {...register("endereco.txEnderecoComplemento",
                                {
                                    required: 'Campo Obrigatorio'
                                })}
                            type="text"
                            className={`form-control ${errors.endereco?.txEnderecoComplemento ? 'is-invalid' : ''}`}
                            id="inputAddress2"
                            placeholder="Apartamento, hotel, casa, etc." name={"endereco.txEnderecoComplemento"}/>
                        <div
                            className={"invalid-feedback d-block"}>{errors.endereco?.txEnderecoComplemento?.message}</div>

                    </div>
                </div>

                <div className="form-row group-endereco">
                    <div className="form-group col-md-3 inputs">
                        <label htmlFor="inputCity">Cidade</label>
                        <input
                            {...register("endereco.txCidade",
                                {
                                    required: 'Campo Obrigatorio'
                                })}
                            type="text" className={`form-control ${errors.endereco?.txCidade ? 'is-invalid' : ''}`}
                            id="inputCity" name={"endereco.txCidade"}/>
                        <div className={"invalid-feedback d-block"}>{errors.endereco?.txCidade?.message}</div>

                    </div>

                    <div className="form-group col-md-4 inputs">
                        <label htmlFor="inputEstado">Estado</label>
                        <select
                            {...register("endereco.txUf",
                                {
                                    required: 'Campo Obrigatorio'
                                })}

                            id="inputEstado" className={`form-control ${errors.endereco?.txUf ? 'is-invalid' : ''}`}
                            name={"endereco.txUf"}>
                            <option selected>Escolher...</option>
                            <option>SP</option>
                            <option>SP</option>
                            <option>SP</option>
                        </select>
                        <div className={"invalid-feedback d-block"}>{errors.endereco?.txUf?.message}</div>

                    </div>
                    <div className="form-group col-md-4 inputs">
                        <label htmlFor="inputUnidade">Unidade</label>
                        <select

                            {...register("unidade.idUnidade",
                                {
                                    required: 'Campo Obrigatorio'


                                })}

                            id="inputUnidade" className={`form-control ${errors.unidade ? 'is-invalid' : ''}`}
                            name={"unidade.numeroUnidade"}>
                            <option></option>
                            {pageListUnidades?.content.map((unidade, key) => {
                                return (
                                    <option key={unidade.idUnidade}
                                            value={unidade.idUnidade}>{unidade.numeroUnidade}</option>
                                );

                            })}
                        </select>
                        <div className={"invalid-feedback d-block"}>{errors.unidade?.message}</div>

                    </div>

                </div>
                <div className="form-group inputs checkbox">
                    <div className="form-check checkbox-item">
                        <input
                            {...register("ativo",
                                {})}
                            className="form-check-input" type="checkbox" id="checkAtivo" name={"ativo"}/>
                        <label className="form-check-label" htmlFor="checkAtivo">
                            Ativo
                        </label>
                    </div>
                    <div className="form-check checkbox-item">
                        <input
                            {...register("enviaSms",
                                {})}
                            className="form-check-input" type="checkbox" id="checkEnviaSms" name={"enviaSms"}/>
                        <label className="form-check-label" htmlFor="checkEnviaSms">
                            Envia Sms
                        </label>
                    </div>
                    <div className="form-check checkbox-item">
                        <input
                            {...register("enviaBoleto",
                                {})}
                            className="form-check-input" type="checkbox" id="checkEnviaBoleto" name={"enviaBoleto"}/>
                        <label className="form-check-label" htmlFor="checkEnviaBoleto">
                            Envia Boleto
                        </label>
                    </div>
                </div>
                <div className={"usuario-crud-btn-container"}>
                    <button type="submit" className="btn btn-primary usuario-crud-btn text-white">Salvar</button>
                    <Link to={"/admin/users"}>
                        <button type="submit" className="btn btn-outline-danger usuario-crud-btn ">Cancelar</button>
                    </Link>
                </div>
            </form>
            {hasError &&
                (<div className="alert alert-danger">
                    Ocorreu ao tentar Salvar o Usuario
                </div>)
            }
        </div>
    </div>)
}

export default UsuariosEdit;