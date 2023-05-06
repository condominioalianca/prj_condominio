package com.condominio.novaalianca.services.validation;


import com.condominio.novaalianca.controller.exeptions.FieldMessage;
import com.condominio.novaalianca.dto.UsuarioInsertDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UsuarioInsertDTO> {

    @Autowired
    private UsuarioRepository userRepository;

    @Override
    public void initialize (UserInsertValid ann){

    }

    @Override
    public boolean isValid(UsuarioInsertDTO dto, ConstraintValidatorContext context) {

        List<FieldMessage> list = new ArrayList<>();

        Usuario user = userRepository.findByTxEmail(dto.getTxEmail());
        if(user != null){
            list.add(new FieldMessage("email", "Email já Existe"));
        }
        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();

        }


        return list.isEmpty();
    }
}
