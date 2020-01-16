package best_route_mod;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class ColorPathManager {
    // Keep a reference of the path we already rendered, so we can disable it later
    private static MapPath coloredPath = new MapPath();
    public static void colorPath(MapPath path){
        coloredPath = new MapPath(path);
        // Determine the color using rooms at the lowest priority index > 0
        ArrayList<Class<?>> roomClassesAtLowestPriorityIndex = RoomClassManager.getActiveRoomClassesAtLowestPriority();
        // Use the color matching the room. If multiple rooms, use cyan
        Color color;
        if(roomClassesAtLowestPriorityIndex.size() > 1){
            color = Color.CYAN;
        }else{
            color = RoomClassManager.getColorOf(roomClassesAtLowestPriorityIndex.get(0));
        }
        for(MapEdge edge: path.getEdges()){
            edge.markAsTaken();
            edge.color = color;
        }
    }

    // Disable the path we colored before
    public static void disableCurrentlyColoredPath(){
        for(MapEdge edge: coloredPath.getEdges()){
            edge.taken = false;
        }
        coloredPath = new MapPath();
    }

    public static boolean isPathColored(){
        return coloredPath.isEmpty();
    }

    public static MapPath getCurrentlyColoredPath(){
        return coloredPath;
    }
}
