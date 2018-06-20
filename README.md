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

4. Execute `.jar` by running:

```
$ java -jar java -jar target/Timbiriche-0.0.1-SNAPSHOT.jar
```
And the following commands:
```
 -size SIZE -ai TYPE -mode MODE -param PARAM -prune PRUNE
```
With the optional command to load from a file:
```
-load FILENAME
```

The values of the arguments are:

* SIZE the size of the board, between 3 and 20
* TYPE that can be 0 for Player vs Player, 1 for AI vs Player, 2 for Player vs AI or 3 for AI vs AI
* MODE that is the type of minimax, it can be ´depth´ or ´time´
* PARAM is the parameter of the mode
* PRUNE can be ´on´ or ´off´ to activate or deactivate prune on the minimax