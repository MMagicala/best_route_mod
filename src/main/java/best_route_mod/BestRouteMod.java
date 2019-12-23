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

    @Override
    public void receivePostInitialize() { }

    public BestRouteMod() {
        // Statically create criteria list: path with most rest sites and least elite encounters
        comparisons = new ArrayList<RoomComparison>() {{
            add(new RoomComparison(RestRoom.class, SignOperator.GREATER));
            add(new RoomComparison(ShopRoom.class, SignOperator.GREATER));
            add(new RoomComparison(MonsterRoom.class, SignOperator.LESS));
            add(new RoomComparison(MonsterRoomElite.class, SignOperator.LESS));
        }};
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    // first row of map only contains starting nodes, other rows always have 7 nodes
    // 0,-1 node is whale

    public static void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode) {
        if (srcNode.y == 0) {
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).markAsTaken();
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).color = Color.RED;
        } else {
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = Color.RED;
        }

    }

    public static ArrayList<MapRoomNode> getStartingNodes() {
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    public static ArrayList<RoomComparison> comparisons;

    public static MapPath findBestPathFromNode(MapRoomNode node){
        return findBestPathFromAdjacentOrStartingNodes(new ArrayList<MapRoomNode>(){{add(node);}});
    }

    public static MapPath findBestPathFromAdjacentOrStartingNodes(ArrayList<MapRoomNode> nodes) {
        MapPath bestPath = new MapPath();
        for (MapRoomNode node : nodes) {
            MapPath currentPath = traverseInDepthOrder(node);
            for (int i = 0; i < comparisons.size(); i++) {
                if (bestPath.notSet() || comparisons.get(i).isMet(currentPath, bestPath)) {
                    bestPath = currentPath;
                    break;
                }
                if (comparisons.get(i).hasEqualNumRooms(currentPath, bestPath)) {
                    continue;
                }
                // Room does not meet the criteria to replace the old one, exit
                break;
            }
        }
        return bestPath;
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

    // THe first row of nodes have x-coordinates different from their array indices
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
}