package jardin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author SERGI
 */
public class Stock extends JPanel{
    
    private final ButtonsListener myListener;
    private final JcomboboxListener myComboListener;
    
    Table table;
    ToolBar toolBar;
    Details details;
    
    String toReturnProduct="";
    String toReturnQuan="";
    String toReturnName="";
    ArrayList<Double> priceProducts=new ArrayList();
    int sum = 0;
    String comment;
    
    public Stock(){
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        myListener = new ButtonsListener();
        myComboListener = new JcomboboxListener();
        
        toolBar = new ToolBar();
        table = new Table();
        details = new Details ();
        details.setVisible(false);
        add(toolBar);
        add(Box.createVerticalGlue());
        add(table);
        add(details);   
    }
    class ButtonsListener implements ActionListener{
    
        @Override
        public void actionPerformed(ActionEvent ae) {

            String option = ae.getActionCommand();
            switch (option){                
                case "Exit":
                    System.exit(0);
                    break;
                //TO-DO.. make it pretty xd
                case "Ver pedido":
                    details.tableOrder.updateModelTable();
                    table.setVisible(false);
                    details.setVisible(true);
                    toolBar.see.setVisible(false);
                    toolBar.products.setVisible(true);
                    toolBar.category.setEnabled(false);
                    break;
                case "Ver tienda":
                    table.setVisible(true);
                    details.setVisible(false);
                    toolBar.see.setVisible(true);
                    toolBar.products.setVisible(false);
                    toolBar.category.setEnabled(true);
                    break;
                case "Anadir cesta":
                    table.addToCart();
                    if(!toReturnQuan.equals("")){                    
                    toolBar.buy.setEnabled(true);
                    toolBar.showCart.setEnabled(true);
                    }
                    break;
                case "Borrar cesta":
                    table.deleteCart();                    
                    toolBar.buy.setEnabled(false);
                    toolBar.showCart.setEnabled(false);
                    break;
                case "Realizar pedido":
                    ConfirmOrder cOrder =  new ConfirmOrder();
                    String[] nameButtons = {"Confirmar","Seguir comprado"};
                    int yesNo = JOptionPane.showOptionDialog(null, cOrder,"¿Realizar pedido?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,nameButtons,null );
                    if(yesNo==0){
                        comment = cOrder.getText();
                        if (Conexion.makeOrder(table.makeOrder())){
                            JOptionPane.showMessageDialog(null,"Pedido realizado con exito!!\n ahora paga");
                        }else {
                            JOptionPane.showMessageDialog(null,":( something went worng!");
                        }
                    }
                    break;
                case "Ver cesta":
                    String a =table.makeCart();
                    String [] moreButtons = {"OK","Guardar"};
                    int html = JOptionPane.showOptionDialog(null, new CartDetail(a), "Detalle cesta" ,JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,moreButtons,null );
                    if(html == 1){
                        table.downloadCart(a);
                    }
                    break;
            }
        }   
    }
    
    class JcomboboxListener implements ItemListener{
        @Override
        public void itemStateChanged(ItemEvent event) {
           if (event.getStateChange() == ItemEvent.SELECTED) {
              JComboBox item = (JComboBox)event.getSource();
              Object[][] data = Conexion.getTableValues((String)item.getSelectedItem());
              table.updateModelTable(data);
           }
        }
    }
    
    class ToolBar extends JPanel{
        
        public JButton see;
        public JButton products;
        public JButton buy;
        private JButton showCart;
        private JComboBox category;
        private ToolBar(){
            
            setLayout(new BorderLayout());
            
            JToolBar toolBar = new JToolBar();            
            see = new JButton("Ver pedido");
            see.addActionListener(myListener);
            see.setMnemonic(KeyEvent.VK_P);
            
            products = new JButton("Ver tienda");
            products.addActionListener(myListener);
            products.setMnemonic(KeyEvent.VK_T);
            products.setVisible(false);
            
            buy = new JButton("Realizar pedido");
            buy.setEnabled(false);
            buy.addActionListener(myListener);
            buy.setMnemonic(KeyEvent.VK_R);
            
            JLabel gama = new JLabel("   Gama  -> ");
            String[] categories = {" ","Herramientas","Aromaticas","Frutales","Ornamentales"};
            category = new JComboBox(categories);
            category.addItemListener(myComboListener);
            
            JButton cart = new JButton("Anadir cesta");
            cart.addActionListener(myListener);
            cart.setMnemonic(KeyEvent.VK_D);
            
            showCart = new JButton("Ver cesta");
            showCart.addActionListener(myListener);
            showCart.setEnabled(false);
            showCart.setMnemonic(KeyEvent.VK_V);
            
            JButton deleteCart = new JButton("Borrar cesta");
            deleteCart.addActionListener(myListener);
            deleteCart.setMnemonic(KeyEvent.VK_B);
            
            toolBar.add(see);
            toolBar.add(products);
            toolBar.add(buy);
            toolBar.add(gama);
            toolBar.add(category);
            toolBar.add(cart);
            toolBar.add(showCart);
            toolBar.add(deleteCart);
            toolBar.setFloatable(false);
            add(toolBar,BorderLayout.NORTH);
            setMaximumSize(new Dimension(1280, 50));
        }
    }
    
