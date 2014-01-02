import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;


public class SelectColor extends JDialog implements ActionListener
{
    JColorChooser jcolor = new JColorChooser();
    JButton brush = new JButton ("Change to Brush Color");
    JButton background = new JButton ("Change to Background Color");
    JButton done = new JButton ("Done");
    
    
    Draw d;
    
    SelectColor (Draw backPan)
    {
	d = backPan;
	
	brush.addActionListener(this);
	background.addActionListener(this);
	done.addActionListener(this);
	
	
	add(jcolor);
	add(brush);
	add(background);
	add(done);
	
	setLayout(new FlowLayout());
	setBounds(220,100,650,400);
	setTitle("Please select a color");
	setVisible(true);
	setResizable(false);
	
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
	if(e.getSource() == brush)
	{
	    d.brushColor = jcolor.getColor();
	}
	if(e.getSource() == background)
	{
	    d.setBackgroundColor(jcolor.getColor());
	}
	if(e.getSource() == done)
	{
	    dispose();
	}
    }
}
