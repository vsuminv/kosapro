package exam.Kosademo.controller.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultMessage {
    OK(0, "굿!"),
    FAIL(-1, "ㅄ!"),
    EMPTY(1,"널임");

    private final int code;
    private final String message;
}
