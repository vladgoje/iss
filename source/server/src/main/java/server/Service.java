package server;


import model.Bug;
import model.BugPriority;
import model.Programmer;
import model.Verifier;
import persistence.BugRepositoryInterface;
import persistence.ProgrammerRepositoryInterface;
import persistence.VerifierRepositoryInterface;
import services.ServiceException;
import services.IObserver;
import services.IService;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements IService {

    private final BugRepositoryInterface bugRepo;
    private final VerifierRepositoryInterface verifierRepo;
    private final ProgrammerRepositoryInterface programmerRepo;
    private Map<Long, IObserver> loggedClients;

    public Service(BugRepositoryInterface bugRepo,
                   VerifierRepositoryInterface verifierRepo,
                   ProgrammerRepositoryInterface programmerRepo){
        this.bugRepo = bugRepo;
        this.verifierRepo = verifierRepo;
        this.programmerRepo = programmerRepo;
        loggedClients = new ConcurrentHashMap<>();
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
    public void loginVerifier(Verifier verifier, IObserver client) throws ServiceException {
        Verifier v = verifierRepo.findByCredentials(verifier.getUsername(), hash(verifier.getPassword()));
        if(v != null){
            if(loggedClients.get(v.getId()) != null)
                throw new ServiceException("Verifier already logged in.");
            loggedClients.putIfAbsent(v.getId(), client);
        } else {
            throw new ServiceException("Invalid login credentials");
        }
    }

    @Override
    public void loginProgrammer(Programmer programmer, IObserver client) throws ServiceException {
        Programmer p = programmerRepo.findByCredentials(programmer.getUsername(), hash(programmer.getPassword()));
        if(p != null){
            if(loggedClients.get(p.getId()) != null)
                throw new ServiceException("Programmer already logged in.");
            loggedClients.putIfAbsent(p.getId(), client);
        } else {
            throw new ServiceException("Invalid login credentials");
        }
    }

    @Override
    public void logoutVerifier(Verifier verifier, IObserver client) throws ServiceException {
        Verifier v = verifierRepo.findByUsername(verifier.getUsername());
        if(v != null){
            if(loggedClients.get(v.getId()) == null)
                throw new ServiceException("Verifier not logged in.");
            loggedClients.remove(v.getId());
        }
    }

    @Override
    public void logoutProgrammer(Programmer programmer, IObserver client) throws ServiceException {
        Programmer p = programmerRepo.findByUsername(programmer.getUsername());
        if(p != null){
            if(loggedClients.get(p.getId()) == null)
                throw new ServiceException("Programmer not logged in.");
            loggedClients.remove(p.getId());
        }
    }

    @Override
    public void register(Verifier verifier) throws ServiceException {
        verifier.setPassword(hash(verifier.getPassword()));
        verifierRepo.save(verifier);
    }

    @Override
    public void registerBug(Bug bug) throws ServiceException {
        Bug added = bugRepo.save(bug);
        notifyBugAdded(added);
    }

    @Override
    public Bug updateBug(Bug bug) throws ServiceException {
        Bug modified = bugRepo.update(bug);
        if(modified != null) {
            notifyBugUpdate(modified);
            return modified;
        }
        return null;
    }

    @Override
    public List<Bug> getBugs() throws ServiceException {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public List<Bug> sortBugsByPriority() {
        return StreamSupport.stream(bugRepo.findAll().spliterator(),false)
                .sorted((x, y) -> {
                    if(x.getPriority().ordinal() < y.getPriority().ordinal())
                        return 1;
                    else if(x.getPriority().ordinal() > y.getPriority().ordinal())
                        return -1;
                    else
                        return 1;
                })
                .collect(Collectors.toList());
    }


    private void notifyBugAdded(Bug bug){
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (Map.Entry<Long, IObserver> entry : loggedClients.entrySet()){
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

        for (Map.Entry<Long, IObserver> entry : loggedClients.entrySet()){
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


}
