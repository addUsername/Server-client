package jardinserver;


import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author SERGI
 */
public class ConexionDB extends Thread{
    
    String user = "root";
    String password = "root";
    String url = "jdbc:mysql://127.0.0.1/jardineria";
    sharedString c;
    NotifyNewOrder n;
    Connection conexion;
    Statement stmt;
    
    public ConexionDB(sharedString c, NotifyNewOrder n){
    
        this.c=c;
        this.n=n;
        File credentials = new File ("configDB.txt");
        try {
            Scanner reading = new Scanner(credentials);
            user = reading.nextLine();
            password = reading.nextLine();
            url = reading.nextLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Estableciendo conexion..");
            conexion = DriverManager.getConnection(url, user, password);
            stmt = conexion.createStatement();
            if(conexion!=null)System.out.println("Conectado con exito");
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String loginDB(String a){
        
        String data=a.split("#")[1];
        String toReturn="";
        try {
            
            ResultSet rs = stmt.executeQuery("select login(\""+data+"\");");
            while(rs.next()){
                toReturn=rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    private String register(String a){
        
        String data=a.split("#")[1];
        String toReturn="";
        try {
            
            ResultSet rs = stmt.executeQuery("select register(\""+data+"\");");
            while(rs.next()){
                toReturn=rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    private String cities(){
        
        String toReturn="";
        try {
            ResultSet rs = stmt.executeQuery("select ciudad from oficina;");
            while(rs.next()){
                toReturn+="/"+rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(toReturn);
        return toReturn;
    }
    private String updateTabla(String a){
        
        String toReturn="";
        try {
            ResultSet rs = stmt.executeQuery("select codigo_producto,nombre,descripcion,precio_venta from producto where gama=\""+a.split("#")[1]+"\";");
            while(rs.next()){
                toReturn+=rs.getString(1);
                toReturn+="@@"+rs.getString(2);
                toReturn+="@@"+rs.getString(3);
                toReturn+="@@"+rs.getString(4)+"##";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(toReturn);
        return toReturn;
    }
    private String makeOrder(String a){
        
        String toReturn = "false";
        String [] params = a.split("#");
        String ID = params[1];
        String comments = params[5];
        String products = params[2];
        String quantity = params[3];
        String sum = params [4];
        try {
            ResultSet rs = stmt.executeQuery("select makeOrder("+ID+",'"+comments+"','"+products+"','"+quantity+"',"+sum+");");
            while(rs.next()){
                toReturn=rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;        
    }
    private String tablePedido(String a){
        String toReturn="";
        
        try {
            ResultSet rs = stmt.executeQuery("select codigo_pedido,fecha_pedido,fecha_esperada,fecha_entrega,estado,comentarios from pedido where codigo_cliente ="+a.split("#")[1]+";");
            
            while(rs.next()){
                toReturn+=rs.getString(1)+"/";
                toReturn+=rs.getString(2)+"/";
                toReturn+=rs.getString(3)+"/";
                toReturn+=rs.getString(4)+"/";
                toReturn+=rs.getString(5)+"/";
                toReturn+=rs.getString(6)+"#";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    private String detallePedido(String a){
        String toReturn="";
         try {
            ResultSet rs = stmt.executeQuery("select codigo_producto,cantidad,precio_unidad,numero_linea from detalle_pedido where codigo_pedido="+a.split("#")[1]+";");
            
            while(rs.next()){
                toReturn+=rs.getString(1)+"/";
                toReturn+=rs.getString(2)+"/";
                toReturn+=rs.getString(3)+"/";
                toReturn+=rs.getString(4)+"#";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public ConexionDB(){
    try {
        Class.forName("com.mysql.jdbc.Driver");
        conexion = DriverManager.getConnection(url, user, password);
        stmt = conexion.createStatement();
        if(conexion!=null)System.out.println("Conectado con exito (Server)");
    } catch (ClassNotFoundException | SQLException ex) {
        Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
    }
        
    }
    public Boolean checkStatusDB(){
        //no tiene sentido pero bueno.. xd
        try {
            ResultSet rs = stmt.executeQuery("SELECT 1");
            while(rs.next()){
                return (rs.getInt(1) == 1)? true:false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public String[][] downloadPedido(int id){
        
        String[][] toReturn=null;
        ArrayList<String []> list=new ArrayList();
        int codigo_pedido=0;
        try {
            ResultSet rs = stmt.executeQuery("select max(codigo_pedido) ,codigo_cliente from pedido where codigo_cliente="+id+";");
            while(rs.next()){
                codigo_pedido=rs.getInt(1);                
            }
            rs = stmt.executeQuery("select codigo_producto,cantidad,precio_unidad from detalle_pedido where codigo_pedido="+codigo_pedido+";");
            while(rs.next()){
                String [] line=new String[3];
                line[0]=rs.getString(1);
                line[1]=rs.getString(2);
                line[2]=rs.getString(3);
                list.add(line);
                
            }
            toReturn = new String[list.size()][3];
            int i=0;
            for(String[] row:list){
                toReturn[i]=row;
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        return toReturn;
    }
    @Override
    public void run(){
    
        while(true){            
        String a = c.read();
        
            switch(a.split("#")[0]){
                case "LOGIN":
                    c.write(loginDB(a));
                    break;
                case "REG":
                    c.write(register(a));
                    break;
                case "CITY":
                    c.write(cities());
                    break;
                case "TABLA":
                    c.write(updateTabla(a));
                    break;
                case "ORDER":                    
                    c.write(makeOrder(a));
                    n.write(a);
                    break;
                case "PEDIDO":
                    c.write(tablePedido(a));
                    break;
                case "DETALLE":
                    c.write(detallePedido(a));
                    break;
                    
            }        
        }
    }   
}
