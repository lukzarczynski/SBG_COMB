/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package com.lukzar.piece;

import com.lukzar.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import main.model.Game;
import main.model.Move;
import main.model.OneMove;
import main.model.Piece;

public class PieceSimulator {

    public static void drawBoardWithMoves(Game game, Piece piece, Point start) {
        Map<Point, Integer> possibleBoardMoves = getPossibleBoardMoves(game, piece, start);

        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < game.getHeight(); y++) {
            for (int x = 0; x < game.getWidth(); x++) {
                builder.append(possibleBoardMoves.keySet().contains(Point.of(x, y)) ?
                        possibleBoardMoves.get(Point.of(x, y)) : ".");
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
    }

    /**
     * returns map of reachable points with minimum number of steps required to reach it
     */
    public static Map<Point, Integer> getPossibleBoardMoves(Game game, Piece piece, Point start) {
        Map<Point, Integer> inHowManySteps = new HashMap<>();
        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        inHowManySteps.put(start, 0);

        while (!queue.isEmpty()) {
            Point node = queue.remove();
            visited.add(node);
            Set<Point> possibleMoves = possibleMoves(game, piece, node);

            Integer init = inHowManySteps.get(node);
            possibleMoves.forEach(p ->
                    inHowManySteps.put(p, Math.min(init + 1, inHowManySteps.getOrDefault(p, Integer.MAX_VALUE)))
            );

            possibleMoves.removeIf(visited::contains);
            queue.addAll(possibleMoves);

        }

        return inHowManySteps;
    }

    public static Set<Point> possibleMoves(Game game, Piece piece, Point start) {
        return piece.getMoves().stream()
                .map(om -> getEnding(om, start, game.getWidth(), game.getHeight()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static Point getEnding(OneMove om, Point start, int width, int height) {
        int x = start.getX();
        int y = start.getY();
        for (Move move : om.getMoves()) {
            x += move.getDx();
            y += move.getDy();
            if (!isBetweenInclusive(x, 0, width - 1) || !isBetweenInclusive(y, 0, height - 1)) {
                return null;
            }
        }
        return Point.of(x, y);
    }

    private static boolean isBetweenInclusive(int v, int min, int max) {
        return min <= v && max >= v;
    }
}
