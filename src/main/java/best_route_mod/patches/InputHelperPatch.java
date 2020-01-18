package best_route_mod.patches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.map.Legend;

public class InputHelperPatch {
    private static boolean isMiddleButtonJustPressed = false;
    private static boolean isMiddleButtonPressedAfterFirstCycle = false;

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
}