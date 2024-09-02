![Over-Engineering TicTacToe](oe-tictactoe.png)

Over-Engineering Tic-Tac-Toe CLI
---

A CLI for running Tic-Tac-Toe - from the project [Over-Engineering Tic-Tac-Toe](https://github.com/briancorbinxyz/overengineering-tictactoe) built using modern Java Frameworks: Quarkus/PicoCLI/GraalVM for development speed and native execution.

## Running the application

### Interface
```bash
tictactoe-cli --help

Usage: tictactoe [-hsV] [-d=<dimension>]
A Game of TicTacToe
  -d, --dimension=<dimension>
                  The dimension of the game board (default: 3, for a 3x3 board).
  -h, --help      Show this help message and exit.
  -s, --serialization-enabled
                  Serialized persistence enabled (default: false).
  -V, --version   Print version information and exit.
```

### Playing
```bash
# Will currently simply run the game with a bot player 'X' facing another bot player 'O'.

tictactoe-cli
```

#### Example Output
```bash
Players: X, O ([Local{playerMarker=X, player=BotPlayer[strategyFunction=org.xxdc.oss.example.bot.BotStrategy$$Lambda/0x000001fe013d3c68@4e7afe5a]}, Local{playerMarker=O, player=BotPlayer[strategyFunction=org.xxdc.oss.example.bot.BotStrategy$$Lambda/0x000001fe013d3c68@4e7afe5a]}])- TicTacToeClient/1.0 [Local (X:BotPlayer)] (IP: 127.0.0.1; Host: corbinm1mac.local; Java: 23; OS: Mac OS X 14.6.1)                             - TicTacToeClient/1.0 [Local (O:BotPlayer)] (IP: 127.0.0.1; Host: corbinm1mac.local; Java: 23; OS: Mac OS X 14.6.1)
___
___
___

__X
___
___


_OX
___
___


_OX
X__
___


_OX
X_O
___


XOX
X_O
___


XOX
X_O
_O_


XOX
XXO
_O_


XOX
XXO
_OO

Winner: Player X!
XOX
XXO
XOO
```

## Benchmarking

Using [Hyperfine](https://github.com/sharkdp/hyperfine) to benchmark the native executable vs. the Java HotSpot executable:

```bash
# Native run vs. Java HotSpot run
❯ hyperfine --warmup 1 -n native 'tictactoe-cli/build/tictactoe-cli-1.2.1-SNAPSHOT-runner' -n hotspot 'java -jar tictactoe-cli/build/quarkus-app/quarkus-run.jar'
Benchmark 1: native
  Time (mean ± σ):      22.6 ms ±   7.5 ms    [User: 6.6 ms, System: 8.9 ms]
  Range (min … max):    17.5 ms …  88.8 ms    117 runs
 
  Warning: Statistical outliers were detected. Consider re-running this benchmark on a quiet system without any interferences from other programs. It might help to use the '--warmup' or '--prepare' options.
 
Benchmark 2: hotspot
  Time (mean ± σ):     511.1 ms ±  21.3 ms    [User: 928.0 ms, System: 99.1 ms]
  Range (min … max):   490.6 ms … 553.7 ms    10 runs
 
  Warning: Statistical outliers were detected. Consider re-running this benchmark on a quiet system without any interferences from other programs. It might help to use the '--warmup' or '--prepare' options.
 
Summary
  native ran
   22.57 ± 7.51 times faster than hotspot

❯ uname -a
Darwin corbinm1mac.local 23.6.0 Darwin Kernel Version 23.6.0: Mon Jul 29 21:14:30 PDT 2024; root:xnu-10063.141.2~1/RELEASE_ARM64_T6000 arm64
```

## Developing, packaging, deploying and running the application

See: [Tic-Tac-Toe CLI - README.MD](tictactoe-cli/README.md)
