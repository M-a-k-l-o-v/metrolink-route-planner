import java.util.List;
import java.util.Scanner;
public class Driver{
    public static void main(String[] args){
        MetrolinkGraph graph =  new MetrolinkGraph("Metrolink_times_linecolour");
        graph.updateRouteList();

        //get and validate location
        Scanner scanner = new Scanner(System.in);
        String Start = graph.queryStart(scanner);
        String Destination = graph.queryDestination(scanner);  
        int validEntry = graph.validLocation(Start, Destination);

        while (validEntry != 1) {  // Keep asking until both locations are valid
            switch (validEntry) {
                case 2:
                    System.out.println("Start location not found.");
                    Start = graph.queryStart(scanner);  
                    validEntry = graph.validLocation(Start, Destination);
                    break;  

                case 3:
                    System.out.println("Destination not found.");
                    Destination = graph.queryDestination(scanner);  
                    validEntry = graph.validLocation(Start, Destination);
                    break;  

                case 0:
                    System.out.println("Neither location found.");
                    Start = graph.queryStart(scanner);  
                    Destination = graph.queryDestination(scanner);
                    validEntry = graph.validLocation(Start, Destination);
                    break;  

                default:
                    System.out.println("Unexpected error.");
                    Start = graph.queryStart(scanner);
                    Destination = graph.queryDestination(scanner);
                    validEntry = graph.validLocation(Start, Destination);
                    break;  
            
            }
        }  
        System.out.println("Both locations are valid.");
        scanner.close();


        List<JourneyNode> path = graph.shortestTimeList(Start, Destination);
        if (!path.isEmpty()) {
            System.out.println("Route found:");
            for (int i = 0; i < path.size(); i++) {
                JourneyNode node = path.get(i);
                if (i == 0) {
                    System.out.print("Start at: " + node.station);
                } else {
                    System.out.print(" -> " + node.station + " (" + node.line + ")");
                }
                System.out.println();
            }
            JourneyNode last = path.get(path.size() - 1);
            System.out.printf("Total journey time: %.1f mins\n", last.time);
        } 
        else {
            System.out.println("No route found between the given locations.");
        }

    
    }
    
}
