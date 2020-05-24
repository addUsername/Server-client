
package jardin;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author SERGI
 */
public class Conexion {
    
public static String send(String data){
    
    int port = 1024;
    String ip = "127.0.0.1";
    DataInputStream in = null;
    String toReturn="";
    
    try {
        
        Socket client = new Socket(ip, port);

        OutputStream outToServer = client.getOutputStream();
        DataOutputStream out = new DataOutputStream(outToServer);

        out.writeUTF(data);
        InputStream inFromServer = client.getInputStream();
        in = new DataInputStream(inFromServer);
         
        toReturn=in.readUTF();
        client.close();
        } catch (IOException e) {
         e.printStackTrace();
        }    
    return toReturn;
}

    public static Boolean sendLogin(String[] data){

        String toReturn="LOGIN#";
        for(String s:data){
            toReturn+=s+"/";
        }
        return (send(toReturn.substring(0, toReturn.length()-1)).equals("true"));
    }
    public static Boolean sendReg(String [] data){
        
        String toReturn="REG#";
        for(String s:data){
            toReturn+=s+"/";
        }
        return (send(toReturn.substring(0, toReturn.length()-1)).equals("true"));
    }
    public static String[] getCities(){
        
        String fromDB = send("CITY#");
        String[] toReturn = fromDB.split("/");
        return toReturn;
    }
    
    public static Object[][] getTableValues(String data){
        
        //mejorar esto.. es tarde
        String BIG = send("TABLA#"+data);
        String[] rows=BIG.split("##");
        int i=rows.length;
        Object[][] toReturn = new Object [i][6];
        
        for(int j=0; j<i; j++){
            Object[] toList=new Object[6];
            String[] split=rows[j].split("@@");
            toList[0]=split[0];
            toList[1]=split[1];
            toList[2]=split[2];
            toList[3]=Double.valueOf(split[3]);
            toList[4]="";
            toList[5]=false;
            toReturn[j]=toList;
        }
        return toReturn;
    }
    public static Object[][] getOrderValues(String data){
        String BIG =send(data);
        String[] rows =  BIG.split("#");
        Object [][] toReturn= new Object [rows.length][6];
        for(int i=0;i<rows.length;i++){
            String [] a = rows[i].split("/");
            toReturn[i][0]=a[0];
            toReturn[i][1]=a[1];
            toReturn[i][2]=a[2];
            toReturn[i][3]=a[3];
            toReturn[i][4]=a[4];
            toReturn[i][5]=a[5];    
        }
        return toReturn;
    }
    public static Object[][] getDetailValues(String data){
        String BIG =send(data);
        String[] rows =  BIG.split("#");
        Object [][] toReturn= new Object [rows.length][4];
        for(int i=0;i<rows.length;i++){
            String [] a = rows[i].split("/");
            toReturn[i][0]=a[0];
            toReturn[i][1]=a[1];
            toReturn[i][2]=a[2];
            toReturn[i][3]=a[3];    
        }
        return toReturn;
    }
    
    public static Boolean makeOrder(String data){
        
        return (send(data).equals("true"));
    }
}
    

    

