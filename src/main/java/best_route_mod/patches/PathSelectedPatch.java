package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import best_route_mod.MapPath;
import com.badlogic.gdx.graphics.Color;
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
        public static void Postfix() {
            BestRouteMod.generateAndShowBestPathFromCurrentNode();
        }
    }

    @SpirePatch(
            clz=AbstractDungeon.class,
            method="generateMap"
    )
    public static class MapGenerated{
        @SpirePostfixPatch
        public static void Postfix() {
            ArrayList<MapRoomNode> startingNodes = getStartingNodes();
            bestPath = findBestPathFromAdjacentOrStartingNodes(startingNodes);
            colorPath(bestPath, Color.RED);
        }
    }
}

