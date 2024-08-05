package exam.Kosademo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class APIDoc<T> {
    private final int code;
    private final String message;
    private final T data;

    public static <T> APIDoc<T> success(T data) {
        return new APIDoc<>(ResultMessage.OK.getCode(), ResultMessage.OK.getMessage(), data);
    }
    public static <T> APIDoc<T> success() {
        return new APIDoc<>(ResultMessage.EMPTY.getCode(), ResultMessage.EMPTY.getMessage(), null);
    }

}