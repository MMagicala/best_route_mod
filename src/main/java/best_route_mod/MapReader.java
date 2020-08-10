package best_route_mod;

import best_route_mod.patches.InputHelperPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;
import javassist.NotFoundException;

public class MapReader {
    public static boolean emeraldKeyExists(){
        for(ArrayList<MapRoomNode> list: AbstractDungeon.map){
            for(MapRoomNode node: list){
                if(node.hasEmeraldKey){
                	return true;
				}
            }
        }
        return false;
    }

    public static boolean nodeContainsCombatRoom(MapRoomNode node){
        return node.room.getClass() == MonsterRoom.class
                || node.room.getClass() == MonsterRoomElite.class
                || node.room.getClass() == MonsterRoomBoss.class;
    }

    // neowsLamentCounter = -1 when we don't factor it in our search
    public static MapPath getBestPathFrom(MapRoomNode node, boolean isEmeraldKeyRequired,
                                          int neowsLamentCounter){
        ArrayList<MapRoomNode> jumpableNodes = getAdjacentNodesAbove(node);
        if(nodeContainsCombatRoom(node) && AbstractDungeon.currMapNode != node && neowsLamentCounter > 0){
            neowsLamentCounter--;
        }
        if (jumpableNodes.isEmpty() || neowsLamentCounter == 0) {
            return new MapPath(node);
        }

        MapPath bestPath;
        if(jumpableNodes.size() == 1){
            bestPath = getBestPathFrom(jumpableNodes.get(0), isEmeraldKeyRequired, neowsLamentCounter);
        }else {
            bestPath = getBestPathFrom(jumpableNodes, isEmeraldKeyRequired, neowsLamentCounter, false);
        }
        bestPath.pushToFront(node);
        return bestPath;
    }

    public static MapPath getBestPathFrom(ArrayList<MapRoomNode> nodes, boolean isEmeraldKeyRequired,
                                          int neowsLamentCounter, boolean isBaseCase){
        MapPath bestPath = null;
        for (MapRoomNode node : nodes) {
            MapPath currentPath = getBestPathFrom(node, isEmeraldKeyRequired, neowsLamentCounter);
            if(iteratedPathExceedsBestPath(bestPath, currentPath, isEmeraldKeyRequired, neowsLamentCounter)) {
                bestPath = currentPath;
            }
        }
        // After finding the best neow's lament path from the start, find the best path from the last node
        if(neowsLamentCounter >= 0 && isBaseCase){
            MapPath bestPathFromNeowsLamentPath = MapReader.getBestPathFrom(bestPath.getLastNode(),
                    isEmeraldKeyRequired, -1);
            // Mark the index of the last node in neow laments path so ColorPathManager can use the right colors
            bestPath.neowsLamentPathEndIndex = bestPath.getNodeCount()-1;
            bestPath.appendPath(bestPathFromNeowsLamentPath);
        }
        return bestPath;
    }
    // Helper methods

    public static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    // Check if we can reach the emerald key from this node
    public static boolean isEmeraldKeyReachableFrom(MapRoomNode node){
        if(node.hasEmeraldKey) return true;
        // Keep searching through the map
        ArrayList<MapRoomNode> adjacentNodesAboveNode = getAdjacentNodesAbove(node);
        if(adjacentNodesAboveNode.isEmpty()){
            return false;
        }else{
            for(MapRoomNode adjNode: adjacentNodesAboveNode){
                 if(isEmeraldKeyReachableFrom(adjNode)) return true;
            }
        }
        return false;
    }

    private static boolean pathHasOrCanReachEmerald(MapPath path){
        return path.hasEmerald() || MapReader.isEmeraldKeyReachableFrom(path.getLastNode());
    }

    private static boolean iteratedPathExceedsBestPath(MapPath bestPath, MapPath iteratedPath,
                                                       boolean isEmeraldKeyRequired, int neowsLamentCounter){
        // If the best path is null, just set it to the iterated path until we inevitably find a path that meets
        // the criteria we need
        if (bestPath == null) return true;

        if (isEmeraldKeyRequired) {
            if(neowsLamentCounter == -1){
                // If we already found a best path with an emerald, we cannot accept a path that doesn't have one
                if(bestPath.hasEmerald() && !iteratedPath.hasEmerald()) return false;
                else if(!bestPath.hasEmerald() && iteratedPath.hasEmerald()) return true;
            }else{
                if(pathHasOrCanReachEmerald(bestPath) && !pathHasOrCanReachEmerald(iteratedPath)) return false;
                else if(!pathHasOrCanReachEmerald(bestPath) && pathHasOrCanReachEmerald(iteratedPath)) return true;
            }
        }

        // Compare for neows lament if it is factored
        if(neowsLamentCounter >= 0){
            if(bestPath.getNodeCount() < iteratedPath.getNodeCount()) return true;
            else if(bestPath.getNodeCount() == iteratedPath.getNodeCount()){
                Class<?>[] bestPathRoomClasses = bestPath.getOrderedRoomClasses();
                Class<?>[] iteratedPathRoomClasses = iteratedPath.getOrderedRoomClasses();

                boolean foundFirstEventRoomInBestPath = false, foundFirstEventRoomInIteratedPath = false;
                for(int i = 0; i < bestPathRoomClasses.length; i++){
                    if(bestPathRoomClasses[i] == EventRoom.class){
                        foundFirstEventRoomInBestPath = true;
                    }
                    if(iteratedPathRoomClasses[i] == EventRoom.class){
                        foundFirstEventRoomInIteratedPath = true;
                    }
                    if(foundFirstEventRoomInBestPath){
                        if(foundFirstEventRoomInIteratedPath) {
                            // Found event room in the same level, keep iterating
                            foundFirstEventRoomInBestPath = false;
                            foundFirstEventRoomInIteratedPath = false;
                        // The iterated path will drag on for longer so use it as the best path
                        }else return true;
                    // The iterated path will generate an event room
                    }else if(foundFirstEventRoomInIteratedPath) return false;
                }
            }
            return false;
        }

        // Iterate through each level
        for(int i = 1; i <= RoomClassManager.getNumRoomClasses(); i++){
            ArrayList<Class<?>> roomClassesWithPriorityIndex = RoomClassManager.getRoomClasses(i);
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
				try
				{
					adjacentNodesAboveGivenNode.add(getNodeAt(mapEdge.dstX, mapEdge.dstY));
				}
				catch (NotFoundException e)
				{
					e.printStackTrace();
				}
			}
        });
        return adjacentNodesAboveGivenNode;
    }

    // Get node using coordinates.  x = order (left to right), y = level. AbstractDungeon.map has ArrayLists for each
    // level (y), and each ArrayList has seven nodes in that level accessible by their index (x).
    private static MapRoomNode getNodeAt(int x, int y) throws NotFoundException
	{
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
		throw new NotFoundException("Node not found");
    }
}
