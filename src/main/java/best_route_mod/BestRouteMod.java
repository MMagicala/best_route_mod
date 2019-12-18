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
    private ArrayList<MapRoomNode> bestPath;

    @Override
    public void receivePostUpdate() {
        if(AbstractDungeon.currMapNode != null && !printedMap) {
            // Start traversal code
            bestPath = getBestPath();

            //System.out.println("Num of edges: " + AbstractDungeon.currMapNode.getEdges().size());
            //AbstractDungeon.currMapNode.getEdges().forEach((edge -> printEdge(edge)));
            printedMap = true;
        }
    }

    private ArrayList<MapRoomNode> getBestPath(){
        ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(1);
        // Find the path with the most rest sites from each starting node
        ArrayList<MapRoomNode> currentBestPath = null;
        for(MapRoomNode startingNode: startingNodes){
            if(currentBestPath == null){
                currentBestPath = traversePathRecur(startingNode);
            }else{
                ArrayList<MapRoomNode> iteratedPath = traversePathRecur(startingNode);
                if(getNumberOfCampSites(iteratedPath) > getNumberOfCampSites(currentBestPath)){
                    currentBestPath = iteratedPath;
                }
            }
        }
        return currentBestPath;
    }

    private ArrayList<MapRoomNode> traversePathRecur(MapRoomNode node){
        ArrayList<MapRoomNode> connectedTopNodes = getConnectedTopNodes(node);
        ArrayList<MapRoomNode> appendedPath;
        if(connectedTopNodes.isEmpty()){
             appendedPath = new ArrayList<MapRoomNode>();
             appendedPath.add(node);
             return appendedPath;
        }
        for(MapRoomNode topNode: connectedTopNodes){
            traversePathRecur(topNode);
        }
    }

    // TODO: make sure it only gets the edges moving upwards
    private ArrayList<MapRoomNode> getConnectedTopNodes(MapRoomNode node){
        ArrayList<MapEdge> mapEdges = node.getEdges();
        mapEdges.forEach(mapEdge -> printEdge(mapEdge));
    }

    private MapRoomNode getNodeAtCoordinates(int x, int y){
        return AbstractDungeon.map.get(y+1).get(x);
    }

    // Debug functions

    private void printNode(MapRoomNode node){
        System.out.print("(" + node.x + "," + node.y + ") ");
    }

    private void printEdge(MapEdge edge){
        System.out.println("Edge: (" + edge.srcX + "," + edge.srcY + ") to (" + edge.dstX + "," + edge.dstY + ")");
    }

    private class MapPath{
        private int numCampSites;
        private 
        public MapPath(){

        }
    }
}