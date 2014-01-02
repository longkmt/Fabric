import java.awt.Color;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Point;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Handles the entire UI and logic of the program
 * Uses Draw.java to create a JFrame, where the drawing is handled
 * 
 * Main calls new Interface
 */
public class Interface extends JFrame
{
	//Class member declaration
	//Draw objDraw;
	
	private ObjectPanel objPanel;
	private JFrame objPanelFrame;


	Font f1 = new Font("Seogoe UI", Font.PLAIN,12);
	
	//UI declarations
	static Draw panelDraw;
	
	private JToggleButton fill = new JToggleButton("No Fill");
	private JButton color = new JButton("Color");
	private JButton clean = new JButton("Clean");
	private JToggleButton line_type = new JToggleButton();
	
	private JLabel strokeThickness = new JLabel("Stroke Thickness");
	private JSlider thickness = new JSlider();
	
	private JToggleButton freedraw_button = new JToggleButton("Freedraw");
	private JToggleButton line_button = new JToggleButton("Line");
	private JToggleButton rectangle_button = new JToggleButton("Rectangle");
	private JToggleButton circle_button = new JToggleButton("Oval");
	private JToggleButton triangle_button = new JToggleButton("Triangle");
	
	private JMenuBar my_menu_bar = new JMenuBar();
	private JMenu filemenu, shapesmenu, symmetrymenu, helpmenu,networkMenu;
	private JMenuItem new_menu_item,open_menu_item, save_menu_item, export_menu_item, exit_menu_item, instruct_item,connectMenuItem;
	private JFileChooser fc = new JFileChooser("Open");
	
	private JToggleButton xSymmetry = new JToggleButton("Horizontal Symmetry");
	private JToggleButton ySymmetry = new JToggleButton("Vertical Symmetry");
	private JToggleButton xySymmetry = new JToggleButton("Diagonal Symmetry");
	private JToggleButton symNew = new JToggleButton("New Sym Point");
	private JButton symReset = new JButton("Reset Sym Point");
	
	private JFileChooser save_fc = new JFileChooser("Save"){		
		@Override public void approveSelection(){
			File f = getSelectedFile();
			if (f.exists() && getDialogType() == SAVE_DIALOG){
				int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
				switch(result){
				
					case JOptionPane.YES_OPTION:
						super.approveSelection();
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						cancelSelection();								
					default: break;				
				}	
			}
			else{
				super.approveSelection();
			}
		}
	};	//End of File Chooser save_fc

