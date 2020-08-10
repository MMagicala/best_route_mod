package best_route_mod;

import com.badlogic.gdx.graphics.Color;

public class RoomClassProperties {
    private char sign;
    private int priorityIndex; // default = 0
    private Color color;

    public RoomClassProperties(Color color){
        sign = '>';
        priorityIndex = 0;
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public int getPriorityIndex(){
        return priorityIndex;
    }

    public char getSign(){
        return sign;
    }

    public void flipSign(){
        sign = sign == '>' ? '<' : '>';
    }

    public void incrementPriorityIndex(){
        priorityIndex++;
    }

    public void decrementPriorityIndex(){
        priorityIndex--;
    }
    public boolean isActive(){
        return priorityIndex > 0;
    }

    public void resetPriorityIndexToZero(){
    	priorityIndex = 0;
	}
}
