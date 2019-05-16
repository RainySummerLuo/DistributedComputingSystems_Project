package ChatClient;

import ChatServer.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;


public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final long serialVersionUID = 7468891722773409712L;
    private ClientGUI chatGUI;
    private String clientServiceName;
    private String name;
    ServerInterface serverIF;
    private boolean connectionProblem = false;


    Client(ClientGUI aChatGUI, String userName) throws RemoteException {
        super();
        this.chatGUI = aChatGUI;
        this.name = userName;
        this.clientServiceName = "ClientListenService_" + userName;
    }

    void startClient() throws RemoteException {
        String hostName = "localhost";
        String[] details = {name, hostName, clientServiceName};

        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            String serviceName = "ChatService";
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
            regClient(details);
        }
        System.out.println("Client Listen RMI Server is running...\n");
    }


    private void regClient(String[] details) {
        try {
            serverIF.getClientInfo(this.ref);
            serverIF.registerClient(details);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getMsg(String message) {
        chatGUI.textArea.append(message);
        //make the gui display the last appended text, ie scroll to bottom
        chatGUI.textArea.setCaretPosition(chatGUI.textArea.getDocument().getLength());
    }


    @Override
    public void getFile(String clientName, byte[] fileBytes, String fileName) {
        chatGUI.textArea.append(clientName + ": " + "I have sent you a file.");
        try {
            Desktop.getDesktop().open(new File(Objects.requireNonNull(byteTofile(fileBytes, fileName))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setClientlist(String[] currentUsers) {
        /*if (currentUsers.length < 2) {
            chatGUI.privateMsgButton.setEnabled(false);
        }*/
        chatGUI.userPanel.remove(chatGUI.clientPanel);
        chatGUI.setClientPanel(currentUsers);
        chatGUI.clientPanel.repaint();
        chatGUI.clientPanel.revalidate();
    }


    private String byteTofile(byte[] fileBytes, String fileName) {
        try {
            File directory = new File("");
            String filePath = directory.getCanonicalPath() + "\\" + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(fileBytes);
            fos.close();
            return filePath;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File Not Found:\n" + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IOException:\n" + e.getMessage());
        }
        return null;
    }
}