package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@SpireInitializer
public class BestRouteMod implements PostInitializeSubscriber {

    public static MapPath bestPath;
    private static final int NUM_COMPARISON_LEVELS = 6;

    @Override
    public void receivePostInitialize() { }

    public BestRouteMod() {
        // Statically create criteria list: path with most rest sites and least elite encounters
        comparisons = new ArrayList<>();
        for(int i = 0; i < NUM_COMPARISON_LEVELS; i++){
            comparisons.add(new ArrayList<>());
        }

        addComparisonToIndex(new RoomComparison(MonsterRoomElite.class, SignOperator.LESS), 1);
        addComparisonToIndex(new RoomComparison(RestRoom.class, SignOperator.GREATER), 0);

        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    // first row of map only contains starting nodes, other rows always have 7 nodes
    // 0,-1 node is whale

    public static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    public static MapPath findBestPathFromNode(MapRoomNode node){
        return traverseInDepthOrder(node);
    }

    private static boolean allComparisonsOnSameLevelMet(ArrayList<RoomComparison> comparisons, MapPath currentPath, MapPath bestPath){
        for(RoomComparison comparison: comparisons){
            if(!comparison.isMet(currentPath, bestPath)){
                return false;
            }
        }
        return true;
    }

    private static boolean allComparisonsOnSameLevelEqual(ArrayList<RoomComparison> comparisons, MapPath currentPath, MapPath bestPath){
        for(RoomComparison comparison: comparisons){
            if(!comparison.hasEqualNumRooms(currentPath, bestPath)){
                return false;
            }
        }
        return true;
    }

    public static MapPath findBestPathFromAdjacentOrStartingNodes(ArrayList<MapRoomNode> nodes) {
        MapPath bestPath = new MapPath();
        for (int i = 0; i < nodes.size(); i++) {
            MapPath currentPath = traverseInDepthOrder(nodes.get(i));
            for (int j = 0; j < comparisons.size(); j++) {
                if (bestPath.notSet() || allComparisonsOnSameLevelMet(comparisons.get(j), currentPath, bestPath)) {
                    bestPath = currentPath;
                    break;
                }
                if (allComparisonsOnSameLevelEqual(comparisons.get(j), currentPath, bestPath)) {
                    continue;
                }
                // Room does not meet the criteria to replace the old one, exit
                break;
            }
        }
        return bestPath;
    }

    public static void colorPath(MapPath path, Color color){
        // Color the edges in the map
        ArrayList<MapRoomNode> pathListOfNodes = path.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            colorEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1), Color.RED);
        }
    }

    public static void disablePath(MapPath path) {
        ArrayList<MapRoomNode> pathListOfNodes = path.getListOfNodes();
        for (int i = 0; i < pathListOfNodes.size() - 1; i++) {
            disableEdgeInMap(pathListOfNodes.get(i), pathListOfNodes.get(i + 1));
        }
    }

    // PRIVATE METHODS

    // Travel all the nodes on the map (except for the boss node)
    private static MapPath traverseInDepthOrder(MapRoomNode node) {
        // printNode(node);
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = getAdjacentNodesAbove(node);
        // Last node will always be a campfire
        if (adjacentNodesAboveGivenNode.isEmpty()) {
            HashMap<Class, Integer> roomCounts = new HashMap<Class, Integer>();
            roomCounts.put(RestRoom.class, 1);
            return new MapPath(node, roomCounts);
        }

        MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(adjacentNodesAboveGivenNode);
        bestPath.pushNodeToFrontOfPath(node);
        bestPath.incrementRoomCount(node.room.getClass());

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
            return AbstractDungeon.map.get(y).get(getArrayIndexOfXCoordinate(x));
        }
        return AbstractDungeon.map.get(y).get(x);
    }

    // The first row of nodes have x-coordinates different from their array indices
    // So we have to loop through the first row to find the correct node
    private static int getArrayIndexOfXCoordinate(int x) {
        for (int i = 0; i < AbstractDungeon.map.get(0).size(); i++) {
            MapRoomNode node = AbstractDungeon.map.get(0).get(i);
            if (node.x == x) {
                return i;
            }
        }
        return -1;
    }

    private static void disableEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode){
        if (srcNode.y == 0) {
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).taken = false;
        } else {
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).taken = false;
        }
    }

    private static void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode, Color color) {
        if (srcNode.y == 0) {
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).markAsTaken();
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).color = color;
        } else {
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = color;
        }
    }

    public static void generateAndShowBestPathFromCurrentNode(){
        if(bestPath != null) disablePath(bestPath);
        bestPath = findBestPathFromNode(AbstractDungeon.currMapNode);
        colorPath(bestPath, Color.RED);
    }

    public static void generateAndShowBestPathFromStartingNodes(){
        if(bestPath != null) disablePath(bestPath);
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
        colorPath(bestPath, Color.RED);
    }

    public static boolean currMapNodeAtWhale(){
        return AbstractDungeon.currMapNode.y == -1;
    }
}