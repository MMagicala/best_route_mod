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

    private static Class roomClass;
    public static MapPath bestPath;
    // Determine what color should represent each room (the order of the rooms inserted is the same as the legend's)
    public static LinkedHashMap<Class, Color> roomClassesAndColors;
    public static int selectedRoomIndex;

    public BestRouteMod() {
        roomClassesAndColors = new LinkedHashMap<>();
        roomClassesAndColors.put(EventRoom.class, Color.BLUE);
        roomClassesAndColors.put(ShopRoom.class, Color.PURPLE);
        roomClassesAndColors.put(TreasureRoom.class, Color.GOLD);
        roomClassesAndColors.put(RestRoom.class, Color.GREEN);
        roomClassesAndColors.put(MonsterRoom.class, Color.RED);
        roomClassesAndColors.put(MonsterRoomElite.class, Color.MAGENTA);

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

    // API methods

    public static void setRoomClass(Class newRoomClass){
        roomClass = newRoomClass;
    }

    public static void generateAndShowBestPathFromCurrentNode(){
        if(bestPath != null) disableCurrentBestPath();
        bestPath = findBestPathFromNode(AbstractDungeon.currMapNode);
        colorBestPath(roomClassesAndColors.get(roomClass));
    }

    public static void generateAndShowBestPathFromStartingNodes(){
        if(bestPath != null) disableCurrentBestPath();
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
        colorBestPath(roomClassesAndColors.get(roomClass));
    }

    // Private implementation

    // Coloring path methods

    private static void colorBestPath(Color color){
        // Color the edges in the map
        ArrayList<MapRoomNode> pathListOfNodes = bestPath.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            colorEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1), color);
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
            if (bestPath == null || currentPath.getRoomCount(roomClass) > bestPath.getRoomCount(roomClass)) {
                bestPath = currentPath;
            }
        }
        return bestPath;
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

    public static boolean currMapNodeAtWhale(){
        return AbstractDungeon.currMapNode.y == -1;
    }

    public static void resetMod(){
        bestPath = null;
        selectedRoomIndex = -1;
    }
}