package com.thumati.springbootwebflux.fluxandmono;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyCustomException extends Throwable{
    private String errorMessage;

    public MyCustomException(String message){
        this.errorMessage = getMessage() + " - "+message;
    }

}
