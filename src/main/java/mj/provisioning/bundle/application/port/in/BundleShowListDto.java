package mj.provisioning.bundle.application.port.in;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class BundleShowListDto {
    @Builder.Default
    private List<BundleShowDto> bundleData = new ArrayList<>();
}
