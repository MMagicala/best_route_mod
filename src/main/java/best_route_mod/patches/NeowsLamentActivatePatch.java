package best_route_mod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz= NeowsLament.class,
        method="atBattleStart"
)
public class NeowsLamentActivatePatch {
    @SpirePrefixPatch
    public static void Prefix(NeowsLament __instance){
        if(__instance.counter == 1){
            InputHelperPatch.isNeowsLamentFactored = false;
            InputHelperPatch.lockNeowsLamentHotKey = false;
        }
    }
}
