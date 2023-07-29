import './styles.css'
import {AxiosRequestConfig} from "axios/index";
import {requestBackend} from "../../../../utils/requests";
import {useEffect, useState} from "react";
import {Link, useHistory, useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import {Unidade} from "../../../../types/unidade";

type UrlParams = {
    idUnidade: string
}
const UnidadeEdit = () => {
    const {from} =  {from : {pathname: '/admin/unidade'}};
    const history = useHistory();

    const [pageTitle] = useState('Cadastro Unidade');

    useEffect(() => {
        document.title = pageTitle;
    }, [pageTitle]);

    const [hasError, setHasError] = useState(false);

    const {register, handleSubmit, formState: {errors}, setValue} = useForm<Unidade>();


    const {idUnidade} = useParams<UrlParams>();
    console.log('idUnidade', idUnidade);
    const isEditing = idUnidade !== 'create'
    console.log('isEditing', isEditing);



    const onSubmit = (unidade: Unidade) => {
        const config: AxiosRequestConfig = {
            method: isEditing ? "PUT" : "POST",
            url: isEditing ? '/unidade/update' : '/unidade/save',
            data: unidade,
            withCredentials: true
        }

        requestBackend(config)
            .then(response => {

                console.log('SUCESSO response post', response);
                history.replace(from)
            })
            .catch(error => {
                setHasError(true);
                console.log('ERRO', error)
            })
            .finally()

        console.log(unidade)

    }

    const handleDelete = (id: string) =>{
        const params: AxiosRequestConfig = {
            method: "DELETE",
            url: `/unidade/delet/${id}`,
            withCredentials:true

        };
        requestBackend(params).then(()=>{
            console.log("Deletado unidade  " + id)
            history.replace('/admin/unidade')

        })

    }

    useEffect(() => {
        if (isEditing) {
            requestBackend({url: `/unidade/${idUnidade}`, method: "GET"})
                .then(response => {

                    const unidade = response.data as Unidade;
                    setValue("idUnidade", unidade.idUnidade);
                    setValue("numeroUnidade", unidade.numeroUnidade);
                    setValue("andarUnidade", unidade.andarUnidade);

                    console.log('SUCESSO response', response);
                })
                .catch(error => {
                    setHasError(true);
                    console.log('ERRO', error)
                })
        }
    }, [isEditing, idUnidade, setValue])


    return (
        <div className={"crud-form"}>
            <div className={"usuario-container"}>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <div className={"unidade-crud-btn-container"}>
                            <div className="form-group col-sm-2 inputs">
                                <label htmlFor="inputNumeroUnidade">Num. Unidade</label>
                                <input {...register("numeroUnidade",
                                    {
                                        required: 'Campo Obrigatorio'
                                    })}
                                       type="text"
                                       className={`form-control ${errors.numeroUnidade ? 'is-invalid' : ''}`}
                                       id="inputNumeroUnidade" placeholder="Numero" name={"numeroUnidade"}/>
                                <div className={"invalid-feedback d-block"}>{errors.numeroUnidade?.message}</div>

                            </div>
                            <div className="form-group col-sm-2 inputs">
                                <label htmlFor="inputAndarUnidade">Andar Unidade</label>
                                <input
                                    {...register("andarUnidade",
                                        {
                                            required: 'Campo Obrigatorio'
                                        })}
                                    type="text" className={`form-control ${errors.andarUnidade ? 'is-invalid' : ''}`}
                                    id="inputAndarUnidade" placeholder="Andar" name={"andarUnidade"}/>
                                <div className={"invalid-feedback d-block"}>{errors.andarUnidade?.message}</div>

                            </div>

                        <button type="submit" className="btn btn-primary usuario-crud-btn text-white">Salvar</button>
                        <Link to={"/admin/unidade"}>
                            <button className="btn btn-outline-danger usuario-crud-btn ">Cancelar</button>
                        </Link>
                        <button onClick={() => handleDelete(idUnidade)} className="btn btn btn-danger usuario-crud-btn text-white">Excluir</button>

                    </div>
                </form>
                {hasError &&
                    (<div className="alert alert-danger">
                        Ocorreu ao tentar Salvar a Unidade
                    </div>)
                }
            </div>
        </div>
    )

}


export default UnidadeEdit;