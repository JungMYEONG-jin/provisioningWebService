package mj.provisioning.svn.dto;

import lombok.*;
import mj.provisioning.svn.domain.ProvisioningRepository;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class ProvisioningRepositoryDto {
    ProvisioningRepository repository;

    public static ProvisioningRepositoryDto of(ProvisioningRepository provisioningRepository){
        return ProvisioningRepositoryDto.builder()
                .repository(provisioningRepository)
                .build();
    }
}
