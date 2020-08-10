package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.NeowsLament;

@SpirePatch(
	clz = AbstractRelic.class
	,method = "obtain"
)
public class NeowsLamentReceivedPatch
{
	@SpirePostfixPatch
	public static void Postfix(AbstractRelic relic){
		if(relic.relicId.equals(NeowsLament.ID)){
			// Factor neows lament automatically if settings is enabled
			if(BestRouteMod.config.getBool("neowsLamentFactoredAutomatically")){
				InputHelperPatch.isNeowsLamentFactored = true;
				BestRouteMod.autoReRenderPath();
			}
		}
	}
}
