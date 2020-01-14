package best_route_mod;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class ColorPath {
    // Keep a copy of the path we already rendered, so we can disable it later
    MapPath coloredPath;
    public void colorPath(MapPath path){
        coloredPath = new MapPath(path);
        // Determine the color using rooms at the lowest priority index > 0
        ArrayList<Class<?>> roomClassesAtLowestPriorityIndex = BestRouteMod.getRoomsAtLowestPriorityIndex();
        // Use the color matching the room. If multiple rooms, use cyan
        Color color = new Color();
        if(roomClassesAtLowestPriorityIndex.size() > 1){
            color = Color.CYAN;
        }else{
            color = BestRouteMod.
        }
    }

    // Disable the path we colored before
    public void disableCurrentlyColoredPath(){
        for(MapEdge edge: coloredPath.getListOfEdges()){

        }
    }

    private void disableEdge(MapRoomNode srcNode, MapRoomNode destNode){
        MapEdge edge = srcNode.getEdgeConnectedTo(destNode);
        edge.taken = false;
    }

    //

    public static void colorBestPath(){
        // Determine color to use using the lowest priority index that contains a room
        // if(lowestPriorityIndex == -1) return; this should not happen

        // Default color if more than one room class at lowest level
        Color colorToUse = Color.CYAN;
        if(roomClassesAtLowestPriorityIndex.size() == 1){
            colorToUse = getColorOfRoomClass(roomClassesAtLowestPriorityIndex.get(0));
        }

        // Color the edges in the map
        ArrayList<MapRoomNode> pathListOfNodes = bestPath.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            colorEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1), colorToUse);
        }
    }

    private void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode, Color color) {
        int xCoordinateOfStartingNode = srcNode.y == 0 ? getArrayIndexOfStartingNode(srcNode.x) : srcNode.x;
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = color;
    }

    private void disableEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode){
        if (srcNode.y == 0) {
            AbstractDungeon.map.get(0).get(getArrayIndexOfStartingNode(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).taken = false;
        } else {
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).taken = false;
        }
    }

}
