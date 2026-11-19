package com.back.global.globalExceptionHandler;

// 행위자는 확인됐지만 해당 작업을 할 권한이 없을 때 → 403
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("권한이 없습니다.");
    }
}
