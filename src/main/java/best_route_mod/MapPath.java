package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.RestRoom;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MapPath {
    // TODO: convert to hashtable (class -> int)
    private HashMap<Class, Integer> roomCounts;
    private int numCampSites;

    public int getNumElites() {
        return numElites;
    }

    private int numElites;
    private ArrayList<MapRoomNode> path;

    public MapPath(){
        this(new ArrayList<MapRoomNode>(), 0, 0);
    }

    public MapPath(MapRoomNode node, int numCampSites, int numElites){
        this(new ArrayList<MapRoomNode>(){{add(node);}}, numCampSites, numElites);
    }

    public MapPath(ArrayList<MapRoomNode> path, int numCampSites, int numElites){
        this.path = path;
        this.numCampSites = numCampSites;
        this.numElites = numElites;
    }

    public void incrementNumCampSites(){
        numCampSites++;
    }

    public void incrementNumElites(){
        numElites++;
    }

    public void pushNodeToFrontOfPath(MapRoomNode node){
        path.add(0,node);
    }

    public boolean isEmpty(){
        return path.isEmpty();
    }

    public int getNumCampSites(){
        return numCampSites;
    }

    public ArrayList<MapRoomNode> getListOfNodes(){
        return path;
    }
}
