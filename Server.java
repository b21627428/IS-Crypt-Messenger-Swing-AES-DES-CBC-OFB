
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server{

    private ServerSocket mainSocket;
    private ArrayList<ServerConnection> serverConnectionArrayList;
    private boolean connect;
    private Map<String,SecretKey> encryptionDecryptionKeys;

    public static void main(String[] args) {
        new Server();
    }

    public Server(){
        BufferedWriter writer = null;
        try {
            prepareForConections();
            writer = new BufferedWriter(new FileWriter("log.txt"));
            while(isConnect()){
                ServerConnection serverConnection = new ServerConnection(getMainSocket().accept(), this,writer);
                serverConnection.start();
                getServerConnectionArrayList().add(serverConnection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void prepareForConections() throws Exception{
        setConnect(true);
        setEncryptionDecryptionKeys(new HashMap<>());
        setServerConnectionArrayList(new ArrayList<>());
        setMainSocket(new ServerSocket(3333));
        createKeys();
    }
    private void createKeys() throws Exception{
        getEncryptionDecryptionKeys().put("AES",generateKey("AES",256));
        getEncryptionDecryptionKeys().put("DES",generateKey("DES",56));
    }
    private SecretKey generateKey(String method,int size) throws Exception{
        Key key;
        SecureRandom rand = new SecureRandom();
        rand.setSeed(1);
        KeyGenerator generator = KeyGenerator.getInstance(method);
        generator.init(size, rand);
        key = generator.generateKey();
        return (SecretKey) key;
    }



    public ServerSocket getMainSocket() {
        return mainSocket;
    }

    public void setMainSocket(ServerSocket mainSocket) {
        this.mainSocket = mainSocket;
    }

    public ArrayList<ServerConnection> getServerConnectionArrayList() {
        return serverConnectionArrayList;
    }

    public void setServerConnectionArrayList(ArrayList<ServerConnection> serverConnectionArrayList) {
        this.serverConnectionArrayList = serverConnectionArrayList;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public Map<String, SecretKey> getEncryptionDecryptionKeys() {
        return encryptionDecryptionKeys;
    }

    public void setEncryptionDecryptionKeys(Map<String, SecretKey> encryptionDecryptionKeys) {
        this.encryptionDecryptionKeys = encryptionDecryptionKeys;
    }
}