import java.awt.Color;

public class ShapeObject {
    ShapeType stype;
    int thick;
    Color color;
    boolean filled;
    boolean dashed;
    int X1,X2,Y1,Y2;
    ShapeObject(ShapeType type, int x1, int y1, int x2, int y2, int _thick, boolean _dashed, Color _color, boolean fill){
	stype = type;
	X1 = x1;
	Y1 = y1;
	X2 = x2;
	Y2 = y2;
	dashed = _dashed;
	color = _color;
	filled = fill;
	thick = _thick;
    }
    
    private String getCommonString()
    {
	StringBuilder str = new StringBuilder();
	str.append(X1).append(" ").append(Y1).append(" ");
	str.append(X2).append(" ").append(Y2).append(" ");
	str.append(thick).append(" ");
	if(dashed)
	    str.append("true ");
	else
	    str.append("false ");
	str.append(color.getRed()).append(" ").append(color.getGreen()).append(" ").append(color.getBlue()).append(" ");
	if(filled)
	    str.append("true\n");
	else
	    str.append("false\n");
	return str.toString();
    }
    
    public String getCommonString(char end)
    {
	StringBuilder str = new StringBuilder();
	str.append(X1).append(" ").append(Y1).append(" ");
	str.append(X2).append(" ").append(Y2).append(" ");
	str.append(thick).append(" ");
	if(dashed)
	    str.append("true ");
	else
	    str.append("false ");
	str.append(color.getRed()).append(" ").append(color.getGreen()).append(" ").append(color.getBlue()).append(" ");
	if(filled)
	    str.append("true");
	else
	    str.append("false");
	str.append(end);
	return str.toString();
    }
    
    //Provide printObj() method to get all information of the object as a string. This information
    //will be saved into a fab file.
    public String printObj() {
	
	StringBuilder obj = new StringBuilder();
	
	switch(stype){
	    case FREEDRAW:
		obj.append("FREEDRAW ");
		obj.append(getCommonString());
		break;
		
	    case CIRCLE:
		obj.append("CIRCLE ");
		obj.append(getCommonString());
		break;
		
		//Similar codes for other shapes
	    case RECTANGLE:
		obj.append("RECTANGLE ");
		obj.append(getCommonString());
		break;
		
	    case TRIANGLE:
		obj.append("TRIANGLE ");
		obj.append(getCommonString());
		break;
		
	    case LINE:
		obj.append("LINE ");
		obj.append(getCommonString());
		break;
	    case ENDFREEDRAW:
		obj.append("ENDFREEDRAW");
		obj.append('\n');
		break;
	}
	
	return obj.toString();
	
	
    }
    
    public String printObj(char end) {
	
	StringBuilder obj = new StringBuilder(end);
	
	switch(stype){
	    case FREEDRAW:
		obj.append("FREEDRAW ");
		obj.append(getCommonString(end));
		break;
		
	    case CIRCLE:
		obj.append("CIRCLE ");
		obj.append(getCommonString(end));
		break;
		
		//Similar codes for other shapes
	    case RECTANGLE:
		obj.append("RECTANGLE ");
		obj.append(getCommonString(end));
		break;
		
	    case TRIANGLE:
		obj.append("TRIANGLE ");
		obj.append(getCommonString(end));
		break;
		
	    case LINE:
		obj.append("LINE ");
		obj.append(getCommonString(end));
		break;
	    case ENDFREEDRAW:
		obj.append("ENDFREEDRAW");
		obj.append(end);
		break;
	}
	
	return obj.toString();
	
	
    }
    
    public int getLocX(){
	return X1;
    }
    
    public int getLocY(){
	
	return Y1;
    }
    
    public int getWidth(){
	
	return X2-X1;
    }
    
    public int getHeight(){
	return Y2-Y1;
    }
    
    public void setLocX(int x) {
	X2 = x+(X2-X1);
	X1 = x;
	
    }
    
    public void setLocY(int y) {
	Y2 = y+(Y2-Y1);
	Y1 = y;
    }
    
    public void setWidth(int w) {
	X2 = X1+w;
    }
    
    public void setHeight(int h) {
	Y2 = Y1+h;
    }
    
    public String getName() {
	switch(stype) {
	    case FREEDRAW:
		return "FREEDRAW";
		
	    case CIRCLE:
		return "CIRCLE";
		
		//Similar codes for other shapes
	    case RECTANGLE:
		return "RECTANGLE";
		
	    case TRIANGLE:
		return "TRIANGLE";
		
	    case LINE:
		return "LINE";
	    case ENDFREEDRAW:
		return "ENDFREEDRAW";
	}
	return "UNKNOWN";
    }
    
}
