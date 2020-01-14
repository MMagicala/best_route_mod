package best_route_mod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.map.MapRoomNode;

public class MouseNodePatch {
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
        @SpirePostfixPatch
        public static void Postfix(MapRoomNode __instance) {
            // Just unhovered logic
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

            // Show best path when node is hovered
            if(__instance.hb.justHovered); // TODO: add payload
            // Revert to previously shown best path if node is unhovered
            else if(UnHoveredFields.justUnHovered.get(__instance)); // TODO: add payload
        }
    }
}