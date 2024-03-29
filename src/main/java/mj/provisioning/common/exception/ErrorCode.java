package mj.provisioning.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PROFILE_NOT_EXIST(404, "PROFILE-ERROR-1", "요청하신 프로비저닝 파일은 존재하지 않습니다."),
    JWT_TRANSFORM_FAILED(500, "JWT-ERROR-1", "JWT 생성에 실패하였습니다."),
    INVALID_KEY(500, "INVALID-KEY-ERROR-1", "JWT 서명키가 유효하지 않습니다."),
    IO_FAILED(500, "IO-ERROR", "파일 읽기에 실패하였습니다."),
    PARSE_FAILED(500, "PARSE-ERROR", "파싱에 실패하였습니다."),
    ALGORITHM_NOT_EXIST(404, "ALGORITHM-ERROR-1", "해당 알고리즘은 존재하지 않습니다."),
    CERTIFICATE_NOT_EXIST(404, "CERTIFICATE-ERROR-1", "요청하신 인증서는 존재하지 않습니다."),
    BUNDLE_NOT_EXIST(404, "BUNDLE-ERROR-1", "요청하신 번들 ID는 존재하지 않습니다."),
    DEVICE_NOT_EXIST(404, "DEVICE-ERROR-1", "프로비저닝과 매칭되는 디바이스가 존재하지 않습니다."),
    PATH_NOT_MULTI(400, "PATH-ERROR-1", "경로는 한개만 선택가능합니다."),
    UPLOAD_FAILED(400, "UPLOAD-ERROR-1", "SVN 업로드에 실패하였습니다."),
    URL_ERROR(404, "URL-ERROR", "잘못된 URL 요청입니다."),
    DOWNLOAD_FAILED(500, "DOWNLOAD-ERROR", "다운로드중 에러가 발생했습니다. 관리자에게 문의바랍니다."),
    API_ERROR(500, "NETWORK-ERROR",    "현재 네트워크 통신이 불안정합니다.")
    ;


    private int status;
    private String code;
    private String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
