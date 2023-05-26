package mj.provisioning.handler;

import lombok.NoArgsConstructor;
import mj.provisioning.common.exception.ErrorCode;

@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String code;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }
}
