package com.seekweb4.chat.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author coderpwh
 */
@Data
public class HttpResponseDTO implements Serializable {

    private Integer code;

    private String msg;

    Long timeMillis;

    private Object data;


    public HttpResponseDTO(Integer code, String msg, Long timeMillis, Object data) {
        this.code = code;
        this.msg = msg;
        this.timeMillis = timeMillis;
        this.data = data;
    }

    public HttpResponseDTO() {}
}
