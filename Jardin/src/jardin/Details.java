package jardin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SERGI
 */
public class Details extends JPanel{
    
    OrderTable tableOrder;
    DetailTable tableDetail;
    Details(){
        
        tableOrder = new OrderTable();
        tableDetail = new DetailTable();
        add(tableOrder);
        Dimension dim = new Dimension(10,10);
        add(new Box.Filler(dim,dim,dim));
        add(tableDetail);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));        
    }
    
    class OrderTable extends JPanel{
        
        DefaultTableModel model;
        JTable info;
        
        public OrderTable(){    
            setLayout(new BorderLayout());
            Object[] colNames = {"Codigo","Fecha pedido","Fecha esperada","fecha entrega","Estado","Comentarios"};            
            
            model = new DefaultTableModel(colNames, 0) {
                @Override
                public Class getColumnClass(int column) {
                
                    return String.class;
                }
                
                @Override 
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            
            info = new JTable(model){
                @Override
                public String getToolTipText(MouseEvent e) {
                return "Click para ver detalles";
                }
            };
            
            info.getColumnModel().getColumn(0).setMaxWidth(90);
            info.addMouseListener(new Mouse());
            JScrollPane table = new JScrollPane(info,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(table,BorderLayout.CENTER);            
        }
        
        public void updateModelTable(Object[][] data){
            model.setRowCount(0);
            
            for(Object [] row:data){
                model.addRow(row);
            }
        }
        public void updateModelTable(){
            Object [][] dataOrder = Conexion.getOrderValues("PEDIDO#"+GUI.ID);
            tableOrder.updateModelTable(dataOrder);
        }
    }
    public class DetailTable extends JPanel{
        DefaultTableModel model2;
        JLabel quantity;
        public DetailTable(){
            setLayout(new BorderLayout());
            Object[] colNames = {"Codigo producto","Cantidad","Precio unidad","Numero linea"};            
            
            model2 = new DefaultTableModel(colNames, 0) {
                @Override
                public Class getColumnClass(int column) {                
                    return String.class;
                }
                
                @Override 
                public boolean isCellEditable(int row, int column){
                    return false;
                }
            };
            
            JTable info = new JTable(model2);
            
            JScrollPane table = new JScrollPane(info,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(table,BorderLayout.CENTER);
            
            JPanel pay = new JPanel();
            pay.setLayout(new BorderLayout());
            quantity = new JLabel("");
            pay.add(quantity, BorderLayout.EAST);
            add(pay,BorderLayout.NORTH);
        }
        public String getTotal(){
            
            Double total=0.0;
            for(int i=0;i < model2.getRowCount();i++){
                total+= Double.valueOf((String)model2.getValueAt(i, 1))*Double.valueOf((String)model2.getValueAt(i, 2));
            }
            return total.toString();
        }
        
        public void updateModelTable(Object[][] data){
            model2.setRowCount(0);
            
            for(Object [] row:data){
                model2.addRow(row);
            }
        }
    }    
    
    class Mouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent me) {
            if (me.getButton() == MouseEvent.BUTTON1) {
                int row =tableOrder.info.rowAtPoint(me.getPoint());
                String idPedido = (String) tableOrder.info.getValueAt(row, 0);
                tableDetail.updateModelTable(Conexion.getDetailValues("DETALLE#"+idPedido));  
                tableDetail.quantity.setText("<html><h2>Precio final: "+tableDetail.getTotal()+" €</h2></html>");
            }            
        }
        @Override
        public void mousePressed(MouseEvent me) {}

        @Override
        public void mouseReleased(MouseEvent me) {}

        @Override
        public void mouseEntered(MouseEvent me) {}

        @Override
        public void mouseExited(MouseEvent me) {}
    }
}
    