package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.RestRoom;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPath {
    private HashMap<Class, Integer> roomCounts;
    private ArrayList<MapRoomNode> path;

    public MapPath(){
        this(new ArrayList<MapRoomNode>(), new HashMap<Class, Integer>());
    }

    public MapPath(MapRoomNode node, HashMap<Class, Integer> roomCounts){
        this(new ArrayList<MapRoomNode>(){{add(node);}}, roomCounts);
    }

    public MapPath(ArrayList<MapRoomNode> path, HashMap<Class, Integer> roomCounts){
        this.path = path;
        this.roomCounts = roomCounts;
    }

    public void incrementRoomCount(Class roomType){
        roomCounts.put(roomType, getRoomCount(roomType)+1);
    }

    public int getRoomCount(Class roomType){
        return roomCounts.get(roomType) == null ? 0 : roomCounts.get(roomType);
    }

    public void pushNodeToFrontOfPath(MapRoomNode node){
        path.add(0,node);
    }

    public ArrayList<MapRoomNode> getListOfNodes(){
        return path;
    }
}
