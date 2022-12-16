package mj.provisioning.bundle.adapter.out.repository;

import mj.provisioning.bundle.domain.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    Optional<Bundle> findByName(String name);
    Optional<Bundle> findByBundleId(String bundleId);
}
