package best_route_mod.patches;

import best_route_mod.BestRouteMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class MapGenerationPatch {
    // Show the best path from starting nodes once a new act has started
    // A new act will always be generated before the player selects a starting node
    @SpirePatch(
            clz= Exordium.class,
            method=SpirePatch.CONSTRUCTOR,
            paramtypez = {
                    AbstractPlayer.class,
                    ArrayList.class
            }
    )
    public static class ExordiumGeneratedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            System.out.println("Exordium generated");
            BestRouteMod.findAndShowBestPathFromStartingNodes();
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
        public static class CityGeneratedPatch{
            @SpirePostfixPatch
            public static void Postfix() {
                System.out.println("City generated");
                BestRouteMod.findAndShowBestPathFromStartingNodes();
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
        public static class BeyondGeneratedPatch{
            @SpirePostfixPatch
            public static void Postfix() {
                System.out.println("Beyond generated");
                BestRouteMod.findAndShowBestPathFromStartingNodes();
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
        public static class EndingGeneratedPatch{
            @SpirePostfixPatch
            public static void Postfix() {
                System.out.println("Ending generated");
                BestRouteMod.findAndShowBestPathFromStartingNodes();
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

    // Generate best path from node where we left off
    public static class ExordiumLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            System.out.println("Exordium loaded");
            BestRouteMod.findAndShowBestPathFromNode(AbstractDungeon.currMapNode);
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
    public static class CityLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            System.out.println("City loaded");
            BestRouteMod.findAndShowBestPathFromNode(AbstractDungeon.currMapNode);
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
    public static class BeyondLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            System.out.println("Beyond loaded");
            BestRouteMod.findAndShowBestPathFromNode(AbstractDungeon.currMapNode);
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
    public static class EndingLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            System.out.println("Ending loaded");
            BestRouteMod.findAndShowBestPathFromNode(AbstractDungeon.currMapNode);
        }
    }
}
