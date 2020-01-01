package best_route_mod;

public class RoomComparison {
    private Class roomClass;
    private char sign;

    public RoomComparison(Class roomClass, char sign){
        this.roomClass = roomClass;
        this.sign = sign;
    }

    public Class getRoomClass(){
        return roomClass;
    }
}
