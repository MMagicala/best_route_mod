package best_route_mod;

import com.badlogic.gdx.graphics.Color;

public class RoomClass {
    public char sign;
    private int priorityLevel; // default = 0
    private Color color;
    // TODO: add color

    public RoomClass(Color color){
        sign = '>';
        priorityLevel = 0;
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public int getPriorityLevel(){
        return priorityLevel;
    }

    public void incrementPriorityLevel(){
        priorityLevel++;
    }

    public void decrementPriorityLevel(){
        priorityLevel--;
    }

    public boolean isActive(){
        return priorityLevel > 0;
    }
}
