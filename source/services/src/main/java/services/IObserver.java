package services;

import model.Bug;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IObserver extends Remote {
     void newBugAdded(Bug bug) throws ServiceException, RemoteException;
     void newBugUpdate(Bug bug) throws ServiceException, RemoteException;
}
