package best_route_mod;

import com.badlogic.gdx.graphics.Color;

public class ColorPicker {
    // Colors to use
    private static Color[] colors = {Color.RED, Color.ORANGE, Color.GREEN, Color.BLUE, Color.PURPLE, Color.BROWN};
    private static int index = 0;

    // Reset to red
    public static void resetPicker(){
        index = 0;
    }

    // ALso move to next color on list
    public static Color getCurrentColorAndMoveIndex(){
        int prevIndex = index;
        if(index == colors.length - 1) index = 0;
        else index++;
        return colors[prevIndex];
    }

    public static Color[] getColorsUsed(){
        return colors;
    }
}