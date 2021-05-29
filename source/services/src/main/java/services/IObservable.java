package services;

public interface IObservable {
    public void addObserver(String username, IObserver observer) throws ServiceException;
    public void removeObserver(String username) throws ServiceException;
}
