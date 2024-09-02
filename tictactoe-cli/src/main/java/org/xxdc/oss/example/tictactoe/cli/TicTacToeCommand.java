package org.xxdc.oss.example.tictactoe.cli;

import picocli.CommandLine;

import org.xxdc.oss.example.BotPlayer;
import org.xxdc.oss.example.Game;
import org.xxdc.oss.example.PlayerNode;

import jakarta.enterprise.context.Dependent;

@CommandLine.Command(name = "tictactoe", description = "A Game of TicTacToe", mixinStandardHelpOptions = true, version = "1.1.1")
public class TicTacToeCommand implements Runnable {

    @CommandLine.Option(names = {"-d", "--dimension"}, description = "The dimension of the game board (default: ${DEFAULT-VALUE}, for a ${DEFAULT-VALUE}x${DEFAULT-VALUE} board).", defaultValue = "3")
    int dimension;

    @CommandLine.Option(names = {"-s", "--serialization-enabled"}, description = "Serialized persistence enabled (default: ${DEFAULT-VALUE}).", defaultValue = "false")
    boolean persistenceEnabled;

    private final GameService gameService;

    public TicTacToeCommand(GameService gameService) { 
        this.gameService = gameService;
    }

    @Override
    public void run() {
        gameService.play(dimension, persistenceEnabled);
    }
}

@Dependent
class GameService {
    void play(int dimension, boolean persistenceEnabled) {
        var game = new Game(
            dimension,
            persistenceEnabled,
            new PlayerNode.Local<>("X", new BotPlayer()),
            new PlayerNode.Local<>("O", new BotPlayer())
        );
        game.play();
    }
}