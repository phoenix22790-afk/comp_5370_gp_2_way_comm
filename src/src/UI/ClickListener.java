package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// thanks stackoverflow
public class ClickListener extends MouseAdapter {
    private Rectangle bounds;
    private Runnable action;

    public ClickListener(JPanel parent, Rectangle bounds, Runnable action) {
        this.bounds = bounds;
        this.action = action;
        parent.addMouseListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (bounds.contains(e.getPoint())) action.run();
    }
}
