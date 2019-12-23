package best_route_mod.patches;

import best_route_mod.MapPath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

import static best_route_mod.BestRouteMod.*;


@SpirePatch(
    clz=AbstractDungeon.class,
    method="setCurrMapNode"
)
public class CurrentNodeSelectedPatch {
    @SpirePostfixPatch
    public static void Postfix(Object o) {
        System.out.println("\ncurrent map node selected!!!\n");

        // Start traversal code
        ArrayList<MapRoomNode> startingNodes = getStartingNodes();
        MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);

        // Color the edges in the map
        ArrayList<MapRoomNode> bestPathNodeList = bestPath.getListOfNodes();
        for (int i = 0; i < bestPathNodeList.size() - 1; i++) {
            colorEdgeInMap(bestPathNodeList.get(i), bestPathNodeList.get(i + 1));
        }
    }
}
