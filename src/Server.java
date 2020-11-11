
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

public class Server{

    private ServerSocket mainSocket;
    private ArrayList<ServerConnection> serverConnectionArrayList;
    private boolean connect;
    private Map<String,SecretKey> encryptionDecryptionKeys;

    public static void main(String[] args) {
        new Server().run();
    }

    public Server(){
        try {
            prepareForConections();
        } catch (Exception e) {
            e.printStackTrace();
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
        SecretKey key;
        SecureRandom rand = new SecureRandom();
        rand.setSeed(1);
        KeyGenerator generator = KeyGenerator.getInstance(method);
        generator.init(size, rand);
        key = generator.generateKey();
        return key;
    }

    public void run(){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
            writeKeysToFile(writer);
            while(isConnect()){
                ServerConnection serverConnection = new ServerConnection(getMainSocket().accept(), this,writer);
                serverConnection.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeKeysToFile(BufferedWriter writer){
        try {
            String encodedKeyAES = Base64.getEncoder().encodeToString(getEncryptionDecryptionKeys().get("AES").getEncoded());
            String encodedKeyDES = Base64.getEncoder().encodeToString(getEncryptionDecryptionKeys().get("DES").getEncoded());
            writer.write("AES Key: "+encodedKeyAES+"\nDES Key: "+encodedKeyDES+"\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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