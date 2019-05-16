package ChatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;


public interface ServerInterface extends Remote {
    void getClientInfo(RemoteRef ref) throws Exception;

    void registerClient(String[] details) throws RemoteException;

    void msgToAll(String clientName, String msg) throws RemoteException;

    void msgToOne(String clientName, String destName, String msg) throws RemoteException;

    void fileToAll(String clientName, byte[] fileBytes, String fileName) throws RemoteException;

    void fileToOne(String clientName, String destName, byte[] fileBytes, String fileName) throws RemoteException;

    void clientLeave(String userName) throws RemoteException;
}
