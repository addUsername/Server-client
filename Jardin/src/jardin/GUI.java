package jardin;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author SERGI
 */
public class GUI extends JFrame{
    
    CardLayout layout = new CardLayout();
    ButtonsListener myListener = new ButtonsListener();
    
    JPanel ppal;
    LoginPanel login;
    RegisterPanel register;
    MenuBar menuBar;
    Stock stock;
    static String ID;    
    
    public GUI(){
        
        setTitle("Identificacion");
        setSize(250,300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setLocationRelativeTo(null);
//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        //setIconImage(new ImageIcon(ImageIO.read("res/icon.png")));
        menuBar = new MenuBar();
        menuBar.setVisible(false);
        add(menuBar);
        setJMenuBar(menuBar);
        
        ppal = new JPanel();
        ppal.setLayout(layout);
        ppal.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        login = new LoginPanel();
        register = new RegisterPanel();        
        stock = new Stock();
        
        ppal.add(login);
        ppal.add(register);
        ppal.add(stock,"stock");
        
        getRootPane().setDefaultButton(login.log);
        setResizable(false);
        
        add(ppal);
    }
    
    class LoginPanel extends JPanel{
        
        private final JTextField userInput;
        private final JPasswordField passInput;
        //para poder ponerlo focused
        public JButton log;
        public LoginPanel(){
            
            setLayout(new GridLayout(6, 1, 5, 5));
            
            JLabel user = new JLabel("Usuario");
            userInput = new JTextField (20);

            JLabel pass = new JLabel("Contraseña");
            passInput = new JPasswordField (20);

            JPanel buttons = new JPanel(); 
            
            log = new JButton("Login");
            log.addActionListener(myListener);
            log.requestFocus();
            
            JButton newUser = new JButton("Registrarse");
            newUser.addActionListener(myListener);
            
            JLabel error = new JLabel ("<html><font color=red>Usuario/contraseña incorrectos</font></html>");
            error.setVisible(false);
            buttons.add(log);
            buttons.add(newUser);
                
            add(user);
            add(userInput);
            add(pass);
            add(passInput);
            add(buttons); 
            add(error);
        }
        public String[] getTexts(){
            //TO-DO see how handle passwords better!! 
            String[] toReturn={userInput.getText(), new String(passInput.getPassword())};
            return toReturn;
        }
    }
    
    public class RegisterPanel extends JPanel{
        
        private final JTextField userInput;
        private final JTextField nameInput;
        private final JTextField phoneInput;
        private final JTextField faxInput;
        private final JRadioButton street;
        private final JRadioButton av;
        private final JRadioButton boulevard;
        private final JTextField adress;
        private final JComboBox city;
        private final JLabel error;
        
        public RegisterPanel(){
            setLayout(new GridLayout(13, 1, 5, 5));

            JLabel user = new JLabel("Nombre");
            userInput = new JTextField (20);
            userInput.setName("Nombre Usuario");

            JLabel nameContact = new JLabel("Nombre contacto");
            nameInput = new JTextField (20);
            nameInput.setName("Nombre contacto");
            
            JLabel phone = new JLabel("Telefono contacto");
            phoneInput = new JTextField (20);
            phoneInput.setName("Telefono");

            JLabel fax = new JLabel("Fax");
            faxInput = new JTextField (20);
            faxInput.setName("Fax");

            JPanel radioButtons = new JPanel();
            radioButtons.setLayout(new GridLayout(1,3,5,5));
            ButtonGroup group = new ButtonGroup();
            street = new JRadioButton("Calle",true);
            av = new JRadioButton("Avenida");
            boulevard = new JRadioButton("Boulevard");
            group.add(street);
            group.add(av);
            group.add(boulevard);
            radioButtons.add(street);
            radioButtons.add(av);
            radioButtons.add(boulevard);
            
            adress = new JTextField (20);
            adress.setName("Direccion");
            
            city = new JComboBox(Conexion.getCities());
            city.setName("Ciudad");
            
            JPanel buttons = new JPanel();
            JButton submit = new JButton("Registro");
            submit.addActionListener(myListener);
            JButton back = new JButton("Volver");
            back.addActionListener(myListener);
            buttons.add(back);
            buttons.add(submit);
            error = new JLabel ("");
            error.setVisible(false);
            
            add(user);
            add(userInput);
            add(nameContact);
            add(nameInput);
            add(phone);
            add(phoneInput);
            add(fax);
            add(faxInput);
            add(radioButtons);
            add(adress);
            add(city);
            add(error);
            add(buttons);   
        }
        
        public String[] getTexts(){
            String fullAdress ="";
            if (street.isSelected()){
                fullAdress = street.getText()+" "+adress.getText();
            } else if (av.isSelected()){
                fullAdress = av.getText()+" "+adress.getText();
            } else {
                fullAdress = boulevard.getText()+" "+adress.getText();
            }
            
            String[] toReturn={userInput.getText(), nameInput.getText(),phoneInput.getText(),faxInput.getText(),fullAdress, (String) city.getSelectedItem()};
            return toReturn;
        }
    }
    
    class MenuBar extends JMenuBar{
        
       public MenuBar(){
            JMenu menuFile = new JMenu("Archivo");
            menuFile.setMnemonic(KeyEvent.VK_A);

            JMenuItem exit = new JMenuItem("Exit");
            exit.setMnemonic(KeyEvent.VK_C);
            exit.addActionListener(myListener);

            menuFile.add(exit);
            add(menuFile);     
       }
    }
    
    class ButtonsListener implements ActionListener{
    
        @Override
        public void actionPerformed(ActionEvent ae) {

            String option = ae.getActionCommand();
            
            switch (option){                
                case "Login":                    
                    String [] data = login.getTexts();
                    
                    if(Conexion.sendLogin(data)){
                        ID=data[1];
                        CardLayout cardLayout = (CardLayout) ppal.getLayout();
                        cardLayout.show(ppal,"stock");
                        menuBar.setVisible(true);
                        setSize(1280,720);
                    } else{
                        login.getComponent(5).setVisible(true);
                    }
                    break;
                    
                case "Registrarse":
                    layout.next(ppal);
                    setTitle("Registro");
                    //pack();
                    setSize(400,600);
                    break;
                    
                case "Volver":
                    layout.previous(ppal);
                    setTitle("Identificacion");
                    setSize(250,300);
                    break;
                    
                case "Registro":
                    //Comprobamos JTextField vacios y si los hay, los rodeamos en rojo y avisamos
                    register.error.setText("");
                    for(Component a: register.getComponents()){
                        if(a instanceof JTextField){
                            if (((JTextField) a).getText().equals("")){
                                Border border = BorderFactory.createLineBorder(Color.RED, 5);
                                ((JTextField) a).setBorder(border);
                                register.error.setVisible(true);
                                register.error.setText("<html><font color=red> Campo "+((JTextField) a).getName()+" vacio!!</font></html>");
                                break;
                            }
                        }
                    }
                    if(((String) register.city.getSelectedItem()).equals("")){
                        Border border = BorderFactory.createLineBorder(Color.RED, 5);
                        register.city.setBorder(border);
                        register.error.setVisible(true);
                        register.error.setText("<html><font color=red> Campo ciudad vacio!!</font></html>");
                        break;
                    }
                    
                    //Comprobamos que el registro sea positivo, para ello error es ""
                    if (register.error.getText().equals("")){
                        if(Conexion.sendReg(register.getTexts())){
                            CardLayout cardLayout = (CardLayout) ppal.getLayout();
                            cardLayout.show(ppal,"stock");
                            menuBar.setVisible(true);
                            register.error.setText("<html><font color=red>Proceso fallido, contacte con el administrador</font></html>");
                            break;
                        }else{
                            //la "unica" manera de llegar aqui sera xq ya esta inscrito ese nb_cliente
                            register.error.setVisible(true);
                            register.error.setText("<html><font color=red>Nombre de cliente ya presente, contacte con el administrador</font></html>");
                            register.userInput.setText("");
                        }
                    }
                    break;
            }
        }    
    }  
}

