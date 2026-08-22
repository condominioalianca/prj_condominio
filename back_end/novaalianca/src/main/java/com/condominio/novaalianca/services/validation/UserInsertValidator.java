package com.condominio.novaalianca.services.validation;


import lombok.RequiredArgsConstructor;
import com.condominio.novaalianca.controller.exeptions.FieldMessage;
import com.condominio.novaalianca.dto.UsuarioChangPasswordtDTO;
import com.condominio.novaalianca.entities.Usuario;
import com.condominio.novaalianca.repositories.UsuarioRepository;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UsuarioChangPasswordtDTO> {

    private final UsuarioRepository userRepository;

    @Override
    public void initialize (UserInsertValid ann){

    }

    @Override
    public boolean isValid(UsuarioChangPasswordtDTO dto, ConstraintValidatorContext context) {

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