    class Table extends JPanel{
        DefaultTableModel model;
        private Table(){    
            setLayout(new BorderLayout());
            Object[] colNames = {"Codigo","Nombre","Descripcion","PVP","Cantidad","Comprar"};            
            
            model = new DefaultTableModel(colNames, 0) {
                @Override
                public Class getColumnClass(int column) {
                    
                    switch(column){
                        case 5:
                            return Boolean.class;
                        case 4:
                            return Integer.class;
                        default:
                            return String.class;
                    }
                }
                @Override 
                public boolean isCellEditable(int row, int column){
                    return (column > 3);
                }
            };            
            
            JTable info = new JTable(model){
                @Override
                public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
//                Maps the index of the column in the view at viewColumnIndex to the index of the column in the table mode ¿?
//                In summary: TableColumnModel is the ultimate owner of column values. TableColumnModel only asks TableModel for
//                values only if it doesn't already have one. For example, in the case where you pass a column into JTable
//                .addColumn() without specifying a header value.
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                if (realColumnIndex == 2) { //Description column
                    tip = "<html><div style='width:200px;'>"+getValueAt(rowIndex, colIndex)+"</div><html>";
                }
                return tip;
                }
            };
//            info.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
//            center.setHorizontalAlignment( JLabel.CENTER );
//            table.setDefaultRenderer(String.class, center);
            //info.getColumnModel().getColumn(0).setPreferredWidth(100).setCellRenderer( center );
            info.getColumnModel().getColumn(1).setPreferredWidth(200);
//            info.getColumnModel().getColumn(2).setPreferredWidth(200);
            info.getColumnModel().getColumn(2).setPreferredWidth(400);
            info.getColumnModel().getColumn(5).setMaxWidth(75);

//            TO-DO..
//            TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(info.getModel());
//            sorter.setSortable(1, false);
//            sorter.setSortable(2, false);
//            sorter.setSortable(4, false);
//            sorter.setSortable(5, false);
//            info.setRowSorter(sorter);
            
            JScrollPane table = new JScrollPane(info,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(table,BorderLayout.CENTER);            
        }
        
        public void updateModelTable(Object[][] data){
            model.setRowCount(0);
            
            for(Object [] row:data){
                model.addRow(row);
            }
        }
        
        public void addToCart(){
            
            String price="";
            sum = 0;
            for(int i=0;i<model.getRowCount();i++){
                if((Boolean)model.getValueAt(i, 5)==true && !model.getValueAt(i, 4).equals("")){
                    toReturnProduct+=model.getValueAt(i, 0)+"/";
                    toReturnQuan+=model.getValueAt(i, 4)+"/";
                    toReturnName+=model.getValueAt(i, 1)+"/";
                    price+=model.getValueAt(i, 3);                    
                    priceProducts.add(Double.valueOf(price));
                    price="";
                    sum++;
                }
            }
        }
        public void deleteCart(){
            toReturnProduct="";
            toReturnQuan="";
            toReturnName="";
            sum = 0;
            priceProducts.clear();
            comment = "";
        }
        public String makeOrder(){
            return "ORDER#"+GUI.ID+"#"+toReturnProduct.substring(0,toReturnProduct.length()-1)+"#"+toReturnQuan.substring(0, toReturnQuan.length()-1)+"#"+sum+"#"+comment;
        }
        public String makeCart(){
            String toReturn="<html>"+new Date()+"<h3>Productos</h3><hr/>"
                    + "<table><tr>"
                    + "<th style=' border: 1px solid black;'>Normbre producto</th>"
                    + "<th style=' border: 1px solid black;'>Cod. producto</th>"
                    + "<th style='border: 1px solid black;'>Cantidad</th>"
                    +"<th style='border: 1px solid black;'>Precio</th>"
                    + "</tr>";
            
            String [] products = toReturnProduct.substring(0, toReturnProduct.length()-1).split("/");
            String [] quantity = toReturnQuan.substring(0, toReturnQuan.length()-1).split("/");
            String [] name = toReturnName.substring(0, toReturnName.length()-1).split("/");
            Double [] prices = priceProducts.toArray(new Double [priceProducts.size()]);
            Double total=0.0;
            for(int i =0;i< products.length;i++){
                toReturn+="<tr><td>"+name[i]+"</td><td>"+products[i]+"</td><td>"+quantity[i]+"</td><td>"+prices[i]+"</td></tr>";
                total += prices[i]*Integer.parseInt(quantity[i]);
            }
            toReturn+="</table><br/>Total = "+total+"<br/>Comentarios: "+comment+"</html>";
            return toReturn;
        }
        public void downloadCart(String a){
            File file=new File("Cesta.html");
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(a);
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Stock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    class CartDetail extends  JPanel{
        
        private CartDetail(String a){
            
            JLabel jlabel = new JLabel();
            jlabel.setText(a);
            jlabel.setVerticalAlignment(JLabel.TOP);
            jlabel.setPreferredSize(new Dimension(400,700));
            JScrollPane scrollpane = new JScrollPane(jlabel ,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(scrollpane);            
        }    
    }
    class ConfirmOrder extends JPanel{
        
        private JTextArea text;
        ConfirmOrder(){
            text = new JTextArea(5,20);
            text.setText("Agregar comentario");
            JScrollPane scroll = new JScrollPane(text);
            add(scroll);
        }
        public String getText(){
            String toReturn = text.getText();
            toReturn=toReturn.replace("/","\\");
            toReturn=toReturn.replace("#","+");
            return (toReturn.equals("Agregar comentario:")? "" : toReturn);
        }    
    }    
}
