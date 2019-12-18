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
    public BestRouteMod() {
        BaseMod.subscribe(this);
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }

    boolean printedMap = false;

    // 0,-1 node is whale

    @Override
    public void receivePostUpdate() {
        if(AbstractDungeon.currMapNode != null && !printedMap) {
            ArrayList<MapRoomNode> startingNodes = AbstractDungeon.map.get(0);
            for(MapRoomNode startingNode: startingNodes){
                startingNode.
            }
            //System.out.println("Num of edges: " + AbstractDungeon.currMapNode.getEdges().size());
            //AbstractDungeon.currMapNode.getEdges().forEach((edge -> printEdge(edge)));
            printedMap = true;
        }
    }

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