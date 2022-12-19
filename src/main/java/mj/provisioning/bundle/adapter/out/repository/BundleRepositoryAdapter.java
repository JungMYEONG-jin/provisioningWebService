package mj.provisioning.bundle.adapter.out.repository;

import lombok.RequiredArgsConstructor;
import mj.provisioning.bundle.application.port.out.BundleRepositoryPort;
import mj.provisioning.bundle.domain.Bundle;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BundleRepositoryAdapter implements BundleRepositoryPort {
    private final BundleRepository bundleRepository;

    @Override
    public List<Bundle> saveAll(List<Bundle> bundles) {
        return bundleRepository.saveAll(bundles);
    }

    @Override
    public List<Bundle> findAll() {
        return bundleRepository.findAll();
    }

    @Override
    public void deleteAll() {
        bundleRepository.deleteAll();
    }
}
