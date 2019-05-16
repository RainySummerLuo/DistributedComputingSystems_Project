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


public class Server extends UnicastRemoteObject implements ServerInterface {
    private Vector<Chat> chatClient;
    private static final long serialVersionUID = 1L;


    private Server() throws RemoteException {
        super();
        chatClient = new Vector<>(10, 1);
    }

    public static void main(String[] args) {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "ChatService";

        if (args.length == 2) {
            hostName = args[0];
            serviceName = args[1];
        }

        try {
            ServerInterface chatServer = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName, chatServer);
            System.out.println("[100%] Chatting Server is running...");
        } catch (Exception e) {
            System.out.println("[Error] Chatting Server had problems starting.");
        }
    }

    private static void startRMIRegistry() {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("[50%] RMI Server is ready.");
        } catch (RemoteException e) {
            System.out.println("[Error] RMI Server has problems starting.");
            e.printStackTrace();
        }
    }


    private void registerChatter(String[] details) {
        try {
            ClientInterface nextClient = (ClientInterface) Naming.lookup("rmi://" + details[1] + "/" + details[2]);
            chatClient.addElement(new Chat(details[0], nextClient));
            nextClient.getMsg("[Server] : Hello " + details[0] + " you are now free to chat.\n");
            msgToAll(details[0] + " has joined the group.\n");
            setClientlist();
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            System.out.println("[Error] Error encountered in bounding client.");
            e.printStackTrace();
        }
    }

    /* ----- Serverside ----- */
    @Override
    public void getClientInfo(RemoteRef ref) {
        try {
            System.out.println(ref.toString());
        } catch (Exception e) {
            System.out.println("[Error] Error encountered in func:passIdentity()");
            e.printStackTrace();
        }
    }


    @Override
    public void registerClient(String[] details) {
        System.out.println(new Date(System.currentTimeMillis()));
        System.out.println(details[0] + " has joined the chat session");
        System.out.println(details[0] + "'s hostname : " + details[1]);
        System.out.println(details[0] + "'sRMI service : " + details[2]);
        registerChatter(details);
    }


    private void setClientlist() {
        String[] allUsers = new String[chatClient.size()];
        for (int i = 0; i < allUsers.length; i++) {
            allUsers[i] = chatClient.elementAt(i).getName();
        }
        for (Chat c : chatClient) {
            try {
                c.getClient().setClientlist(allUsers);
            } catch (RemoteException e) {
                System.out.println("[Error] Error encountered in setClientUserList().");
                e.printStackTrace();
            }
        }
    }


    @Override
    public void msgToAll(String name, String nextPost) {
        String message = name + " : " + nextPost + "\n";
        for (Chat c : chatClient) {
            try {
                c.getClient().getMsg(message);
            } catch (RemoteException e) {
                System.out.println("[Error] Error encountered in sendToAll().");
                e.printStackTrace();
            }
        }
    }

    void msgToAll(String msg) {
        msgToAll("Server", msg);
    }


    @Override
    public void msgToOne(int[] privateGroup, String privateMessage) throws RemoteException {
        Chat pc;
        for (int i : privateGroup) {
            pc = chatClient.elementAt(i);
            pc.getClient().getMsg(privateMessage);
        }
    }


    @Override
    public void clientLeave(String userName) {
        for (Chat c : chatClient) {
            if (c.getName().equals(userName)) {
                System.out.println(userName + " left the chat session");
                System.out.println(new Date(System.currentTimeMillis()));
                msgToAll(userName + " left the chat session");
                chatClient.remove(c);
                break;
            }
        }
        if (!chatClient.isEmpty()) {
            setClientlist();
        }
    }
}
