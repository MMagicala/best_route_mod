package best_route_mod;

import basemod.BaseMod;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

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
            ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
            for(MapRoomNode startingNode: startingNodes){
                traverseInDepthOrder(startingNode);
            }
            printedMap = true;
        }
    }

    private void traverseInDepthOrder(MapRoomNode node) {
        printNode(node);
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = getAdjacentNodesAbove(node);
        if(adjacentNodesAboveGivenNode.isEmpty()) return;
        for(MapRoomNode adjacentNode: adjacentNodesAboveGivenNode){
            traverseInDepthOrder(adjacentNode);
        }
    }

    private boolean nodeAtLastLevel(MapRoomNode node){
        if(node.x)
    }

    private ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node){
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<MapRoomNode>();
        mapEdges.forEach(mapEdge -> {
            adjacentNodesAboveGivenNode.add(getNodeAtCoordinates(mapEdge.dstX, mapEdge.dstY));
        });
        return adjacentNodesAboveGivenNode;
    }

    private MapRoomNode getNodeAtCoordinates(int x, int y){
        return AbstractDungeon.map.get(y).get(x);
    }

    // Debug functions

    private void printNode(MapRoomNode node){
        System.out.println("Node (" + node.x + "," + node.y + ") ");
    }


    private void printEdge(MapEdge edge){
        System.out.print("(" + edge.srcX + "," + edge.srcY + ") -> (" + edge.dstX + "," + edge.dstY + ")");
    }

}