package best_route_mod;

public class Criterium {
    Class roomType;
    SignOperator comparisonOperator;

    public Criterium(Class roomType, SignOperator comparisonOperator){
        this.roomType = roomType;
        this.comparisonOperator = comparisonOperator;
    }

    public boolean isCriteriumMet(MapPath bestPath, MapPath currentPath){
        switch(comparisonOperator) {
            case LESS:

                break;
            case GREATER:

        }
    }
}