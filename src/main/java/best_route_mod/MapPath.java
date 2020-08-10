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
    private ArrayList<MapRoomNode> nodes;
    private boolean _hasEmerald;
    public int neowsLamentPathEndIndex;

    /* Constructors */

    // Create a MapPath with only one node
    public MapPath(MapRoomNode node){
        this.nodes = new ArrayList<>();
        this.nodes.add(node);
        this.roomCounts = new HashMap<>();
        this.roomCounts.put(node.room.getClass(), 1);
        // Set the rest of the room counts to zero
        for(Object roomClass: RoomClassManager.getRoomClasses()){
            if(roomClass != node.room.getClass()) this.roomCounts.put((Class<?>)roomClass, 0);
        }
        // Check for emerald key
        this._hasEmerald = node.hasEmeraldKey;
    }

    // Get the number of rooms of a certain type in this path.
    public int getRoomCount(Class<?> roomType){
        return roomCounts.get(roomType);
    }

    public void pushToFront(MapRoomNode node){
        this.nodes.add(0,node);
        // Increment the number of rooms for this class
        Class<?> roomClass = node.room.getClass();
        roomCounts.put(roomClass, getRoomCount(roomClass)+1);
        // Check for emerald
        if(node.hasEmeraldKey){
            _hasEmerald = true;
        }
    }

    // Get the edges between the nodes of the path
    public ArrayList<MapEdge> getEdges(){
        // For every node except the last one, add the edge to a list
        ArrayList<MapEdge> edges = new ArrayList<>();
        for(int i = 0; i < nodes.size() - 1; i++){
            MapEdge edge = nodes.get(i).getEdgeConnectedTo(nodes.get(i+1));
            edges.add(edge);
        }
        return edges;
    }

    public int getNodeCount(){
        return nodes.size();
    }

    public int getLowestYLevel(){
        return nodes.get(0).y;
    }

    public MapRoomNode getLastNode(){
        return nodes.get(getNodeCount()-1);
    }

    public void appendPath(MapPath path){
        if(path.nodes.isEmpty()) return;
        // The last node of this must be the same as the first node of the given path
        nodes.remove(getLastNode());
        nodes.addAll(path.nodes);
    }
    public boolean hasEmerald(){
        return _hasEmerald;
    }

    public Class<?>[] getOrderedRoomClasses(){
        Class<?>[] roomClasses = new Class<?>[getNodeCount()];
        for(int i = 0; i < getNodeCount(); i++){
            roomClasses[i] = nodes.get(i).room.getClass();
        }
        return roomClasses;
    }
}