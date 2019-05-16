package ChatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;


public interface ServerInterface extends Remote {
    void getClientInfo(RemoteRef ref) throws Exception;

    void registerClient(String[] details) throws RemoteException;

    void msgToAll(String clientName, String msg) throws RemoteException;

    void msgToOne(int[] clientGroup, String msg) throws RemoteException;

    void fileToAll(String clientName, byte[] fileBytes, String fileName) throws RemoteException;

    void fileToOne(int[] clientGroup, byte[] fileBytes, String filePath) throws RemoteException;

    void clientLeave(String userName) throws RemoteException;
}
