package ChatClient;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;


public class ClientGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private String name, message;
    private Font meiryoFont = new Font("Meiryo", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);//top,r,b,l
    private Client chatClient;
    private JList<String> list;

    JTextArea textArea; // , userArea;
    JFrame frame;
    JButton privateMsgButton;
    private JButton startButton;
    private JButton sendButton;
    JPanel clientPanel, userPanel;


    public static void main(String[] args) {
        //set the look and feel to 'Nimbus'
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        new ClientGUI();
    }


    // Constructor of ClientGUI
    private ClientGUI() {
        frame = new JFrame("Client Chat Console");
        frame.setResizable(false);
        // Forbidden window resize (maximize)

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            // Close the window
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (chatClient != null) {
                    try {
                        chatClient.serverIF.clientLeave(name);
                    } catch (RemoteException e) {
                        JOptionPane.showMessageDialog(frame, "An error encountered: \n" + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        Container container = getContentPane();
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(getInputPanel(), BorderLayout.CENTER);
        panel.add(getTextPanel(), BorderLayout.NORTH);

        container.setLayout(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        container.add(getUsersPanel(), BorderLayout.WEST);

        frame.add(container);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setLocation(150, 150);
        textField.requestFocus();

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    private JPanel getTextPanel() {
        String welcome = "Welcome enter your name and press Start to begin\n";
        textArea = new JTextArea(welcome, 14, 34);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(meiryoFont);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JPanel textPanel = new JPanel();
        textPanel.add(scrollPane);

        textPanel.setFont(new Font("Meiryo", Font.PLAIN, 14));
        return textPanel;
    }


    private JPanel getInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        inputPanel.setBorder(blankBorder);
        textField = new JTextField();
        textField.setFont(meiryoFont);
        inputPanel.add(textField);
        return inputPanel;
    }


    private JPanel getUsersPanel() {
        userPanel = new JPanel(new BorderLayout());
        String userStr = " Current Users      ";

        JLabel userLabel = new JLabel(userStr, JLabel.CENTER);
        userPanel.add(userLabel, BorderLayout.NORTH);
        userLabel.setFont(new Font("Meiryo", Font.PLAIN, 16));

        String[] noClientsYet = {"No other users"};
        setClientPanel(noClientsYet);

        clientPanel.setFont(meiryoFont);
        userPanel.add(makeButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(blankBorder);

        return userPanel;
    }


    void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String s : currClients) {
            listModel.addElement(s);
        }
        if (currClients.length > 1) {
            privateMsgButton.setEnabled(true);
        }

        //Create the list and put it in a scroll pane.
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(meiryoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        userPanel.add(clientPanel, BorderLayout.CENTER);
    }


    private JPanel makeButtonPanel() {
        sendButton = new JButton("Send ");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);

        privateMsgButton = new JButton("Send PM");
        privateMsgButton.addActionListener(this);
        privateMsgButton.setEnabled(false);

        startButton = new JButton("Start ");
        startButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        buttonPanel.add(privateMsgButton);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(startButton);
        buttonPanel.add(sendButton);

        return buttonPanel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == startButton) {
                name = JOptionPane.showInputDialog(frame, "Pleas enter your name:" );
                if (name.length() != 0) {
                    frame.setTitle("LittleChatting - " + name);
                    textField.setText("");
                    // textArea.append("username : " + name + " connecting to chat...\n");
                    getConnected(name);
                    if (!chatClient.connectionProblem) {
                        startButton.setEnabled(false);
                        sendButton.setEnabled(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Enter your name to Start");
                }
            }

            if (e.getSource() == sendButton) {
                message = textField.getText();
                textField.setText("");
                sendMessage(message);
                System.out.println("Sending message : " + message);
            }

            if (e.getSource() == privateMsgButton) {
                int[] privateList = list.getSelectedIndices();

                for (int value : privateList) {
                    System.out.println("selected index :" + value);
                }
                message = textField.getText();
                textField.setText("");
                sendPrivate(privateList);
            }

        } catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        }

    }


    private void sendMessage(String chatMessage) throws RemoteException{
        chatClient.serverIF.msgToAll(name, chatMessage);
    }


    private void sendPrivate(int[] privateList) throws RemoteException{
        String privateMessage = "[PM from " + name + "] :" + message + "\n";
        chatClient.serverIF.msgToOne(privateList, privateMessage);
    }


    private void getConnected(String userName) throws RemoteException {
        //remove whitespace and non word characters to avoid malformed url
        String cleanedUserName;
        cleanedUserName = userName.replaceAll("\\W+", "_");
        try {
            chatClient = new Client(this, cleanedUserName);
            chatClient.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw e;
        }
    }
}










