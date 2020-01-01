package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.util.*;

@SpireInitializer
public class BestRouteMod implements PostDungeonInitializeSubscriber {

    // TODO: work on having multiple best paths if all of their criterias are equal
    private static MapPath bestPath;
    private static LinkedHashMap<Class<?>, RoomClassProperties> roomClassProperties;

    public BestRouteMod() {
        roomClassProperties = new LinkedHashMap<>();
        // Same order as legend items
        roomClassProperties.put(EventRoom.class, new RoomClassProperties());
        roomClassProperties.put(ShopRoom.class, new RoomClassProperties());
        roomClassProperties.put(TreasureRoom.class, new RoomClassProperties());
        roomClassProperties.put(RestRoom.class, new RoomClassProperties());
        roomClassProperties.put(MonsterRoom.class, new RoomClassProperties());
        roomClassProperties.put(MonsterRoomElite.class, new RoomClassProperties());

        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    // Just use this since we need to subscribe to at least one thing
    @Override
    public void receivePostDungeonInitialize() { }

    public static void initialize() {
        new BestRouteMod();
    }

    // first row of map only contains starting nodes, other rows always have 7 nodes
    // 0,-1 node is whale

    private static int getNumActiveRoomClasses(){
        int numActiveRoomClasses = 0;
        for(RoomClassProperties properties: roomClassProperties.values()){
            if(properties.isActive()){
                numActiveRoomClasses++;
            }
        }
        return numActiveRoomClasses;
    }

    // API methods

    public static Class<?> getRoomClassByLegendIndex(int index){
        return (Class<?>)roomClassProperties.keySet().toArray()[index];
    }

    // 0 - not active
    // > 0 - active
    public static boolean raiseRoomClassPriority(Class<?> roomClass){
        if(roomClassProperties.get(roomClass).getPriorityLevel() < getNumActiveRoomClasses()){
            roomClassProperties.get(roomClass).incrementPriorityLevel();
            return true;
        }
        return false;
    }

    public static boolean lowerRoomClassPriority(Class<?> roomClass){
        if(roomClassProperties.get(roomClass).getPriorityLevel() > 0){
            roomClassProperties.get(roomClass).decrementPriorityLevel();
            // Check for priority indices above the maximum limit and adjust
            if(!roomClassProperties.get(roomClass).isActive()){
                for(RoomClassProperties properties: roomClassProperties.values()){
                    if(properties.getPriorityLevel() > getNumActiveRoomClasses()){
                        properties.decrementPriorityLevel();
                    }
                }
            }
            return true;
        }
        return false;
    }

    public static void generateAndShowBestPathFromCurrentNode(){
        if(bestPath != null) disableCurrentBestPath();
        bestPath = findBestPathFromNode(AbstractDungeon.currMapNode);
        colorBestPath();
    }

    public static void generateAndShowBestPathFromStartingNodes(){
        if(bestPath != null) disableCurrentBestPath();
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
        colorBestPath();
    }

    // Private implementation

    // Coloring path methods

    private static void colorBestPath(){
        // Color the edges in the map
        ArrayList<MapRoomNode> pathListOfNodes = bestPath.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            colorEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1), Color.RED);
        }
    }

    private static void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode, Color color) {
        int xCoordinateOfStartingNode = srcNode.y == 0 ? getArrayIndexOfStartingNode(srcNode.x) : srcNode.x;
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = color;
    }

    // Disable path methods

    private static void disableCurrentBestPath() {
        ArrayList<MapRoomNode> pathListOfNodes = bestPath.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            disableEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1));
        }
    }

    private static void disableEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode){
        if (srcNode.y == 0) {
            AbstractDungeon.map.get(0).get(getArrayIndexOfStartingNode(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).taken = false;
        } else {
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).taken = false;
        }
    }

    // Traversal methods

    private static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    // Travel all the nodes on the map (except for the boss node)
    private static MapPath findBestPathFromNode(MapRoomNode node) {
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = getAdjacentNodesAbove(node);
        // Last node will always be a campfire
        if (adjacentNodesAboveGivenNode.isEmpty()) {
            HashMap<Class, Integer> roomCounts = new HashMap<>();
            roomCounts.put(RestRoom.class, 1);
            return new MapPath(node, roomCounts);
        }

        MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(adjacentNodesAboveGivenNode);
        bestPath.pushNodeToFrontOfPath(node);
        bestPath.incrementRoomCount(node.room.getClass());

        return bestPath;
    }


    private static MapPath findBestPathFromAdjacentOrStartingNodes(ArrayList<MapRoomNode> nodes) {
        MapPath bestPath = null;
        for (MapRoomNode node : nodes) {
            MapPath currentPath = findBestPathFromNode(node);
            if (bestPath == null || currentPathCriteriaExceedsBestPath(bestPath, currentPath)) {
                bestPath = currentPath;
            }else if(currentPathCriteriaMatchesBestPath(bestPath, currentPath)){
                continue;
            }
        }
        return bestPath;
    }

    private static boolean currentPathCriteriaExceedsBestPath(MapPath bestPath, MapPath currentPath){
        // TODO: work on priorities
        for(int i = 1; i <= getNumActiveRoomClasses(); i++){
            boolean currentReplacesBestPath = currentPath.getRoomCount(properties.getKey()) > bestPath.getRoomCount(properties.getKey());
            if(properties.getValue().getSign() == '>' && currentReplacesBestPath){
                return true;
            }else{

            }
        }
    }

    private static boolean currentPathCriteriaMatchesBestPath(MapPath bestPath, MapPath currentPath){
        // Max possible number of priority levels
        for(int i = 1; i <= getNumActiveRoomClasses(); i++){
            ArrayList<Class<?>> roomClassesWithPriorityIndex = getRoomClassesWithPriorityIndex(i);
            for(Class<?> roomClass: roomClassesWithPriorityIndex){
                if(currentPath.getRoomCount(roomClass) != bestPath.getRoomCount(roomClass)) return false;
            }
        }
    }

    // TODO: work on this
    private static ArrayList<Class<?>> getRoomClassesWithPriorityIndex(int priorityIndex){
        ArrayList<Class<?>> roomClasses = new ArrayList<>();
        for(int i = 0; i < roomClassProperties.size(); i++){
            if(roomClassProperties.get().getPriorityLevel() == priorityIndex){
                roomClasses.add(roomClassProperties.);
            }
        }
    }

    private static ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node) {
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<MapRoomNode>();
        mapEdges.forEach(mapEdge -> {
            // The boss node is 2 levels above the last rest site nodes, don't count it since we can't access it on the
            // AbstractDungeon.map object
            if (mapEdge.dstY - node.y == 1) {
                adjacentNodesAboveGivenNode.add(getNodeAtCoordinates(mapEdge.dstX, mapEdge.dstY));
            }
        });
        return adjacentNodesAboveGivenNode;
    }

    private static MapRoomNode getNodeAtCoordinates(int x, int y) {
        if (y == 0) {
            return AbstractDungeon.map.get(y).get(getArrayIndexOfStartingNode(x));
        }
        return AbstractDungeon.map.get(y).get(x);
    }

    // The first row of nodes have x-coordinates different from their array indices
    // So we have to loop through the first row to find the correct node
    private static int getArrayIndexOfStartingNode(int x) {
        for (int i = 0; i < AbstractDungeon.map.get(0).size(); i++) {
            MapRoomNode node = AbstractDungeon.map.get(0).get(i);
            if (node.x == x) {
                return i;
            }
        }
        return -1;
    }

    // Patch: Sometimes player can start at (15, -1), not just (-1, 0)
    public static boolean atBeginningOfAct(){
        return AbstractDungeon.currMapNode.y == -1 || (AbstractDungeon.currMapNode.x == -1 && AbstractDungeon.currMapNode.y == 15);
    }

    public static void resetMod(){
        bestPath = null;
    }
}