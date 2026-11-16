public class Edges { 
    // keep track of which line im using from point A to B
    String toStation;
    String line;
    Double time;

    public Edges(String toStation,String line, Double time){
        this.toStation = toStation;
        this.line = line;
        this.time = time;

    }
}
