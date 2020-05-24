package jardinserver;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
/**
 *
 * @author SERGI
 */
public class GUI {
    
    private TrayIcon icon;    
    private MenuItem pedido;
    private ConexionDB db = new ConexionDB();
    private String down = "<html>";
    public GUI(){
        
        Image image = null;
        java.net.URL img = getClass().getResource("icon.png");
        if(img==null){
            image = Toolkit.getDefaultToolkit().getImage("icon.png");
        }else{
            image = Toolkit.getDefaultToolkit().getImage(img);
        }
        
        icon =new TrayIcon(image);
        icon.setToolTip("JardinServer");
        
        ActionListener myListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                switch(e.getActionCommand()){
                    case "Exit":
                        System.exit(0);
                    case "Status":
                        Boolean statusDB = db.checkStatusDB();
                        Boolean statusSS = ConexionClient.checkStatusSS();
                        
                        icon.displayMessage("Status", 
                        "BBDD: "+((statusDB)? "ON":"OFF")+"\nSERVER: "+((statusSS)? "ON":"OFF"),
                        TrayIcon.MessageType.NONE);
                    
                    case "Descargar":
                        download();
                        down="";
                        pedido.setEnabled(false);                        
                        break;
                }
            }
        };
        
        PopupMenu popup = new PopupMenu();
        
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(myListener);
        
        MenuItem status = new MenuItem("Status");
        status.addActionListener(myListener);
        
        pedido = new MenuItem("Descargar");
        pedido.addActionListener(myListener);
        pedido.setEnabled(false);            
        
        //pedidos.add(pedido);
        popup.add(pedido);
        popup.add(status);
        popup.add(exit);
        
        icon.setImageAutoSize(true);
        icon.addActionListener(myListener);
        icon.setPopupMenu(popup);
        
        SystemTray tray = SystemTray.getSystemTray();
        
        try {
            tray.add(icon);
        } catch (AWTException e) {}
    }
    public void ShowMessage(String [] order){
        icon.displayMessage("Nueva orden recibida!!", 
                 "ID Cliente: "+order[0]+"\nComentarios: "+order[1]+"\nDescarge el pedido para mas información",
                        TrayIcon.MessageType.NONE);  
        updateOrders(order[0]);
    }
    public void updateOrders(String id){
        String[][] download = db.downloadPedido(Integer.parseInt(id));
        Double total=0.0;
        down+=new Date()+"<h2>Cliente: "+id+"</h2>"
                    + "<table><tr>"
                    + "<th style=' border: 1px solid black;'>Codigo producto</th>"
                    + "<th style='border: 1px solid black;'>Cantidad</th>"
                    +"<th style='border: 1px solid black;'>Precio</th>"
                    + "</tr>";
        for(String [] line:download){
            down+="<tr><td>"+line[0]+"</td><td>"+line[1]+"</td><td>"+line[2]+"</td>";
                total += Integer.parseInt(line[1])*Double.parseDouble(line[2]);
        }
        down+="</table><br/>Total = "+total+"<br/>";
        pedido.setEnabled(true);        
    }
    public void download(){
        File dir=new File("Pedidos");
        dir.mkdir();
        File file=new File(dir.getAbsolutePath()+File.separator+"Pedido"+dir.list().length+1+".html");
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(down);
            fw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
