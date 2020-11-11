import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerConnection extends Thread {

    private Socket socket;
    private Server server;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean connect;
    private BufferedWriter bufferedWriter;


    public ServerConnection(Socket socket, Server server,BufferedWriter bufferedWriter) {
        try {
            prepareForServerConnections(socket,server,bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void prepareForServerConnections(Socket socket,Server server,BufferedWriter bufferedWriter) throws Exception{
        setBufferedWriter(bufferedWriter);
        setSocket(socket);
        setServer(server);
        server.getServerConnectionArrayList().add(this);
        setConnect(true);
        createStreams();
        sendKeysToClient();
    }
    private void createStreams() throws Exception{
        setDataInputStream(new DataInputStream(getSocket().getInputStream()));
        setDataOutputStream(new DataOutputStream(getSocket().getOutputStream()));
    }
    private void sendKeysToClient() throws Exception{
        Map<String, SecretKey> encryptionDecryptionKeys = getServer().getEncryptionDecryptionKeys();
        if(encryptionDecryptionKeys.containsKey("AES") && encryptionDecryptionKeys.containsKey("DES")){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(getDataOutputStream());
            objectOutputStream.writeObject(encryptionDecryptionKeys.get("AES"));
            objectOutputStream.writeObject(encryptionDecryptionKeys.get("DES"));
        }
    }



    @Override
    public void run(){
        try {
            while(isConnect()){
                String cipherTextSenderUsername = getDataInputStream().readUTF();
                String cipherText = getDataInputStream().readUTF();

                // Konsolo ve log file yazma kısmı
                System.out.println(cipherTextSenderUsername+"\n"+cipherText);
                getBufferedWriter().write(cipherTextSenderUsername+"\n"+cipherText+"\n");
                getBufferedWriter().flush();

                broadcastMessageToAllClients(cipherTextSenderUsername,cipherText);
            }
        } catch (Exception e) {
            closeSocket();
        }
    }


    private void broadcastMessageToAllClients(String cipherTextSenderUsername,String cipherText){
        for (ServerConnection serverConnection : getServer().getServerConnectionArrayList()) {
            serverConnection.sendMessageToClient(cipherTextSenderUsername, cipherText);
        }

    }
    private void sendMessageToClient(String cipherTextSenderUsername,String cipherText){
        try {
            getDataOutputStream().writeUTF(cipherTextSenderUsername);
            getDataOutputStream().writeUTF(cipherText);
            getDataOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void closeSocket(){
        try {
            getDataInputStream().close();
            getDataOutputStream().close();
            getSocket().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
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

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }
}
