package org.master.sprojrctbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RestBean<T> {
    private int status;
    private T message;
    private boolean success;

    private RestBean(int status, boolean success,T message){
        this.status = status;
        this.message = message;
        this.success = success;
    }

    public static <T> RestBean<T> success(){
        return new RestBean<T>(200, true, null);
    }

    public static <T> RestBean<T> success(T data){
        return new RestBean<T>(200, true, data);
    }

    public static <T> RestBean<T> failure(int status,T data){
        return new RestBean<T>(status,false,data);
    }


}
