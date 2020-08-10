package best_route_mod.patches;

import basemod.ReflectionHacks;
import best_route_mod.*;
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
import javassist.NotFoundException;

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
            // Use ReflectionHacks to retrieve legend item index and get its corresponding room class
            int index = (int)ReflectionHacks.getPrivate(__instance, LegendItem.class, "index");
			Class<?> roomClassValue = null;
			try
			{
				roomClassValue = getRoomClassByIndex(index);
			}
			catch (NotFoundException e)
			{
				e.printStackTrace();
			}
			// Assign
            RoomClassField.roomClass.set(__instance, roomClassValue);
        }

		// Assume the legend items are listed in the correct order
        private static Class<?> getRoomClassByIndex(int index) throws NotFoundException
		{
            switch(index){
                case 0:
                    return EventRoom.class;
                case 1:
                    return ShopRoom.class;
                case 2:
                    return TreasureRoom.class;
                case 3:
                    return RestRoom.class;
                case 4:
                    return MonsterRoom.class;
                case 5:
                    return MonsterRoomElite.class;
            }
        	throw new NotFoundException("Could not match LegendItem to the corresponding room class");
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
            if(__instance.hb.hovered){
                // Middle click also "left" clicks, so use more specific checks
                if(InputHelper.justClickedLeft || Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                    if(InputHelperPatch.getMiddleButtonJustPressed() || Gdx.input.isKeyJustPressed(Input.Keys.B)){
                        // Switch the sign of the room class
                        Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                        RoomClassManager.flipSign(roomClass);
                        // Regenerate and rerender the path if necessary
                        BestRouteMod.autoReRenderPath();
                    }
                    // Is left mouse button being pressed down
                    if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
                        Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                        if(RoomClassManager.getPriorityIndexOf(roomClass) < RoomClassManager.getNumRoomClasses()){
                            RoomClassManager.incrementPriorityIndexOf(roomClass);
                            // Rerender path
                            BestRouteMod.autoReRenderPath();
                        }
                    }
                }else if(InputHelper.justClickedRight){
                    Class<?> roomClass = RoomClassField.roomClass.get(__instance);
                    if(RoomClassManager.isRoomClassActive(roomClass)){
                        RoomClassManager.decrementPriorityIndexOf(roomClass);
                        // Rerender path
                        BestRouteMod.autoReRenderPath();
                    }
                }
			}

            // Render the text
            String origLabel = (String)ReflectionHacks.getPrivate(__instance, LegendItem.class, "label");
            if(origLabel.endsWith(")"))
			{
				// Remove parantheses and its contents
				int endIndex = origLabel.indexOf(" (");
				origLabel = origLabel.substring(0, endIndex);
			}
            // Rerender that part
			Class<?> roomClass = RoomClassField.roomClass.get(__instance);
			String newLabel = origLabel + " (" + RoomClassManager.getPriorityIndexOf(roomClass) + ", " +
				RoomClassManager.getSignOf(roomClass) + ")";
			ReflectionHacks.setPrivate(__instance, LegendItem.class, "label", newLabel);
        }
    }
}