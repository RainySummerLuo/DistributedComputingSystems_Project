package ChatServer;

import ChatClient.ClientInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;
// import java.util.Vector;

// import ChatClient.ClientInterface;


public class Server extends UnicastRemoteObject implements ServerInterface {
    private String line = "---------------------------------------------\n";
    private Vector<Chat> chatters;
    private static final long serialVersionUID = 1L;


    private Server() throws RemoteException {
        super();
        chatters = new Vector<>(10, 1);
    }


    public static void main(String[] args) {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "GroupChatService";

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ServerInterface hello = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
            System.out.println("Group Chat RMI Server is running...");
        } catch (Exception e) {
            System.out.println("Server had problems starting");
        }
    }


    private static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI Server ready");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*
    public String sayHello(String ClientName) throws RemoteException {
        System.out.println(ClientName + " sent a message");
        return "Hello " + ClientName + " from group chat server";
    }
    */


    @Override
    public void updateChat(String name, String nextPost) {
        String message = name + " : " + nextPost + "\n";
        sendToAll(message);
    }


    @Override
    public void passIdentity(RemoteRef ref) {
        //System.out.println("\n" + ref.remoteToString() + "\n");
        try {
            System.out.println(line + ref.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void registerListener(String[] details) {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        registerChatter(details);
    }


    private void registerChatter(String[] details) {
        try {
            ClientInterface nextClient = (ClientInterface) Naming.lookup("rmi://" + details[1] + "/" + details[2]);

            chatters.addElement(new Chat(details[0], nextClient));

            nextClient.messageFromServer("[Server] : Hello " + details[0] + " you are now free to chat.\n");

            sendToAll("[Server] : " + details[0] + " has joined the group.\n");

            updateUserList();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }


    private void updateUserList() {
        String[] currentUsers = getUserList();
        for (Chat c : chatters) {
            try {
                c.getClient().updateUserList(currentUsers);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private String[] getUserList() {
        // generate an array of current users
        String[] allUsers = new String[chatters.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = chatters.elementAt(i).getName();
        }
        return allUsers;
    }


    private void sendToAll(String newMessage) {
        for (Chat c : chatters) {
            try {
                c.getClient().messageFromServer(newMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void leaveChat(String userName) {
        for (Chat c : chatters) {
            if (c.getName().equals(userName)) {
                System.out.println(line + userName + " left the chat session");
                System.out.println(new Date(System.currentTimeMillis()));
                chatters.remove(c);
                break;
            }
        }
        if (!chatters.isEmpty()) {
            updateUserList();
        }
    }


    @Override
    public void sendPM(int[] privateGroup, String privateMessage) throws RemoteException {
        Chat pc;
        for (int i : privateGroup) {
            pc = chatters.elementAt(i);
            pc.getClient().messageFromServer(privateMessage);
        }
    }
}
