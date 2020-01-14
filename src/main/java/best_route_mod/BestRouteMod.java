package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.*;
import best_route_mod.patches.SelectedNextRoomPatch;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.util.*;

@SpireInitializer
public class BestRouteMod implements StartGameSubscriber {
    // TODO: restructure everything
    private static MapPath bestPath; // from current node or the whole map
    private static MapPath bestPathFromHoveredNode;

    private static HashMap<Class<?>, RoomClass> roomClasses;

    private static ColorPath colorPath;
    private static MapTraversal mapTraversal;

    public BestRouteMod() {
        // Initialize class instances
        colorPath = new ColorPath();
        mapTraversal = new MapTraversal();

        // TODO: load from preferences
        // Load default room class settings
        // TODO: make dynamic by iterating through legend items
        roomClasses = new HashMap<>();
        roomClasses.put(EventRoom.class, new RoomClass(createColorFrom255(0,0, 255)));
        roomClasses.put(ShopRoom.class, new RoomClass(createColorFrom255(127,0, 255)));
        roomClasses.put(TreasureRoom.class, new RoomClass(createColorFrom255(255,255, 0)));
        roomClasses.put(RestRoom.class, new RoomClass(createColorFrom255(0,255, 0)));
        roomClasses.put(MonsterRoom.class, new RoomClass(createColorFrom255(255,0, 0)));
        roomClasses.put(MonsterRoomElite.class, new RoomClass(createColorFrom255(255,127, 0)));

        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    private Color createColorFrom255(int r, int g, int b){
        return new Color(r/255f, g/255f, b/255f, 1);
    }

    // Reset this variable so we know if we have changed rooms
    @Override
    public void receiveStartGame() {
        SelectedNextRoomPatch.resetChangedRooms();
    }

    public static void initialize() {
        new BestRouteMod();
    }

    // API methods

    // Priority index methods

    public static boolean allPriorityIndicesAreZero(){
        for(RoomClass properties: roomClasses.values()){
            if(properties.isActive()) return false;
        }
        return true;
    }

    // Get the lowest priority index > 0 with an active room class
    public static ArrayList<Class<?>> getRoomsAtLowestPriorityIndex() {
        int lowestPriorityIndex = -1;
        for (RoomClass properties : roomClasses.values()) {
            if (properties.isActive() && (lowestPriorityIndex == -1 || properties.getPriorityLevel() < lowestPriorityIndex))
                lowestPriorityIndex = properties.getPriorityLevel();

        }
    }

    /*
    private static Color getColorOfRoomClass(Class<?> roomClass){
        return roomClassProperties.get(roomClass).getColor();
    }

    public static int getPriorityIndexOfRoomClass(Class<?> roomClass){
        return roomClassProperties.get(roomClass).getPriorityLevel();
    }

    public static Class<?> getRoomClassByLegendIndex(int index){
        return (Class<?>)roomClassProperties.keySet().toArray()[index];
    }
*/

    public static Class<?>[] getRoomClasses(){
        return (Class<?>[])roomClasses.keySet().toArray();
    }

    public static void findAndShowBestPathFromNode(MapRoomNode node){
        colorPath.disableCurrentlyColoredPath();
        bestPath = mapTraversal.findBestPathFromNode(node);
        colorPath.colorPath(bestPath);
    }

    public static void findAndShowBestPathFromStartingNodes(){
        colorPath.disableCurrentlyColoredPath();
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        bestPath = mapTraversal.findBestPathFromNodes(startingNodes);
        colorPath.colorPath(bestPath);
    }

    private static void generateAndDisplayBestPath(){
        if(!currNodeAt(0, -1)){
            generateAndDisplayBestPathFromStartingNodes();
        }else if(!currNodeAt(-1, 15)){
            generateAndDisplayBestPathFromNode(AbstractDungeon.currMapNode);
        }
    }

    private static boolean currNodeAt(int x, int y){
        return AbstractDungeon.currMapNode.x == x && AbstractDungeon.currMapNode.y == y;
    }

    // Change room class properties and regenerate and show the path

    public static void raisePriority(Class<?> roomClass){
        int priorityLevel = roomClasses.get(roomClass).getPriorityLevel();
        // Max priority is the number of room classes
        if(priorityLevel < roomClasses.size()){
            roomClasses.get(roomClass).incrementPriorityLevel();
            // Current node may or may not be in map, so just use wrapper function to determine logic later
            generateAndDisplayBestPath();
        }
    }

    public static void lowerPriority(Class<?> roomClass){
        if(roomClasses.get(roomClass).isActive()){
            roomClasses.get(roomClass).decrementPriorityLevel();
            if(!allPriorityIndicesAreZero()) generateAndDisplayBestPath();
        }
    }

    public static void switchSign(Class<?> roomClass){
        roomClasses.get(roomClass).sign = roomClasses.get(roomClass).sign == '>' ? '<' : '>';
        if(!allPriorityIndicesAreZero()) generateAndDisplayBestPath();
    }

    // Private implementation

    // Traversal methods

    private static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
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
        for(Map.Entry<Class<?>, RoomClass> entry: roomClassProperties.entrySet()){
            if(entry.getValue().getPriorityLevel() == priorityIndex){
                roomClasses.add(entry.getKey());
            }
        }
        return roomClasses;
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
}