	//Default constructor 
	public Interface(){
		super ("Fabric - PixelDude");
		
		//Create File Menu
		filemenu = new JMenu("File");
		networkMenu = new JMenu("Network");
		//create obj panel
		
		new_menu_item = new JMenuItem("New File", KeyEvent.VK_N);
		open_menu_item = new JMenuItem("Open...", KeyEvent.VK_O);
		save_menu_item = new JMenuItem("Save As...", KeyEvent.VK_S);	
		export_menu_item = new JMenuItem("Export Image...", KeyEvent.VK_E);
		exit_menu_item = new JMenuItem("Exit", KeyEvent.VK_Q);
		
		new_menu_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				System.out.println("New called from menu");
				/*objDraw.clearImage();
				panelDraw.repaint();*/
				new Interface();
			}
		});
		
		open_menu_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				System.out.println("Open called from menu");
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					try {
						fileProcess(file);
						if (returnVal == JFileChooser.APPROVE_OPTION){
							file = fc.getSelectedFile();
							
							try {
								//panelDraw.shapes.clear();
								fileProcess(file);
								
							}
							catch (IOException ex) {
								// TODO Auto-generated catch block
								System.out.println("There is an erroer");
							}
							
							String str = file.toString();
							
						}
					}
					catch (IOException ex) {
							// TODO Auto-generated catch block
							System.out.println("There is an error");
						}
				}
			}
		});
                
                thickness.addChangeListener( new ChangeListener() {
                    @Override public void stateChanged (ChangeEvent event) {
                        panelDraw.thickness = thickness.getValue();
                        if(line_type.isSelected()) {
			    final float dash1[] = {10.0f};
			    panelDraw.brushStroke = new BasicStroke(thickness.getValue(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
			}
			else
			    panelDraw.brushStroke = new BasicStroke(thickness.getValue());
                    }
		    
                });
		
		save_menu_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				System.out.println("Save called from menu");
				
				if (panelDraw.shapes.size() >0){
					System.out.println("Print the shapes vector: ");
					System.out.println("There are: " + panelDraw.shapes.size() + " objects");
					//System.out.println("There are: " + panelDraw.shapes.get(0).loc.size() + " item in the coorlist");
					//System.out.println("That coor is: " + panelDraw.shapes.get(0).loc.get(0).x + " and " + panelDraw.shapes.get(0).loc.get(0).y );
					//System.out.println("There are: " + panelDraw.shapes.get(0).sizes.size() + " items in sizes list");
					
				}
				if (e.getSource() == save_menu_item){
					
					StringBuilder to_save = new StringBuilder();

					for (int i=0; i< panelDraw.shapes.size(); i++){
						System.out.print(panelDraw.shapes.get(i).printObj());
						to_save.append(panelDraw.shapes.get(i).printObj());
					}
					
					int returnVal = save_fc.showSaveDialog(null);
					
					if (returnVal == JFileChooser.APPROVE_OPTION){
						
						File new_file = new File(save_fc.getSelectedFile() + ".fab");
						
				
					
                                                    try (BufferedWriter outfile = new BufferedWriter(new FileWriter(new_file))) {
                                                        outfile.write(to_save.toString());
                                                        outfile.flush();
                                                    }
							
						catch (IOException ex) {
							// TODO Auto-generated catch block
							System.out.println("There is an erroer");
						}
					}						
				}
			}
		});
	
		
		export_menu_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				System.out.println("Export called from menu");
				int status = fc.showSaveDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					String extension = "";
					BufferedImage image = panelDraw.getImage();
					if(fc.getFileFilter() == fc.getChoosableFileFilters()[1])
					{
						extension = "jpg";
						//Should add this extension onto the user's input if the user forgot
						//selectedFile.getName() = file.jpg;
					}
					else if(fc.getFileFilter() == fc.getChoosableFileFilters()[2])
						extension = "png";
					else if(fc.getFileFilter() == fc.getChoosableFileFilters()[3])
						extension = "gif";
					else if(fc.getFileFilter() == fc.getChoosableFileFilters()[4])
						extension = "fab";
					try{
						System.out.println(extension);
						ImageIO.write(image,extension,selectedFile);
					}
					catch (IOException ex){}
				}
			}
		});
		exit_menu_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				
				//dispatchEvent(new WindowEvent(, WindowEvent.WINDOW_CLOSING));
				
				//Ask if user wants to exit
			    int close_dialog = JOptionPane.showConfirmDialog(null, "Work is not auto-saved.\nAre you sure you want to quit?", "Really Quit?", JOptionPane.YES_NO_OPTION);
			    if (close_dialog == JOptionPane.YES_OPTION)
			    	System.exit(0);
			    //This can be changed to a exit function that handles all exits in program
			}
		});
		
		new_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
		open_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
		save_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
		export_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
		exit_menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
		//End File Menu
		
		
		connectMenuItem = new JMenuItem("Connect to Server");
		connectMenuItem.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
			    try {
				panelDraw.connect("107.21.80.97",6789);
			    } catch(Exception ex) {
				JOptionPane.showMessageDialog(Interface.this, "Error connecting to server\n" + ex.getMessage() );
			    }
			}
		});
		
		//Create Help Menu
		helpmenu = new JMenu("Help");
		
		instruct_item = new JMenuItem("Instructions", KeyEvent.VK_H);
		
		instruct_item.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				System.out.println("Help called from menu");
				showHelp();
			}
		});
		
		instruct_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.ALT_MASK));
		//End Help Menu
		
		my_menu_bar.add(filemenu);
		my_menu_bar.add(networkMenu);
		my_menu_bar.add(helpmenu);
		filemenu.add(new_menu_item);
		filemenu.add(open_menu_item);
		filemenu.add(save_menu_item);
		filemenu.add(export_menu_item);
		filemenu.add(exit_menu_item);
		
		networkMenu.add(connectMenuItem);
		
		helpmenu.add(instruct_item);
	
		setJMenuBar(my_menu_bar);
		setLocationRelativeTo(null);
		
		//Dynamically set size based on screen
		int screen_size_width = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width;
		int screen_size_height = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;
		setBounds(0,0,screen_size_width,screen_size_height);
		
		//Draw is created with dynamic size
		panelDraw = new Draw(170, 200, screen_size_width-210, screen_size_height-275, symNew);
		
		objPanel = new ObjectPanel(panelDraw);
		//objPanelFrame = new JFrame("Object Panel");
		//objPanelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//objPanel.setOpaque(true); //content panes must be opaque
		//objPanelFrame.setContentPane(objPanel);
		
		objPanel.setBounds(170,10,600,150);
		objPanel.setVisible(true);
		//objPanel.setBorder(new LineBorder(Color.gray));

		//Display object panel.
		//objPanelFrame.pack();
		//objPanelFrame.setVisible(true);
		
		setVisible(true);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);	
		
		this.addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent e)
			{
				//Ask if user wants to exit
			    int close_dialog = JOptionPane.showConfirmDialog(null, "Work is not auto-saved.\nAre you sure you want to quit?", "Really Quit?", JOptionPane.YES_NO_OPTION);
			    if (close_dialog == JOptionPane.YES_OPTION)
			    	System.exit(0);
			}
		}
		);
		
		initComponents();
		
	}
	
	private void initComponents()
	{
		getContentPane().setLayout(null);
		/*
		shape_buttons.add(freedraw_button);
		shape_buttons.add(line_button);
		shape_buttons.add(rectangle_button);
		shape_buttons.add(circle_button);
		shape_buttons.add(triangle_button);
		*/
		
		//freedraw_button.setBounds(10,100,150,25);
		//line_button.setBounds(10,125,150,25);
		//rectangle_button.setBounds(10,150,150,25);
		//circle_button.setBounds(10,175,150,25);
		//triangle_button.setBounds(10,200,150,25);
		
		freedraw_button.setSelected(true);
		
		freedraw_button.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.shape = ShapeType.FREEDRAW;
			if(!freedraw_button.isSelected())
				freedraw_button.setSelected(true);
			line_button.setSelected(false);
			rectangle_button.setSelected(false);
			circle_button.setSelected(false);
			triangle_button.setSelected(false);
		    }
		});
		line_button.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.shape = ShapeType.LINE;
			if(!line_button.isSelected())
				line_button.setSelected(true);
			freedraw_button.setSelected(false);
			rectangle_button.setSelected(false);
			circle_button.setSelected(false);
			triangle_button.setSelected(false);
		    }
		});
		rectangle_button.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.shape = ShapeType.RECTANGLE;
			if(!rectangle_button.isSelected())
				rectangle_button.setSelected(true);
			freedraw_button.setSelected(false);
			line_button.setSelected(false);
			circle_button.setSelected(false);
			triangle_button.setSelected(false);
		    }
		});
		circle_button.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.shape = ShapeType.CIRCLE;
			if(!circle_button.isSelected())
				circle_button.setSelected(true);
			freedraw_button.setSelected(false);
			line_button.setSelected(false);
			rectangle_button.setSelected(false);
			triangle_button.setSelected(false);
		    }
		});
		triangle_button.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.shape = ShapeType.TRIANGLE;
			if(!triangle_button.isSelected())
				triangle_button.setSelected(true);
			freedraw_button.setSelected(false);
			line_button.setSelected(false);
			rectangle_button.setSelected(false);
			circle_button.setSelected(false);
		    }
		});
		
		//xSymmetry.setBounds(10,275,150,25);
		//ySymmetry.setBounds(10,300,150,25);
		//xySymmetry.setBounds(10,325,150,25);
		//symNew.setBounds(10,355,150,25);
		xSymmetry.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.xSymmetry = !panelDraw.xSymmetry;
			panelDraw.repaint();
		    }
		});
		ySymmetry.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.ySymmetry = !panelDraw.ySymmetry;
			panelDraw.repaint();
		    }
		});
		xySymmetry.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.xySymmetry = !panelDraw.xySymmetry;
			panelDraw.repaint();
		    }
		});
		symNew.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.symNew = !panelDraw.symNew;
		    }
		});
		
		//Suposed to reset sym point to center. not working currently.
		symReset.addActionListener(new ActionListener() {
		    @Override public void actionPerformed(ActionEvent e) {
			panelDraw.symmetryPoint = new Point(panelDraw.getWidth()/2,panelDraw.getHeight()/2); 
			panelDraw.repaint();
		    }
		});
		
		
		//fill.setBounds(10, 205, 150, 25);
		fill.setEnabled(true);
		fill.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				if(fill.isSelected()){
				    fill.setText("Fill Color");
				}
				else{
					fill.setText("No Fill");
				}
				panelDraw.fill = !panelDraw.fill;
			}
		});
		
		//line_type.setBounds(10, 75, 150, 25);
		line_type.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				if(line_type.isSelected()){
				    line_type.setText("Dashed Line");
				    final float dash1[] = {10.0f};
				    panelDraw.brushStroke = new BasicStroke(thickness.getValue(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
				}
				else{
				    line_type.setText("Solid Line");
				    panelDraw.brushStroke = new BasicStroke(thickness.getValue());
				}
			}
		});
		
		//color.setBounds(10, 25, 150, 25);		
		color.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				new SelectColor(panelDraw);
			}
		}
		);
		
		//strokeThickness.setBounds(10, 230, 150, 25);
		strokeThickness.setFont(f1);
		thickness.setMaximum(10);
		//thickness.setBounds(10, 250, 150, 25);
		//clean.setBounds(10, 0, 150, 25);
		clean.addActionListener(new ActionListener()
		{
			@Override public void actionPerformed(ActionEvent e)
			{
				panelDraw.clearImage(true);
				objPanel.repaint();
			}
		}
		);
		panelDraw.setBorder(new LineBorder(Color.gray));
		panelDraw.setBackground(Color.white);
		
		
		
		//Set file types that can be saved
		FileFilter filterJPG = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg");
		FileFilter filterPNG = new FileNameExtensionFilter("PNG file", "PNG", "PNG");
		FileFilter filterGIF = new FileNameExtensionFilter("GIF file", "GIF", "GIF");
		FileFilter filterFAB = new FileNameExtensionFilter("FAB file", "FAB", "FAB");
		fc.addChoosableFileFilter(filterJPG);
		fc.addChoosableFileFilter(filterPNG);
		fc.addChoosableFileFilter(filterGIF);
		fc.addChoosableFileFilter(filterFAB);
		save_fc.addChoosableFileFilter(filterFAB);

		
		//Set Bounds for UI Sidebar
		int top = 5;
		int group1 = 35;
		int group2 = group1+125;
		int group3 = group2+135;

		clean.setBounds				(10,top,150,25);
		//space of 5
		color.setBounds				(10,group1,150,25);	
		line_type.setBounds			(10,group1+25,150,25);
		fill.setBounds				(10,group1+50,150,25);
		strokeThickness.setBounds	(10,group1+75,150,25);
		thickness.setBounds			(10,group1+95,150,25);
		//space of 5
		freedraw_button.setBounds	(10,group2,150,25);
		line_button.setBounds		(10,group2+25,150,25);
		rectangle_button.setBounds	(10,group2+50,150,25);
		circle_button.setBounds		(10,group2+75,150,25);
		triangle_button.setBounds	(10,group2+100,150,25);
		//space of 10
		xSymmetry.setBounds			(10,group3,150,25);
		ySymmetry.setBounds			(10,group3+25,150,25);
		xySymmetry.setBounds		(10,group3+50,150,25);
		//space of 5
		symNew.setBounds			(10,group3+80,150,25);
		symReset.setBounds			(10,group3+105,150,25);

		
		getContentPane().add(color);
		getContentPane().add(strokeThickness);
		getContentPane().add(thickness);
		getContentPane().add(clean);
		getContentPane().add(line_type);
		//System.out.println("Position of JPanel: ");
		//System.out.println("JPanel X " + panelDraw.getLocation().x);
		//System.out.println("JPanel Y " + panelDraw.getLocation().y);
		
		getContentPane().add(objPanel);
		getContentPane().add(panelDraw);
		line_type.setText("Solid Line");
		//getContentPane().add(shapes);
		//getContentPane().add(freedraw);
		
		getContentPane().add(fill);
		getContentPane().add(freedraw_button);
		getContentPane().add(line_button);
		getContentPane().add(rectangle_button);
		getContentPane().add(circle_button);
		getContentPane().add(triangle_button);
		
		getContentPane().add(xSymmetry);
		getContentPane().add(ySymmetry);
		getContentPane().add(xySymmetry);
		getContentPane().add(symNew);
		getContentPane().add(symReset);
		
		
		getContentPane().repaint();
	}	
	
	// Show help dialog called from menu bar
	private void showHelp(){
		JFrame f = new JFrame("Help Me!");
		f.getContentPane().setLayout(null);
		f.getContentPane().setPreferredSize(new Dimension(250, 250));	
				
		JTextArea output = new JTextArea("");
		output.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(output, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);			    
		scrollPane.setSize(200,200);		    
		scrollPane.setLocation(10, 25);
		f.getContentPane().add(scrollPane);     
		
		//Get instructions from a outside txt file
		output.append("This is how to run our program! We call it Fabric because it is a mesh of different ideas together to form one cohesive unit.");
			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		f.pack();
		f.setVisible(true);
	}
	
	private void fileProcess(File _file) throws IOException{	
	    BufferedReader br = new BufferedReader(new FileReader(_file));
	    String line;
	    ArrayList<String> lines = new ArrayList<>();
	    while ((line = br.readLine()) != null){
		    lines.add(line);
	    }
	    panelDraw.parseDraw(lines);
	    panelDraw.repaint();
	}
	
	public static void main(String args[])
	{
		new Interface();
	}
}

