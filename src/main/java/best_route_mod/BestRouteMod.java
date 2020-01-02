package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.WingBoots;
import com.megacrit.cardcrawl.rooms.*;

import java.io.IOException;
import java.util.*;

@SpireInitializer
public class BestRouteMod implements PostDungeonInitializeSubscriber {

    // TODO: work on having multiple best paths if all of their criterias are equal
    // TODO: in game configurations
    // TODO; add support for winged boots
    private static MapPath bestPath;

    public static MapPath getBestPath(){
        return bestPath;
    }

    private static LinkedHashMap<Class<?>, RoomClassProperties> roomClassProperties;

    public BestRouteMod() {
        roomClassProperties = new LinkedHashMap<>();
        // Same order as legend items
        roomClassProperties.put(EventRoom.class, new RoomClassProperties(createColorFrom255(0,0, 255)));
        roomClassProperties.put(ShopRoom.class, new RoomClassProperties(createColorFrom255(127,0, 255)));
        roomClassProperties.put(TreasureRoom.class, new RoomClassProperties(createColorFrom255(255,255, 0)));
        roomClassProperties.put(RestRoom.class, new RoomClassProperties(createColorFrom255(0,255, 0)));
        roomClassProperties.put(MonsterRoom.class, new RoomClassProperties(createColorFrom255(255,0, 0)));
        roomClassProperties.put(MonsterRoomElite.class, new RoomClassProperties(createColorFrom255(255,127, 0)));

        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    private Color createColorFrom255(int r, int g, int b){
        return new Color(r/255f, g/255f, b/255f, 1);
    }

    // TODO: presets
    @Override
    public void receivePostDungeonInitialize() { }

    public static void initialize() {
        new BestRouteMod();
    }

    // first row of map only contains starting nodes, other rows always have 7 nodes
    // 0,-1 node is whale

    // API methods

    public static boolean allPriorityIndicesAreZero(){
        for(RoomClassProperties properties: roomClassProperties.values()){
            if(properties.isActive()) return false;
        }
        return true;
    }

    private static int getLowestPriorityIndexWithRoom(){
        int lowestPriorityIndex = -1;
        for(RoomClassProperties properties: roomClassProperties.values()){
            if(properties.isActive() && (properties.getPriorityLevel() < lowestPriorityIndex || lowestPriorityIndex == -1))
                lowestPriorityIndex = properties.getPriorityLevel();
        }
        return lowestPriorityIndex;
    }

    private static Color getColorOfRoomClass(Class<?> roomClass){
        return roomClassProperties.get(roomClass).getColor();
    }

    public static int getPriorityIndexOfRoomClass(Class<?> roomClass){
        return roomClassProperties.get(roomClass).getPriorityLevel();
    }

    public static char getSignOfRoomClass(Class<?> roomClass){
        return roomClassProperties.get(roomClass).sign;
    }

    public static void setSignOfRoomClass(Class<?> roomClass, char sign){
        roomClassProperties.get(roomClass).sign = sign;
    }

    public static Class<?> getRoomClassByLegendIndex(int index){
        return (Class<?>)roomClassProperties.keySet().toArray()[index];
    }

    // 0 - not active
    // > 0 - active
    public static boolean raiseRoomClassPriority(Class<?> roomClass){
        int priorityLevel = roomClassProperties.get(roomClass).getPriorityLevel();
        if(priorityLevel < roomClassProperties.size()){
            roomClassProperties.get(roomClass).incrementPriorityLevel();
            return true;
        }
        return false;
    }

    public static boolean lowerRoomClassPriority(Class<?> roomClass){
        if(roomClassProperties.get(roomClass).isActive()){
            roomClassProperties.get(roomClass).decrementPriorityLevel();
            return true;
        }
        return false;
    }

    public static void generateAndShowBestPathFromCurrentNode(){
        if(allPriorityIndicesAreZero()) return;
        if(bestPath != null) disableCurrentBestPath();
        bestPath = findBestPathFromNode(AbstractDungeon.currMapNode);
        colorBestPath();
    }

    public static void generateAndShowBestPathFromStartingNodes(){
        if(allPriorityIndicesAreZero()) return;
        if(bestPath != null) disableCurrentBestPath();
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
        colorBestPath();
    }

    // Private implementation

    // Coloring path methods

    private static void colorBestPath(){
        // Determine color to use using the lowest priority index that contains a room
        int lowestPriorityIndex = getLowestPriorityIndexWithRoom();
        // if(lowestPriorityIndex == -1) return; this should not happen
        ArrayList<Class<?>> roomClassesAtLowestPriorityIndex = getRoomClassesWithPriorityIndex(lowestPriorityIndex);

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

    private static void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode, Color color) {
        int xCoordinateOfStartingNode = srcNode.y == 0 ? getArrayIndexOfStartingNode(srcNode.x) : srcNode.x;
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
        AbstractDungeon.map.get(srcNode.y).get(xCoordinateOfStartingNode).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = color;
    }

    // Disable path methods

    public static void disableCurrentBestPath() {
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

    private static int getRelicCounter(Class<?> relicClass){
        for(AbstractRelic relic: AbstractDungeon.player.relics){
            if(relic.getClass() == relicClass){
                return relic.counter;
            }
        }
    }

    // Travel all the nodes on the map (except for the boss node)
    private static MapPath findBestPathFromNode(MapRoomNode node) {
        int wingBootsCounter = AbstractDungeon.player.hasRelic(WingBoots.ID) ? getRelicCounter(WingBoots.class) : 0;
        // TODO: here
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
            }
        }
        return bestPath;
    }

    private static boolean currentPathCriteriaExceedsBestPath(MapPath bestPath, MapPath currentPath){
        // Iterate through each level
        for(int i = 1; i <= roomClassProperties.size(); i++){
            ArrayList<Class<?>> roomClassesWithPriorityIndex = getRoomClassesWithPriorityIndex(i);
            // Just skip to the next level to compare
            // if(roomClassesWithPriorityIndex.isEmpty()) continue;
            for(Class<?> roomClass: roomClassesWithPriorityIndex){
                boolean roomCountGreaterThan = currentPath.getRoomCount(roomClass) > bestPath.getRoomCount(roomClass);
                boolean roomCountLessThan = currentPath.getRoomCount(roomClass) < bestPath.getRoomCount(roomClass);
                boolean signGreaterThan = roomClassProperties.get(roomClass).sign == '>';
                boolean signLessThan = roomClassProperties.get(roomClass).sign == '<';
                if((roomCountGreaterThan && signGreaterThan) || (roomCountLessThan && signLessThan)){
                    return true;
                }
                if(currentPath.getRoomCount(roomClass) == bestPath.getRoomCount(roomClass)) {
                    continue;
                }
                return false;
            }
        }
        return false;
    }

    private static ArrayList<Class<?>> getRoomClassesWithPriorityIndex(int priorityIndex){
        ArrayList<Class<?>> roomClasses = new ArrayList<>();
        for(Map.Entry<Class<?>, RoomClassProperties> entry: roomClassProperties.entrySet()){
            if(entry.getValue().getPriorityLevel() == priorityIndex){
                roomClasses.add(entry.getKey());
            }
        }
        return roomClasses;
    }

    private static ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node) {
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<>();
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

    public static void resetBestPath(){
        bestPath = null;
    }
}