package io.vom.exceptions;

public class SelectorNotFoundException extends RuntimeException{
    public SelectorNotFoundException(String cause){
        super(cause);
    }
}
