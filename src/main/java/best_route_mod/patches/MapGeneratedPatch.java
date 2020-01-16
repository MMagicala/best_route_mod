package best_route_mod.patches;

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
            System.out.println("Exordium generated");
            if(!RoomClassManager.allRoomClassesInActive()){
                MapReader.getBestPathFrom(MapReader.getStartingNodes());
            }
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
                if(!RoomClassManager.allRoomClassesInActive()){
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }
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
                if(!RoomClassManager.allRoomClassesInActive()){
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }
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
                if(!RoomClassManager.allRoomClassesInActive()){
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }
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
            System.out.println("Exordium loaded");
            if(!RoomClassManager.allRoomClassesInActive()) {
                if (AbstractDungeon.firstRoomChosen) {
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }else{
                    MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                }
            }
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
            if(!RoomClassManager.allRoomClassesInActive()) {
                if (AbstractDungeon.firstRoomChosen) {
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }else{
                    MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                }
            }
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
            if(!RoomClassManager.allRoomClassesInActive()) {
                if (AbstractDungeon.firstRoomChosen) {
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }else{
                    MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                }
            }
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
            if(!RoomClassManager.allRoomClassesInActive()) {
                if (AbstractDungeon.firstRoomChosen) {
                    MapReader.getBestPathFrom(MapReader.getStartingNodes());
                }else{
                    MapReader.getBestPathFrom(AbstractDungeon.currMapNode);
                }
            }
        }
    }
}
