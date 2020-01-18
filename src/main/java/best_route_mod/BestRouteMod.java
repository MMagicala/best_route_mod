package best_route_mod;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;

import java.io.IOException;
import java.util.*;

@SpireInitializer
public class BestRouteMod{
    public BestRouteMod() {
        /*
                    put(EventRoom.class, new RoomClassProperties(createColorFrom255(0, 0, 255)));
            put(ShopRoom.class, new RoomClassProperties(createColorFrom255(127, 0, 255)));
            put(TreasureRoom.class, new RoomClassProperties(createColorFrom255(255, 255, 0)));
            put(RestRoom.class, new RoomClassProperties(createColorFrom255(0, 255, 0)));
            put(MonsterRoom.class, new RoomClassProperties(createColorFrom255(255, 0, 0)));
            put(MonsterRoomElite.class, new RoomClassProperties(createColorFrom255(255, 127, 0)));
         */
        // Load config
        try {
            // Default configs for room class colors
            Properties defaultConfig = new Properties();
            defaultConfig.setProperty(EventRoom.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));
            defaultConfig.setProperty(ShopRoom.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));
            defaultConfig.setProperty(TreasureRoom.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));
            defaultConfig.setProperty(RestRoom.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));
            defaultConfig.setProperty(MonsterRoom.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));
            defaultConfig.setProperty(MonsterRoomElite.class.toString(), Integer.toString(Color.argb8888(1, 0, 0, 1)));

            SpireConfig config = new SpireConfig("BestRouteMod", "config", defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Best Route Mod initialized. Enjoy! -Mysterio's Magical Assistant");
    }

    public static void initialize() {
        new BestRouteMod();
    }
}