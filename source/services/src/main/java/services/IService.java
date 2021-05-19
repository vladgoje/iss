package services;

import model.Bug;
import model.Programmer;
import model.Verifier;

import java.util.List;

public interface IService {
    void loginVerifier(Verifier verifier, IObserver client) throws ServiceException;
    void loginProgrammer(Programmer programmer, IObserver client) throws ServiceException;
    void logoutVerifier(Verifier verifier, IObserver client) throws ServiceException;
    void logoutProgrammer(Programmer verifier, IObserver client) throws ServiceException;
    void register(Verifier verifier) throws ServiceException;
    void registerBug(Bug bug) throws ServiceException;
    Bug updateBug(Bug bug) throws ServiceException;
    List<Bug> getBugs() throws ServiceException;
    List<Bug> sortBugsByPriority();
}
