package com.condominio.novaalianca.services.validation;

import com.condominio.novaalianca.controller.exeptions.FieldMessage;
import com.condominio.novaalianca.dto.UsuarioUpdateDTO;

import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;


import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UsuarioUpdateDTO> {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UsuarioRepository userRepository;

    @Override
    public void initialize (UserUpdateValid ann){

    }

    @Override
    public boolean isValid(UsuarioUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uriVars = (Map<String,String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        long userId = Long.parseLong(uriVars.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        Usuario user = userRepository.findByTxEmail(dto.getTxEmail());
        if(user != null && userId != user.getIdUsuario()){
            list.add(new FieldMessage("email", "Email já Existe"));
        }
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();

        }


        return list.isEmpty();
    }
}
