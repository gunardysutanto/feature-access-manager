package feature.access.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
public class FeatureAccess {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Please fill-up the feature name since it is required.")
    private String featureName;

    @NotBlank(message = "Please fill-up the user e-mail address since it is required.")
    @Email(message = "Please fill-up the user e-mail address by using the proper e-mail format.")
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    @NotBlank(message = "Please fill-up the status for user access since it is required.")
    @Pattern(regexp = "^(?i)(true|false)$", message = "Please fill-up the status for feature accessibility with one of these options: true or false.")
    private String enable;

    public FeatureAccess mergeForAccessStatusWith(FeatureAccess modifiedFeatureAccess) {
        setEnable(modifiedFeatureAccess.getEnable());
        return this;
    }
}
