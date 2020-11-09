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


    public ServerConnection(Socket socket, Server server,BufferedWriter bufferedWriter) throws IOException {
        try {
            setBufferedWriter(bufferedWriter);
            setSocket(socket);
            setServer(server);
            prepareForServerConnections();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run(){
        try {
            while(isConnect()){
                String username = getDataInputStream().readUTF();
                String cipherText = getDataInputStream().readUTF();
                System.out.println(username+"\n"+cipherText); // Konsolo ve loga yazma kısmı
                getBufferedWriter().write(username+"\n"+cipherText+"\n");
                getBufferedWriter().flush();
                broadcast(username,cipherText);
            }
            closeSocket();
        } catch (Exception e) {
        }
    }
    private void prepareForServerConnections() throws Exception{
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

    private void broadcast(String username,String msg){
        for (ServerConnection sc: getServer().getServerConnectionArrayList()){
            try {
                sc.getDataOutputStream().writeUTF(username);
                sc.getDataOutputStream().writeUTF(msg);
                sc.getDataOutputStream().flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void closeSocket(){
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
