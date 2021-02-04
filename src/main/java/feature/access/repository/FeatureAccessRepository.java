package feature.access.repository;

import feature.access.model.FeatureAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FeatureAccessRepository extends JpaRepository<FeatureAccess, Long> {
    @Query("select featureAccess from FeatureAccess featureAccess where featureAccess.featureName=:featureName and featureAccess.email=:email")
    public Optional<FeatureAccess> findFeatureAccessByFeatureNameAndEmail(@Param("featureName") String featureName, @Param("email") String email);
}
