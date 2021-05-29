package services;

import model.Bug;
import model.Programmer;
import model.VerificationRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObserver extends Remote {
     void newBugAdded(Bug bug) throws ServiceException, RemoteException;
     void newBugUpdate(Bug bug) throws ServiceException, RemoteException;
     void newRequestAdded(VerificationRequest request) throws ServiceException, RemoteException;
     void newRequestUpdate(VerificationRequest request) throws ServiceException, RemoteException;
     void newBugPending(Bug bug, Programmer programmer) throws ServiceException, RemoteException;
}
