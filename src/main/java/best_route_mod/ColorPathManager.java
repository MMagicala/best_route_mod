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
    private static MapPath neowsLamentPath = emptyPath;
    public static void colorPath(MapPath path){
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
        colorPath(coloredPath, color);
    }

    public static void colorNeowsLamentPath(MapPath path){
        // Save the newly colored path appropriately
        neowsLamentPath = path;
        colorPath(neowsLamentPath, Color.MAGENTA);
    }

    private static void colorPath(MapPath path, Color color){
        for(MapEdge edge: path.getEdges()){
            edge.markAsTaken();
            edge.color = color;
        }
    }

    // Disable the path we colored
    public static void disableCurrentlyColoredPath(){
        for(MapEdge edge: coloredPath.getEdges()){
            edge.taken = false;
        }
        coloredPath = emptyPath;
    }

    public static void disableNeowsLamentPath(){
        
    }

    public static boolean isPathColored(){
        return coloredPath.hasEdge();
    }

    public static MapPath getCurrentlyColoredPath(){
        return coloredPath;
    }
}
