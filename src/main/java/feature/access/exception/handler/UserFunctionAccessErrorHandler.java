package feature.access.exception.handler;

import feature.access.exception.InvalidUserInputException;
import feature.access.exception.NoSuchFeatureAccessException;
import feature.access.exception.ack.ApplicationError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserFunctionAccessErrorHandler {
    @ExceptionHandler(NoSuchFeatureAccessException.class)
    public ResponseEntity handleForUserAccessNotAvailable(NoSuchFeatureAccessException noSuchFeatureAccessException) {
        return ResponseEntity.badRequest().body(new ApplicationError(noSuchFeatureAccessException.getMessage(), null));
    }

    @ExceptionHandler(InvalidUserInputException.class)
    public ResponseEntity handleForInvalidUserInput(InvalidUserInputException invalidUserInputException) {
        return ResponseEntity.badRequest().body(constructErrorAcknowledgment(invalidUserInputException));
    }

    private ApplicationError constructErrorAcknowledgment(InvalidUserInputException invalidUserInputException) {
        if(hasValidationErrorInUserInput(invalidUserInputException))
            return new ApplicationError(invalidUserInputException.getMessage(),invalidUserInputException.getValidationErrorsInUserInput());
        else
            return new ApplicationError(invalidUserInputException.getMessage(),null);
    }

    private boolean hasValidationErrorInUserInput(InvalidUserInputException invalidUserInputException) {
        return (invalidUserInputException.getValidationErrorsInUserInput() != null && !invalidUserInputException.getValidationErrorsInUserInput().isEmpty());
    }
}
