package services;

import model.Bug;
import model.Programmer;
import model.Verifier;

import java.util.ArrayList;
import java.util.List;

public class Validator {
    public void validateBug(Bug bug) throws ServiceException {
        List<String> errors = new ArrayList<>();
        if(bug.getName().equals("")){
            errors.add("Invalid name");
        }
        if(bug.getDescription().equals("")){
            errors.add("Invalid descriprion");
        }
        if(bug.getPriority() == null){
            errors.add("Invalid bug priority");
        }
        if(errors.size() > 0){
            throw new ServiceException(errors);
        }
    }

    public void validateVerifier(Verifier verifier) throws ServiceException {
        List<String> errors = new ArrayList<>();
        if(verifier.getUsername().length() < 3){
            errors.add("Username too short");
        }
        if(verifier.getPassword().length() < 6){
            errors.add("Password too weak");
        }

        if(errors.size() > 0){
            throw new ServiceException(errors);
        }
    }

    public void validateProgrammer(Programmer programmer) throws ServiceException {
        List<String> errors = new ArrayList<>();
        if(programmer.getUsername().length() < 3){
            errors.add("Username too short");
        }
        if(programmer.getPassword().length() < 6){
            errors.add("Password too weak");
        }

        if(errors.size() > 0){
            throw new ServiceException(errors);
        }
    }
}
