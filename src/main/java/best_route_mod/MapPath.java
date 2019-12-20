package best_route_mod;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.RestRoom;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapPath {
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

    // Dont use, broken
    public void printPath(){
        System.out.println("Best path has " + numCampSites + ":");
        for(int i = 0; i <= path.size()-1; i++){
            System.out.print("Node " + BestRouteMod.printNode(path.get(i)));
            if(path.get(i).room instanceof RestRoom) System.out.print(" - Rest site ");
            if(i != path.size() - 1){
                System.out.print("- Path exists to " + BestRouteMod.printNode(path.get(i+1)));
                System.out.print("? " + (AbstractDungeon.map.get(path.get(i).y).get(path.get(i).x).getEdgeConnectedTo(AbstractDungeon.map.get(path.get(i+1).y).get(path.get(i+1).x)) != null));
                System.out.print("\nList of available edges: ");
                AbstractDungeon.map.get(path.get(i).y).get(path.get(i).x).getEdges().forEach((mapEdge -> {
                    System.out.print(BestRouteMod.printEdge(mapEdge) + " ");
                }));
            }
            System.out.println();
        }
    }

    public ArrayList<MapRoomNode> getListOfNodes(){
        return path;
    }
}
