package best_route_mod.patches;

import basemod.ReflectionHacks;
import best_route_mod.ColorPathManager;
import best_route_mod.MapPath;
import best_route_mod.MapReader;
import best_route_mod.RoomClassManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.map.LegendItem;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

public class LegendItemPatch {

    // Inject roomClass field into LegendItem class
    @SpirePatch(
            clz= LegendItem.class,
            method=SpirePatch.CLASS
    )
    public static class RoomClassField{
        public static SpireField<Class<?>> roomClass = new SpireField<>(() -> AbstractRoom.class);
    }

    // Assign the room class value in the constructor using it's label
    @SpirePatch(
            clz= LegendItem.class,
            method=SpirePatch.CONSTRUCTOR
    )
    public static class LegendItemRoomClassPatch{
        @SpirePostfixPatch
        public static void Postfix(LegendItem __instance){
            // Use ReflectionHacks to retrieve private field label and get its corresponding room class
            String label = (String)ReflectionHacks.getPrivate(__instance, LegendItem.class, "label");
            Class<?> roomClassValue = getRoomClassByLabel(label);
            // Assign
            RoomClassField.roomClass.set(__instance, roomClassValue);
        }

        // Pass in the LegendItem label to get the matching room class
        private static Class<?> getRoomClassByLabel(String label){
            switch(label){
                case "Unknown":
                    return EventRoom.class;
                case "Merchant":
                    return ShopRoom.class;
                case "Treasure":
                    return TreasureRoom.class;
                case "Rest":
                    return RestRoom.class;
                case "Enemy":
                    return MonsterRoom.class;
                case "Elite":
                    return MonsterRoomElite.class;
            }
            // This should not happen, like, at all
            return null;
        }
    }

    // Check if LegendItem was clicked and return corresponding room class
    @SpirePatch(
            clz= LegendItem.class,
            method="update"
    )
    public static class LegendItemClickPatch{
        @SpirePostfixPatch
        public static void Postfix(LegendItem __instance){
            boolean propertyChanged = false;
            if(__instance.hb.hovered){
                // Middle click also "left" clicks, so use more specific checks
                if(InputHelper.justClickedLeft) {
                    if(InputHelperPatch.getMiddleButtonJustPressed()){
                        // Switch the sign of the room class
                        Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                        RoomClassManager.flipSign(roomClass);
                        // Regenerate and rerender the path if necessary
                        if(!RoomClassManager.allRoomClassesInActive()){
                            ColorPathManager.disableCurrentlyColoredPath();
                            reRenderPath();
                        }
                        propertyChanged = true;
                    }
                    // Is left mouse button being pressed down
                    if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                        Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                        if(RoomClassManager.getPriorityIndexOf(roomClass) < RoomClassManager.getNumRoomClasses()){
                            RoomClassManager.incrementPriorityIndexOf(roomClass);
                            // Rerender path
                            if(!RoomClassManager.allRoomClassesInActive()) {
                                ColorPathManager.disableCurrentlyColoredPath();
                            }
                            reRenderPath();
                            propertyChanged = true;
                        }
                    }
                }else if(InputHelper.justClickedRight){
                    Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                    if(RoomClassManager.isRoomClassActive(roomClass)){
                        RoomClassManager.decrementPriorityIndexOf(roomClass);
                        // Rerender path
                        ColorPathManager.disableCurrentlyColoredPath();
                        if(!RoomClassManager.allRoomClassesInActive()) reRenderPath();
                        propertyChanged = true;
                    }
                }
            }
            // Render the text
            String origLabel = (String)ReflectionHacks.getPrivate(__instance, LegendItem.class, "label");
            if(origLabel.charAt(origLabel.length()-1) != ')') {
                // Change text to new format on first update
                Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                String newLabel = origLabel + " (" + RoomClassManager.getPriorityIndexOf(roomClass) + ", " +
                        RoomClassManager.getSignOf(roomClass) + ")";
                ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", newLabel);
            }
            if(propertyChanged){
                // Rerender text when a property has changed
                Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                int endIndex = origLabel.indexOf(" (");
                origLabel = origLabel.substring(0, endIndex);
                String newLabel = origLabel + " (" + RoomClassManager.getPriorityIndexOf(roomClass) + ", " +
                        RoomClassManager.getSignOf(roomClass) + ")";
                ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", newLabel);
            }
        }

        // Get and print out best path
        private static void reRenderPath(){
            MapPath bestPath;
            if(AbstractDungeon.firstRoomChosen){
                bestPath = MapReader.getBestPathFrom(AbstractDungeon.currMapNode, InputHelperPatch.isEmeraldKeyRequired());
            }else{
                bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes(), InputHelperPatch.isEmeraldKeyRequired());
            }
            ColorPathManager.colorPath(bestPath);
        }
    }
}