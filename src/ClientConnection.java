import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class ClientConnection extends Thread {


    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean connect;
    private HashMap<String,SecretKey> keys;
    private ClientPage clientPage;

    public ClientConnection(Socket socket,ClientPage clientPage){
        try {
            this.clientPage =clientPage;
            prepareForConnection(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void prepareForConnection(Socket socket) throws Exception{
        setSocket(socket);
        setKeys(new HashMap<>());
        setConnect(true);
        createStreams();
        getEncryptionDecryptionKeysFromServer();
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            while (isConnect()){
                String username = getMsgFromServer();
                String cipherText = getMsgFromServer();
                clientPage.otherClientMsg(username,cipherText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }
    }

    private String getMsgFromServer() throws IOException{
        return getDataInputStream().readUTF();
    }


    public void sendMsgToServer(String message) throws Exception{
        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }

    public String encrypt(String plainText) throws Exception{
        String method = clientPage.getAESRadioButton().isSelected() ? "AES":"DES";
        String mode = clientPage.getOFBRadioButton().isSelected() ? "OFB": "CBC";
        return EncryptionDecryption.encrypt(plainText,mode,method,getKeys().get(method));
    }
    public String decrypt(String cipherText) throws Exception{
        String method = clientPage.getAESRadioButton().isSelected() ? "AES":"DES";
        String mode = clientPage.getOFBRadioButton().isSelected() ? "OFB": "CBC";
        return EncryptionDecryption.decrypt(cipherText,mode,method,getKeys().get(method));
    }


    public void exit(){
        try{
            getDataInputStream().close();
            getDataOutputStream().close();
            getSocket().close();
        }catch (IOException e){
            e.printStackTrace();
        }
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


}
