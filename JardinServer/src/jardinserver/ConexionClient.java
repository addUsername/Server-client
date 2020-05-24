package jardinserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SERGI
 */
public class ConexionClient extends Thread {
    
    int puerto = 1024;
    ServerSocket ss;
    sharedString c;

    public ConexionClient (sharedString c){

        this.c=c;
        try {
            ss = new ServerSocket(puerto);
            ss.setSoTimeout(100000);
        } catch (IOException ex) {
            Logger.getLogger(ConexionClient.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }
    public static boolean checkStatusSS() {
        int port = 1024;
        String ip = "127.0.0.1";
        try (Socket s = new Socket(ip, port)) {
            return true;
        } catch (IOException ex) {}
        return false;
    }
    @Override
    public void run(){

        while(true){
            try {
                //ponemos a la escucha
                Socket server = ss.accept();

                //recibimos magia
                DataInputStream in = new DataInputStream(server.getInputStream());
                String fromDB = c.writeANDread(in.readUTF());

                //enviamos magia
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                out.writeUTF(fromDB);

                server.close();
            } catch (IOException ex) {
                Logger.getLogger(ConexionClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}