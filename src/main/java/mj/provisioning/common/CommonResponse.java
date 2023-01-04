package mj.provisioning.common;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonResponse {
    private int status;
    private String message;

    @Builder
    public CommonResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
