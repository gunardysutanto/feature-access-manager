package feature.access.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvalidUserInputException extends Exception {
    private Map<String,List<String>> validationErrorsInUserInput;

    public InvalidUserInputException() {
        super("Please provide both the name of the feature and the user e-mail address to perform this operation.");
    }

    public InvalidUserInputException(Map<String,List<String>> validationErrorsInUserInput) {
        super("Please fill-up the required information correctly by referring to the user entry violation(s) below.");
        setValidationErrorsInUserInput(validationErrorsInUserInput);
    }
}
