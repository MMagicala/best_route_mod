package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class Debug {
    // Debug functions

    public static String printNode(MapRoomNode node){
        return ("(" + node.x + "," + node.y + ")");
    }

    public static String printEdge(MapEdge edge){
        return ("(" + edge.srcX + "," + edge.srcY + ") -> (" + edge.dstX + "," + edge.dstY + ")");
    }

    public static void printDungeon(){
        for(ArrayList<MapRoomNode> list: AbstractDungeon.map){
            for(MapRoomNode node: list){
                System.out.print(printNode(node) + " ");
            }
            System.out.println();
        }
    }
}
