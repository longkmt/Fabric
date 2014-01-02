import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Handles drawing on the canvas
 */
class Draw extends JPanel implements MouseListener, MouseMotionListener {
    //List keeps all shapes drawn to canvas
    ArrayList<ShapeObject> shapes = new ArrayList<>();
    ArrayList<Integer> shapes_index = new ArrayList<>();
    int ftm_index, networkItems;
    boolean network;
    Socket fabSocket;
    Thread thread;
    String sender;
    //Declarations of coordinate values
    int Xi, Yi, freeX, freeY;
    
    int thickness;
    public BasicStroke brushStroke;
    
    boolean xSymmetry, ySymmetry, xySymmetry;
    Point symmetryPoint;
    boolean symNew;
    JToggleButton symButton;
    //Declarations of values from UI
    
    Color brushColor = new Color(0,0,0);
    Color backgroundColor = new Color(255,255,255);
    Color gridLines = new Color(180,180,180);
    BasicStroke gridStroke;
    ShapeType shape = ShapeType.FREEDRAW;
    
    boolean fill = false;
    
    BufferedImage canvas, canvas_temp;
    //StringBuilder to_save = new StringBuilder();
    
    //Constructor - Called at beginning of Interface
    public Draw(int x, int y, int width, int height, JToggleButton _symButton){
	this.setBounds(x,y,width,height);
	canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	canvas_temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	
	thickness = 10;
	brushStroke = new BasicStroke(thickness);
	
	xSymmetry = false;
	ySymmetry = false;
	xySymmetry = false;
	symmetryPoint = new Point(width/2,height/2);
	symNew = false;
	symButton = _symButton;
	
	float[] dash = {10.0f};
	gridStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
	
	ftm_index = 1;
	network = false;
	networkItems = 0;
	sender = "";
	
	this.addMouseListener(this);
	this.addMouseMotionListener(this);
    }
    
    //NEVER USED IN DRAW
    //move to Interface. Use set variable to set methods in DRAW
    public void getCoordinates(MouseEvent ev){
	Xi = ev.getX();
	Yi = ev.getY();
    }
    
    /**
     * Draws shapes to panel canvas
     *
     * @param ev - MouseEvent
     * @param thickness - Line Thickness determined by UI
     */
    
