import {ReactComponent as MainImage} from 'assets/images/main-image.svg';

import {Link} from 'react-router-dom';

import './styles.css';
import ButtonIcon from "../../components/ButtonIcon";

const Home = () => {
    return (
        <div className={"home-container"}>
            <div className={"base-card home-card"}>
                <div className={"home-content-container"}>
                    <div>
                        <h1>DEPOIS TROCAR</h1>
                        <p>Dashboard , Emissão De Boletos, e Prestação de Contas</p>
                    </div>
                    <div>
                        <Link to={"/admin/auth/login"}>
                            <ButtonIcon tituloBotao = "Entrar " />
                        </Link>

                    </div>
                </div>
                {/*<div className={"home-image-container"}>*/}
                {/*    <MainImage/>*/}
                {/*</div>*/}

            </div>
        </div>

    );
}

export default Home;
