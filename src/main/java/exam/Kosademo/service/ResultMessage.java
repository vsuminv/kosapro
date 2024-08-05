package exam.Kosademo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResultMessage {
    OK(0, "굿!"),
    FAIL(-1, "no!"),
    EMPTY(1,"NULL~");

    private final int code;
    private final String message;
}
