package nphs;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author jloomis
 */
public class PuzzlePanel extends JPanel implements ActionListener, MouseListener {

    private Point point;//stores previously pressed square
    private int[][] values;
    private JButton[][] buttons;
    private int dir;

    public PuzzlePanel() {
        newPanel(5, 5);
    }

    private void createButtons() {
        int cols = values[0].length;
        int rows = values.length;

        GridLayout buttonLayout = new GridLayout(rows, cols);
        this.setLayout(buttonLayout);
        for (int i = 0; i < rows * cols; i++) {
            int r = i / cols;
            int c = i % cols;
            JButton button = new JButton("");
            button.addActionListener(this);
            button.addMouseListener(this);
            button.setEnabled(values[r][c] == -1);
            this.add(button);
            buttons[r][c] = button;
        }
    }

    public void newPanel(int rows, int cols) {
        dir = -1;
        point = null;
        values = new int[rows][cols];
        buttons = new JButton[rows][cols];
        placeValues();
        this.removeAll();
        createButtons();
        revalidate();
        repaint();
    }

    private void placeValues() {
        Point point = new Point((int) (Math.random() * 5), (int) (Math.random() * 5));
        values[(int) point.getX()][(int) point.getY()] = -1;
        int dir = (int) (Math.random() * 4 + 1);
        int newDir;
        do {
            newDir = (int) (Math.random() * 4 + 1);
        } while (!isValidDir(newDir, dir, point));
        dir = newDir;
        for (int x = 0; x < 8; x++) {
            int count = (int) (Math.random() * checkSpaces(dir, point));
            changePoint(dir, point);
            for (int j = 0; j < count; j++) {
                changePoint(dir, point);
                values[(int) point.getX()][(int) point.getY()] = -2;
            }
            values[(int) point.getX()][(int) point.getY()] = -1;
            int infLoopProtection = 0;
            do {
                infLoopProtection++;
                newDir = (int) (Math.random() * 4 + 1);
            } while (!isValidDir(newDir, dir, point) && infLoopProtection < 100);
            if(infLoopProtection == 100)
                return;
            dir = newDir;
        }
    }

    public int getStatus() {
        return -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        int r = -1;
        int c = -1;
        for (int x = 0; x < buttons[0].length; x++) {
            for (int y = 0; y < buttons.length; y++) {
                if (button == buttons[y][x]) {//I acknowledge that == is generally not good to use on objects, but i know what im doing 
                    r = y;
                    c = x;
                }
            }
        }

        button.setEnabled(!validMove(r, c));
        if (isSuccess())
            JOptionPane.showMessageDialog(this, "Success!");
        if (isFailed())
            JOptionPane.showMessageDialog(this, "Failed :(");
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private boolean validMove(int r, int c) {
        if (point == null) {
            point = new Point(r, c);//i kinda have it reversed but its fine cuz im consistent
            dir = -1;
            return true;
        } else {
            if ((point.getX() == r || point.getY() == c) && dir == -1) {
                changeDir(r, c, point);
                point = new Point(r, c);
                return true;
            }
            if (point.getX() == r || point.getY() == c) {
                int temp = dir;
                changeDir(r, c, point);
                if ((dir + 2) % 4 == temp % 4) {
                    dir = temp;
                    return false;
                }
                point = new Point(r, c);
                return true;
            }

        }
        return false;
    }

    public boolean isSuccess() {
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (buttons[r][c].isEnabled()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isFailed() {
        if(isSuccess()){
            return false;
        }
        if (point == null) {
            return false;
        } else {
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    if ((point.getX() == r || point.getY() == c) && dir == -1 && buttons[r][c].isEnabled()) {
                        return false;
                    }
                    if ((point.getX() == r || point.getY() == c) && buttons[r][c].isEnabled()) {
                        int temp = dir;
                        changeDir(r, c, point);
                        if ((dir + 2) % 4 == temp % 4) {
                            dir = temp;
                            continue;
                        }
                        dir = temp;
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void changeDir(int r, int c, Point point) {
        if (point.getX() > r) {
            dir = 1;
        }
        if (point.getX() < r) {
            dir = 3;
        }
        if (point.getY() > c) {
            dir = 2;
        }
        if (point.getY() < c) {
            dir = 4;
        }
    }

    private boolean opposite(int dir, int opp) {
        return (dir + 2) % 4 == opp % 4;
    }

    private boolean isValidDir(int dir, int oldDir, Point point) {
        if (opposite(oldDir, dir)) {
            return false;
        }
        if (dir == 3 && (point.getX() == 0 || values[(int) point.getX() - 1][(int) point.getY()] != 0)) {
            return false;
        }
        if (dir == 4 && (point.getY() == 0 || values[(int) point.getX()][(int) point.getY() - 1] != 0)) {
            return false;
        }
        if (dir == 1 && (point.getX() + 1 == values.length || values[(int) point.getX() + 1][(int) point.getY()] != 0)) {
            return false;
        }
        if (dir == 2 && (point.getY() + 1 == values[0].length || values[(int) point.getX()][(int) point.getY() + 1] != 0)) {
            return false;
        }
        return true;
    }

    private void changePoint(int dir, Point point) {
        if (dir == 1) {
            point.translate(1, 0);
        }
        if (dir == 2) {
            point.translate(0, 1);
        }
        if (dir == 3) {
            point.translate(-1, 0);
        }
        if (dir == 4) {
            point.translate(0, -1);
        }
    }

    private int checkSpaces(int dir, Point p) {
        for (int i = 0; i < Math.max(values.length, values[0].length); i++) {
            Point temp = (Point) p.clone();
            for (int x = 0; x <= i; x++) {
                changePoint(dir, temp);
            }
            if (!pointInBounds(temp) || values[(int) temp.getX()][(int) temp.getY()] != 0) {
                return i - 1;
            }
        }
        return 0;
    }

    private boolean pointInBounds(Point p) {
        if (p.getX() < 0 || p.getY() < 0) {
            return false;
        }
        if (p.getX() >= values.length || p.getY() >= values[0].length) {
            return false;
        }
        return true;
    }
}
