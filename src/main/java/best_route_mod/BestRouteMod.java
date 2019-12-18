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

@SpireInitializer
public class BestRouteMod implements basemod.interfaces.PostUpdateSubscriber, basemod.interfaces.StartActSubscriber  {
    // 0,-1 node is whale
    private ArrayList<MapRoomNode> bestPath;

    public BestRouteMod() {
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");

        // Initialize variables
        bestPath = new ArrayList<MapRoomNode>();
    }

    public static void initialize() {
        new BestRouteMod();
    }

    boolean printedMap = false;

    @Override
    public void receivePostUpdate() {
        if(AbstractDungeon.currMapNode != null && !printedMap) {
            ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(1);

            for(MapRoomNode startingNode: startingNodes){
                findBestPathRecur(startingNode);
            }

            //System.out.println("Num of edges: " + AbstractDungeon.currMapNode.getEdges().size());
            //AbstractDungeon.currMapNode.getEdges().forEach((edge -> printEdge(edge)));
            printedMap = true;
        }
    }

    private ArrayList<MapRoomNode> findBestPathRecur(MapRoomNode node){

    }

    // TODO: make sure it only gets the edges moving upwards
    private ArrayList<MapRoomNode> getConnectedNodesOnTop(MapRoomNode node){
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

    @Override
    public void receiveStartAct() {
        printedMap = false;
    }
}