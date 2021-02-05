package feature.access;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnableOrDisableAccessTesting {
    @Autowired
    private FeatureAccessRepository featureAccessRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void dataSetup() {
        FeatureAccess featureAccess = prepareNewTestData("Billing Payment","john.doe@dev.net",Boolean.TRUE);
        featureAccessRepository.save(featureAccess);
    }

    @Test
    public void enablingTheNewFeature() throws Exception {
        FeatureAccess newFeatureAccess = prepareNewTestData("Customer Portfolio","john.doe@dev.net",Boolean.TRUE);
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(newFeatureAccess))).andExpect(status().isOk());
    }

    @Test
    public void disablingTheExistingFeatureAccess() throws Exception {
        FeatureAccess testFeatureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payment","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payment","john.doe@dev.net"));
        testFeatureAccess.setEnable(Boolean.FALSE.toString());
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(testFeatureAccess))).andExpect(status().isOk());
    }

    @Test
    public void withoutChangingTheStatus() throws Exception {
        FeatureAccess testFeatureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payment","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payment","john.doe@dev.net"));
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(testFeatureAccess))).andExpect(status().isNotModified());
    }

    @Test
    public void withoutAnyUserInput() throws Exception {
        FeatureAccess featureAccess = new FeatureAccess();
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(featureAccess))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",is(errorMessageHeaderForInvalidFormEntry())))
                .andExpect(jsonPath("$.userInputViolations.featureName",hasItem("Please fill-up the feature name since it is required.")))
                .andExpect(jsonPath("$.userInputViolations.email",hasItem("Please fill-up the user e-mail address since it is required.")))
                .andExpect(jsonPath("$.userInputViolations.enable",hasItem("Please fill-up the status for user access since it is required.")));
    }

    @Test
    public void withIncorrectEmailFormat() throws Exception {
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payment","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payment","john.doe@dev.net"));
        featureAccess.setEmail("john.doe");
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(featureAccess))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",is(errorMessageHeaderForInvalidFormEntry())))
                .andExpect(jsonPath("$.userInputViolations.email",hasItem("Please fill-up the user e-mail address by using the proper e-mail format.")));
    }

    @Test
    public void withTheIncorrectAccessStatus() throws Exception {
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payment","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payment","john.doe@dev.net"));
        featureAccess.setEnable("Yes");
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(featureAccess))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",is(errorMessageHeaderForInvalidFormEntry())))
                .andExpect(jsonPath("$.userInputViolations.enable",hasItem("Please fill-up the status for feature accessibility with one of these options: true or false.")));
    }

    @Test
    public void withEmptyAccessStatus() throws Exception {
        FeatureAccess featureAccess = featureAccessRepository.findFeatureAccessByFeatureNameAndEmail("Billing Payment","john.doe@dev.net").orElseThrow(()-> new NoSuchFeatureAccessException("Billing Payment","john.doe@dev.net"));
        featureAccess.setEnable("");
        mockMvc.perform(post("/features").contentType(MediaType.APPLICATION_JSON).content(convertIntoStringForm(featureAccess))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",is(errorMessageHeaderForInvalidFormEntry())))
                .andExpect(jsonPath("$.userInputViolations.enable",hasItem("Please fill-up the status for user access since it is required.")))
                .andExpect(jsonPath("$.userInputViolations.enable",hasItem("Please fill-up the status for feature accessibility with one of these options: true or false.")));
    }

    @AfterAll
    public void dataCleanup() {
        featureAccessRepository.deleteAll();
    }

    private FeatureAccess prepareNewTestData(String featureName, String email, Boolean enable) {
        FeatureAccess featureAccess = new FeatureAccess();
        featureAccess.setFeatureName(featureName);
        featureAccess.setEmail(email);
        featureAccess.setEnable(enable.toString());
        return featureAccess;
    }

    private String convertIntoStringForm(FeatureAccess featureAccess) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        return writer.writeValueAsString(featureAccess);
    }

    private String errorMessageHeaderForInvalidFormEntry() {
        return "Please fill-up the required information correctly by referring to the user entry violation(s) below.";
    }
}
