package mj.provisioning.profile.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class ProfileShowListDto {
    @Builder.Default
    private List<ProfileShowDto> data = new ArrayList<>();
}
