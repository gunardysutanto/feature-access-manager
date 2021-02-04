package feature.access.exception;

public class NoSuchFeatureAccessException extends Exception {
    public NoSuchFeatureAccessException(String featureName, String userEmailAddress) {
       super(String.format("There is no feature access for the given references as it does not exist [feature = %s, e-mail = %s].",featureName,userEmailAddress));
    }
}
