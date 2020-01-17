package best_route_mod.patches;

import best_route_mod.ColorPathManager;
import best_route_mod.MapPath;
import best_route_mod.MapReader;
import best_route_mod.RoomClassManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class MapGeneratedPatch {
    // Show the best path once a new act has started
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
            // System.out.println("Exordium generated");
            HelperFunctions.mapGeneratedHelper();
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
                // System.out.println("City generated");
                HelperFunctions.mapGeneratedHelper();
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
                // System.out.println("Beyond generated");
                HelperFunctions.mapGeneratedHelper();
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
                // System.out.println("Ending generated");
                HelperFunctions.mapGeneratedHelper();
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
    public static class ExordiumLoadedPatch{
        @SpirePostfixPatch
        public static void Postfix() {
            // System.out.println("Exordium loaded");
            HelperFunctions.mapLoadedHelper();
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
            // System.out.println("City loaded");
            HelperFunctions.mapLoadedHelper();
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
            // System.out.println("Beyond loaded");
            HelperFunctions.mapLoadedHelper();
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
            // System.out.println("Ending loaded");
            HelperFunctions.mapLoadedHelper();
        }
    }

    public static class HelperFunctions{
        public static void mapGeneratedHelper(){
            if(!RoomClassManager.allRoomClassesInActive()){
                MapPath bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes());
                ColorPathManager.colorPath(bestPath);
            }
        }

        public static void mapLoadedHelper(){
            if(!RoomClassManager.allRoomClassesInActive()) {
                MapPath bestPath;
                if (!AbstractDungeon.firstRoomChosen) {
                    bestPath = MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }else{
                    bestPath = MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                }
                ColorPathManager.colorPath(bestPath);
            }
        }
    }
}
