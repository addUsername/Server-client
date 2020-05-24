package jardinserver;

/**
 *
 * @author SERGI
 */
public class GodThread extends Thread{
    
    NotifyNewOrder n;
    GUI gui;
    
    public GodThread(NotifyNewOrder n,GUI gui){
        
        this.n=n;
        this.gui=gui;        
    }
    @Override
    public void run(){
        while(true){        
            String [] params = n.read().split("#");
            String [] message = new String[2];
            message[0] = params[1];
            message[1] = params[5];
            gui.ShowMessage(message);
            
        }
    }    
}
