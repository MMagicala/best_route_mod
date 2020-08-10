package best_route_mod.patches;

import best_route_mod.*;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class GetDungeonPatch {
    // Show the best path once a new act has started
        @SpirePatch(
                clz= CardCrawlGame.class,
                method="getDungeon",
                paramtypez = {
                        String.class,
                        AbstractPlayer.class
                }
        )
        public static class MapGenerationPatch{
            @SpirePostfixPatch
            public static void Postfix() {
				initialMapPathRender();
			}
        }

    @SpirePatch(
            clz= CardCrawlGame.class,
            method="getDungeon",
            paramtypez = {
                    String.class,
                    AbstractPlayer.class,
                    SaveFile.class
            }
    )
    public static class MapLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
			initialMapPathRender();
        }
    }

    private static void initialMapPathRender(){
		InputHelperPatch.disableFactorRequirements();
		InputHelperPatch.disableLocks();
		// Automatically factor emerald in path if enabled
		if(MapReader.emeraldKeyExists()
			&& (!AbstractDungeon.firstRoomChosen || MapReader.isEmeraldKeyReachableFrom(AbstractDungeon.currMapNode))
			&& BestRouteMod.config.getBool("emeraldFactoredAutomatically"))
		{
			InputHelperPatch.isEmeraldKeyRequired = true;
		}
		BestRouteMod.reRenderPath(MapReader.getStartingNodes());
	}
}
