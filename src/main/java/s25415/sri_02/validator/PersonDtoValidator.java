package s25415.sri_02.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import s25415.sri_02.dto.PersonDto;

@Component
public class PersonDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PersonDto person = (PersonDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "firstName.empty", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "lastName.empty", "Last name is required");

        if (person.getBirthDate() == null) {
            errors.rejectValue("birthDate", "birthDate.null", "Birth date is required");
        }
    }
}