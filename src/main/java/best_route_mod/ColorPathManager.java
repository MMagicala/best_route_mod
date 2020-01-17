package best_route_mod;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class ColorPathManager {
    // Keep a reference of the path we already rendered, so we can disable it later
    private static MapPath emptyPath = new MapPath();
    private static MapPath coloredPath = emptyPath;
    // If neows lament not factored in, set counter to 0 by default
    public static void colorPath(MapPath path, int neowsLamentCounter){
        coloredPath = path;
        // Determine the color using rooms at the lowest priority index > 0
        ArrayList<Class<?>> roomClassesAtLowestPriorityIndex = RoomClassManager.getActiveRoomClassesAtLowestPriority();
        // Use the color matching the room. If multiple rooms, use cyan
        Color color;
        if(roomClassesAtLowestPriorityIndex.size() > 1){
            color = Color.CYAN;
        }else{
            color = RoomClassManager.getColorOf(roomClassesAtLowestPriorityIndex.get(0));
        }
        colorPath(coloredPath, color, neowsLamentCounter);
    }

    private static void colorPath(MapPath path, Color color, int neowsLamentCounter){
        for(MapEdge edge: path.getEdges()) {
            edge.markAsTaken();
            if (neowsLamentCounter > 0) {

            } else {
                edge.color = color;
            }
        }
    }

    // Disable the path we colored
    public static void disableCurrentlyColoredPath(){
        for(MapEdge edge: coloredPath.getEdges()){
            edge.taken = false;
        }
        coloredPath = emptyPath;
    }

    public static boolean isPathColored(){
        return coloredPath.hasEdge();
    }

    public static MapPath getCurrentlyColoredPath(){
        return coloredPath;
    }
}
