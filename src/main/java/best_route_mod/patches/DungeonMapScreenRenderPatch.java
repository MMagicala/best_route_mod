package best_route_mod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import javax.smartcardio.Card;
import java.awt.*;

// TODO: DungeonMapScreen also renders in other screens???
@SpirePatch(
        clz= DungeonMapScreen.class,
        method="render"
)
public class DungeonMapScreenRenderPatch {
    @SpirePostfixPatch
    public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb) {
        if (InputHelperPatch.isEmeraldKeyRequired() && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP) {
            int lw = (int)((int)ReflectionHacks.getPrivateStatic(Legend.class, "LW") * Settings.scale);
            FontHelper.renderWrappedText(sb, FontHelper.charTitleFont, "Emerald required",
                    Legend.X, Legend.Y + lw/2f, lw);
        }
    }
}
