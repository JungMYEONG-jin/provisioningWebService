package mj.provisioning.svn.dto;

import lombok.*;
import mj.provisioning.svn.domain.SvnRepoInfo;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ProvisioningRepositoryDto {
    private Long id;
    private String appName;

    public static ProvisioningRepositoryDto of(SvnRepoInfo svnRepoInfo){
        return ProvisioningRepositoryDto.builder()
                .id(svnRepoInfo.getId())
                .appName(svnRepoInfo.getProvisioningName())
                .build();
    }
}
