package mj.provisioning.svn.dto;

import lombok.*;
import mj.provisioning.svn.domain.SvnRepoInfo;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProvisioningRepositoryDto {
    private Long id;
    private String appName;
    private boolean chosen;

    public static ProvisioningRepositoryDto of(SvnRepoInfo svnRepoInfo){
        return ProvisioningRepositoryDto.builder()
                .id(svnRepoInfo.getId())
                .appName(svnRepoInfo.getProvisioningName())
                .chosen(false)
                .build();
    }

    public static ProvisioningRepositoryDto of(SvnRepoInfo svnRepoInfo, boolean chosen){
        return ProvisioningRepositoryDto.builder()
                .id(svnRepoInfo.getId())
                .appName(svnRepoInfo.getProvisioningName())
                .chosen(chosen)
                .build();
    }
}
