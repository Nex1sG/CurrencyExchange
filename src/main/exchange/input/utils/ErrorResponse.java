package main.exchange.input.utils;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ErrorResponse {

    private int code;
    private String message;
    private String timestamp;
    private String path;

    public ErrorResponse(int code, String message, String path){
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.path = path;
    }
}
