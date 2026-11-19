package com.back.global.globalExceptionHandler;

// 행위자(actorId)를 확인할 수 없을 때 → 401
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("로그인 후 이용해주세요.");
    }
}
