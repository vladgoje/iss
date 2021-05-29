package services;

import model.*;

import java.util.List;

public interface IService extends IObservable {
    // employees
    Programmer register(Programmer programmer) throws ServiceException;
    Verifier register(Verifier verifier) throws ServiceException;
    Employee login(Employee employee) throws ServiceException;
    void logout(Employee employee, IObserver client) throws ServiceException;
    List<Programmer> getProgrammers();

    // bugs
    Bug registerBug(Bug bug) throws ServiceException;
    Bug updateBug(Bug bug) throws ServiceException;
    List<Bug> getBugs();
    List<Bug> getProgrammerBugs(Long id);
    List<Bug> sortBugsByPriority();
    List<Bug> sortBugsByPriorityProgrammer(Long id);
    List<Bug> getActiveBugs();
    List<Bug> getPendingBugs();
    List<Bug> getSolvedBugs();
    void addBugSolver(BugSolver solver);
    void removeBugSolver(BugSolver solver);

    // request
    VerificationRequest sendRequest(VerificationRequest request);
    VerificationRequest updateRequest(VerificationRequest request);
    List<VerificationRequest> getAllRequests();
    List<VerificationRequest> getProgrammerRequests(Long id);


}
