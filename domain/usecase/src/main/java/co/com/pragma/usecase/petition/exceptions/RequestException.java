package co.com.pragma.usecase.petition.exceptions;

public class RequestException extends RuntimeException{

    public RequestException(String message){
        super(message);
    }

}
