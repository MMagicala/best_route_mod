package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.RestRoom;

import java.util.ArrayList;
import java.util.Map;

@SpireInitializer
public class BestRouteMod implements basemod.interfaces.PostUpdateSubscriber, basemod.interfaces.StartActSubscriber  {

    public BestRouteMod() {
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    boolean printedMap = false;

    @Override
    public void receiveStartAct() {
        printedMap = false;
    }

    // 0,-1 node is whale

    @Override
    public void receivePostUpdate() {
        if(AbstractDungeon.currMapNode != null && !printedMap) {
            // Start traversal code
            ArrayList<MapRoomNode> startingNodes = getStartingNodes();
            MapPath bestPath = new MapPath();
            for(MapRoomNode startingNode: startingNodes){
                if(bestPath.isEmpty()){
                    bestPath = traverseInDepthOrder(startingNode);
                }else{
                    MapPath currentPath = traverseInDepthOrder(startingNode);
                    if(bestPath.getNumCampSites() < currentPath.getNumCampSites()){
                        bestPath = currentPath;
                    }
                }
            }
            bestPath.printPath();
            printedMap = true;
        }
    }

    private ArrayList<MapRoomNode> getStartingNodes(){
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
        startingNodes.removeIf(mapRoomNode -> !mapRoomNode.hasEdges());
        return startingNodes;
    }

    // Travel all the nodes on the map (except for the boss node)
    private MapPath traverseInDepthOrder(MapRoomNode node) {
        // printNode(node);
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = getAdjacentNodesAbove(node);
        if(adjacentNodesAboveGivenNode.isEmpty()) return new MapPath(node, 1);
        MapPath bestPath = new MapPath();
        for(MapRoomNode adjacentNode: adjacentNodesAboveGivenNode){
            MapPath pathFromNode = traverseInDepthOrder(adjacentNode);
            // Compare number of rest sites for paths
            if(bestPath.isEmpty() || bestPath.getNumCampSites() < pathFromNode.getNumCampSites()){
                bestPath = pathFromNode;
            }
        }
        bestPath.pushNodeToFrontOfPath(node);
        if(node.room instanceof RestRoom) bestPath.incrementNumCampSites();
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

    private MapRoomNode getNodeAtCoordinates(int x, int y){
        return AbstractDungeon.map.get(y).get(x);
    }

    // Debug functions

    private void printNode(MapRoomNode node){
        System.out.print("Node (" + node.x + "," + node.y + ") ");
    }

    private void printEdge(MapEdge edge){
        System.out.print("(" + edge.srcX + "," + edge.srcY + ") -> (" + edge.dstX + "," + edge.dstY + ")");
    }

}