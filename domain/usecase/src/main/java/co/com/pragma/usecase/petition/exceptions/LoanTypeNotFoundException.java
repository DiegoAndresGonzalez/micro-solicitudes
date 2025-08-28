package co.com.pragma.usecase.petition.exceptions;

public class LoanTypeNotFoundException extends RuntimeException{

    public LoanTypeNotFoundException(String message){
        super(message);
    }

}
