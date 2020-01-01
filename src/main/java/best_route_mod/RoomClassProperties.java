package best_route_mod;

import com.badlogic.gdx.graphics.Color;

public class RoomClassProperties {
    public char sign;
    private int priorityLevel; // default = 0
    // TODO: add color

    public RoomClassProperties(){
        sign = '>';
        priorityLevel = 0;
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
