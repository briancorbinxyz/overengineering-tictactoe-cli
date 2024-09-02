package org.xxdc.oss.example.tictactoe.cli;

import picocli.CommandLine;

import java.util.List;

import org.xxdc.oss.example.BotPlayer;
import org.xxdc.oss.example.Game;
import org.xxdc.oss.example.GameBoard;
import org.xxdc.oss.example.HumanPlayer;
import org.xxdc.oss.example.PlayerNode;
import org.xxdc.oss.example.bot.BotStrategy;
import org.xxdc.oss.example.bot.BotStrategyConfig;

import jakarta.enterprise.context.Dependent;

@CommandLine.Command(
    name = "tictactoe",
    description = {
        "A game of Tic-Tac-Toe that can be played on an arbitrary sized board by humans and/or AI opponents.%n",
        "When started without any arguments, the game will be played on a 3x3 board with two random bot players.",
    },
    mixinStandardHelpOptions = true,
    version = "1.2.1",
    sortOptions = false,
    headerHeading = "@|bold,underline Usage|@:%n%n",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n",
    header = "Play a game of tic-tac-toe.")
public class TicTacToeCommand implements Runnable {

    private static final List<PlayerOption> DEFAULT_PLAYER_LIST = List.of(new PlayerOption("X"), new PlayerOption("O"));

    @CommandLine.Option(names = {"-d", "--dimension"}, description = "The dimension of the game board (default: ${DEFAULT-VALUE}, for a ${DEFAULT-VALUE}x${DEFAULT-VALUE} board).", defaultValue = "3")
    int dimension;

    @CommandLine.Option(names = {"-s", "--serialization-enabled"}, description = "Serialized persistence enabled (default: ${DEFAULT-VALUE}).", defaultValue = "false")
    boolean persistenceEnabled;

    @CommandLine.Option(names = {"-n", "--native"}, description = "Use native game board for enhanced performance if available (default: ${DEFAULT-VALUE}).", defaultValue = "false")
    boolean useNative;

    @CommandLine.ArgGroup(heading = "A player participating in the game denoted by their marker e.g. ['X', 'O', ...] by default two random bots 'X' and 'O' will play.%n", multiplicity = "0..*", validate = false, order = 0)
    List<PlayerOption> players;
    
    static class PlayerOption {

        @CommandLine.Option(names = {"-m", "--marker"}, description = "The marker of the player, e.g. 'X', 'O'.", required = true, order = 1)
        String marker;

        @CommandLine.Option(names = {"-p", "--player", "-a", "--algorithm"}, description = {
            "The player/algorithm type, one of @|bold ${COMPLETION-CANDIDATES}|@ (default: ${FALLBACK-VALUE}).",
            "- @|bold Example 1:|@ `-p HUMAN -m X -p RANDOM -m O` will create a game with a human player and a random bot player.",
            "- @|bold Example 2:|@ `-p HUMAN -m X -p PARANOID -m O -p RANDOM -m W` will create a game with a human player and two bot players using paranoid and random respectively.",
            "- @|bold Example 3:|@ `-p MCTS -mi 1000 -m O -p RANDOM -m X` will create a game with a mcts bot player with a maximum number of iterations of 1000 vs. a random bot player.",
            "- @|bold Example 4:|@ `-mO -mX` will create a game with two random bot players.",
            "- @|bold Example 5:|@ `-mX -mO -p MCTS -mi 500` will create a game with a random bot and a mcts bot player with a maximum number of iterations of 500.",
            }, fallbackValue = "RANDOM", required = false, order = 2)
        PlayerType player = PlayerType.RANDOM;

        @CommandLine.Option(names = {"-mi", "--max-bot-iterations"}, description = "The maximum number of iterations for bot player types.", required = false, order = 3)
        Integer maxIterations;

        @CommandLine.Option(names = {"-md", "--max-bot-depth"}, description = "The maximum depth of search for bot player types.", required = false, order = 4)
        Integer maxDepth;

        @CommandLine.Option(names = {"-mt", "--max-bot-time"}, description = "The maximum time in millis to determine next move for bot player types.", required = false, order = 5)
        Integer maxTimeMillis;

        /**
         * Constructs a new `PlayerOption` instance with the `PlayerType.RANDOM` player type.
         * This constructor is used to create a `PlayerOption` with the default random player type.
         */
        PlayerOption() {
            this.player = PlayerType.RANDOM;
        }

        /**
         * Constructs a new `PlayerOption` instance with the specified marker character.
         * The player type is set to `PlayerType.RANDOM` by default.
         *
         * @param marker The marker character for the player (e.g. 'X', 'O').
         */
        PlayerOption(String marker) {
            this.marker = marker;
            this.player = PlayerType.RANDOM;
        }
        
        static PlayerNode toPlayerNode(PlayerOption playerOption) {
            return getPlayer(String.valueOf(playerOption.marker), playerOption.player, playerOption.maxIterations, playerOption.maxDepth, playerOption.maxTimeMillis);
        }

        static PlayerNode.Local<?> getPlayer(String playerMarker, PlayerType playerType, Integer maxIterations, Integer maxDepth, Integer maxTimeMillis) {
            return switch (playerType) {
                case HUMAN -> new PlayerNode.Local<>(playerMarker, new HumanPlayer());
                case MINIMAX -> new PlayerNode.Local<>(playerMarker, new BotPlayer(BotStrategy.minimax(configFor(maxIterations, maxDepth, maxTimeMillis))));
                case ALPHABETA -> new PlayerNode.Local<>(playerMarker, new BotPlayer(BotStrategy.alphabeta(configFor(maxIterations, maxDepth, maxTimeMillis))));
                case PARANOID -> new PlayerNode.Local<>(playerMarker, new BotPlayer(BotStrategy.paranoid(configFor(maxIterations, maxDepth, maxTimeMillis))));
                case RANDOM -> new PlayerNode.Local<>(playerMarker, new BotPlayer(BotStrategy.random(configFor(maxIterations, maxDepth, maxTimeMillis))));
                case MCTS -> new PlayerNode.Local<>(playerMarker, new BotPlayer(BotStrategy.mcts(configFor(maxIterations, maxDepth, maxTimeMillis))));
            };
        }

        static BotStrategyConfig configFor(Integer maxIterations, Integer maxDepth, Integer maxTimeMillis) {
            var config = BotStrategyConfig.newBuilder();
            if (maxIterations != null) {
                config.maxIterations(Math.max(maxIterations, 1));
            }
            if (maxDepth != null) {
                config.maxDepth(maxDepth);
            }
            if (maxTimeMillis != null) {
                config.maxTimeMillis(Math.max(maxTimeMillis.longValue(), 0L));
            } else {
                config.maxTimeMillis(1000L);
            }
            return config.build();
        }
    }

    private final GameService gameService;

    public TicTacToeCommand(GameService gameService) { 
        this.gameService = gameService;
    }

    @Override
    public void run() {
        gameService.play(dimension, persistenceEnabled, useNative, (players != null ? players : DEFAULT_PLAYER_LIST).stream().map(PlayerOption::toPlayerNode).toList());
    }
}

@Dependent
class GameService {

    void play(int dimension, boolean persistenceEnabled, boolean useNative, List<PlayerNode> players) {
        GameBoard.useNative.set(useNative);
        var game = new Game(
            dimension,
            persistenceEnabled,
            players.toArray(new PlayerNode[]{})
        );
        game.play();
    }
}

enum PlayerType {
    HUMAN,
    MINIMAX,
    ALPHABETA,
    PARANOID,
    RANDOM,
    MCTS,
}
