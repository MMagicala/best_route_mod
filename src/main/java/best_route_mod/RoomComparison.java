package best_route_mod;

public class RoomComparison {
    public Class getRoomType() {
        return roomType;
    }

    private Class roomType;

    public SignOperator getComparisonOperator() {
        return comparisonOperator;
    }

    private SignOperator comparisonOperator;

    public RoomComparison(Class roomType){
        this(roomType, SignOperator.GREATER);
    }

    public RoomComparison(Class roomType, SignOperator comparisonOperator){
        this.roomType = roomType;
        this.comparisonOperator = comparisonOperator;
    }

    public boolean isMet(MapPath currentPath, MapPath bestPath){
        switch(comparisonOperator) {
            case LESS:
                if(currentPath.getRoomCount(roomType) < bestPath.getRoomCount(roomType)) return true;
                break;
            case GREATER:
                if(currentPath.getRoomCount(roomType) > bestPath.getRoomCount(roomType)) return true;
        }
        return false;
    }

    public boolean hasEqualNumRooms(MapPath currentPath, MapPath bestPath){
        return currentPath.getRoomCount(roomType) == bestPath.getRoomCount(roomType);
    }
}