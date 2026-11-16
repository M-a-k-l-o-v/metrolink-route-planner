public class JourneyNode implements Comparable<JourneyNode> {
    // keep track of each point in the journey for djikstras
    // this should help keep track of the number of lines changes later for task 4
    public final String station;
    public final String line;
    public final double time;
    public final JourneyNode previous;

    public JourneyNode(String station, String line, double time, JourneyNode previous) {
        this.station = station;
        this.line = line;
        this.time = time;
        this.previous = previous;
    }

    @Override // as the time double is initially final
    public int compareTo(JourneyNode other) {
        return Double.compare(this.time, other.time); // use time to compare journey instances
    }
}