package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class DungeonMapReader {
    // Get node using coordinates.  x = order (left to right), y = level. AbstractDungeon.map has ArrayLists for each
    // level (y), and each ArrayList has seven nodes in that level accessible by their index (x).
    public MapRoomNode getNode(int x, int y){
        if(y == 0){
            // The first level (at y = 0) only contains the starting nodes, so we have to iterate through the list to
            // find the right node
            for(MapRoomNode node: AbstractDungeon.map.get(0)){
                if(node.x == x){
                    return node;
                }
            }
        }else{
            return AbstractDungeon.map.get(y).get(x);
        }
        return null; // Node not found
    }
}