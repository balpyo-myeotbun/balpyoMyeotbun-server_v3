package site.balpyo.fcm.entity;


import lombok.Getter;

@Getter
public enum SuccessCode {
    SELECT_SUCCESS(200, "조회 성공"),
    SELECT_FAIL(400, "조회 실패");

    private final int status;
    private final String message;

    SuccessCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

}
