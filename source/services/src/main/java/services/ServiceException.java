package services;

import java.util.ArrayList;
import java.util.List;

public class ServiceException extends Exception {
    List<String> errors = new ArrayList<>();

    public ServiceException() {
    }

    public ServiceException(Exception e) {
        errors.add(e.getMessage());
    }

    public ServiceException(String message) {
        super(message);
        errors.add(message);
    }

    public ServiceException(List<String> errors){ this.errors = errors; }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }


    public String getErrors(){
        StringBuilder errors = new StringBuilder();
        for(String err : this.errors){
            errors.append(err).append("\n");
        }
        return errors.toString();
    }
}