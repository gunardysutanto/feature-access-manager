package feature.access.exception.ack;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ApplicationError {
    private final String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, List<String>> userInputViolations;
}
