package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.RestRoom;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapPath {
    private HashMap<Class<?>, Integer> roomCounts;
    private ArrayList<MapRoomNode> path;

    /* Constructors */

    // Make a copy of a MapPath instance
    public MapPath(MapPath original){
        this(original.path, original.roomCounts);
    }

    // Create a MapPath with only one node
    public MapPath(MapRoomNode node){
        this.path = new ArrayList<>();
        path.add(node);
        this.roomCounts = new HashMap<>();
        this.roomCounts.put(node.room.getClass(), 1);
        // Set the rest of the room counts to zero
        for(Object roomClass: RoomClassManager.getRoomClasses()){
            if(roomClass != node.room.getClass()) roomCounts.put((Class<?>)roomClass, 0);
        }
    }

    public MapPath(ArrayList<MapRoomNode> path, HashMap<Class<?>, Integer> roomCounts){
        this.path = path;
        this.roomCounts = roomCounts;
    }

    // Use to replace null
    public MapPath() {
        this(new ArrayList<>(), new HashMap<>());
    }

    // Get the number of rooms of a certain type in this path.
    public int getRoomCount(Class<?> roomType){
        return roomCounts.get(roomType);
    }

    public void pushToFront(MapRoomNode node){
        path.add(0,node);
        // Increment the number of rooms for this class
        Class<?> roomClass = node.room.getClass();
        roomCounts.put(roomClass, getRoomCount(roomClass)+1);
    }

    // Get the edges between the nodes of the path
    public ArrayList<MapEdge> getEdges(){
        // For every node except the last one, add the edge to a list
        ArrayList<MapEdge> edges = new ArrayList<>();
        for(int i = 0; i < path.size() - 1; i++){
            MapEdge edge = path.get(i).getEdgeConnectedTo(path.get(i+1));
            edges.add(edge);
        }
        return edges;
    }

    public boolean isEmpty(){
        return path.isEmpty();
    }

    public boolean hasEdge(){
        return path.size() > 1;
    }
}
