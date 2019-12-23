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

public class PathSelectedPatch {
    @SpirePatch(
            clz=AbstractDungeon.class,
            method="setCurrMapNode"
    )
    public static class CurrNodeSelected{
        @SpirePostfixPatch
        public static void Postfix(MapRoomNode currentNode) {
            MapPath bestPath = findBestPathFromNode(currentNode);
            colorBestPath(bestPath);
        }
    }

    @SpirePatch(
            clz=AbstractDungeon.class,
            method="generateMap"
    )
    public static class MapGenerated{
        @SpirePostfixPatch
        public static void Postfix() {
            // TODO: make check so method doesnt run again when continuing game
            ArrayList<MapRoomNode> startingNodes = getStartingNodes();
            MapPath bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
            colorBestPath(bestPath);
        }
    }

    private static void colorBestPath(MapPath bestPath){
        // Color the edges in the map
        ArrayList<MapRoomNode> bestPathNodeList = bestPath.getListOfNodes();
        for (int i = 0; i < bestPathNodeList.size() - 1; i++) {
            colorEdgeInMap(bestPathNodeList.get(i), bestPathNodeList.get(i + 1));
        }
    }
}