    @Override public void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D panel_g = (Graphics2D) g;
	panel_g.setColor(backgroundColor);
	panel_g.setStroke(brushStroke);
	panel_g.fillRect(0, 0, this.getWidth(), this.getHeight());
	panel_g.drawImage(canvas, null, 0, 0);
	panel_g.drawImage(canvas_temp,null,0,0);
	panel_g.setColor(gridLines);
	panel_g.setStroke(gridStroke);
	if(xSymmetry || xySymmetry)
	    panel_g.drawLine(symmetryPoint.x, 0, symmetryPoint.x, getHeight());
	if(ySymmetry || xySymmetry)
	    panel_g.drawLine(0, symmetryPoint.y, getWidth(), symmetryPoint.y);
    }
    
    void connect(String server, int port) throws Exception
    {
	network = true;
	fabSocket = new Socket(server, port);
	
	thread = new Thread() {
	    Draw draw;
	    
	    String response;
	    
	    @Override
	    public void run() {
		try {
		    System.out.println("Connected to server successfully.");
		    draw = Draw.this;
		    
		    while(network) {
			try {
			    //System.out.println("Sleeping 5 seconds.");
			    sleep(500);
			    //System.out.println("I have awoken!");
			} catch(InterruptedException ie) {
			    System.err.println(ie.getMessage());
			}
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(fabSocket.getInputStream()));
			DataOutputStream outToServer = new DataOutputStream(fabSocket.getOutputStream());
			if(!sender.equals(""))
			    outToServer.writeBytes(sender + '\n');
			else
			    outToServer.writeBytes("UPDATE\n");
			sender = "";
			response = inFromServer.readLine();
			//System.out.println("Server respone: " + response);
			if(!response.matches("^\\s*$")) {
			    System.out.println("Contains REAL characters!");
			    draw.addShapes(response);
			}
		    }
		} catch(Exception e) {
		    System.err.println("Runtime exception: " + e.getMessage());
		    e.printStackTrace();
		}
	    }
	    
	};
	thread.start();
    }
    
    void sendNewObjects()
    {
	List<ShapeObject> newElems = shapes.subList(networkItems, shapes.size());
	StringBuilder str = new StringBuilder();
	for(ShapeObject elem : newElems)
	{
	    str.append(elem.printObj('|'));
	}
	sender = str.toString();
    }
    
    void addShapes(String str)
    {
	String[] lines = str.toUpperCase().split("\\|");
	ArrayList<String> objs = new ArrayList<>();
	//System.out.println("Lines: " + lines.length);
	//Graphics2D g = (Graphics2D) canvas.createGraphics();
	for(int i = 0; i < lines.length; i++)
	{
	    //System.out.println("Line[" + i + "]: " + lines[i]);
	    String[] splitted = lines[i].toUpperCase().split("\\s+");
	    switch(splitted[0])
	    {
		case "BACKGROUND":
		    backgroundColor = new Color(Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]));
		    break;
		case "CLEAR":
		    networkItems = 0;
		    clearImage(false);
		    break;
		default:
		    System.out.println(lines[i]);
		    objs.add(lines[i]);
		    
	    }
	}
	parseDraw(objs);
	repaint();
    }
    
    public void setBackgroundColor(Color c)
    {
	backgroundColor = c;
	if(network)
	    sender += "BACKGROUND " + c.getRed() + " " + c.getGreen() + " " + c.getBlue() + "|";
	repaint();
    }
    
    public void setStroke(int _thickness, boolean _dashed)
    {
	thickness = _thickness;
	if(thickness > 10)
	    thickness = 10;
	if(thickness <= 0)
	    thickness = 1;
	if(_dashed) {
	    final float[] dash1 = {10.0f};
	    brushStroke = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
	}
	else
	    brushStroke = new BasicStroke(thickness);
    }
    
    public void setColor(int r, int g, int b)
    {
	brushColor = new Color(r,g,b);
    }
    
    public ShapeObject freeDraw(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	g.drawLine(startX, startY, endX, endY);
	return new ShapeObject(ShapeType.LINE,startX-Xi,startY-Yi,endX-Xi,endY-Yi,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,false);
    }
    
    public ShapeObject ovalDraw(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int leftX = (startX<endX) ? startX : endX;
	int topY = (startY<endY) ? startY : endY;
	int width = (startX<endX) ? endX-startX : startX-endX;
	int height = (startY<endY) ? endY-startY : startY-endY;
	
	g.drawOval(leftX,topY,width,height);
	return new ShapeObject(ShapeType.CIRCLE,leftX,topY,leftX+width,topY+height,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,false);
    }
    
    public ShapeObject ovalFill(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int leftX = (startX<endX) ? startX : endX;
	int topY = (startY<endY) ? startY : endY;
	int width = (startX<endX) ? endX-startX : startX-endX;
	int height = (startY<endY) ? endY-startY : startY-endY;
	
	g.fillOval(leftX,topY,width,height);
	return new ShapeObject(ShapeType.CIRCLE,leftX,topY,leftX+width,topY+height,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,true);
    }
    
    public ShapeObject rectDraw(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int leftX = (startX<endX) ? startX : endX;
	int topY = (startY<endY) ? startY : endY;
	int width = (startX<endX) ? endX-startX : startX-endX;
	int height = (startY<endY) ? endY-startY : startY-endY;
	
	g.drawRect(leftX,topY,width,height);
	return new ShapeObject(ShapeType.RECTANGLE,leftX,topY,leftX+width,topY+height,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,false);
    }
    
    public ShapeObject rectFill(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int leftX = (startX<endX) ? startX : endX;
	int topY = (startY<endY) ? startY : endY;
	int width = (startX<endX) ? endX-startX : startX-endX;
	int height = (startY<endY) ? endY-startY : startY-endY;
	
	g.fillRect(leftX,topY,width,height);
	return new ShapeObject(ShapeType.RECTANGLE,leftX,topY,leftX+width,topY+height,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,true);
    }
    
    public ShapeObject triDraw(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int width = endX-startX;
	int height = endY-startY;
	
	int[] xCoors = {startX+width/2,startX,startX+width};
	int[] yCoors = {startY,startY+height,startY+height};
	
	g.drawPolygon(xCoors,yCoors,3);
	return new ShapeObject(ShapeType.TRIANGLE,startX,startY,endX,endY,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,false);
    }
    
    public ShapeObject triFill(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	int width = endX-startX;
	int height = endY-startY;
	
	int[] xCoors = {startX+width/2,startX,startX+width};
	int[] yCoors = {startY,startY+height,startY+height};
	
	g.fillPolygon(xCoors,yCoors,3);
	return new ShapeObject(ShapeType.TRIANGLE,startX,startY,endX,endY,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,true);
    }
    
    public ShapeObject lineDraw(Graphics2D g, int startX, int startY, int endX, int endY)
    {
	g.drawLine(startX, startY, endX, endY);
	return new ShapeObject(ShapeType.LINE,startX, startY, endX, endY,thickness,!brushStroke.equals(new BasicStroke(thickness)),brushColor,false);
    }
    
    
    /*	//this is function is not used anywhere, yet..... Shuvo
     * public void clearShape(int choice, Graphics2D g_temp, Graphics2D g2)
     * {
     * g2.setBackground(Color.white);
     * switch(choice)
     * {
     * case 1:
     * g2.clearRect(0,0,canvas_temp.getHeight(),canvas_temp.getWidth());
     * g2.clearRect(0,0,canvas.getHeight(),canvas.getWidth());
     * }
     * }*/
    
    //Get and set and clear the current canvas of the panel
    public BufferedImage getImage()
    {
	BufferedImage returnImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	Graphics2D returnImageGraphics = (Graphics2D) returnImage.createGraphics();
	returnImageGraphics.setColor(backgroundColor);
	returnImageGraphics.setStroke(brushStroke);
	returnImageGraphics.fillRect(0, 0, this.getWidth(), this.getHeight());
	returnImageGraphics.drawImage(canvas, null, 0, 0);
	returnImageGraphics.drawImage(canvas_temp,null,0,0);
	return returnImage;
    }
    public void setImage(BufferedImage _image){
	
	canvas = _image;
    }
    public void tempClear() {
	Graphics2D g_temp = (Graphics2D) canvas_temp.createGraphics();
	
	g_temp.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
	Rectangle2D.Double rect = new Rectangle2D.Double(0,0,canvas.getWidth(),getHeight());
	g_temp.fill(rect);
    }
    public void clearImage(boolean send) //RENAME
    {
	if(send)
	    sender += "CLEAR|";
	Graphics2D g = (Graphics2D) canvas.createGraphics();
	Graphics2D g_temp = (Graphics2D) canvas_temp.createGraphics();
	g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
	g_temp.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
	Rectangle2D.Double rect = new Rectangle2D.Double(0,0,canvas.getWidth(),getHeight());
	g.fill(rect);
	g_temp.fill(rect);
	shapes.clear();
	shapes_index.clear();
	ftm_index=1;
	repaint();
	FabricTableModel.data.clear();
    }
    
    //this is to update the canvas after a row in the object panel is deleted
    public void updateShapesVector(int row){
	ShapeObject obj = shapes.get(row);
	int index = (int) shapes_index.get(row);
	shapes.remove(index);
	shapes_index.remove(row);

	if(obj.stype == ShapeType.FREEDRAW)
	{
	    do {
		obj = shapes.get(index);
		shapes.remove(index);
	    } while(row < shapes.size() && obj.stype !=  ShapeType.ENDFREEDRAW);
	}
	
	updateCanvas();
    }
    
    //This is to update the canvas after the values in the object panel change.
    public void updateShapesVector(int row, int col, Object obj ) {
	
	switch(col){
	    
	    case 2: //change X
		shapes.get(shapes_index.get(row)).setLocX((int) obj);
		break;
		
	    case 3: //change Y
		shapes.get(shapes_index.get(row)).setLocY((int) obj);
		break;
		
	    case 4: //change width
		shapes.get(shapes_index.get(row)).setWidth((int)obj);
		break;
		
	    case 5: //change height
		shapes.get(shapes_index.get(row)).setHeight((int)obj);
		break;
		
	    case 6: //change thickness
		shapes.get(shapes_index.get(row)).thick = (int) obj;
		break;
		
	    case 7: //change color
		shapes.get(shapes_index.get(row)).color = (Color) obj;
		break;
		
	    case 8: //change filled
		shapes.get(shapes_index.get(row)).filled = (boolean) obj;
		break;
		
	    default:
		System.err.println("This does not make sense at all");
		break;
		
	}
	
	
	updateCanvas();
	
    }
    
    private void updateCanvas(){
	ShapeObject tmp;
	//clearImage(false);
	Graphics2D g = canvas.createGraphics();
	Graphics2D g1 = canvas.createGraphics();
	Graphics2D g_temp = (Graphics2D) canvas_temp.createGraphics();
	g1.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
	g_temp.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
	Rectangle2D.Double rect = new Rectangle2D.Double(0,0,canvas.getWidth(),getHeight());
	g1.fill(rect);
	g_temp.fill(rect);
	g1.dispose();
	g_temp.dispose();
	//System.out.println("In update canvas!");
	for (int i=0; i<shapes.size(); i++){
	    tmp = shapes.get(i);
	    //System.out.println(tmp.printObj('\0'));
	    g.setStroke(new BasicStroke(tmp.thick));
	    g.setColor(tmp.color);
	    switch(tmp.stype){
		case RECTANGLE:
		    if (tmp.filled){
			rectFill(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    }
		    else{
			rectDraw(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    }
		    break;
		case CIRCLE:
		    if (tmp.filled){
			ovalFill(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    }
		    else{
			ovalDraw(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    }
		    break;
		case TRIANGLE:
		    if (tmp.filled)
			triFill(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    else
			triDraw(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    break;
		    
		case LINE:
		    //System.out.println("redrawing line");
		    //System.out.println(Integer.toHexString(tmp.color.getRGB()));
		    lineDraw(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    break;
		case FREEDRAW:
		    //System.out.println("Freedraw object");
		    freeDraw(g,tmp.X1,tmp.Y1,tmp.X2,tmp.Y2);
		    int x = tmp.getLocX();
		    int y = tmp.getLocY();
		    while(i+1 < shapes.size()) {
			i++;
			tmp = shapes.get(i);
			if(tmp.stype == ShapeType.ENDFREEDRAW)
			    break;
			freeDraw(g,x+tmp.X1,tmp.Y1+y,x+tmp.X2,tmp.Y2+y);
		    }
		    break;
		default:
		    System.out.println("Get to default!");
		    break;
	    }
	}
	g.dispose();
	repaint();
	
    }
    
    public void parseDraw(ArrayList<String> shapesString)
    {
	Graphics2D g = (Graphics2D) canvas.createGraphics();
	ListIterator<String> it = shapesString.listIterator();
	while(it.hasNext())
	{
	    String line = it.next();
	    String[] splitted = line.toUpperCase().split("\\s+");
	    if(splitted[0].equals("BACKGROUND")) {
		backgroundColor = new Color(Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]));
		continue;
	    }
	    if(splitted.length > 6)
		setStroke(Integer.parseInt(splitted[5]), splitted[6].equals("TRUE"));
	    if(splitted.length > 10)
		setColor(Integer.parseInt(splitted[7]),Integer.parseInt(splitted[8]),Integer.parseInt(splitted[9]));
	    g.setStroke(brushStroke);
	    g.setColor(brushColor);
	    switch(splitted[0])
	    {
		case "RECTANGLE":
		    if(splitted[10].equals("TRUE"))
			rectFill(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    else
			rectDraw(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    break;
		case "CIRCLE":
		    if(splitted[10].equals("TRUE"))
			ovalFill(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    else
			ovalDraw(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    break;
		case "TRIANGLE":
		    if(splitted[10].equals("TRUE"))
			triFill(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    else
			triDraw(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    break;
		case "FREEDRAW":
		    lineDraw(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    int offsetX = Integer.parseInt(splitted[1]);
		    int offsetY = Integer.parseInt(splitted[2]);
		    while(it.hasNext())
		    {
			line = it.next();
			{
			    //System.out.println(line);
			    splitted = line.toUpperCase().split("\\s+");
			    if(splitted[0].equals("ENDFREEDRAW"))
				break;
			    try {
				lineDraw(g, offsetX+Integer.parseInt(splitted[1]),offsetY+Integer.parseInt(splitted[2]),offsetX+Integer.parseInt(splitted[3]),offsetY+Integer.parseInt(splitted[4]));
			    } catch(Exception e) {
				System.err.println(e.getMessage());
				System.err.println("In line:" + line);
			    }
			    
			}
		    }
		    break;
		case "LINE":
		    lineDraw(g, Integer.parseInt(splitted[1]),Integer.parseInt(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]));
		    break;
		default:
		    System.err.println("Uknown Object Type: " + splitted[0] + " on line " + it.nextIndex() + ".");
	    }
	}
    }
    
    
    @Override
    public void mousePressed(MouseEvent e)
    {
	if(symNew) {
	    symmetryPoint.x = e.getX();
	    symmetryPoint.y = e.getY();
	    repaint();
	    return;
	}
	Graphics2D g = (Graphics2D) canvas_temp.createGraphics();
	g.setStroke(brushStroke);
	g.setColor(brushColor);
	
	Xi = e.getX();
	Yi = e.getY();
	
	int x = 2*symmetryPoint.x - Xi;
	int x2 = x;
	int y = 2*symmetryPoint.y - Yi;
	int y2 = y;
	if(!e.isMetaDown()){
	    switch (shape){
		case FREEDRAW:
		    
		    g = (Graphics2D) canvas.createGraphics();
		    if(network)
			g = (Graphics2D) canvas_temp.createGraphics();
		    g.setStroke(brushStroke);
		    g.setColor(brushColor);
		    ArrayList<ShapeObject> object = new ArrayList<>();
		    freeX = e.getX();
		    freeY = e.getY();
		    ShapeObject fd = new ShapeObject(ShapeType.FREEDRAW,freeX,freeY,freeX,freeY,thickness,false,brushColor,false);
		    object.add(fd);
		    shapes_index.add(shapes.size());
		    FabricTableModel ftm = (FabricTableModel)ObjectPanel.table.getModel();
		    ftm.addRow(Arrays.asList(ftm_index++,fd.getName(),fd.getLocX(),fd.getLocY(),fd.getWidth(),fd.getHeight(),fd.thick,fd.color,fd.filled));
		    freeDraw(g, freeX,freeY,e.getX(),e.getY());
		    if(xSymmetry)
			object.add(freeDraw(g, x,freeY,x2,e.getY()));
		    if(ySymmetry)
			object.add(freeDraw(g, freeX,y,e.getX(),y2));
		    if(xySymmetry)
			object.add(freeDraw(g, x,y,x2,y2));
		    for(ShapeObject o : object)
			shapes.add(o);
		    break;
		case CIRCLE:
		    if(this.fill) {
			ovalFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    ovalFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    ovalFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    ovalFill(g, x,y,x2,y2);
		    }
		    else {
			ovalDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    ovalDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    ovalDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    ovalDraw(g, x,y,x2,y2);
		    }
		    break;
		case RECTANGLE:
		    if(this.fill) {
			rectFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    rectFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    rectFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    rectFill(g, x,y,x2,y2);
		    }
		    else {
			rectDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    rectDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    rectDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    rectDraw(g, x,y,x2,y2);
		    }
		    break;
		case TRIANGLE:
		    if(this.fill) {
			triFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    triFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    triFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    triFill(g, x,y,x2,y2);
		    }
		    else {
			triDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    triDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    triDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    triDraw(g, x,y,x2,y2);
		    }
		    break;
		case LINE:
		    lineDraw(g, Xi,Yi,e.getX(),e.getY());
		    if(xSymmetry)
			lineDraw(g, x,Yi,x2,e.getY());
		    if(ySymmetry)
			lineDraw(g, Xi,y,e.getX(),y2);
		    if(xySymmetry)
			lineDraw(g, x,y,x2,y2);
		    
	    }
	}
	g.dispose();
	repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
	if(symNew) {
	    symmetryPoint.x = e.getX();
	    symmetryPoint.y = e.getY();
	    repaint();
	    symNew = false;
	    symButton.setSelected(false);
	    return;
	}
	Graphics2D g = (Graphics2D) canvas.createGraphics();
	if(network)
	    g = (Graphics2D) canvas_temp.createGraphics();
	g.setStroke(brushStroke);
	g.setColor(brushColor);
	
	int x = 2*symmetryPoint.x - Xi;
	int x2 = 2*symmetryPoint.x - e.getX();
	int y = 2*symmetryPoint.y - Yi;
	int y2 = 2*symmetryPoint.y - e.getY();
	ArrayList<ShapeObject> object = new ArrayList<>();
	if(!e.isMetaDown()){
	    switch (shape){
		case FREEDRAW:
		    x+=Xi-freeX;
		    y+=Yi-freeY;
		    object.add(freeDraw(g, freeX,freeY,e.getX(),e.getY()));
		    if(xSymmetry)
			object.add(freeDraw(g, x,freeY,x2,e.getY()));
		    if(ySymmetry)
			object.add(freeDraw(g, freeX,y,e.getX(),y2));
		    if(xySymmetry)
			object.add(freeDraw(g, x,y,x2,y2));
		    object.add(new ShapeObject(ShapeType.ENDFREEDRAW,Xi,Yi,Xi,Yi,thickness,false,brushColor,false));
		    Xi = e.getX();
		    Yi = e.getY();
		    break;
		case CIRCLE:
		    if(this.fill) {
			object.add(ovalFill(g, Xi,Yi,e.getX(),e.getY()));
			if(xSymmetry)
			    object.add(ovalFill(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(ovalFill(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(ovalFill(g, x,y,x2,y2));
		    }
		    else {
			object.add(ovalDraw(g, Xi,Yi,e.getX(),e.getY()));
			
			if(xSymmetry)
			    object.add(ovalDraw(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(ovalDraw(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(ovalDraw(g, x,y,x2,y2));
		    }
		    break;
		case RECTANGLE:
		    if(this.fill) {
			object.add(rectFill(g, Xi,Yi,e.getX(),e.getY()));
			if(xSymmetry)
			    object.add(rectFill(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(rectFill(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(rectFill(g, x,y,x2,y2));
		    }
		    else {
			object.add(rectDraw(g, Xi,Yi,e.getX(),e.getY()));
			if(xSymmetry)
			    object.add(rectDraw(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(rectDraw(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(rectDraw(g, x,y,x2,y2));
		    }
		    break;
		case TRIANGLE:
		    if(this.fill) {
			object.add(triFill(g, Xi,Yi,e.getX(),e.getY()));
			if(xSymmetry)
			    object.add(triFill(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(triFill(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(triFill(g, x,y,x2,y2));
		    }
		    else {
			object.add(triDraw(g, Xi,Yi,e.getX(),e.getY()));
			if(xSymmetry)
			    object.add(triDraw(g, x,Yi,x2,e.getY()));
			if(ySymmetry)
			    object.add(triDraw(g, Xi,y,e.getX(),y2));
			if(xySymmetry)
			    object.add(triDraw(g, x,y,x2,y2));
		    }
		    break;
		case LINE:
		    object.add(lineDraw(g, Xi,Yi,e.getX(),e.getY()));
		    if(xSymmetry)
			object.add(lineDraw(g, x,Yi,x2,e.getY()));
		    if(ySymmetry)
			object.add(lineDraw(g, Xi,y,e.getX(),y2));
		    if(xySymmetry)
			object.add(lineDraw(g, x,y,x2,y2));
		    break;
		    
	    }
	}
	for(ShapeObject o : object)
	{
	    shapes.add(o);
	    if(shape != ShapeType.FREEDRAW) {
		FabricTableModel ftm = (FabricTableModel)ObjectPanel.table.getModel();
		ftm.addRow(Arrays.asList(ftm_index++,o.getName(),o.getLocX(),o.getLocY(),o.getWidth(),o.getHeight(),o.thick,o.color,o.filled));
		shapes_index.add(shapes.size()-1);
	    }
	}
	if(network)
	    sendNewObjects();
	g.dispose();
	repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
	if(symNew) {
	    symmetryPoint.x = e.getX();
	    symmetryPoint.y = e.getY();
	    repaint();
	    return;
	}
	Graphics2D g = (Graphics2D) canvas_temp.createGraphics();
	g.setStroke(brushStroke);
	g.setColor(brushColor);
	if(shape != ShapeType.FREEDRAW)
	    tempClear();
	int x = 2*symmetryPoint.x - Xi;
	int x2 = 2*symmetryPoint.x - e.getX();
	int y = 2*symmetryPoint.y - Yi;
	int y2 = 2*symmetryPoint.y - e.getY();
	if(!e.isMetaDown()){
	    switch (shape){
		case FREEDRAW:
		    g = (Graphics2D) canvas.createGraphics();
		    g.setStroke(brushStroke);
		    g.setColor(brushColor);
		    ArrayList<ShapeObject> object = new ArrayList<>();
		    x+=Xi-freeX;
		    y+=Yi-freeY;
		    object.add(freeDraw(g, freeX,freeY,e.getX(),e.getY()));
		    if(xSymmetry)
			object.add(freeDraw(g, x,freeY,x2,e.getY()));
		    if(ySymmetry)
			object.add(freeDraw(g, freeX,y,e.getX(),y2));
		    if(xySymmetry)
			object.add(freeDraw(g, x,y,x2,y2));
		    freeX = e.getX();
		    freeY = e.getY();
		    for(ShapeObject o : object)
			shapes.add(o);
		    break;
		case CIRCLE:
		    if(this.fill) {
			ovalFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    ovalFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    ovalFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    ovalFill(g, x,y,x2,y2);
		    }
		    else {
			ovalDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    ovalDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    ovalDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    ovalDraw(g, x,y,x2,y2);
		    }
		    break;
		case RECTANGLE:
		    if(this.fill) {
			rectFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    rectFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    rectFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    rectFill(g, x,y,x2,y2);
		    }
		    else {
			rectDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    rectDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    rectDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    rectDraw(g, x,y,x2,y2);
		    }
		    break;
		case TRIANGLE:
		    if(this.fill) {
			triFill(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    triFill(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    triFill(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    triFill(g, x,y,x2,y2);
		    }
		    else {
			triDraw(g, Xi,Yi,e.getX(),e.getY());
			if(xSymmetry)
			    triDraw(g, x,Yi,x2,e.getY());
			if(ySymmetry)
			    triDraw(g, Xi,y,e.getX(),y2);
			if(xySymmetry)
			    triDraw(g, x,y,x2,y2);
		    }
		    break;
		case LINE:
		    //System.out.println("Line being dragged.");
		    lineDraw(g, Xi,Yi,e.getX(),e.getY());
		    if(xSymmetry)
			lineDraw(g, x,Yi,x2,e.getY());
		    if(ySymmetry)
			lineDraw(g, Xi,y,e.getX(),y2);
		    if(xySymmetry)
			lineDraw(g, x,y,x2,y2);
		    
	    }
	}
	g.dispose();
	repaint();
    }
    
    @Override public void mouseMoved   (MouseEvent e) {}
    @Override public void mouseEntered (MouseEvent e) {}
    @Override public void mouseExited  (MouseEvent e) {}
    @Override public void mouseClicked (MouseEvent e) {}
}
