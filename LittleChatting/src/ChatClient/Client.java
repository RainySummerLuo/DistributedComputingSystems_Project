package ChatClient;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import ChatServer.ServerInterface;


public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final long serialVersionUID = 7468891722773409712L;
    ClientGUI chatGUI;
    private String hostName = "localhost";
    private String serviceName = "GroupChatService";
    private String clientServiceName;
    private String name;
    ServerInterface serverIF;
    boolean connectionProblem = false;


    public Client(ClientGUI aChatGUI, String userName) throws RemoteException {
        super();
        this.chatGUI = aChatGUI;
        this.name = userName;
        this.clientServiceName = "ClientListenService_" + userName;
    }


    public void startClient() throws RemoteException {
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            serverIF = (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        } catch (ConnectException e) {
            JOptionPane.showMessageDialog(
                    chatGUI.frame, "The server seems to be unavailable\nPlease try later",
                    "Connection problem", JOptionPane.ERROR_MESSAGE);
            connectionProblem = true;
            e.printStackTrace();
        } catch (NotBoundException | MalformedURLException me) {
            connectionProblem = true;
            me.printStackTrace();
        }
        if (!connectionProblem) {
            registerWithServer(details);
        }
        System.out.println("Client Listen RMI Server is running...\n");
    }


    private void registerWithServer(String[] details) {
        try {
            serverIF.passIdentity(this.ref);//now redundant ??
            serverIF.registerListener(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void messageFromServer(String message) throws RemoteException {
        System.out.println(message);
        chatGUI.textArea.append(message);
        //make the gui display the last appended text, ie scroll to bottom
        chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
    }


    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {

        if (currentUsers.length < 2) {
            chatGUI.privateMsgButton.setEnabled(false);
        }
        chatGUI.userPanel.remove(chatGUI.clientPanel);
        chatGUI.setClientPanel(currentUsers);
        chatGUI.clientPanel.repaint();
        chatGUI.clientPanel.revalidate();
    }
}
