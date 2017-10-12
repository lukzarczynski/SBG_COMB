package com.lukzar;

import com.lukzar.piece.PieceMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import main.model.Game;
import main.model.Piece;
import main.parser.FileParser;

public class Main {

    public static void main(String[] args) throws IOException {
        File chess = new File("/Users/lzarczynski/Documents/Projects/mgr/SBG/src/main/resources/chess.sbg");

        Game game = new FileParser().parseGame(chess);

        for (Piece piece : game.getPieces()) {
            System.out.println("\n========\n");
            System.out.println(piece.getName());
            Map<String, Object> values = new PieceMapper().getValues(game, piece);

            values.forEach((k, v) -> System.out.println(k + " : " + v.toString()));
        }

    }
}
