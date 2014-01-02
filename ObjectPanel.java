import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class ObjectPanel extends JPanel implements TableModelListener{

	 static JTable table;
	 
    
   public ObjectPanel(Draw _panelDraw){
  	 
  	 		
        
        table = new JTable(new FabricTableModel(_panelDraw));
		

        table.setPreferredScrollableViewportSize(new Dimension(550,125));
        table.setFillsViewportHeight(true);

        //create the scroll pane and add the table to it
        //
        JScrollPane scrollPane = new JScrollPane();// = new JScrollPane(table);
        scrollPane.getViewport().add(table);

       	add(scrollPane);
        
        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(Color.class,
                                 new ColorRenderer(true));
        table.setDefaultEditor(Color.class,
                               new ColorEditor());
        
        //set up DELETE key
        table.setRowSelectionAllowed(true); 
        
       
        InputMap inputMap = table.getInputMap(JTable.WHEN_FOCUSED);
        ActionMap actionMap = table.getActionMap();
        
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        actionMap.put("delete", new AbstractAction() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		    // TODO Auto-generated method stub
		    int row =  table.getSelectedRow();
		    System.out.println("Print selected row: " + row);
		    row = table.convertRowIndexToModel(row);
		    ((FabricTableModel) table.getModel()).removeRow(row);
	    }
        });
        
        
        table.getColumnModel().getColumn(2).setCellEditor(new IntegerEditor(0,_panelDraw.getWidth())); //limit location from 0-1000
        table.getColumnModel().getColumn(3).setCellEditor(new IntegerEditor(0,_panelDraw.getHeight())); //limit size from 0-1000
        table.getColumnModel().getColumn(4).setCellEditor(new IntegerEditor(-_panelDraw.getWidth(),_panelDraw.getWidth())); //limit width from 0-10*/
        table.getColumnModel().getColumn(5).setCellEditor(new IntegerEditor(-_panelDraw.getHeight(),_panelDraw.getHeight())); //limit height from 0-10*/
        table.getColumnModel().getColumn(6).setCellEditor(new IntegerEditor(0,1000)); //limit thickness from 0-10*/
        
        //listen to table's data change
        table.getModel().addTableModelListener(this);
   }

	@Override
	public void tableChanged(TableModelEvent e) { }

   

}

class FabricTableModel extends AbstractTableModel { 
	
			private List<String> columnNames = new ArrayList();
			static List<List> data = new ArrayList();
			
			Draw panelDraw;
			
			FabricTableModel(Draw _panelDraw){
				
				panelDraw = _panelDraw;
				
				columnNames.add("Id");
				columnNames.add("Type");
				columnNames.add("LocationX");
				columnNames.add("LocationY");
				columnNames.add("Width");
				columnNames.add("Height");
				columnNames.add("Thickness");
				columnNames.add("Color");
				columnNames.add("Fill");
		
		
}			
				

		
			@Override
			public int getColumnCount() {
				
				return columnNames.size();
			}
		
			@Override
			public int getRowCount() {
				try{
				return data.size();
				}
				catch (NullPointerException e){
					System.out.println("Null Pointer Exception");
				}
				return 0;
			}
		
			@Override
			public Object getValueAt(int row, int col) {
				
				return data.get(row).get(col);
			}
			
			public void addRow(List obj){
				
				data.add(obj);
				fireTableRowsInserted(data.size() - 1, data.size() - 1);
			}
	
			
			public void removeRow(int row){
				
				
				data.remove(row);
				fireTableRowsDeleted(row, row);
				//panelDraw.clearImage();
				panelDraw.updateShapesVector(row);
				panelDraw.repaint();
			}
			
			@Override
			public String getColumnName(int col){
				
				return columnNames.get(col);
			}
			
			@Override
			public Class getColumnClass(int c){
				
				return getValueAt(0,c).getClass();
			}
			
			//Provide method to prevent users from changing the values of id and type
			@Override
			public boolean isCellEditable(int row, int col){
				
					if (col < 2){ //id and type are uneditable
						return false;
					}
					else{ //the rest is editable
						return true;
					}
				
				
			}
			
			//set new value for editable cells
			@Override
			public void setValueAt(Object obj, int row, int col){
				
				//panelDraw.clearImage();
				
				data.get(row).set(col,obj);
				fireTableCellUpdated(row,col);
				System.out.println("Test");
				panelDraw.updateShapesVector(row, col, obj);
				panelDraw.repaint();
			}
			
	
}



