package mj.provisioning.profile.application.port.in;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileShowListDto {
    private List<ProfileShowDto> data;
}
