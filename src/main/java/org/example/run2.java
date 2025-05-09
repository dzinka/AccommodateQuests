package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class run2 {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    static class State {
        int[] robotPositions; // [x1, y1, x2, y2, x3, y3, x4, y4]
        int keys;
        int steps;

        State(int[] robotPositions, int keys, int steps) {
            this.robotPositions = robotPositions.clone();
            this.keys = keys;
            this.steps = steps;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return keys == state.keys && Arrays.equals(robotPositions, state.robotPositions);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(keys);
            result = 31 * result + Arrays.hashCode(robotPositions);
            return result;
        }
    }

    private static char[][] getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        char[][] maze = new char[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            maze[i] = lines.get(i).toCharArray();
        }

        return maze;
    }

    private static int solve(char[][] maze) {
        int m = maze.length;
        int n = maze[0].length;
        List<int[]> robots = new ArrayList<>();
        int totalKeys = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                char c = maze[i][j];
                if (c == '@') {
                    robots.add(new int[]{i, j});
                } else if (c >= 'a' && c <= 'z') {
                    totalKeys++;
                }
            }
        }

        if (robots.size() != 4) {
            return -1;
        }

        int[] initialPositions = new int[8];
        for (int i = 0; i < 4; i++) {
            initialPositions[2 * i] = robots.get(i)[0];
            initialPositions[2 * i + 1] = robots.get(i)[1];
        }

        int allKeysMask = (1 << totalKeys) - 1;
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        State initialState = new State(initialPositions, 0, 0);
        queue.offer(initialState);
        visited.add(initialState);

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.keys == allKeysMask) {
                return current.steps;
            }

            for (int robot = 0; robot < 4; robot++) {
                int x = current.robotPositions[2 * robot];
                int y = current.robotPositions[2 * robot + 1];

                for (int[] dir : DIRECTIONS) {
                    int nx = x + dir[0];
                    int ny = y + dir[1];

                    if (nx >= 0 && nx < m && ny >= 0 && ny < n && maze[nx][ny] != '#') {
                        char cell = maze[nx][ny];
                        int newKeys = current.keys;

                        if (cell >= 'A' && cell <= 'Z') {
                            int keyNeeded = cell - 'A';
                            if ((current.keys & (1 << keyNeeded)) == 0) {
                                continue;
                            }
                        }

                        if (cell >= 'a' && cell <= 'z') {
                            int keyIndex = cell - 'a';
                            newKeys = current.keys | (1 << keyIndex);
                        }

                        int[] newPositions = current.robotPositions.clone();
                        newPositions[2 * robot] = nx;
                        newPositions[2 * robot + 1] = ny;

                        State newState = new State(newPositions, newKeys, current.steps + 1);
                        if (!visited.contains(newState)) {
                            visited.add(newState);
                            queue.offer(newState);
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) throws IOException {
        char[][] data = getInput();
        int result = solve(data);
        System.out.println(result);
    }
}
