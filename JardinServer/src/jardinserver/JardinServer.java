package jardinserver;

import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author SERGI
 */
public class JardinServer {    
    
    public static void main(String[] args) {
        
        
               
        sharedString c = new sharedString();
        NotifyNewOrder n = new NotifyNewOrder();
        
        Thread serverSocket = new Thread(new ConexionClient(c),"Socket");
        serverSocket.start();
        
        Thread serverDB = new Thread(new ConexionDB(c,n),"DB");
        serverDB.start();
        GUI icon = new GUI();
        
        Thread GOD = new Thread(new GodThread(n,icon),"GOD");
        GOD.start();
    }    
}

class sharedString{
    
    public String string="";
    public sharedString(){}

    synchronized String writeANDread(String fromClient){
        //Aqui solo entra serversocket
        this.string = fromClient;
        System.out.println("socket escribe linea: "+this.string);
        notify();
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(JardinServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.string;
    }

    synchronized String read(){
        try {
            //aqui solo entra db
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(sharedString.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.string;      
    }
    synchronized void write(String fromDB){
        //aqui solo entra db
        this.string=fromDB;
        System.out.println("DB escribe y notifica.. linea: "+this.string);
        notify();            
    }    
}

class NotifyNewOrder{
    
    public String newOrder;
    
    NotifyNewOrder(){}
    
    synchronized String read(){
        try {
            //aqui solo entra GUI
            System.out.println("GUI ESPERA");
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(sharedString.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.newOrder;      
    }
    synchronized void write(String fromDB){
        //aqui solo entra db
        System.out.println("entra DB y escribe en notifynewordeeer");
        this.newOrder=fromDB;
        System.out.println("DB AVISA");
        notify();
    }
}
