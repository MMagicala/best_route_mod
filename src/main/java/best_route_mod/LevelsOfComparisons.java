package best_route_mod;

import java.util.ArrayList;

public class LevelsOfComparisons {
    public static ArrayList<ArrayList<RoomComparison>> comparisons;

    private static void addComparisonToIndex(RoomComparison comparison, int index){
        comparisons.get(index).add(comparison);
    }

    public static void addComparisonAtBottomLevel(RoomComparison comparison){
        addComparisonToIndex(comparison, 0);
    }

    public static void moveComparisonUp(RoomComparison comparison){
        for(int i = 0; i < comparisons.size(); i++){
            if(i < NUM_COMPARISON_LEVELS-1 && comparisons.get(i).contains(comparison)){
                comparisons.get(i).remove(comparison);
                comparisons.get(i+1).add(comparison);
            }
        }
    }

    public static void removeComparisonAtBottomLevel(RoomComparison comparison){
        comparisons.get(0).remove(comparison);
    }

    public static void moveComparisonDown(RoomComparison comparison){
        for(int i = 0; i < comparisons.size(); i++){
            // Just delete the comparison if it is at the bottom of the list
            if(i > 0 && comparisons.get(i).contains(comparison)){
                comparisons.get(i).remove(comparison);
                comparisons.get(i-1).add(comparison);
            }
        }
    }

    public static int getLevelOfComparison(RoomComparison comparison){

    }

    public static boolean doesComparisonExist(RoomComparison comparison){

    }
}
