package mj.provisioning.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String code;
    private String message;

    public ErrorResponse(ErrorCode e) {
        this.status = e.getStatus();
        this.code = e.getCode();
        this.message = e.getMessage();
    }
}
