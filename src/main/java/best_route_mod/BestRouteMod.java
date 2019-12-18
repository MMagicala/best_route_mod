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
    // 0,-1 node is whale
    private ArrayList<MapRoomNode> bestPath;

    public BestRouteMod() {
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    boolean printedMap = false;

    @Override
    public void receivePostUpdate() {
        if(AbstractDungeon.currMapNode != null && !printedMap) {
            ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(1);
            ArrayList<MapRoomNode> currentPath;
            int highestRestSiteCount = 0;
            for(MapRoomNode startingNode: startingNodes){
                if(bestPath == null){
                    bestPath = traversePathRecur(startingNode);
                    highestRestSiteCount = getNumberOfCampSites(bestPath);
                }else{
                    currentPath = traversePathRecur(startingNode);
                    // Compare number of rest sites
                    int numRestSitesForCurrentPath = getNumberOfCampSites(currentPath);
                    if(numRestSitesForCurrentPath > highestRestSiteCount){
                        highestRestSiteCount = numRestSitesForCurrentPath;
                        bestPath = currentPath;
                    }
                }
            }

            //System.out.println("Num of edges: " + AbstractDungeon.currMapNode.getEdges().size());
            //AbstractDungeon.currMapNode.getEdges().forEach((edge -> printEdge(edge)));
            printedMap = true;
        }
    }

    private ArrayList<MapRoomNode> traversePathRecur(MapRoomNode node){
        ArrayList<MapRoomNode> connectedTopNodes = getConnectedTopNodes(node);
        if(connectedTopNodes.isEmpty()){
            return new ArrayList<MapRoomNode>();
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

    // TODO: filter out all the nodes except for rest sites and count them
    private int getNumberOfCampSites(ArrayList<MapRoomNode> path){
        path.removeIf(mapRoomNode -> mapRoomNode.room.combatEvent);
        return path.size();
    }

    // Debug functions

    private void printNode(MapRoomNode node){
        System.out.print("(" + node.x + "," + node.y + ") ");
    }

    private void printEdge(MapEdge edge){
        System.out.println("Edge: (" + edge.srcX + "," + edge.srcY + ") to (" + edge.dstX + "," + edge.dstY + ")");
    }

    @Override
    public void receiveStartAct() {
        printedMap = false;
    }
}