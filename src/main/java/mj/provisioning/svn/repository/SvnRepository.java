package mj.provisioning.svn.repository;

import mj.provisioning.svn.domain.SvnRepoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SvnRepository extends JpaRepository<SvnRepoInfo, Long> {
    SvnRepoInfo findByProvisioningName(String provisioningName);
}
