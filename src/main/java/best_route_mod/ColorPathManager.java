package best_route_mod;

import best_route_mod.patches.InputHelperPatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.NeowsLament;

import java.util.ArrayList;

public class ColorPathManager {
    // Keep a reference of the path we already rendered, so we can disable it later
    private static MapPath coloredPath = null;

    public static void colorPath(MapPath path, boolean colorNeowsLament){
        coloredPath = path;
        // Determine the color using rooms at the lowest priority index > 0
        ArrayList<Class<?>> roomClassesAtLowestPriorityIndex = RoomClassManager.getActiveRoomClassesAtLowestPriority();
        // Use the color matching the room. If multiple rooms, use cyan
        Color color;
        if (roomClassesAtLowestPriorityIndex.size() > 1) {
            color = Color.CYAN;
        } else {
            color = RoomClassManager.getColorOf(roomClassesAtLowestPriorityIndex.get(0));
        }
        boolean neowsLamentPathEnded = false;
        ArrayList<MapEdge> edges = path.getEdges();
        for(int i = 0; i < edges.size(); i++){
            MapEdge edge = edges.get(i);
            edge.markAsTaken();

            if(i == 0 && path.neowsLamentPathEndIndex == i) neowsLamentPathEnded = true;
            if(colorNeowsLament && !neowsLamentPathEnded) edge.color = Color.MAGENTA;
            else edge.color = color;
            if(path.neowsLamentPathEndIndex == i+1) neowsLamentPathEnded = true;
        }
    }

        // Disable the path we colored before
    public static void disableCurrentlyColoredPath(){
        if(coloredPath == null){
            return;
        }
        for(MapEdge edge: coloredPath.getEdges()){
            edge.taken = false;
        }
        coloredPath = null;
    }
}
