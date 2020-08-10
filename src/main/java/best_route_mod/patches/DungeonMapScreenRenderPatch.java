package best_route_mod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.map.Legend;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;

import javax.smartcardio.Card;
import java.awt.*;

public class DungeonMapScreenRenderPatch
{ @SpirePatch(
		clz = DungeonMapScreen.class,
		method = "render"
	)
	public static class DungeonMapScreenPostRenderPatch
	{

		private static String[] instructions = {"N - factor neow's lament",
			"Q - factor emerald",
			"B / middle click - change signs",
			"I - reset all priorities to zero",
			"left click - raise priority",
			"right click - lower priority",
		};

		@SpirePostfixPatch
		public static void Postfix(DungeonMapScreen __instance, SpriteBatch sb)
		{
			if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.MAP)
			{
				int lw = (int) ((int) ReflectionHacks.getPrivateStatic(Legend.class, "LW") * Settings.scale);
				Color color = InputHelperPatch.isEmeraldKeyRequired ? Color.WHITE : Color.GRAY;
				FontHelper.renderWrappedText(sb, FontHelper.charTitleFont, "Emerald required",
					Legend.X, Legend.Y + lw / 2f, lw, color, 0.75f);
				color = InputHelperPatch.isNeowsLamentFactored ? Color.WHITE : Color.GRAY;
				FontHelper.renderWrappedText(sb, FontHelper.charTitleFont, "Neows Lament factored",
					Legend.X, Legend.Y + lw / 2f + 40, lw, color, 0.75f);
				color = InputHelperPatch.isHoverModeEnabled ? Color.WHITE : Color.GRAY;
				FontHelper.renderWrappedText(sb, FontHelper.charTitleFont, "Hover mode enabled",
					Legend.X, Legend.Y + lw / 2f + 80, lw, color, 0.75f);
				// Print the instructions below the map
				if (color == Color.GRAY) color = Color.WHITE;
				for (int i = 0; i < instructions.length; i++)
				{
					FontHelper.renderWrappedText(sb, FontHelper.charTitleFont, instructions[i], Legend.X, Legend.Y - lw / 2f - 25 * (i + 1), lw, color, 0.6f);
				}
			}
		}
	}
}