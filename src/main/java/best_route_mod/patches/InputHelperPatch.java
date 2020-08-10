package best_route_mod.patches;

import basemod.BaseMod;
import basemod.DevConsole;
import best_route_mod.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.relics.NeowsLament;

public class InputHelperPatch {
    private static boolean isMiddleButtonJustPressed = false;
    private static boolean isMiddleButtonPressedAfterFirstCycle = false;
    public static boolean isEmeraldKeyRequired = false;
    public static boolean isNeowsLamentFactored = false;
    // Used to prevent neow's lament hotkey from being enabled after NextRoomTransitionPatch detects the
    public static boolean lockNeowsLamentHotKey = false;

    public static boolean isHoverModeEnabled = false;

    // Inject middle button just pressed code into InputHelper's update function
    @SpirePatch(
            clz= InputHelper.class,
            method="updateFirst"
    )
    public static class MiddleButtonJustPressedPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)){
                if(!isMiddleButtonJustPressed && !isMiddleButtonPressedAfterFirstCycle){
                    isMiddleButtonJustPressed = true;
                }else{
                    isMiddleButtonJustPressed = false;
                    isMiddleButtonPressedAfterFirstCycle = true;
                }
            }else{
                isMiddleButtonJustPressed = false;
                isMiddleButtonPressedAfterFirstCycle = false;
            }
        }
    }

    // So we can tell if middle button was just pressed outside of this class
    public static boolean getMiddleButtonJustPressed(){
        return isMiddleButtonJustPressed;
    }

    @SpirePatch(
            clz= InputHelper.class,
            method="updateFirst"
    )
    public static class HotKeyPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            // TODO: clean up conditions
            // Make sure the map exists before checking for hotkey input
            if(AbstractDungeon.currMapNode != null && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP
                && !DevConsole.visible) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.Q)
                        && MapReader.emeraldKeyExists()
                        && (!AbstractDungeon.firstRoomChosen
                        || MapReader.isEmeraldKeyReachableFrom(AbstractDungeon.currMapNode))) {
                    isEmeraldKeyRequired = !isEmeraldKeyRequired;
                    // Rerender path
                    BestRouteMod.autoReRenderPath();
                }
                if(Gdx.input.isKeyJustPressed(Input.Keys.N)
                    && AbstractDungeon.player.hasRelic(NeowsLament.ID)
                    && !AbstractDungeon.player.getRelic(NeowsLament.ID).usedUp
                    && !lockNeowsLamentHotKey){
                    isNeowsLamentFactored = !isNeowsLamentFactored;
                    BestRouteMod.autoReRenderPath();
                }
				if(Gdx.input.isKeyJustPressed(Input.Keys.H))
				{
					isHoverModeEnabled = !isHoverModeEnabled;
					if(!isHoverModeEnabled){
						BestRouteMod.autoReRenderPath();
					}
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.I))
				{
					RoomClassManager.resetPriorityIndicesToZero();
					BestRouteMod.autoReRenderPath();
				}
			}
        }
    }

    public static void disableFactorRequirements(){
		isNeowsLamentFactored = false;
		isEmeraldKeyRequired = false;
	}

    public static void disableLocks(){
        lockNeowsLamentHotKey = false;
    }
}