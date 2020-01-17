package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;

//@SuppressWarnings (value="unchecked")
public class MapReader {
    public static MapPath getBestPathFrom(MapRoomNode node, int neowsLamentCounter){
        ArrayList<MapRoomNode> jumpableNodes = getAdjacentNodesAbove(node);
        if(neowsLamentCounter > 0 && node.room.getClass() == MonsterRoomBoss.class
                || node.room.getClass() == MonsterRoom.class
                || node.room.getClass() == MonsterRoomElite.class){
            neowsLamentCounter--;
        }
        if (jumpableNodes.isEmpty() || neowsLamentCounter == 0) {
            return new MapPath(node);
        }

        MapPath bestPath;
        if(jumpableNodes.size() == 1){
            bestPath = getBestPathFrom(jumpableNodes.get(0), neowsLamentCounter);
        }else {
            bestPath = getBestPathFrom(jumpableNodes, neowsLamentCounter);
        }
        bestPath.pushToFront(node);
        return bestPath;
    }

    public static MapPath getBestPathFrom(MapRoomNode node){
        return getBestPathFrom(node, -1);
    }

    public static MapPath getBestPathFrom(ArrayList<MapRoomNode> nodes, int neowsLamentCounter){
        MapPath bestPath = new MapPath();
        for (MapRoomNode node : nodes) {
            MapPath currentPath = getBestPathFrom(node, neowsLamentCounter);
            if (bestPath.isEmpty() || currentPath.size() > bestPath.size() && neowsLamentCounter >= 0
                    || (iteratedPathExceedsBestPath(bestPath, currentPath) && neowsLamentCounter == -1)) {
                bestPath = currentPath;
            }
        }
        return bestPath;
    }

    public static MapPath getBestPathFrom(ArrayList<MapRoomNode> nodes){
        return getBestPathFrom(nodes, -1);
    }

    public static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    private static boolean iteratedPathExceedsBestPath(MapPath bestPath, MapPath iteratedPath){
        // Iterate through each level
        for(int i = 1; i <= RoomClassManager.getNumRoomClasses(); i++){
            ArrayList<Class<?>> roomClassesWithPriorityIndex = RoomClassManager.getRoomClasses(i);
            // Just skip to the next level to compare
            // if(roomClassesWithPriorityIndex.isEmpty()) continue;
            for(Class<?> roomClass: roomClassesWithPriorityIndex){
                boolean roomCountGreaterThan = iteratedPath.getRoomCount(roomClass) > bestPath.getRoomCount(roomClass);
                boolean roomCountLessThan = iteratedPath.getRoomCount(roomClass) < bestPath.getRoomCount(roomClass);
                boolean signGreaterThan = RoomClassManager.getSignOf(roomClass) == '>';
                boolean signLessThan = RoomClassManager.getSignOf(roomClass) == '<';
                if((roomCountGreaterThan && signGreaterThan) || (roomCountLessThan && signLessThan)){
                    return true;
                }
                if(iteratedPath.getRoomCount(roomClass) == bestPath.getRoomCount(roomClass)) {
                    continue;
                }
                return false;
            }
        }
        return false;
    }

    private static ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node) {
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<>();
        mapEdges.forEach(mapEdge -> {
            // The boss node is 2 levels above the last rest site nodes, don't count it since we can't access it on the
            // AbstractDungeon.map object
            if (mapEdge.dstY - node.y == 1) {
                adjacentNodesAboveGivenNode.add(getNodeAt(mapEdge.dstX, mapEdge.dstY));
            }
        });
        return adjacentNodesAboveGivenNode;
    }

    // Get node using coordinates.  x = order (left to right), y = level. AbstractDungeon.map has ArrayLists for each
    // level (y), and each ArrayList has seven nodes in that level accessible by their index (x).
    private static MapRoomNode getNodeAt(int x, int y){
        if(y == 0){
            // The first level (at y = 0) only contains the starting nodes, so we have to iterate through the list to
            // find the right node
            for(MapRoomNode node: AbstractDungeon.map.get(0)){
                if(node.x == x){
                    return node;
                }
            }
        }else{
            return AbstractDungeon.map.get(y).get(x);
        }
        return null; // Node not found
    }
}
