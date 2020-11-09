
import javax.swing.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{


    private ClientConnection clientConnection;
    private String username;

    public static void main(String[] args) {
        new Client();
    }
    public Client(){
        try {
            ClientPage cp = new ClientPage(this);
            cp.setVisible(true);
            setUsername(JOptionPane.showInputDialog("Enter user name:"));

            setClientConnection(new ClientConnection(new Socket("localhost", 3333),cp));
            getClientConnection().start();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}