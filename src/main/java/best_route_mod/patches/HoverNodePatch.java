package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import best_route_mod.MapPath;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.map.MapRoomNode;

@SpirePatch(
        clz= MapRoomNode.class,
        method="update"
)
public class HoverNodePatch {
    private static boolean justHovered = false;
    private static boolean hoveredAfterFirstCycle = false;
    private static boolean justUnhovered = false;
    private static MapPath previouslyRenderedPath;
    @SpirePostfixPatch
    public static void Postfix(MapRoomNode __instance) {
        // just hovered code
        if (__instance.hb.hovered) {
            if (!justHovered && !hoveredAfterFirstCycle) {
                justHovered = true;
                // save previously rendered path by making a copy of it
                if(BestRouteMod.bestPath != null) previouslyRenderedPath = new MapPath(BestRouteMod.bestPath);
            } else {
                justHovered = false;
                hoveredAfterFirstCycle = true;
            }
        } else {
            if (justHovered || hoveredAfterFirstCycle) {
                // render the original map again, but run this code only once
                justHovered = false;
                hoveredAfterFirstCycle = false;
                justUnhovered = true;
            } else {
                justUnhovered = false;
            }
        }

        // generate best path from hovered node
        if (justHovered){
            BestRouteMod.generateAndShowBestPathFromNode(__instance);
        }else if(justUnhovered){
            // Don't regenerate, just rerender the old path
            if(BestRouteMod.bestPath != null) BestRouteMod.disableCurrentBestPath();
            if(previouslyRenderedPath != null) {
                BestRouteMod.bestPath = previouslyRenderedPath;
                BestRouteMod.colorBestPath();
            }
        }
    }
}
