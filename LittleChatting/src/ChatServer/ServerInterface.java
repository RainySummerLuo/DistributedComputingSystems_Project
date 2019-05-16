package ChatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;


public interface ServerInterface extends Remote {
    void getClientInfo(RemoteRef ref) throws Exception;

    void registerClient(String[] details) throws RemoteException;

    void msgToAll(String userName, String chatMessage) throws RemoteException;

    void msgToOne(int[] privateGroup, String privateMessage) throws RemoteException;

    void clientLeave(String userName) throws RemoteException;
}
