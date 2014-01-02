import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.JDialog;




public class SelectShape extends JDialog implements ActionListener 
{
	
	JButton circle = new JButton ("Circle");
	JButton line = new JButton ("Line");
	JButton rect = new JButton ("Rectangle");
	JButton tri = new JButton ("Triangle");

	JToggleButton fillToggle;

	
	Draw d;
	
	SelectShape (Draw dib, JToggleButton filler)
	{
		d = dib;
		
		circle.addActionListener(this);
		line.addActionListener(this);
		rect.addActionListener(this);
		tri.addActionListener(this);

		add(circle);
		add(line);
		add(rect);
		add(tri);
		
		fillToggle = filler;
		
		setLayout(new FlowLayout());
		setSize(500,430);
		setTitle("Please select a shape");
		setVisible(true);
		setLocation(220, 100);
		setResizable(false);
		
	}
	@Override public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == circle)
		{
			d.shape = ShapeType.CIRCLE;
			fillToggle.setEnabled(true);
		}
		else if(e.getSource() == rect)
		{
			d.shape = ShapeType.RECTANGLE;
			fillToggle.setEnabled(true);
		}
		else if(e.getSource() == tri)
		{
			d.shape = ShapeType.TRIANGLE;
			fillToggle.setEnabled(true);
		}
		else if(e.getSource() == line)
		{
			d.shape = ShapeType.LINE;
		}
		dispose();
	}
}