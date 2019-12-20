package best_route_mod;

public class Criteria {
    Class roomType;
    String comparisonOperator;

    public Criteria(Class roomType, String comparisonOperator){
        this.roomType = roomType;
        this.comparisonOperator = comparisonOperator;
    }
}