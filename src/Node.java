import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

class Node implements Comparable<Node> {
    private int[][] state;
    private Node parent;
    private String move;
    protected int g;
    private int h;
    private int n;

    public Node(int[][] state, Node parent, String move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.g = 0;
        this.h = heuristic();
        this.n = notformalsit();
    }

    @Override
    public int compareTo(Node other) {
        return (this.g + this.n * this.h) - (other.g + other.n * other.h);
    }

    public int heuristic() {
        int distance = 0;
        int[][] finalState = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 },
                { 13, 14, 15, 0 }
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == finalState[i][j] || state[i][j] == 0) {
                    continue;
                } else {
                    int[] finalPosition = findPosition(finalState, state[i][j]);
                    distance += Math.abs(i - finalPosition[0]) + Math.abs(j - finalPosition[1]);
                }
            }
        }
        return distance;
    }

    public int notformalsit() {
        int count = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] != 4 * i + j) {
                    count++;
                }
            }
        }
        return count;
    }

    public int[][] getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public String getMove() {
        return move;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public int getN() {
        return n;
    }

    private int[] findPosition(int[][] array, int value) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] == value) {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }
}

class EightPuzzle {
    public static int countInversions(int[] sequence) {
        int inversions = 0;
        for (int i = 0; i < sequence.length; i++) {
            for (int j = i + 1; j < sequence.length; j++) {
                if (sequence[i] > sequence[j] && sequence[j] != 0) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    public static int[][] createInitialState() {
        int[] sequence = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        Random random = new Random();

        do {
            for (int i = sequence.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int temp = sequence[i];
                sequence[i] = sequence[j];
                sequence[j] = temp;
            }
        } while (countInversions(sequence) % 2 != 0);

        sequence = Arrays.copyOf(sequence, sequence.length + 1);
        sequence[sequence.length - 1] = 0;

        int[][] initialState = new int[4][4];
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                initialState[i][j] = sequence[k];
                k++;
            }
        }

        return initialState;
    }

    public static List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        int i_0 = -1, j_0 = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (node.getState()[i][j] == 0) {
                    i_0 = i;
                    j_0 = j;
                    break;
                }
            }
        }

        for (int[] direction : directions) {
            int next_i = i_0 + direction[0];
            int next_j = j_0 + direction[1];
            if (next_i >= 0 && next_i < 4 && next_j >= 0 && next_j < 4) {
                int[][] newState = Arrays.stream(node.getState())
                        .map(int[]::clone)
                        .toArray(int[][]::new);
                newState[i_0][j_0] = newState[next_i][next_j];
                newState[next_i][next_j] = 0;
                neighbors.add(new Node(newState, node, Integer.toString(newState[next_i][next_j])));
            }
        }
        return neighbors;
    }

    public static List<int[][]> aStar(int[][] initial_state, int[][] final_state) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        Node initialNode = new Node(initial_state, null, "");
        openList.add(initialNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (Arrays.deepEquals(currentNode.getState(), final_state)) {
                List<int[][]> path = new ArrayList<>();
                while (currentNode != null) {
                    if (!currentNode.getMove().isEmpty()) {
                        path.add(currentNode.getState());
                    }
                    currentNode = currentNode.getParent();
                }
                List<int[][]> reversedPath = new ArrayList<>();
                for (int i = path.size() - 1; i >= 0; i--) {
                    reversedPath.add(path.get(i));
                }
                return reversedPath;
            }

            closedSet.add(Arrays.deepToString(currentNode.getState()));

            for (Node neighbor : getNeighbors(currentNode)) {
                if (!closedSet.contains(Arrays.deepToString(neighbor.getState()))) {
                    neighbor.g = currentNode.getG() + 1;
                    openList.add(neighbor);
                }
            }
        }

        return null;
    }

    public static void main(String[] args) {
        int[][] initial_state = createInitialState();
        System.out.println("初始状态");
        for (int[] row : initial_state) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();

        int[][] final_state = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 },
                { 13, 14, 15, 0 }
        };

        List<int[][]> solution = aStar(initial_state, final_state);
        int step = 1;
        for (int[][] state : solution) {
            System.out.println("第" + step + "步");
            for (int[] row : state) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println();
            step++;
        }

        System.out.println("累计步数: " + (solution.size() - 1));
    }
}
