package mj.provisioning.svn.repository;

import mj.provisioning.svn.domain.ProvisioningRepository;
import mj.provisioning.svn.domain.SvnRepoInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SvnRepositoryTest {

    @Autowired
    SvnRepository svnRepository;

    @Commit
    @Test
    public void saveTest() {
        List<SvnRepoInfo> res = new ArrayList<>();
        for (ProvisioningRepository value : ProvisioningRepository.values()) {
            res.add(SvnRepoInfo.builder()
                    .provisioningName(value.name())
                    .uri(value.getUri())
                    .build());
        }
        svnRepository.saveAll(res);
    }
}