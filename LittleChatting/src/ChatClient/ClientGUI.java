package ChatClient;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.rmi.RemoteException;


public class ClientGUI extends JFrame implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private String name = "";//, message;
    private Font segeoFont = new Font("Segeo UI", Font.PLAIN, 14);
    private Border blankBorder = BorderFactory.createEmptyBorder(10, 10, 20, 10);//top,r,b,l
    private Client chatClient;

    JTextPane textPane; // , userArea;
    JFrame frame;
    //JButton privateMsgButton;
    private JButton fileBtn;
    private JButton msgBtn;
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

        container.setLayout(new BorderLayout(0,0));
        container.add(panel, BorderLayout.CENTER);
        container.add(getUsersPanel(), BorderLayout.WEST);

        frame.add(container);
        frame.pack();
        frame.setAlwaysOnTop(true);
        frame.setLocation(150, 150);
        textField.requestFocus();

        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        name = JOptionPane.showInputDialog(frame, "Pleas enter your name:");
        if (name.isEmpty()) {
            System.exit(0);
        }
        frame.setTitle(name + "'s console ");
        frame.setTitle("LittleChatting - " + name);
        textField.setText("");
        textPane.setText(textPane.getText() + "\n" + "username : " + name + " connecting to chat...\n");
        // textPane.append("username : " + name + " connecting to chat...\n");
        try {
            getConnected(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        frame.setVisible(true);
    }


    private JPanel getTextPanel() {
        String welcome = "Welcome enter your name and press Start to begin\n";
        textPane = new JTextPane();
        textPane.setText(welcome);
        textPane.setMargin(new Insets(0, 0, 0, 0));
        textPane.setFont(segeoFont);
        //textPane.setLineWrap(true);
        //textPane.setWrapStyleWord(true);
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);
        JPanel textPanel = new JPanel();
        textPanel.add(scrollPane);
        textPanel.setFont(segeoFont);
        return textPanel;
    }


    private JPanel getInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        inputPanel.setBorder(blankBorder);
        textField = new JTextField();
        textField.setFont(segeoFont);
        inputPanel.add(textField);
        
        textField.addKeyListener(this);
        
        return inputPanel;
    }


    private JPanel getUsersPanel() {
        userPanel = new JPanel(new BorderLayout());

        String[] noClientsYet = {"Broadcast"};
        setClientPanel(noClientsYet);

        clientPanel.setFont(segeoFont);
        userPanel.add(setButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBorder(blankBorder);

        return userPanel;
    }


    void setClientPanel(String[] currClients) {
        clientPanel = new JPanel(new BorderLayout());
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String s : currClients) {
            listModel.addElement(s);
        }
        /*if (currClients.length > 1) {
            privateMsgButton.setEnabled(true);
        }*/

        //Create the list and put it in a scroll pane.
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setVisibleRowCount(8);
        list.setFont(segeoFont);
        JScrollPane listScrollPane = new JScrollPane(list);

        //clientPanel.add(listScrollPane, BorderLayout.CENTER);
        //userPanel.add(clientPanel, BorderLayout.CENTER);
        userPanel.add(listScrollPane, BorderLayout.CENTER);
    }


    private JPanel setButtonPanel() {
        msgBtn = new JButton("Send ");
        msgBtn.addActionListener(this);
        msgBtn.setEnabled(true);

        fileBtn = new JButton("Send File");
        fileBtn.addActionListener(this);
        fileBtn.setEnabled(true);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
        //buttonPanel.add(privateMsgButton);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(msgBtn);
        buttonPanel.add(fileBtn);

        return buttonPanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == msgBtn) {
                String message = textField.getText();
                textField.setText("");
                sendMsg(message);
                System.out.println("Send Msg: " + message);
            }

            if (e.getSource() == fileBtn) {
                JFileChooser jf = new JFileChooser();
                jf.showOpenDialog(frame);
                File file = jf.getSelectedFile();
                System.out.println("Send File: " + file.getName());
                sendFile(fileTobyte(file.getAbsolutePath()), file.getName());
            }

            /*if (e.getSource() == privateMsgButton) {
                int[] privateList = list.getSelectedIndices();

                for (int value : privateList) {
                    System.out.println("selected index :" + value);
                }
                message = textField.getText();
                textField.setText("");
                sendPrivate(privateList);
            }*/
        } catch (RemoteException remoteExc) {
            remoteExc.printStackTrace();
        }
    }

    private byte[] fileTobyte(String filePath) {
        try {
            System.out.println(filePath + "\n");
            File file = new File(filePath);
            if (file.length() > Integer.MAX_VALUE) {
                JOptionPane.showMessageDialog(frame, "The file is too large.");
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size;
            while ((size = fis.read(temp)) != -1) {
                baos.write(temp, 0, size);
            }
            fis.close();
            byte[] fileBytes;
            fileBytes = baos.toByteArray();
            return fileBytes;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "File Not Found:\n" + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "IOException:\n" + e.getMessage());
        }
        return null;
    }


    private void sendMsg(String chatMessage) throws RemoteException {
        chatClient.serverIF.msgToAll(name, chatMessage);
    }

    private void sendFile(byte[] fileBytes, String fileName) throws RemoteException {
        chatClient.serverIF.fileToAll(name, fileBytes, fileName);
    }

    /*
    private void sendPrivate(int[] privateList) throws RemoteException {
        String privateMessage = "[PM from " + name + "] :" + message + "\n";
        chatClient.serverIF.msgToOne(privateList, privateMessage);
    }
    */

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


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(keyCode == KeyEvent.VK_ENTER) {
	        try {
	            String message = textField.getText();
	            textField.setText("");
				sendMsg(message);
				System.out.println("Send Msg: " + message);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}











