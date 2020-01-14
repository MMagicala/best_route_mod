package best_route_mod.patches;

import basemod.ReflectionHacks;
import best_route_mod.BestRouteMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
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
            if(__instance.hb.hovered) {
                Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                if (InputHelper.justClickedLeft) {
                    // Middle click also "left" clicks, so use more specific checks
                    if (InputHelperPatch.getMiddleButtonJustPressed()) {
                        BestRouteMod.switchSign(roomClass);
                    }
                    // Is left mouse button being pressed down
                    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                        BestRouteMod.lowerPriority(roomClass);
                    }
                } else if (InputHelper.justClickedRight) {
                    BestRouteMod.raisePriority(roomClass);
                }
            }
        }
    }
}