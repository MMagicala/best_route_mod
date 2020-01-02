package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class MapGenerationPatch {
    @SpirePatch(
            clz= AbstractDungeon.class,
            method="generateMap"
    )
    public static class RegularGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.resetBestPath();
        }
    }

    @SpirePatch(
            clz= TheEnding.class,
            method="generateSpecialMap"
    )
    public static class SpecialGenerationPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.resetBestPath();
        }
    }

    @SpirePatch(
            clz= Exordium.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class ExordiumLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.generateAndShowBestPathFromStartingNodes();
        }
    }

    @SpirePatch(
            clz= TheCity.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class CityLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.generateAndShowBestPathFromStartingNodes();
        }
    }

    @SpirePatch(
            clz= TheBeyond.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class BeyondLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.generateAndShowBestPathFromStartingNodes();
        }
    }

    @SpirePatch(
            clz= TheEnding.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class EndingLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            BestRouteMod.generateAndShowBestPathFromStartingNodes();
        }
    }

    @SpirePatch(
            clz= Exordium.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
SaveFile.class
            }
    )
    public static class ExordiumLoadedPatch2{
        @SpirePostfixPatch
        public static void Postfix() {
            if(!AbstractDungeon.firstRoomChosen) BestRouteMod.generateAndShowBestPathFromStartingNodes();
            else BestRouteMod.generateAndShowBestPathFromCurrentNode();
        }
    }

    @SpirePatch(
            clz= TheCity.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
SaveFile.class
            }
    )
    public static class CityLoadedPatch2{
        @SpirePostfixPatch
        public static void Postfix() {
            if(!AbstractDungeon.firstRoomChosen) BestRouteMod.generateAndShowBestPathFromStartingNodes();
            else BestRouteMod.generateAndShowBestPathFromCurrentNode();
        }
    }

    @SpirePatch(
            clz= TheBeyond.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
SaveFile.class
            }
    )
    public static class BeyondLoadedPatch2{
        @SpirePostfixPatch
        public static void Postfix() {
            if(!AbstractDungeon.firstRoomChosen) BestRouteMod.generateAndShowBestPathFromStartingNodes();
            else BestRouteMod.generateAndShowBestPathFromCurrentNode();
        }
    }

    @SpirePatch(
            clz= TheEnding.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
SaveFile.class
            }
    )
    public static class EndingLoadedPatch2{
        @SpirePostfixPatch
        public static void Postfix() {
            if(!AbstractDungeon.firstRoomChosen) BestRouteMod.generateAndShowBestPathFromStartingNodes();
            else BestRouteMod.generateAndShowBestPathFromCurrentNode();
        }
    }
}
