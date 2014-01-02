import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * A start screen that can be used to display information about the program
 * Currently unused
 * 
 * Main calls new MainMenu
 */
public class MainMenu extends JFrame{
	
	public MainMenu() {
		super("Fabric v0.3");
		setLocationRelativeTo(null);
		
		//Dynamically get screen size (should account for insets etc)
		int screen_size_width = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width;
		int screen_size_height = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;
		setBounds(0,0,screen_size_width,screen_size_height);
		
		setVisible(true);
		
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				//This is not needed at this menu. There is no side effect to closing
				/*
				//Ask if user wants to exit
			    int close_dialog = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Really Quit?", JOptionPane.YES_NO_OPTION);
			    if (close_dialog == JOptionPane.YES_OPTION)
			    	System.exit(0);
			    */
			}
		}
		);
		
		//Does this function need to be here?
		initComponents();
	}
	
	public void initComponents()
	{
		
		getContentPane().setLayout(null);
		
		start_button.setBounds((getWidth()/2)-100, (getHeight()/2)+50, 200, 50);
		start_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
				new Interface();
			}
		}
		);
		title.setBounds((getWidth()/2)-100, (getHeight()/2)-100, 400, 100);
		title.setFont(f1);
		
		//backImage.setBounds(0, 0, 400, 100);
		
		//getContentPane().add(backImage);
		getContentPane().add(title);
		getContentPane().add(start_button);
		getContentPane().repaint();	
	}
	
	Font f1 = new Font("Seogoe UI", Font.PLAIN, 60);
	//This should get changed to something fancier eventually:
	JLabel title = new JLabel("Fabric");
	//ImageIcon image = new ImageIcon("FabricBack.png");
	//JLabel backImage = new JLabel(image);
	JButton start_button = new JButton("Start");
	JOptionPane pane;
	
	//Starts from Main Menu if Main Menu is called
	//Interface also has a main that will start the program at Interface
	public static void main(String args[])
	{
		new MainMenu();
	}
}
