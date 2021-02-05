package feature.access.resource;

import feature.access.exception.InvalidUserInputException;
import feature.access.exception.NoSuchFeatureAccessException;
import feature.access.model.FeatureAccess;
import feature.access.repository.FeatureAccessRepository;
import feature.access.response.FeatureAccessStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@RestController
@RequestMapping("/features")
public class FeatureAccessResource {
    @Autowired
    private FeatureAccessRepository featureAccessRepository;

    @GetMapping
    public FeatureAccessStatusResponse gettingFeatureAccessFor(@RequestParam(value = "featureName", required = false, defaultValue = "") String featureName,
                                                               @RequestParam(value = "email", required = false, defaultValue = "") String email) throws NoSuchFeatureAccessException, InvalidUserInputException {
        validateUserInputFromQueryString(featureName,email);
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail(featureName, email).orElseThrow(()-> new NoSuchFeatureAccessException(featureName,email));
        return FeatureAccessStatusResponse.builder().canAccess(Boolean.valueOf(featureAccess.getEnable())).build();
    }

    @PostMapping
    @Transactional
    public ResponseEntity saveFeatureAccess(@Valid @RequestBody FeatureAccess featureAccess, BindingResult bindingResult) throws NoSuchFeatureAccessException, InvalidUserInputException {
        if(!bindingResult.hasFieldErrors()) {
            if (isThisFeatureAccessExist(featureAccess.getFeatureName(), featureAccess.getEmail())) {
                FeatureAccess existingFeatureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail(featureAccess.getFeatureName(), featureAccess.getEmail()).orElseThrow(() -> new NoSuchFeatureAccessException(featureAccess.getFeatureName(), featureAccess.getEmail()));
                if(isThereAnyChangesForTheStatus(existingFeatureAccess,featureAccess)) {
                    existingFeatureAccess = existingFeatureAccess.mergeForAccessStatusWith(featureAccess);
                    featureAccessRepository.save(existingFeatureAccess);
                    return ResponseEntity.ok().build();
                } else
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            } else {
                featureAccessRepository.save(featureAccess);
                return ResponseEntity.ok().build();
            }
        } else {
            Map<String,List<String>> userInputErrors = bindingResult.getFieldErrors().stream().collect(groupingBy(FieldError::getField,mapping(FieldError::getDefaultMessage,toList())));
            throw new InvalidUserInputException(userInputErrors);
        }
    }

    private void validateUserInputFromQueryString(String featureName, String userEmailAddress) throws InvalidUserInputException {
        if(featureName.isEmpty() && userEmailAddress.isEmpty())
            throw new InvalidUserInputException();
    }

    private boolean isThisFeatureAccessExist(String featureName, String userEmailAddress) {
        return featureAccessRepository.findFeatureAccessByFeatureNameAndEmail(featureName,userEmailAddress).isPresent();
    }

    private boolean isThereAnyChangesForTheStatus(FeatureAccess existingFeatureAccess, FeatureAccess modifiedAccess) {
        return existingFeatureAccess.getEnable().compareTo(modifiedAccess.getEnable())>0;
    }
}
