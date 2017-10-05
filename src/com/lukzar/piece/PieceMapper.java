package com.lukzar.piece;

import com.lukzar.model.Values;
import main.Game;
import main.model.Piece;

/**
 * Created by lukasz on 02.10.17.
 */
public class PieceMapper {

    public Values getValues(Game game, Piece piece) {

    }

    private int piecesOnBoard(Game game, Piece piece) {
        return game.getPiecesCount().values().stream().mapToInt(a -> a).sum();
    }

    private int countOnBoard(Game game, Piece piece) {
        return game.getPiecesCount().get(piece.getName());
    }

    private int minimumMove(Piece piece) {
        piece.getMoves().stream()
                .map()
    }
}
