package best_route_mod;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.rooms.*;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomClassManager {
    // Statically create room classes and assign colors
    private static HashMap<Class<?>, RoomClassProperties> roomClassProperties = new HashMap<Class<?>, RoomClassProperties>() {
        {
            put(EventRoom.class, new RoomClassProperties(createColorFrom255(0, 0, 255)));
            put(ShopRoom.class, new RoomClassProperties(createColorFrom255(127, 0, 255)));
            put(TreasureRoom.class, new RoomClassProperties(createColorFrom255(255, 255, 0)));
            put(RestRoom.class, new RoomClassProperties(createColorFrom255(0, 255, 0)));
            put(MonsterRoom.class, new RoomClassProperties(createColorFrom255(255, 0, 0)));
            put(MonsterRoomElite.class, new RoomClassProperties(createColorFrom255(255, 127, 0)));
        }
    };

    // Helper method to convert RGB to decimals
    private static Color createColorFrom255(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    public static boolean allRoomClassesInActive() {
        for (RoomClassProperties roomClassProperties : roomClassProperties.values()) {
            if (roomClassProperties.isActive()) return false;
        }
        return true;
    }

    public static int getNumRoomClasses() {
        return roomClassProperties.size();
    }

    public static ArrayList<Class<?>> getRoomClasses(int priorityIndex) {
        ArrayList<Class<?>> matchingClasses = new ArrayList<>();
        for (Object roomClass : roomClassProperties.keySet().toArray()) {
            Class<?> roomClassKey = (Class<?>) roomClass; // Pass Class<?> to stop warnings
            if (priorityIndex == roomClassProperties.get(roomClassKey).getPriorityIndex()) {
                matchingClasses.add((Class<?>) roomClass);
            }
        }
        return matchingClasses;
    }

    public static Object[] getRoomClasses() {
        return roomClassProperties.keySet().toArray();
    }

    public static char getSignOf(Class<?> roomClass) {
        return roomClassProperties.get(roomClass).getSign();
    }

    public static Color getColorOf(Class<?> roomClass) {
        return roomClassProperties.get(roomClass).getColor();
    }

    public static void flipSign(Class<?> roomClass) {
        roomClassProperties.get(roomClass).flipSign();
    }

    public static boolean isRoomClassActive(Class<?> roomClass) {
        return roomClassProperties.get(roomClass).isActive();
    }

    public static void decrementPriorityIndexOf(Class<?> roomClass) {
        roomClassProperties.get(roomClass).decrementPriorityIndex();
    }

    public static void incrementPriorityIndexOf(Class<?> roomClass) {
        roomClassProperties.get(roomClass).incrementPriorityIndex();
    }

    public static ArrayList<Class<?>> getActiveRoomClassesAtLowestPriority() {
        int lowestPriority = getNumRoomClasses() + 1; // If this is the value returned, something is wrong
        for (Object roomClass : roomClassProperties.keySet().toArray()) {
            Class<?> roomClassKey = (Class<?>) roomClass;
            if (roomClassProperties.get(roomClassKey).getPriorityIndex() < lowestPriority &&
                    roomClassProperties.get(roomClassKey).isActive()) {
                lowestPriority = roomClassProperties.get(roomClassKey).getPriorityIndex();
            }
        }
        return getRoomClasses(lowestPriority);
    }

    public static int getPriorityIndexOf(Class<?> roomClass) {
        return roomClassProperties.get(roomClass).getPriorityIndex();
    }

    public static void resetPriorityIndicesToZero(){
    	for(Class<?> roomClass: roomClassProperties.keySet()){
			if(roomClassProperties.get(roomClass).getPriorityIndex() != 0){
				roomClassProperties.get(roomClass).resetPriorityIndexToZero();
			}
		}
	}
}
