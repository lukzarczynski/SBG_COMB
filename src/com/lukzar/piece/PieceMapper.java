package com.lukzar.piece;

import com.lukzar.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import main.model.Game;
import main.model.Move;
import main.model.MoveType;
import main.model.OneMove;
import main.model.Piece;

/**
 * Created by lukasz on 02.10.17.
 */
public class PieceMapper {

    public Map<String, Object> getValues(Game game, Piece piece) {
        //util
        Map<MoveType, Integer> endingCount = endingCount(piece);
        Map<MoveType, Integer> middleCount = middleCount(piece);
        int totalMoves = piece.getMoves().size();

        Map<Point, Boolean> startingPositions = getStartingPositions(game, piece);
        List<Integer> maximumLengthMoves = new ArrayList<>();
        List<Double> averageMoveCounts = new ArrayList<>();
        List<Integer> areasCovered = new ArrayList<>();
        for (Map.Entry<Point, Boolean> startingPosition : startingPositions.entrySet()) {
            Map<Point, Integer> possibleBoardMoves = PieceSimulator.getPossibleBoardMoves(game,
                    startingPosition.getValue() ? piece.mirror() : piece,
                    startingPosition.getKey());

            maximumLengthMoves.add(possibleBoardMoves.values().stream().mapToInt(a -> a).max().orElse(0));

            List<Integer> reachablePoints = new ArrayList<>();
            possibleBoardMoves.keySet().forEach(rp -> {
                reachablePoints.add(PieceSimulator.possibleMoves(game, piece, rp).size());
            });
            averageMoveCounts.add(reachablePoints.stream().mapToInt(a -> a).average().orElse(0));
            areasCovered.add(possibleBoardMoves.keySet().size());
        }

        Map<String, Object> result = new TreeMap<>();

        //====================================================

        // how common is the piece in the initial position
        result.put("01. how common is the piece in the initial position", percentOnMap(game, piece));

        // fraction of movements ending with move/capture/selfcapture

        result.put("02. fraction of movements ending with move", (double) endingCount.get(MoveType.EMPTY) / totalMoves);
        result.put("03. fraction of movements ending with capture", (double) endingCount.get(MoveType.PIECE) / totalMoves);
        result.put("04. fraction of movements ending with selfcapture", (double) endingCount.get(MoveType.OWN) / totalMoves);

        // how much movements depends on other pieces (self/opponent)

        result.put("05. how much movements depends on self pieces", (double) middleCount.get(MoveType.PIECE) / totalMoves);
        result.put("06. how much movements depends on opponent pieces", (double) middleCount.get(MoveType.OWN) / totalMoves);

        // is piece required to positional win/lose
        result.put("07. is piece required to positional win/lose", requiredToWinOrLose(game, piece));

        // is piece required to capture win/lose
        result.put("08. is piece required to capture win/lose", requiredToCaptureToWinOrLose(game, piece));

        // how large is average coverable area from the piece's starting positions
        result.put("09. how large is average coverable area from the piece's starting positions (average)", areasCovered.stream().mapToInt(a -> a).average().orElse(0)
                / (game.getWidth() * game.getHeight()));

        // how long it takes to reach the most distant squares
        result.put("10. how long it takes to reach the most distant squares (average)", maximumLengthMoves.stream().mapToInt(a -> a).average().orElse(0));

        // average number of legal moves from any reachable square on board
        result.put("11. average number of legal moves from any reachable square on board (average)", averageMoveCounts.stream().mapToDouble(a -> a).average().orElse(0));

        return result;
    }

    private Map<Point, Boolean> getStartingPositions(Game game, Piece piece) {
        Map<Point, Boolean> result = new HashMap<>();
        String[][] board = game.getBoard();
        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                if (board[x][y].equalsIgnoreCase(piece.getName())) {
                    boolean isUpperCase = board[x][y].equals(board[x][y].toUpperCase());
                    result.put(Point.of(x, y), isUpperCase);
                }
            }
        }
        return result;
    }

    private boolean requiredToCaptureToWinOrLose(Game game, Piece piece) {
        return game.getGoals().getMinimumPiece().containsKey(piece.getName().toUpperCase())
                || game.getGoals().getMinimumPiece().containsKey(piece.getName().toLowerCase());
    }

    private boolean requiredToWinOrLose(Game game, Piece piece) {
        return game.getGoals().getPieceGoals().containsKey(piece.getName().toUpperCase())
                || game.getGoals().getPieceGoals().containsKey(piece.getName().toLowerCase());
    }

    private Map<MoveType, Integer> middleCount(Piece piece) {
        Map<MoveType, Integer> result = new HashMap<>();
        result.put(MoveType.OWN, 0);
        result.put(MoveType.PIECE, 0);

        for (OneMove move : piece.getMoves()) {
            if (hasNotLast(move, MoveType.OWN)) {
                result.compute(MoveType.OWN, (m, o) -> o + 1);
            }
            if (hasNotLast(move, MoveType.PIECE)) {
                result.compute(MoveType.PIECE, (m, o) -> o + 1);
            }
        }

        return result;
    }

    private boolean hasNotLast(OneMove move, MoveType search) {
        List<Move> moves = move.getMoves();
        for (int i = 0; i < moves.size() - 1; i++) {
            Move m = moves.get(i);
            if (m.getMoveType().equals(search)) {
                return true;
            }
        }
        return false;
    }

    private Map<MoveType, Integer> endingCount(Piece piece) {
        Map<MoveType, Integer> result = new HashMap<>();
        result.put(MoveType.EMPTY, 0);
        result.put(MoveType.OWN, 0);
        result.put(MoveType.PIECE, 0);

        for (OneMove move : piece.getMoves()) {
            MoveType endsOn = move.getMoves().get(move.getMoves().size() - 1).getMoveType();
            result.compute(endsOn, (k, old) -> old + 1);
        }

        return result;
    }

    /**
     * - ile figur tego typu jest  procentowo  na plansz (król 2/32, pionek 16/32, ...)
     */
    private double percentOnMap(Game game, Piece piece) {
        int total = game.getPiecesCount().values().stream().mapToInt(a -> a).sum();
        Integer onMap = game.getPiecesCount().get(piece.getName().toUpperCase());

        return (double) onMap / total;
    }

}
