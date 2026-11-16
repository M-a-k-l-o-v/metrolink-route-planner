// the aim of this file is to have all graphing and traversal tooling in one place
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;



public class MetrolinkGraph {
    String filePath;
    List<String> routeList;
    
    public MetrolinkGraph(String fileName) {
        this.filePath = fileName + ".csv"; 
        this.routeList = new ArrayList<>();
    }

    public void updateRouteList(){
        System.out.println("Reading file: " + this.filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                routeList.add(line.toLowerCase());
            }
        }

        catch (IOException e) {
            e.printStackTrace(); // Handle file I/O errors
        }
        
        
    }

    public int validLocation(String Start, String Destination) {

        boolean foundDestination = false;
        boolean foundStart = false;

        if (Start == null || Start.isEmpty())  return 2; 
        if (Destination == null || Destination.isEmpty()) return 3; 
        

    // Check for the start location
    for (int i = 0; i < routeList.size(); i++) {
        String route = routeList.get(i); // each route is checked
        String[] parts = route.split(",");
        if (parts.length >= 2) {
            String from = parts[0].trim().toLowerCase(); // just making sure the lettercase is in order
            String to = parts[1].trim().toLowerCase();
            if (from.equals(Start) || to.equals(Start)) {
                System.out.println("Start found on line " + (i + 1) + ": " + route);
                foundStart = true;
                break; 
            }
        }
    }

    // Check for the destination location
    for (int i = 0; i < routeList.size(); i++) {
        String route = routeList.get(i);
        String[] parts = route.split(",");
        if (parts.length >= 2) {
            String from = parts[0].trim().toLowerCase();
            String to = parts[1].trim().toLowerCase();
            if (from.equals(Destination) || to.equals(Destination)) {
                System.out.println("Destination found on line " + (i + 1) + ": " + route);
                foundDestination = true;
                break; 
            }
        }
    }         
        
        if (foundStart && foundDestination) return 1; // both locations exists on map
        if (!foundStart && !foundDestination) return 0;// neither locations exists on map
        if (!foundStart) return 2; //the start location doesnt exist
        if (!foundDestination) return 3; //the end location doesnt exist
        
        return -1;
    }
    
    public String queryStart(Scanner scanner) {
        System.out.print("Where are you beginning from - ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public String queryDestination(Scanner scanner) {
        System.out.print("Where is your destination - ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public Map<String, List<Edges>> mapLayoutVerTwo() {
        Map<String, List<Edges>> layout = new HashMap<>();
        boolean firstRow = true;
        
        for (String line : routeList) {
            if (firstRow) {
                firstRow = false;
                continue;
            }
    
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                String from = parts[0].trim().toLowerCase();
                String to = parts[1].trim().toLowerCase();
                String lineColor = parts[2].trim().toLowerCase();
                try {
                    double time = Double.parseDouble(parts[3].trim());
                    layout.putIfAbsent(from, new ArrayList<>());
                    layout.putIfAbsent(to, new ArrayList<>());
                    layout.get(from).add(new Edges(to, lineColor, time));
                    layout.get(to).add(new Edges(from, lineColor, time));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid time format in row: " + line);
                }
            }
        }
        return layout;
    }
    

    public List<String> path(String Start, String Destination){
        Map<String, List<Edges>> layout = mapLayoutVerTwo();
        Map<String, String> lastStation = new HashMap<>(); // keeps track of where we coming from
        Queue<String> bfs = new LinkedList<>();
        Set<String> stationsVisited = new HashSet<>();

        bfs.add(Start);// first point in BFS 
        stationsVisited.add(Start);

        while (!bfs.isEmpty()) { // should loop while there are stations left to explore
            String current = bfs.poll();
            if(current.equals(Destination)) { // checks if we are at the destination
                List<String> path = new ArrayList<>();
                for (String at = Destination; at != null; at = lastStation.get(at)) {
                    path.add(at);// backtrack to start using lastStation to create station list
                }
                Collections.reverse(path);
                return path;
            }

            for (Edges neighbor : layout.getOrDefault(current, new ArrayList<>())) {
            // visits all connecting stations if they arent visited then adds it to the queue to explore next
                if (!stationsVisited.contains(neighbor.toStation)) {
                    stationsVisited.add(neighbor.toStation);
                    lastStation.put(neighbor.toStation, current);
                    bfs.add(neighbor.toStation);//new point in BFS
                }
            }
        }
            return Collections.emptyList(); // if no path found
    }

    public List<JourneyNode> shortestTimeList(String Start, String Destination){
        PriorityQueue<JourneyNode> queue = new PriorityQueue<>(); // again using time compare in journeyNode to get the shortest time of journeys(this should be the 1st element of the priotrity queue).
        Map<String, Double> bestTimes = new HashMap<>(); 
        Set<String> visited = new HashSet<>();

        queue.add(new JourneyNode(Start, null, 0, null)); // so from start point with no time used yet and no line chosen
        bestTimes.put(Start, 0.0); 

        while (!queue.isEmpty()) { 
            // loops for every journeyNode
            JourneyNode current = queue.poll();
            // becuaese this is priority queue this will alway be the current shortest path
            String currentStation = current.station;
    
            if (visited.contains(currentStation)) continue;
            visited.add(currentStation);
    
            if (currentStation.equals(Destination)) {
                List<JourneyNode> path = new ArrayList<>();
                for (JourneyNode node = current; node != null; node = node.previous) {
                    path.add(node);
                    //so using previous node from jouney,backtrack to find path
                }
                Collections.reverse(path); // to get the order right
                return path;
            }

            List<Edges> neighbors = mapLayoutVerTwo().getOrDefault(currentStation, new ArrayList<>());

            for (Edges edge : neighbors) {
                double switchPenalty = (current.line != null && !current.line.equals(edge.line)) ? 2.0 : 0.0; // line changing penalty depending on if line changed or not
                double totalTime = current.time + edge.time + switchPenalty; //considering line change this is current total time

                if (!bestTimes.containsKey(edge.toStation) || totalTime < bestTimes.get(edge.toStation)) {// just to check if this new time is our lowest time
                    bestTimes.put(edge.toStation, totalTime);
                    queue.add(new JourneyNode(edge.toStation, edge.line, totalTime, current));
                }
            }
        }

        return Collections.emptyList(); // route doesnt exist

    }
}


