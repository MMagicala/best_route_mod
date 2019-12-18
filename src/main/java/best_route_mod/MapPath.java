package best_route_mod;

import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class MapPath {
    private int numCampSites;
    private ArrayList<MapRoomNode> path;
    public MapPath(){
        this(new ArrayList<MapRoomNode>(), 0);
    }

    public MapPath(ArrayList<MapRoomNode> path){
        this(path, 0);
    }

    public MapPath(ArrayList<MapRoomNode> path, int numCampSites){
        this.path = path;
        this.numCampSites = numCampSites;
    }
}
