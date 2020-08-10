package best_route_mod.patches;

import best_route_mod.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;

import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import java.awt.*;

public class HoverNodePatch {
	// Add these fields to check if node was just unhovered
    @SpirePatch(
            clz=MapRoomNode.class,
            method=SpirePatch.CLASS
    )
    public static class UnHoveredFields{
        public static SpireField<Boolean> justUnHovered = new SpireField<>(() -> false);
        public static SpireField<Boolean> unHoveredAfterFirstCycle = new SpireField<>(() -> true);
    }

    // Inject just unhovered code into MapRoomNode's update method and show/hide best path from that node when hovered
    @SpirePatch(
            clz=MapRoomNode.class,
            method="update"
    )
    public static class JustUnHoveredCode {
        // Save the previously rendered path so we can revert to it later
        private static MapPath savedRenderedPath;

        @SpirePostfixPatch
        public static void Postfix(MapRoomNode __instance) {
            // Just unhovered logic
			// reset if the map is closing / closed
            if (__instance.hb.justHovered) {
                if (UnHoveredFields.unHoveredAfterFirstCycle.get(__instance)){
                    UnHoveredFields.unHoveredAfterFirstCycle.set(__instance, false);
                }
                else if (UnHoveredFields.justUnHovered.get(__instance)){
                    UnHoveredFields.justUnHovered.set(__instance, false);
                }
            } else if (!__instance.hb.hovered) {
                if (!UnHoveredFields.justUnHovered.get(__instance) &&
                        !UnHoveredFields.unHoveredAfterFirstCycle.get(__instance)) {
                    UnHoveredFields.justUnHovered.set(__instance, true);
                } else if (UnHoveredFields.justUnHovered.get(__instance)) {
                    UnHoveredFields.justUnHovered.set(__instance, false);
                    UnHoveredFields.unHoveredAfterFirstCycle.set(__instance, true);
                }
            }

            if(InputHelperPatch.isHoverModeEnabled)
			{
				// Show best path when node is hovered and a path can be generated
				if (__instance.hb.hovered)
				{
					BestRouteMod.reRenderPath(__instance, false);
				}
				// Revert to previously shown best path if node is unhovered
				else if (UnHoveredFields.justUnHovered.get(__instance))
				{
					BestRouteMod.autoReRenderPath();
				}
			}
        }
    }
}