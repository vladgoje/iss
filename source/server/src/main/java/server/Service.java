package server;

import model.*;
import persistence.BugRepositoryInterface;
import persistence.ProgrammerRepositoryInterface;
import persistence.RequestRepositoryInterface;
import persistence.VerifierRepositoryInterface;
import services.*;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements IService, IObservable {

    private final BugRepositoryInterface bugRepo;
    private final VerifierRepositoryInterface verifierRepo;
    private final ProgrammerRepositoryInterface programmerRepo;
    private final RequestRepositoryInterface requestRepo;
    private final Map<String, IObserver> loggedClients;
    private final Validator validator;

    public Service(BugRepositoryInterface bugRepo, VerifierRepositoryInterface verifierRepo, ProgrammerRepositoryInterface programmerRepo, RequestRepositoryInterface requestRepo){
        this.bugRepo = bugRepo;
        this.verifierRepo = verifierRepo;
        this.programmerRepo = programmerRepo;
        this.requestRepo = requestRepo;
        loggedClients = new ConcurrentHashMap<>();
        validator = new Validator();
    }

    private String hash(String base){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void addObserver(String username, IObserver observer) throws ServiceException {
        if(loggedClients.get(username) != null)
            throw new ServiceException("Employee already logged in");
        loggedClients.putIfAbsent(username, observer);
    }


    @Override
    public void removeObserver(String username) throws ServiceException {
        if(loggedClients.get(username) != null) {
            loggedClients.remove(username);
            return;
        }
        throw new ServiceException("Employee not logged in");
    }


    @Override
    public Employee login(Employee employee) throws ServiceException {
        Programmer p = programmerRepo.findByCredentials(employee.getUsername(), hash(employee.getPassword()));
        if(p != null){
            return p;
        } else {
            Verifier v = verifierRepo.findByCredentials(employee.getUsername(), hash(employee.getPassword()));
            if(v != null){
                return v;
            }
        }

        throw new ServiceException("Invalid credentials");
    }

    @Override
    public void logout(Employee employee, IObserver client) throws ServiceException {
        Programmer p = programmerRepo.findByUsername(employee.getUsername());
        if(p != null){
            removeObserver(p.getUsername());
        } else {
            Verifier v = verifierRepo.findByUsername(employee.getUsername());
            if(v != null){
                removeObserver(v.getUsername());
            }
        }
    }

    @Override
    public Verifier register(Verifier verifier) throws ServiceException {
        validator.validateVerifier(verifier);
        if(verifierRepo.findByUsername(verifier.getUsername()) != null){
            throw new ServiceException("Angajatul exista deja");
        }
        if(programmerRepo.findByUsername(verifier.getUsername()) != null){
            throw new ServiceException("Angajatul exista deja");
        }
        verifier.setPassword(hash(verifier.getPassword()));
        return verifierRepo.save(verifier);

    }

    @Override
    public Programmer register(Programmer programmer) throws ServiceException {
        validator.validateProgrammer(programmer);
        if(programmerRepo.findByUsername(programmer.getUsername()) != null){
            throw new ServiceException("Angajatul exista deja");
        }
        if(verifierRepo.findByUsername(programmer.getUsername()) != null){
            throw new ServiceException("Angajatul exista deja");
        }
        programmer.setPassword(hash(programmer.getPassword()));
        return programmerRepo.save(programmer);
    }

    @Override
    public List<Programmer> getProgrammers(){
        return StreamSupport.stream(programmerRepo.findAll().spliterator(),false)
                .sorted(Comparator.comparing(Programmer::getScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Bug registerBug(Bug bug) throws ServiceException {
        validator.validateBug(bug);
        Bug added = bugRepo.save(bug);
        if(added != null){
            notifyBugAdded(added);
        }
       return added;
    }

    @Override
    public Bug updateBug(Bug bug) throws ServiceException {
        validator.validateBug(bug);
        Bug modified = bugRepo.update(bug);
        if(modified != null) {
            notifyBugUpdate(modified);
        }
        return modified;
    }

    @Override
    public List<Bug> getBugs() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public List<Bug> getProgrammerBugs(Long id) {
        System.out.println("GET PROGRAMMER BUGS ID: " + id);
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .filter(x -> x.getStatus().equals(BugStatus.ACTIVE) || (x.getStatus().equals(BugStatus.PENDING) && bugRepo.existsSolver(new BugSolver(x.getId(), id))))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bug> sortBugsByPriority() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .sorted((x, y) -> Integer.compare(y.getPriority().ordinal(), x.getPriority().ordinal()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bug> sortBugsByPriorityProgrammer(Long id){
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .filter(x -> x.getStatus().equals(BugStatus.ACTIVE) || (x.getStatus().equals(BugStatus.PENDING) && bugRepo.existsSolver(new BugSolver(x.getId(), id))))
                .sorted((x, y) -> Integer.compare(y.getPriority().ordinal(), x.getPriority().ordinal()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bug> getActiveBugs() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .filter(x -> x.getStatus().equals(BugStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bug> getPendingBugs() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .filter(x -> x.getStatus().equals(BugStatus.PENDING))
                .collect(Collectors.toList());
    }

    @Override
    public List<Bug> getSolvedBugs() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .filter(x -> x.getStatus().equals(BugStatus.SOLVED))
                .collect(Collectors.toList());
    }

    @Override
    public void addBugSolver(BugSolver solver) {
        bugRepo.addBugSolver(solver);
        notifyBugPending(bugRepo.findOne(solver.getBugId()), programmerRepo.findOne(solver.getProgrammerId()));
    }

    @Override
    public void removeBugSolver(BugSolver solver) {
        bugRepo.removeBugSolver(solver);
        notifyBugPending(bugRepo.findOne(solver.getBugId()), programmerRepo.findOne(solver.getProgrammerId()));
    }

    private void notifyBugAdded(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<String, IObserver> entry : loggedClients.entrySet()){
            executor.execute(() -> {
                try {
                    entry.getValue().newBugAdded(bug);
                } catch (RemoteException | ServiceException e) {
                    System.err.println("Error notifying organizers");
                }
            });
        }
        executor.shutdown();
    }

    private void notifyBugUpdate(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<String, IObserver> entry : loggedClients.entrySet()){
            executor.execute(() -> {
                try {
                    entry.getValue().newBugUpdate(bug);
                } catch (RemoteException | ServiceException e) {
                    System.err.println("Error notifying organizers");
                }
            });
        }
        executor.shutdown();
    }

    private void notifyBugPending(Bug bug, Programmer programmer){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<String, IObserver> entry : loggedClients.entrySet()){
            executor.execute(() -> {
                try {
                    entry.getValue().newBugPending(bug, programmer);
                } catch (RemoteException | ServiceException e) {
                    System.err.println("Error notifying organizers");
                }
            });
        }
        executor.shutdown();
    }

    private void notifyRequestAdded(VerificationRequest request){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<String, IObserver> entry : loggedClients.entrySet()){
            executor.execute(() -> {
                try {
                    entry.getValue().newRequestAdded(request);
                } catch (RemoteException | ServiceException e) {
                    System.err.println("Error notifying organizers");
                }
            });
        }
        executor.shutdown();
    }

    private void notifyRequestUpdate(VerificationRequest request){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<String, IObserver> entry : loggedClients.entrySet()){
            executor.execute(() -> {
                try {
                    entry.getValue().newRequestUpdate(request);
                } catch (RemoteException | ServiceException e) {
                    System.err.println("Error notifying organizers");
                }
            });
        }
        executor.shutdown();
    }



    public VerificationRequest sendRequest(VerificationRequest request){
        VerificationRequest added = requestRepo.save(request);
        if(added != null){
            notifyRequestAdded(added);
        }
        return added;
    }

    public List<VerificationRequest> getAllRequests(){
        return StreamSupport.stream(requestRepo.findAll().spliterator(),false)
                .collect(Collectors.toList());
    }

    public List<VerificationRequest> getProgrammerRequests(Long id){
        return StreamSupport.stream(requestRepo.findAll().spliterator(),false)
                .filter(x -> x.getProgrammer().getId().equals(id))
                .collect(Collectors.toList());
    }

    public VerificationRequest updateRequest(VerificationRequest request){
        VerificationRequest modified = requestRepo.update(request);
        if(modified != null) {
            Bug bug = request.getBug();
            Programmer programmer = request.getProgrammer();
            if(request.getStatus().equals(RequestStatus.ACCEPTED)){
                bug.setStatus(BugStatus.SOLVED);
                programmer.setFixedBugs(programmer.getFixedBugs() + 1);
                programmer.setScore(programmer.getScore() + score(bug.getPriority()));
            } else {
                bug.setStatus(BugStatus.ACTIVE);
            }
            try{
                updateBug(bug);
                programmerRepo.update(programmer);
            } catch (ServiceException e){
                e.printStackTrace();
            }
            notifyRequestUpdate(modified);
        }
        return modified;
    }

    private int score(BugPriority priority){
        int score = 0;
        if(priority.equals(BugPriority.LOW)) {
            score += 50;
        } else if(priority.equals(BugPriority.MEDIUM)){
            score += 100;
        } else {
            score += 200;
        }
        return score;
    }

}
