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

    public static ArrayList<ArrayList<RoomComparison>> comparisons;
    public static MapPath bestPath;

    @Override
    public void receivePostInitialize() { }

    public BestRouteMod() {
        // Statically create criteria list: path with most rest sites and least elite encounters
        comparisons = new ArrayList<>();

        addComparisonOnTop(new RoomComparison(RestRoom.class, SignOperator.GREATER));
        addComparisonOnTop(new RoomComparison(ShopRoom.class, SignOperator.GREATER));
        //addComparisonAtIndex(new RoomComparison(MonsterRoom.class, SignOperator.LESS), 0);
        //addComparisonAtIndex(new RoomComparison(MonsterRoomElite.class, SignOperator.LESS), 0);

        System.out.println("# of levels: " + comparisons.size() + ", # of comparisons: " + comparisons.get(0).size());

        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    public void addComparisonOnTop(RoomComparison comparison){
        comparisons.add(new ArrayList<>());
        comparisons.get(comparisons.size()-1).add(comparison);
    }

    public void addComparisonBelow(RoomComparison comparison){
        comparisons.add(0, new ArrayList<>());
        comparisons.get(0).add(comparison);
    }

    public void addComparisonAtIndex(RoomComparison comparison, int index){
        comparisons.get(index).add(comparison);
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
        System.out.println("Finding best path from node list of length " + nodes.size() + " using # of comparisons " + comparisons.get(0).size());
        MapPath bestPath = new MapPath();
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println("i: " + i);
            MapPath currentPath = traverseInDepthOrder(nodes.get(i));
            for (int j = 0; j < comparisons.size(); j++) {
                System.out.println("j: " + j);
                if (bestPath.notSet() || allComparisonsOnSameLevelMet(comparisons.get(j), currentPath, bestPath)) {
                    bestPath = currentPath;
                    System.out.println(bestPath.notSet());
                    break;
                }
                if (allComparisonsOnSameLevelEqual(comparisons.get(j), currentPath, bestPath)) {
                    System.out.println("Current path meets same criteria as best path. continuing");
                    continue;
                }
                // Room does not meet the criteria to replace the old one, exit
                System.out.println("Room does not meet criteria. exit");
                break;
            }
        }
        System.out.println("returning best path");
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
}