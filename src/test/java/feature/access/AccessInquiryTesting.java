package feature.access;

import feature.access.exception.NoSuchFeatureAccessException;
import feature.access.model.FeatureAccess;
import feature.access.repository.FeatureAccessRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class AccessInquiryTesting {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeatureAccessRepository featureAccessRepository;


    @BeforeAll
    public void dataSetup() {
        FeatureAccess featureAccess = new FeatureAccess();
        featureAccess.setFeatureName("Billing Payments");
        featureAccess.setEmail("john.doe@dev.net");
        featureAccess.setEnable(Boolean.TRUE.toString());
        featureAccessRepository.save(featureAccess);
    }

    @Test
    public void inquiryWithoutUserInput() throws Exception {
        mockMvc.perform(get("/features").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorMessage",is("Please provide both the name of the feature and the user e-mail address to perform this operation.")));
    }

    @Test
    public void inquiryWithCorrectUserInput() throws Exception {
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payments","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payments","john.doe@dev.net"));
        mockMvc.perform(get("/features").accept(MediaType.APPLICATION_JSON).queryParams(queryParamsFrom(featureAccess))).andExpect(status().isOk()).andExpect(jsonPath("$.canAccess",is(Boolean.valueOf(featureAccess.getEnable()))));
    }

    @Test
    public void inquiryWithNonExistingFeatureAccess() throws Exception {
        LinkedMultiValueMap<String, String> paramsForUserInput = new LinkedMultiValueMap<>();
        paramsForUserInput.add("featureName","Interbank Transfer");
        paramsForUserInput.add("email","john.doe@dev.com");

        mockMvc.perform(get("/features").accept(MediaType.APPLICATION_JSON).queryParams(paramsForUserInput)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorMessage",is(String.format("There is no feature access for the given references as it does not exist [feature = %s, e-mail = %s].",paramsForUserInput.get("featureName").get(0),paramsForUserInput.get("email").get(0)))));

    }

    @Test
    public void afterUpdatingTheAccessStatus() throws Exception {
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payments","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payments","john.doe@dev.net"));
        featureAccess.setEnable(Boolean.FALSE.toString());
        featureAccessRepository.save(featureAccess);

        mockMvc.perform(get("/features").accept(MediaType.APPLICATION_JSON).queryParams(queryParamsFrom(featureAccess))).andExpect(status().isOk()).andExpect(jsonPath("$.canAccess",is(Boolean.valueOf(featureAccess.getEnable()))));
    }

    @AfterAll
    public void cleanUp() {
        featureAccessRepository.deleteAll();
    }

    private LinkedMultiValueMap<String,String> queryParamsFrom(FeatureAccess featureAccess) {
        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("featureName",featureAccess.getFeatureName());
        queryParams.add("email",featureAccess.getEmail());
        return queryParams;
    }
}
