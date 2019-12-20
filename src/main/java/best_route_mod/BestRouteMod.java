package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

@SpireInitializer
public class BestRouteMod implements PostUpdateSubscriber, StartActSubscriber {

    public BestRouteMod() {
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    boolean foundBestPath = false;

    // Find the new best path for each act
    @Override
    public void receiveStartAct() {
        foundBestPath = false;
    }

    // first row of map only contains starting nodes, other rows always have 7 nodes
    // 0,-1 node is whale

    @Override
    public void receivePostUpdate() {
        // Statically create criteria list
        if(criteriaList == null) {
            criteriaList = new ArrayList<Criterium>() {{
                add(new Criterium(RestRoom.class, SignOperator.GREATER));
                add(new Criterium(MonsterRoomElite.class, SignOperator.LESS));
            }};
        }

        if(AbstractDungeon.currMapNode != null && !foundBestPath) {
            // Start traversal code
            ArrayList<MapRoomNode> startingNodes = getStartingNodes();
            MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);

            // Print for debug
            // printDungeon();
            // bestPath.printPath();

            // Color the edges in the map
            ArrayList<MapRoomNode> bestPathNodeList = bestPath.getListOfNodes();
            for(int i = 0; i < bestPathNodeList.size()-1; i++){
                colorEdgeInMap(bestPathNodeList.get(i), bestPathNodeList.get(i+1));
            }
            foundBestPath = true;
        }
    }

    private void colorEdgeInMap(MapRoomNode srcNode, MapRoomNode destNode){
        if(srcNode.y == 0){
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).markAsTaken();
            AbstractDungeon.map.get(0).get(getArrayIndexOfXCoordinate(srcNode.x)).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get((destNode.x))).color = Color.RED;
        }else{
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).markAsTaken();
            AbstractDungeon.map.get(srcNode.y).get(srcNode.x).getEdgeConnectedTo(AbstractDungeon.map.get(destNode.y).get(destNode.x)).color = Color.RED;
        }

    }

    private ArrayList<MapRoomNode> getStartingNodes(){
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    private ArrayList<Criterium> criteriaList;

    private MapPath findBestPathFromAdjacentOrStartingNodes(ArrayList<MapRoomNode> nodes){
        MapPath bestPath = new MapPath();
        for(MapRoomNode node: nodes){
            MapPath currentPath = traverseInDepthOrder(node);
            for(Criterium criterium: criteriaList){

                if(bestPath.isEmpty() || pathReplacesOldOne){
                    bestPath = currentPath;
                    break;
                }
                if(lookForNextCriteriaToTest){
                    // Room has the same value for this criteria, move on to next criteria to test
                    continue;
                }
                // Room does not meet the criteria to replace the old one, exit
                break;
            }
        }
        return bestPath;
    }

    // Travel all the nodes on the map (except for the boss node)
    private MapPath traverseInDepthOrder(MapRoomNode node) {
        // printNode(node);
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = getAdjacentNodesAbove(node);
        // Last node will always be a campfire
        if(adjacentNodesAboveGivenNode.isEmpty()) return new MapPath(node, 1, 0);

        MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(adjacentNodesAboveGivenNode);
        bestPath.pushNodeToFrontOfPath(node);

        if(node.room instanceof RestRoom) bestPath.incrementNumCampSites();
        else if(node.room instanceof MonsterRoomElite) bestPath.incrementNumElites();

        return bestPath;
    }

    private ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node){
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<MapRoomNode>();
        mapEdges.forEach(mapEdge -> {
            // The boss node is 2 levels above the last rest site nodes, don't count it since we can't access it on the
            // AbstractDungeon.map object
            if(mapEdge.dstY - node.y == 1){
                adjacentNodesAboveGivenNode.add(getNodeAtCoordinates(mapEdge.dstX, mapEdge.dstY));
            }
        });
        return adjacentNodesAboveGivenNode;
    }

    public MapRoomNode getNodeAtCoordinates(int x, int y){
        if(y == 0){
            return AbstractDungeon.map.get(y).get(getArrayIndexOfXCoordinate(x));
        }
        return AbstractDungeon.map.get(y).get(x);
    }

    // THe first row of nodes have x-coordinates different from their array indices
    // So we have to loop through the first row to find the correct node
    private int getArrayIndexOfXCoordinate(int x){
        for(int i = 0; i < AbstractDungeon.map.get(0).size(); i++){
            MapRoomNode node = AbstractDungeon.map.get(0).get(i);
            if(node.x == x){
                return i;
            }
        }
        return -1;
    }
}