# Metrolink route planner

A command-line route planner for the Manchester Metrolink tram network, written in Java in May 2025. You type a start and a destination station and it prints the fastest route, the line used on each leg, and the total journey time.

This was a first-year university programming assignment at Lancaster University. It was solo work and all the Java here is mine. The station and journey-time data file (`Metrolink_times_linecolour.csv`) was provided with the assignment.

## How it works

- **Data:** each row of the CSV is one link between two neighbouring stations: `From, To, Line, Time (mins)`. There are 99 stations on 7 line colours.
- **Graph:** `MetrolinkGraph` turns the rows into an adjacency list, a `HashMap` from station name to a list of `Edges` (neighbour, line colour, minutes). Links are added in both directions.
- **Fastest route:** `shortestTimeList` is Dijkstra's algorithm with a `PriorityQueue` of `JourneyNode` objects ordered by time so far. Moving onto a different line colour adds a 2 minute change penalty. Each node keeps a link to the previous one, so the route is read back from the destination.
- **Any route:** `path` is a breadth-first search that finds a route with the fewest stops, ignoring times. It is in the code but the program does not call it.
- **Input checking:** station names are matched case-insensitively against the file, and the program asks again until both exist.

## Build and run

Needs a JDK. Checked with OpenJDK 27. Run from the repo folder so the CSV is found:

```bash
javac -d out *.java
java -cp out Driver
```

## Example

Real output from a run (typed input shown after the prompts):

```text
Reading file: Metrolink_times_linecolour.csv
Where are you beginning from - Victoria
Where is your destination - Old Trafford
Start found on line 11: queens road,victoria,yellow,5
Destination found on line 22: stretford,old trafford,purple,2
Both locations are valid.
Route found:
Start at: victoria
 -> exchange square (pink)
 -> st. peter's square (pink)
 -> deansgate/castlefield (purple)
 -> cornbrook (purple)
 -> trafford bar (purple)
 -> old trafford (purple)
Total journey time: 16.5 mins
```

An unknown station gets `Start location not found.` (or `Destination not found.`) and the prompt is repeated.

## Limitations

- Only the fastest-time option is offered. The assignment's "fewest changes" option was not implemented, and changes are not counted or marked in the output.
- Dijkstra tracks the best time per station, not per station and line, so when two lines share a stretch of track the result can occasionally be slightly slower than the true optimum.
- The graph is rebuilt from the file rows on every step of the search, which is wasteful but unnoticeable at this size.
- Station names must be typed exactly as in the CSV (any letter case). There is no fuzzy matching.
- Journey times are the fixed values from the 2025 data file, not live data.
