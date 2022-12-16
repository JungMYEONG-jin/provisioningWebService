package mj.provisioning.bundle.application.port.out;

import mj.provisioning.bundle.domain.Bundle;

import java.util.List;

public interface BundleRepositoryPort{
    List<Bundle> saveAll(List<Bundle> bundles);
    void deleteAll();
}
