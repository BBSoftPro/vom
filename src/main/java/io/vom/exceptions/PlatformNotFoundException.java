package io.vom.exceptions;

public class PlatformNotFoundException extends RuntimeException{
    public PlatformNotFoundException(String message){
        super(message);
    }
}
