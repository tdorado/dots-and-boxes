# Dots And Boxes

## Requirements

* Java 1.8
* Maven

## How to use

1. Clone this repo
2. Open a terminal and `cd` into the project's root.
3. Create `.jar` by running:

```
$ mvn clean install
```

This should have generated a `.jar` file under `./target/` folder.

4. Execute `.jar` by running one of the following:

```
$ java -jar java -jar target/dots-and-boxes.jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off]
```
or
```
$ java -jar java -jar target/dots-and-boxes.jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off] -load [file]
```

##The values of the arguments are:

* -size [n]: it's the size of the board.
* -ai [m]: it's the rol of the ai: 0 HUMAN vs HUMAN, 1 AI vs HUMAN, 2 HUMAN vs AI, 3 AI vs AI.
* -mode [time|depth]: it's the mode of the mimimax algorithm, by time or depth.
* -param [k]: it's the number of the last argument. When "time" it's seconds. When “depth” it's the depth of the minimax tree.
* -prune [on|off]: turns on or off the prune of the minimax tree.
* -load [file]: loads a saved game from file.