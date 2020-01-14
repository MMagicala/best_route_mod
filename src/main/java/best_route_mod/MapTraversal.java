package best_route_mod;

import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.RestRoom;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings (value="unchecked")
public class MapTraversal {
    public MapPath findBestPathFromNode(MapRoomNode node){
        ArrayList<MapRoomNode> jumpableNodes = getAdjacentNodesAbove(node);
        if (jumpableNodes.isEmpty()) {
            return new MapPath(node);
        }

        MapPath bestPath;
        if(jumpableNodes.size() == 1){
            bestPath = findBestPathFromNode(jumpableNodes.get(0));
        }else {
            bestPath = findBestPathFromNodes(jumpableNodes);
        }
        bestPath.pushNodeToFront(node);
        return bestPath;
    }

    public MapPath findBestPathFromNodes(ArrayList<MapRoomNode> nodes){
        MapPath bestPath = new MapPath();
        for (MapRoomNode node : nodes) {
            MapPath currentPath = findBestPathFromNode(node);
            if (bestPath.isEmpty() || iteratedPathExceedsCriteria(bestPath, currentPath)) {
                bestPath = currentPath;
            }
        }
        return bestPath;

    }

    private boolean iteratedPathExceedsCriteria(MapPath iteratedPath, MapPath bestPath){

    }

    private static ArrayList<MapRoomNode> getAdjacentNodesAbove(MapRoomNode node) {
        ArrayList<MapEdge> mapEdges = node.getEdges();
        ArrayList<MapRoomNode> adjacentNodesAboveGivenNode = new ArrayList<>();
        mapEdges.forEach(mapEdge -> {
            // The boss node is 2 levels above the last rest site nodes, don't count it since we can't access it on the
            // AbstractDungeon.map object
            if (mapEdge.dstY - node.y == 1) {
                adjacentNodesAboveGivenNode.add(getNodeAtCoordinates(mapEdge.dstX, mapEdge.dstY));
            }
        });
        return adjacentNodesAboveGivenNode;
    }

}
