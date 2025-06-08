import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class FifteenPuzzle {
    private final int SIZE = 4;
    private JFrame frame;
    private JButton[][] buttons = new JButton[SIZE][SIZE];
    private int[][] board = new int[SIZE][SIZE];
    private int blankRow, blankCol;
    private int moveCount = 0;
    private JLabel moveLabel;
    private JLabel timerLabel;
    private boolean timerStarted = false;
    private long startTime;
    private Timer gameTimer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FifteenPuzzle().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("15 Puzzle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        JPanel topPanel = new JPanel(new BorderLayout());
        moveLabel = new JLabel("Moves: 0");
        timerLabel = new JLabel("Time: 00.00", SwingConstants.RIGHT);

        topPanel.add(moveLabel, BorderLayout.WEST);
        topPanel.add(timerLabel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);

        initializeBoard();
        shuffleBoard();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton btn = new JButton();
                buttons[i][j] = btn;
                int r = i, c = j; // capture for lambda
                btn.addActionListener(e -> tryMove(r, c));
                boardPanel.add(btn);
            }
        }

        updateButtons();

        frame.setSize(400, 450);
        frame.setVisible(true);
    }

    private void initializeBoard() {
        int value = 1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = value++;
        board[SIZE - 1][SIZE - 1] = 0;
        blankRow = SIZE - 1;
        blankCol = SIZE - 1;
    }

    private void shuffleBoard() {
        for (int i = 0; i < 1000; i++) {
            int dir = (int)(Math.random() * 4);
            int r = blankRow, c = blankCol;
            switch (dir) {
                case 0: r--; break;
                case 1: r++; break;
                case 2: c--; break;
                case 3: c++; break;
            }
            if (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                board[blankRow][blankCol] = board[r][c];
                board[r][c] = 0;
                blankRow = r;
                blankCol = c;
            }
        }
        moveCount = 0;
    }

    private void tryMove(int r, int c) {
        if (!timerStarted) {
            timerStarted = true;
            startTime = System.currentTimeMillis();
            startTimer();
        }

        if (r == blankRow) {
            if (c < blankCol) {
                for (int j = blankCol; j > c; j--)
                    board[r][j] = board[r][j - 1];
            } else if (c > blankCol) {
                for (int j = blankCol; j < c; j++)
                    board[r][j] = board[r][j + 1];
            } else return;
            board[r][c] = 0;
            blankCol = c;
            moveCount++;
        } else if (c == blankCol) {
            if (r < blankRow) {
                for (int i = blankRow; i > r; i--)
                    board[i][c] = board[i - 1][c];
            } else if (r > blankRow) {
                for (int i = blankRow; i < r; i++)
                    board[i][c] = board[i + 1][c];
            } else return;
            board[r][c] = 0;
            blankRow = r;
            moveCount++;
        } else {
            return;
        }

        moveLabel.setText("Moves: " + moveCount);
        updateButtons();

        if (isSolved()) {
            if (gameTimer != null) gameTimer.cancel();
            double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
            JPanel panel = new JPanel(new BorderLayout());
            JLabel message = new JLabel("\u2728 Puzzle Solved in " + moveCount + " moves and " + String.format("%.2f", elapsed) + " seconds!", SwingConstants.CENTER);
            JButton playAgain = new JButton("Play Another");
            playAgain.addActionListener(e -> {
                frame.dispose();
                SwingUtilities.invokeLater(() -> new FifteenPuzzle().createAndShowGUI());
            });
            panel.add(message, BorderLayout.CENTER);
            panel.add(playAgain, BorderLayout.SOUTH);
            JOptionPane.showMessageDialog(frame, panel);
        }
    }

    private void updateButtons() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int val = board[i][j];
                JButton btn = buttons[i][j];
                if (val == 0) {
                    btn.setText("");
                    btn.setBackground(Color.LIGHT_GRAY);
                } else {
                    btn.setText(String.valueOf(val));
                    btn.setBackground(Color.WHITE);
                }
                btn.setFont(new Font("Arial", Font.BOLD, 24));
            }
        }
    }

    private boolean isSolved() {
        int num = 1;
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (i == SIZE - 1 && j == SIZE - 1)
                    return board[i][j] == 0;
                else if (board[i][j] != num++) return false;
        return true;
    }

    private void startTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                SwingUtilities.invokeLater(() -> timerLabel.setText(String.format("Time: %.2f", elapsed)));
            }
        }, 0, 100);
    }
}
