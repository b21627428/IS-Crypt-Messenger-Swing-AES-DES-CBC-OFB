
import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client{


    private String username;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean connect;
    private HashMap<String, SecretKey> keys;
    private ClientGUI clientGUI;

    public static void main(String[] args) {
        new Client().listenSocket();
    }
    public Client(){
        try {
            prepareForConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void prepareForConnection() throws Exception{
        setClientGUI(new ClientGUI(this));
        getClientGUI().setVisible(true);
        setSocket(new Socket("localhost",3333));
        setKeys(new HashMap<>());
        setConnect(true);
        createStreams();
        getEncryptionDecryptionKeysFromServer();
        getUsernameFromGUI();
    }
    private void createStreams() throws Exception{
        setDataInputStream(new DataInputStream(getSocket().getInputStream()));
        setDataOutputStream(new DataOutputStream(getSocket().getOutputStream()));
    }
    private void getEncryptionDecryptionKeysFromServer(){
        try {
            ObjectInputStream objectInputStream= new ObjectInputStream(getDataInputStream());
            getKeys().put("AES",(SecretKey) objectInputStream.readObject());
            getKeys().put("DES",(SecretKey) objectInputStream.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getUsernameFromGUI(){
        String username = JOptionPane.showInputDialog("Enter user name:");
        if(username == null || "".equals(username.trim())) {
            System.exit(0);
        }
        setUsername(username);
    }


    public void listenSocket(){
        try {
            while (isConnect()){
                String cipherTextSenderUsername = getMsgFromServer();
                String cipherText = getMsgFromServer();
                String plainText = decryptCipherText(cipherText);
                getClientGUI().addMsgToChatBox(cipherTextSenderUsername,cipherText,plainText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeSocket();
        }
    }


    private String getMsgFromServer() throws IOException{
        return getDataInputStream().readUTF();
    }
    private String decryptCipherText(String cipherText) throws Exception{
        String method = getMethod();
        return EncryptionDecryption.decrypt(cipherText,getMode(),method,getKeys().get(method));
    }


    public String encryptPlainText(String plainText) throws Exception{
        String method = getMethod();
        return EncryptionDecryption.encrypt(plainText,getMode(),method,getKeys().get(method));
    }
    public void sendMsgToServer(String message) throws Exception{
        getDataOutputStream().writeUTF(message);
        getDataOutputStream().flush();
    }


    private String getMethod(){
        return getClientGUI().getAESRadioButton().isSelected() ? "AES":"DES";
    }
    private String getMode(){
        return getClientGUI().getOFBRadioButton().isSelected() ? "OFB": "CBC";
    }


    private void closeSocket(){
        try{
            getDataInputStream().close();
            getDataOutputStream().close();
            getSocket().close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }







    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public void setDataInputStream(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public HashMap<String, SecretKey> getKeys() {
        return keys;
    }

    public void setKeys(HashMap<String, SecretKey> keys) {
        this.keys = keys;
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }
}