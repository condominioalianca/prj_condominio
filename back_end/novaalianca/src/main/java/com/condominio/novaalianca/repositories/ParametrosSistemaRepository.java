package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.ParametrosSitema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface ParametrosSistemaRepository extends JpaRepository<ParametrosSitema, Long> {

	@Query("SELECT parametro.valorParametro from ParametrosSitema parametro WHERE parametro.descParametro = ?1 ")
	String findValorParametro(String valorDescricao);
	
	@Query("SELECT parametro.valorParametro from ParametrosSitema parametro WHERE parametro.descParametro = ?1 ")
	String findValorMulta(String valorMulta);
	
	@Query("SELECT parametro.valorParametro from ParametrosSitema parametro WHERE parametro.descParametro = ?1 ")
	String findValorMora(String valorMora);
	
	@Query("SELECT parametro.valorParametro from ParametrosSitema parametro WHERE parametro.descParametro = ?1 ")
	String findDtVencimento(String diaVencimento);

}
