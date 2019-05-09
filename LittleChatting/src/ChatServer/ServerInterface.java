package ChatServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;


public interface ServerInterface extends Remote {
    void updateChat(String userName, String chatMessage) throws RemoteException;

    void passIdentity(RemoteRef ref) throws RemoteException;

    void registerListener(String[] details) throws RemoteException;

    void leaveChat(String userName) throws RemoteException;

    void sendPM(int[] privateGroup, String privateMessage) throws RemoteException;
}


