package best_route_mod;

public class Criterium {
    Class roomType;
    String comparisonOperator;

    public Criterium(Class roomType, String comparisonOperator){
        this.roomType = roomType;
        this.comparisonOperator = comparisonOperator;
    }
}