package com.condominio.novaalianca.repositories;

import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.entities.Usuario_;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecification {
    public static Specification<Usuario> findByIdUnidade(Long idUnidade){
        return  (root, criteriaQuery, criteriaBuilder) -> {
            final List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get(Usuario_.unidade),idUnidade)));

            criteriaQuery.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[predicates.size()]));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
