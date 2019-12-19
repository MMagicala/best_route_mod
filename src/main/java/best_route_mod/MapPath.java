package best_route_mod;

import com.megacrit.cardcrawl.map.MapRoomNode;

import java.util.ArrayList;

public class MapPath {
    private int numCampSites;
    private ArrayList<MapRoomNode> path;

    public MapPath(){
        this(new ArrayList<MapRoomNode>(), 0);
    }

    public MapPath(MapRoomNode node, int numCampSites){
        path = new ArrayList<MapRoomNode>();
        path.add(node);
        this.numCampSites = numCampSites;
    }

    public MapPath(ArrayList<MapRoomNode> path, int numCampSites){
        this.path = path;
        this.numCampSites = numCampSites;
    }

    public void incrementNumCampSites(){
        numCampSites++;
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

    public void printPath(){
        System.out.println("Best path:");
        for(MapRoomNode node: path){
            System.out.println("Node (" + node.x + "," + node.y + ") ");
        }
    }
}
