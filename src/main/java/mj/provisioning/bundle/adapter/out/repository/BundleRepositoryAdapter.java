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
    public Bundle findByBundle(String bundleId) {
        return bundleRepository.findByBundleId(bundleId).orElseThrow(()->new RuntimeException("번들 ID와 매치되는 정보가 존재하지 않습니다."));
    }

    @Override
    public void deleteAll() {
        bundleRepository.deleteAll();
    }
}
