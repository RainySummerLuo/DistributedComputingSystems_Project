package ChatClient;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ClientInterface extends Remote {
    void getMsg(String message) throws RemoteException;

    void setClientlist(String[] currentUsers) throws RemoteException;
}