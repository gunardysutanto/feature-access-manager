package feature.access.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeatureAccessStatusResponse {
    @JsonFormat(shape = JsonFormat.Shape.BOOLEAN)
    private boolean canAccess;
}
