package co.com.pragma.usecase.petition.exceptions;

public class StatusNotFoundException extends RuntimeException {
    public StatusNotFoundException(String message){
        super(message);
    }
}
