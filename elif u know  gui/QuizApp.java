
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
 

public class QuizApp extends JFrame {
 
    static final Color BG    = new Color(0xFFF7FB);
    static final Color INK   = new Color(0x4B4560);
    static final Color SOFT  = new Color(0x8B84A0);
    static final Color PINK  = new Color(0xFFC8DD);
    static final Color PEACH = new Color(0xFFD6A5);
    static final Color LEMON = new Color(0xFDFFB6);
    static final Color MINT  = new Color(0xCAFFBF);
    static final Color AQUA  = new Color(0x9BF6FF);
    static final Color SKY   = new Color(0xA0C4FF);
    static final Color LAV   = new Color(0xBDB2FF);
    static final Color GOOD  = new Color(0x8FE3AE);
    static final Color BAD   = new Color(0xFF9DB5);
    static final Color[] NAV_COLORS   = {PINK, PEACH, MINT, AQUA, LAV};
    static final Color[] BADGE_COLORS = {PINK, AQUA, LAV, PEACH, MINT, SKY};
 
    
    static final double FONT_SCALE = 1.25;
 
    static Font font(int style, int size) {
        return new Font("SansSerif", style, (int) Math.round(size * FONT_SCALE));
    }
 
    
    static void applyDefaultFonts() {
        javax.swing.plaf.FontUIResource body = new javax.swing.plaf.FontUIResource(font(Font.PLAIN, 14));
        javax.swing.plaf.FontUIResource bold = new javax.swing.plaf.FontUIResource(font(Font.BOLD, 14));
        for (String key : new String[]{"OptionPane.messageFont", "ComboBox.font", "Spinner.font",
                "RadioButton.font", "TextField.font", "PasswordField.font", "ToolTip.font",
                "TextArea.font", "Label.font"}) {
            UIManager.put(key, body);
        }
        UIManager.put("OptionPane.buttonFont", bold);
    }
 
    static Color mix(Color a, Color b, double t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
 
    static Color badgeColor(int quizId) {
        return BADGE_COLORS[Math.floorMod(quizId - 1, BADGE_COLORS.length)];
    }
 
    static boolean isPerfect(Result r) {
        return r.getTotalItems() > 0 && r.getScore() == r.getTotalItems();
    }
 
    
    static class RoundButton extends JButton {
        private Color base;
 
        RoundButton(String text, Color base) {
            super(text);
            this.base = base;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(INK);
            setFont(font(Font.BOLD, 14));
            setBorder(new EmptyBorder(10, 18, 10, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
 
        void setBase(Color c) {
            base = c;
            repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = base;
            if (!isEnabled()) {
                c = mix(base, Color.WHITE, 0.55);
            } else if (getModel().isPressed()) {
                c = mix(base, INK, 0.12);
            } else if (getModel().isRollover()) {
                c = mix(base, Color.WHITE, 0.35);
            }
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fill(new RoundRectangle2D.Double(0, 3, getWidth(), getHeight() - 3, 24, 24));
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() - 3, 24, 24));
            g2.dispose();
            super.paintComponent(g);
        }
    }
 
    static class Card extends JPanel {
        private final Color fill;
 
        Card(Color fill) {
            this.fill = fill;
            setOpaque(false);
            setBorder(new EmptyBorder(16, 20, 16, 20));
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xBDB2FF).brighter());
            g2.setColor(new Color(189, 178, 255, 60));
            g2.fill(new RoundRectangle2D.Double(0, 4, getWidth(), getHeight() - 4, 30, 30));
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() - 4, 30, 30));
            g2.dispose();
            super.paintComponent(g);
        }
    }
 
    
    static class BgPanel extends JPanel {
        BgPanel() {
            setOpaque(true);
            setBackground(BG);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double[][] spots = {{0.06, 0.10, 90}, {0.94, 0.16, 70}, {0.88, 0.88, 120},
                    {0.08, 0.90, 80}, {0.52, 0.04, 40}, {0.70, 0.55, 30}};
            Color[] cols = {PINK, LAV, MINT, LEMON, AQUA, PEACH};
            for (int i = 0; i < spots.length; i++) {
                Color c = cols[i];
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 110));
                double r = spots[i][2];
                g2.fill(new Ellipse2D.Double(getWidth() * spots[i][0] - r, getHeight() * spots[i][1] - r, r * 2, r * 2));
            }
            g2.dispose();
        }
    }
 
    
    static class Bar extends JComponent {
        private double value;
        private final Color color;
 
        Bar(double value, Color color) {
            this.value = value;
            this.color = color;
            setPreferredSize(new Dimension(200, 18));
        }
 
        void setValue(double v) {
            value = Math.max(0, Math.min(1, v));
            repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight();
            g2.setColor(new Color(0xEFEAF7));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), h, h, h));
            int w = (int) (getWidth() * Math.max(0, Math.min(1, value)));
            if (w > 0) {
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Double(0, 0, Math.max(w, h), h, h, h));
            }
            g2.dispose();
        }
    }
 
    // The badge
    static class BadgeIcon extends JComponent {
        private final boolean earned;
        private final Color color;
        private final int size;
 
        BadgeIcon(boolean earned, Color color, int size) {
            this.earned = earned;
            this.color = color;
            this.size = size;
            setPreferredSize(new Dimension(size, (int) (size * 1.15) + 2));
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double s = size;
            double cx = getWidth() / 2.0;
            double cy = 0.40 * s;
            Color main = earned ? color : new Color(0xE6E3EE);
            Color dark = earned ? mix(color, INK, 0.25) : new Color(0xCBC7D6);
 
            for (int side = -1; side <= 1; side += 2) {
                double x0 = cx + side * 0.06 * s;
                double x1 = cx + side * 0.30 * s;
                double xm = cx + side * 0.18 * s;
                Path2D tail = new Path2D.Double();
                tail.moveTo(x0, 0.55 * s);
                tail.lineTo(x1, 0.55 * s);
                tail.lineTo(x1, 1.12 * s);
                tail.lineTo(xm, 1.0 * s);
                tail.lineTo(x0, 1.12 * s);
                tail.closePath();
                g2.setColor(dark);
                g2.fill(tail);
            }
            circle(g2, cx, cy, 0.40 * s, dark);
            circle(g2, cx, cy, 0.35 * s, main);
            circle(g2, cx, cy, 0.27 * s, new Color(255, 255, 255, 175));
 
            if (earned) {
                star(g2, cx, cy, 0.21 * s, 0.09 * s, new Color(0xFFC94D));
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fill(new Ellipse2D.Double(cx + 0.36 * s, cy - 0.40 * s, 0.07 * s, 0.07 * s));
                g2.fill(new Ellipse2D.Double(cx - 0.44 * s, cy - 0.12 * s, 0.05 * s, 0.05 * s));
            } else {
                g2.setColor(new Color(0xB0ABBE));
                g2.setFont(new Font("SansSerif", Font.BOLD, (int) (0.34 * s)));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("?", (float) (cx - fm.stringWidth("?") / 2.0), (float) (cy + fm.getAscent() / 2.6));
            }
            g2.dispose();
        }
 
        private static void circle(Graphics2D g2, double cx, double cy, double r, Color c) {
            g2.setColor(c);
            g2.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        }
 
        private static void star(Graphics2D g2, double cx, double cy, double big, double small, Color c) {
            Path2D p = new Path2D.Double();
            for (int i = 0; i < 10; i++) {
                double a = -Math.PI / 2 + i * Math.PI / 5;
                double r = (i % 2 == 0) ? big : small;
                double x = cx + Math.cos(a) * r;
                double y = cy + Math.sin(a) * r;
                if (i == 0) {
                    p.moveTo(x, y);
                } else {
                    p.lineTo(x, y);
                }
            }
            p.closePath();
            g2.setColor(c);
            g2.fill(p);
        }
    }
 
    
    private final ArrayList<User> users;
    private final ArrayList<Quiz> quizzes;
    private final DataSave data;
    private final ArrayList<Quiz> createdQuizzes = new ArrayList<>();   // quizzes made by the instructor
    private JPanel content;                 // right side of the main screen
    private javax.swing.Timer quizTimer;    // running quiz countdown (if any)
    private int loginAttempts = 0;
    private final Map<String, BufferedImage> avatars = new HashMap<>();   // userId -> profile picture
    private JComponent sideAvatar;                                         // avatar shown in the sidebar
 
    public QuizApp(ArrayList<User> users, ArrayList<Quiz> quizzes, DataSave data) {
        super("Elif U Know - Python Quiz Adventure");
        applyDefaultFonts();
        this.users = users;
        
        this.quizzes = new ArrayList<>();
        this.data = data;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        setIconImages(java.util.Arrays.asList(makeIcon(16), makeIcon(32), makeIcon(64), makeIcon(128)));
        showLogin();
    }
 
    
    private static java.awt.image.BufferedImage makeIcon(int px) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(px, px, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        BadgeIcon badge = new BadgeIcon(true, PINK, (int) (px / 1.15));
        badge.setSize(px, px);
        Graphics2D g = img.createGraphics();
        badge.paint(g);
        g.dispose();
        return img;
    }
 
    private void setScreen(JComponent c) {
        stopQuizTimer();
        getRootPane().setDefaultButton(null);
        getContentPane().removeAll();
        getContentPane().add(c);
        getContentPane().revalidate();
        getContentPane().repaint();
    }
 
    private void setContent(JComponent c) {
        stopQuizTimer();
        content.removeAll();
        content.add(c, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }
 
    private void stopQuizTimer() {
        if (quizTimer != null) {
            quizTimer.stop();
            quizTimer = null;
        }
    }
 
    
    static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }
 
    static JLabel label(String text, int style, int size) {
        return label(text, style, size, INK);
    }
 
    static JPanel stack(JComponent... items) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 14, 0);
        int row = 0;
        for (JComponent c : items) {
            g.gridy = row++;
            p.add(c, g);
        }
        return p;
    }
 
    static Card lines(Color fill, JComponent... items) {
        Card c = new Card(fill);
        c.setLayout(new GridLayout(0, 1, 0, 6));
        for (JComponent i : items) {
            c.add(i);
        }
        return c;
    }
 
    static JTextField field() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }
 
    static void styleField(JTextField f) {
        f.setFont(font(Font.PLAIN, 15));
        f.setForeground(INK);
        f.setBorder(BorderFactoryHelper.pill());
    }
 
    static class BorderFactoryHelper {
        static javax.swing.border.Border pill() {
            return javax.swing.BorderFactory.createCompoundBorder(
                    new LineBorder(LAV, 2, true), new EmptyBorder(7, 12, 7, 12));
        }
    }
 
    
    private JPanel page(String title, String subtitle, JComponent body) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        JPanel head = new JPanel(new GridLayout(0, 1));
        head.setOpaque(false);
        head.add(label(title, Font.BOLD, 28));
        if (subtitle != null) {
            head.add(label(subtitle, Font.PLAIN, 14, SOFT));
        }
        p.add(head, BorderLayout.NORTH);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 0, 8, 8));
        wrap.add(body, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(wrap);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }
 
    private JComponent tableOf(String[] cols, List<Object[]> rows) {
        DefaultTableModel model = new DefaultTableModel(rows.toArray(new Object[0][]), cols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable t = new JTable(model);
        t.setRowHeight((int) Math.round(32 * FONT_SCALE));
        t.setFont(font(Font.PLAIN, 14));
        t.setForeground(INK);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(PINK);
        t.setSelectionForeground(INK);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xFFF1F7));
                }
                setForeground(INK);
                return this;
            }
        });
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(tb, v, sel, foc, r, c);
                setOpaque(true);
                setBackground(LAV);
                setForeground(INK);
                setFont(font(Font.BOLD, 14));
                setBorder(new EmptyBorder(8, 10, 8, 10));
                return this;
            }
        });
        t.getTableHeader().setReorderingAllowed(false);
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(t.getTableHeader(), BorderLayout.NORTH);
        p.add(t, BorderLayout.CENTER);
        return p;
    }
 
    
    private void showLogin() {
        BgPanel bg = new BgPanel();
        bg.setLayout(new GridBagLayout());
 
        Card card = new Card(Color.WHITE);
        card.setBorder(new EmptyBorder(26, 34, 26, 34));
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(460, 560));
 
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        BadgeIcon logo = new BadgeIcon(true, PINK, 64);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = label("Elif U Know", Font.BOLD, 34);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = label("Python Quiz Adventure", Font.PLAIN, 15, SOFT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(sub);
 
        JLabel who = label("Who is logging in?", Font.BOLD, 18);
        who.setHorizontalAlignment(SwingConstants.CENTER);
 
        RoundButton studentBtn = new RoundButton("I am a Student", MINT);
        RoundButton instructorBtn = new RoundButton("I am an Instructor", LAV);
        RoundButton exitBtn = new RoundButton("Exit", LEMON);
        studentBtn.setFont(font(Font.BOLD, 17));
        instructorBtn.setFont(font(Font.BOLD, 17));
        studentBtn.addActionListener(e -> showLogin("Student"));
        instructorBtn.addActionListener(e -> showLogin("Instructor"));
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Thank you for using Elif U Know!");
            System.exit(0);
        });
 
        JPanel choices = new JPanel(new GridLayout(0, 1, 0, 12));
        choices.setOpaque(false);
        choices.add(who);
        choices.add(studentBtn);
        choices.add(instructorBtn);
        choices.add(exitBtn);
 
        card.add(header, BorderLayout.NORTH);
        card.add(choices, BorderLayout.CENTER);
        bg.add(card);
        setScreen(bg);
    }
 
    
    private void showLogin(String chosenRole) {
        BgPanel bg = new BgPanel();
        bg.setLayout(new GridBagLayout());
 
        Card card = new Card(Color.WHITE);
        card.setBorder(new EmptyBorder(26, 34, 26, 34));
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(460, 640));
 
        
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        BadgeIcon logo = new BadgeIcon(true, PINK, 64);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = label("Elif U Know", Font.BOLD, 34);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = label(chosenRole + " Login", Font.BOLD, 16, SOFT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(title);
        header.add(Box.createVerticalStrut(2));
        header.add(sub);
 
        // Form
        JTextField user = field();
        JPasswordField pass = new JPasswordField();
        styleField(pass);
        JLabel error = label(" ", Font.PLAIN, 13, new Color(0xD6406A));
        error.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.setOpaque(false);
        form.add(label("Username", Font.BOLD, 13, SOFT));
        form.add(user);
        form.add(label("Password", Font.BOLD, 13, SOFT));
        form.add(pass);
        form.add(error);
 
        // Buttons
        RoundButton login = new RoundButton("Login", PINK);
        RoundButton exit = new RoundButton("Back", LEMON);
        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        buttons.setOpaque(false);
        buttons.add(login);
        buttons.add(exit);
 
        Runnable attempt = () -> {
            String u = user.getText().trim();
            String p = new String(pass.getPassword()).trim();
 
            
            if (u.isEmpty() || p.isEmpty()) {
                error.setText("Please enter your username and password.");
                return;
            }
 
           
            User found = null;
            for (User candidate : users) {
                if (candidate.login(u, p)) {
                    found = candidate;
                    break;
                }
            }
 
            if (found == null) {
                loginAttempts++;
                if (loginAttempts >= 3) {
                    loginAttempts = 0;
                    error.setText("Too many failed attempts. Try again!");
                } else {
                    error.setText("Wrong username or password. Attempts left: " + (3 - loginAttempts));
                }
                pass.setText("");
                return;
            }
 
            
            if (!found.getRole().equals(chosenRole)) {
                error.setText("This is not a " + chosenRole + " account.");
                pass.setText("");
                return;
            }
 
            
            loginAttempts = 0;
            final User loggedIn = found;
            if (loggedIn instanceof Student) {
                showWelcome(loggedIn, () -> studentHome((Student) loggedIn));
            } else if (loggedIn instanceof Instructor) {
                showWelcome(loggedIn, () -> instructorHome((Instructor) loggedIn));
            }
        };
        login.addActionListener(e -> attempt.run());
        user.addActionListener(e -> pass.requestFocusInWindow());
        pass.addActionListener(e -> attempt.run());
        exit.addActionListener(e -> showLogin());
 
        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        bg.add(card);
        setScreen(bg);
        SwingUtilities.invokeLater(user::requestFocusInWindow);
    }
 
    
    private void showWelcome(User u, Runnable openMenu) {
        BgPanel bg = new BgPanel();
        bg.setLayout(new GridBagLayout());
 
        boolean student = u instanceof Student;
        Color roleColor = student ? MINT : LAV;
 
        Card card = new Card(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 40, 28, 40));
        card.setLayout(new BorderLayout(0, 14));
        card.setPreferredSize(new Dimension(500, 480));
 
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        BadgeIcon icon = new BadgeIcon(true, student ? MINT : LAV, 72);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel ok = label("Login successful!", Font.BOLD, 26);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        top.add(icon);
        top.add(Box.createVerticalStrut(8));
        top.add(ok);
 
        JLabel welcome = new JLabel("<html><div style='text-align:center;width:300px'>Welcome, "
                + u.getName() + "</div></html>");
        welcome.setFont(font(Font.PLAIN, 17));
        welcome.setForeground(INK);
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
 
        JLabel roleTitle = label("Role identified", Font.PLAIN, 13, SOFT);
        roleTitle.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel role = label(u.getRole(), Font.BOLD, 24);
        role.setHorizontalAlignment(SwingConstants.CENTER);
        Card roleCard = lines(roleColor, roleTitle, role);
 
        JPanel mid = stack(welcome, roleCard);
 
        RoundButton go = new RoundButton(student ? "Go to Student Menu" : "Go to Instructor Menu", PINK);
        go.addActionListener(e -> openMenu.run());
        RoundButton back = new RoundButton("Back to Login", LEMON);
        back.addActionListener(e -> showLogin());
        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 8));
        buttons.setOpaque(false);
        buttons.add(go);
        buttons.add(back);
 
        card.add(top, BorderLayout.NORTH);
        card.add(mid, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        bg.add(card);
        setScreen(bg);
        getRootPane().setDefaultButton(go);
    }
 
    
    private static String initialOf(User u) {
        String n = u.getName();
        String first = n.contains(",") ? n.split(",")[1].trim() : n.replace("Ms. ", "").replace("Mr. ", "");
        return first.isEmpty() ? "?" : first.substring(0, 1).toUpperCase();
    }
 
    
    static final int PRESET_COUNT = 8;
    private final Map<Integer, BufferedImage> presets = new HashMap<>();
    private final Map<String, Integer> picks = new HashMap<>();   // userId -> chosen avatar (0-5) or -1
 
    private int pickOf(User u) {
        if (!picks.containsKey(u.getUserId())) {
            int idx = -1;
            File f = new File("profile_pics", u.getUserId() + ".txt");
            if (f.exists()) {
                try {
                    idx = Integer.parseInt(new String(java.nio.file.Files.readAllBytes(f.toPath())).trim());
                } catch (Exception ex) {
                    idx = -1;
                }
            }
            picks.put(u.getUserId(), idx >= 0 && idx < PRESET_COUNT ? idx : -1);
        }
        return picks.get(u.getUserId());
    }
 
    private BufferedImage avatarOf(User u) {
        int idx = pickOf(u);
        return idx < 0 ? null : presetImage(idx);
    }
 
    
    private static List<File> avatarDirs() {
        List<File> dirs = new ArrayList<>();
        dirs.add(new File("avatars"));
        try {
            File here = new File(QuizApp.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File base = here.isFile() ? here.getParentFile() : here;
            dirs.add(new File(base, "avatars"));
            if (base.getParentFile() != null) {
                dirs.add(new File(base.getParentFile(), "avatars"));
            }
        } catch (Exception ex) {
            
        }
        return dirs;
    }
 
    
    private BufferedImage presetImage(int i) {
        if (presets.containsKey(i)) {
            return presets.get(i);
        }
        BufferedImage img = null;
        search:
        for (File dir : avatarDirs()) {
            for (String name : new String[]{"avatar" + (i + 1), "avatar" + (i + 1) + "_png"}) {
                for (String ext : new String[]{".png", ".jpg", ".jpeg"}) {
                    File f = new File(dir, name + ext);
                    if (f.exists()) {
                        try {
                            BufferedImage src = ImageIO.read(f);
                            if (src != null) {
                                img = squareScale(src, 256);
                                break search;
                            }
                        } catch (Exception ex) {
                            
                        }
                    }
                }
            }
        }
        presets.put(i, img);
        return img;
    }
 
    
    private class AvatarView extends JComponent {
        private final User user;
        private final int d;
 
        AvatarView(User user, int d) {
            this.user = user;
            this.d = d;
            setPreferredSize(new Dimension(d, d));
            setMaximumSize(new Dimension(d, d));
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            BufferedImage img = avatarOf(user);
            if (img != null) {
                g2.setClip(new Ellipse2D.Double(0, 0, d, d));
                g2.drawImage(img, 0, 0, d, d, null);
                g2.setClip(null);
            } else {
                g2.setColor(LAV);
                g2.fillOval(0, 0, d, d);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, d / 2));
                FontMetrics fm = g2.getFontMetrics();
                String l = initialOf(user);
                g2.drawString(l, (d - fm.stringWidth(l)) / 2, (d + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.setColor(LAV);
            g2.setStroke(new BasicStroke(3f));
            g2.draw(new Ellipse2D.Double(1.5, 1.5, d - 3, d - 3));
            g2.dispose();
        }
    }
 
    private static BufferedImage squareScale(BufferedImage src, int px) {
        int side = Math.min(src.getWidth(), src.getHeight());
        int x = (src.getWidth() - side) / 2;
        int y = (int) ((src.getHeight() - side) * 0.15);   // keep the face: crop mostly from the top
        BufferedImage out = new BufferedImage(px, px, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src.getSubimage(x, y, side, side), 0, 0, px, px, null);
        g.dispose();
        return out;
    }
 
    private void pickAvatar(User u, int idx) {
        picks.put(u.getUserId(), idx);
        try {
            File dir = new File("profile_pics");
            dir.mkdirs();
            java.nio.file.Files.write(new File(dir, u.getUserId() + ".txt").toPath(), String.valueOf(idx).getBytes());
        } catch (Exception ex) {
            
        }
        if (sideAvatar != null) {
            sideAvatar.repaint();
        }
        setContent(profilePage(u));
    }
 
    private void removePicture(User u) {
        new File("profile_pics", u.getUserId() + ".txt").delete();
        picks.put(u.getUserId(), -1);
        if (sideAvatar != null) {
            sideAvatar.repaint();
        }
        setContent(profilePage(u));
    }
 
    
    private class AvatarTile extends JComponent {
        private final User user;
        private final int idx;
 
        AvatarTile(User user, int idx) {
            this.user = user;
            this.idx = idx;
            setPreferredSize(new Dimension(110, 110));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Avatar " + (idx + 1));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    pickAvatar(AvatarTile.this.user, AvatarTile.this.idx);
                }
            });
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            int d = Math.min(getWidth(), getHeight()) - 12;
            int x = (getWidth() - d) / 2;
            int y = (getHeight() - d) / 2;
            boolean selected = pickOf(user) == idx;
            g2.setClip(new Ellipse2D.Double(x, y, d, d));
            g2.drawImage(presetImage(idx), x, y, d, d, null);
            g2.setClip(null);
            g2.setStroke(new BasicStroke(selected ? 5f : 2f));
            g2.setColor(selected ? GOOD : LAV);
            g2.draw(new Ellipse2D.Double(x, y, d, d));
            g2.dispose();
        }
    }
 
    private JComponent profilePage(User u) {
        Card card = new Card(Color.WHITE);
        card.setLayout(new BorderLayout(24, 0));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
 
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.add(new AvatarView(u, 140));
        card.add(left, BorderLayout.WEST);
 
        JPanel info = new JPanel(new GridLayout(0, 1, 0, 6));
        info.setOpaque(false);
        info.add(label(u.getName(), Font.BOLD, 22));
        info.add(label(u.getRole(), Font.BOLD, 15, SOFT));
        info.add(label("Username: " + u.getUsername(), Font.PLAIN, 15));
        if (u instanceof Student) {
            Student st = (Student) u;
            info.add(label("Student ID: " + st.getStudentId(), Font.PLAIN, 15));
            info.add(label("Section: " + st.getSection(), Font.PLAIN, 15));
        } else if (u instanceof Instructor) {
            info.add(label("Department: " + ((Instructor) u).getDepartment(), Font.PLAIN, 15));
        }
        card.add(info, BorderLayout.CENTER);
 
        if (u instanceof Student) {
            return page("My Profile", "Your account details and badge collection",
                    stack(card, label("My Badges", Font.BOLD, 22), badgesBody((Student) u)));
        }
        return page("My Profile", "Your account details", card);
    }
 
    
    private void shell(User u, String subtitle, Map<String, Runnable> nav, Runnable first) {
        BgPanel root = new BgPanel();
        root.setLayout(new BorderLayout(18, 0));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
 
        Card side = new Card(Color.WHITE);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(320, 0));
 
        AvatarView avatar = new AvatarView(u, 84);
        sideAvatar = avatar;
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel name = new JLabel(u.getName(), SwingConstants.CENTER);
        name.setFont(font(Font.BOLD, 15));
        name.setForeground(INK);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel sub = label(subtitle, Font.PLAIN, 13, SOFT);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
 
        side.add(avatar);
        side.add(Box.createVerticalStrut(8));
        side.add(name);
        side.add(Box.createVerticalStrut(2));
        side.add(sub);
        side.add(Box.createVerticalStrut(18));
 
        int i = 0;
        for (Map.Entry<String, Runnable> e : nav.entrySet()) {
            RoundButton b = new RoundButton(e.getKey(), NAV_COLORS[i++ % NAV_COLORS.length]);
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            Runnable action = e.getValue();
            b.addActionListener(ev -> action.run());
            side.add(b);
            side.add(Box.createVerticalStrut(8));
        }
        side.add(Box.createVerticalGlue());
        RoundButton profile = new RoundButton("Profile", SKY);
        profile.setAlignmentX(Component.CENTER_ALIGNMENT);
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        profile.addActionListener(ev -> setContent(profilePage(u)));
        side.add(profile);
        side.add(Box.createVerticalStrut(8));
        RoundButton logout = new RoundButton("Logout", LEMON);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        logout.addActionListener(ev -> {
            u.logout();
            showLogin();
        });
        side.add(logout);
 
        content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        root.add(side, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setScreen(root);
        first.run();
    }
 
    
    private void studentHome(Student s) {
        Map<String, Runnable> nav = new LinkedHashMap<>();
        nav.put("Home", () -> setContent(studentWelcome(s)));
        nav.put("Take a Quiz", () -> setContent(takeQuizPage(s)));
        nav.put("Latest Result", () -> setContent(latestResultPage(s)));
        nav.put("Attempt History", () -> setContent(historyPage(s)));
        nav.put("Study Tips", () -> setContent(studyPage(s)));
        shell(s, "Section " + s.getSection(), nav, () -> setContent(studentWelcome(s)));
    }
 
    private int badgeCount(Student s) {
        int n = 0;
        for (Quiz q : quizzes) {
            if (s.getPerfectResult(q.getQuizId()) != null) {
                n++;
            }
        }
        return n;
    }
 
    private JComponent studentWelcome(Student s) {
        String full = s.getName().contains(",") ? s.getName().split(",")[1].trim() : s.getName();
        String first = full.split(" ")[0];
 
        double best = 0;
        for (Result r : s.getAttemptHistory()) {
            best = Math.max(best, r.getPercentage());
        }
 
        Card hello = lines(PINK,
                label("Hi, " + first + "!", Font.BOLD, 26),
                label("Ready to learn some Python today? Get a perfect score to collect a badge!", Font.PLAIN, 15));
 
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.add(statCard("Attempts", String.valueOf(s.getAttemptHistory().size()), MINT));
        stats.add(statCard("Best score", String.format("%.0f%%", best), LEMON));
        stats.add(statCard("Badges", badgeCount(s) + " / " + quizzes.size(), AQUA));
 
        return page("Home", "Welcome back", stack(hello, stats));
    }
 
    private Card statCard(String title, String value, Color color) {
        JLabel v = label(value, Font.BOLD, 30);
        v.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel t = label(title, Font.PLAIN, 14, SOFT);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        return lines(color, v, t);
    }
 
    private JComponent takeQuizPage(Student s) {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
 
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        chips.setOpaque(false);
        String[] levels = {"All", "Basic", "Average", "Advanced"};
        Color[] colors = {LAV, MINT, PEACH, PINK};
        for (int i = 0; i < levels.length; i++) {
            String level = levels[i];
            RoundButton b = new RoundButton(level, colors[i]);
            b.addActionListener(e -> {
                holder.removeAll();
                holder.add(quizList(s, level), BorderLayout.NORTH);
                holder.revalidate();
                holder.repaint();
            });
            chips.add(b);
        }
 
        holder.add(quizList(s, "All"), BorderLayout.NORTH);
 
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(chips, BorderLayout.NORTH);
        body.add(holder, BorderLayout.CENTER);
        return page("Take a Quiz", "Pick All, or choose a difficulty, then pick a topic", body);
    }
 
    private JComponent quizList(Student s, String level) {
        ArrayList<JComponent> cards = new ArrayList<>();
        for (Quiz q : quizzes) {
            if (!level.equals("All") && !q.getDifficultyLevel().equals(level)) {
                continue;
            }
            Card c = new Card(Color.WHITE);
            c.setLayout(new BorderLayout(16, 0));
            boolean has = s.getPerfectResult(q.getQuizId()) != null;
            c.add(new BadgeIcon(has, badgeColor(q.getQuizId()), 46), BorderLayout.WEST);
 
            JPanel text = new JPanel(new GridLayout(0, 1, 0, 2));
            text.setOpaque(false);
            text.add(label(q.getTopic(), Font.BOLD, 17));
            text.add(label(q.getDifficultyLevel() + "  |  " + q.getTotalQuestions() + " items  |  "
                    + q.getTimeLimit() + " seconds", Font.PLAIN, 13, SOFT));
            text.add(label(has ? "Badge collected!" : "Score 100% to earn this badge", Font.PLAIN, 13,
                    has ? new Color(0x3C9D68) : SOFT));
            c.add(text, BorderLayout.CENTER);
 
            RoundButton start = new RoundButton("Start", MINT);
            start.addActionListener(e -> new QuizSession(s, q));
            JPanel east = new JPanel(new GridBagLayout());
            east.setOpaque(false);
            east.add(start);
            c.add(east, BorderLayout.EAST);
            cards.add(c);
        }
        if (cards.isEmpty()) {
            cards.add(lines(LEMON, label(level.equals("All") ? "No quizzes yet. Your instructor has not uploaded any quiz."
                    : "No quizzes available for this level yet.", Font.PLAIN, 15)));
        }
        return stack(cards.toArray(new JComponent[0]));
    }
 
    
    private class QuizSession {
        private final Student student;
        private final Quiz quiz;
        private final int total;
        private final int[] answers;
        private int cur = 0;
        private boolean locked = false;
        private final TimeQuiz tq;
        private final javax.swing.Timer timer;
 
        private final JLabel progress = label("", Font.BOLD, 15, SOFT);
        private final JLabel timeLabel = label("", Font.BOLD, 18);
        private final Bar timeBar = new Bar(1, MINT);
        private final JTextArea questionText = new JTextArea();
        private final RoundButton[] opts = new RoundButton[4];
        private final JLabel feedback = label(" ", Font.BOLD, 15);
        private final RoundButton next = new RoundButton("Next", PINK);
        private final Color[] optColors = {PINK, PEACH, AQUA, LAV};
 
        QuizSession(Student student, Quiz quiz) {
            this.student = student;
            this.quiz = quiz;
            this.total = quiz.getTotalQuestions();
            this.answers = new int[total];
            this.tq = new TimeQuiz(quiz.getTimeLimit());
 
            JPanel top = new JPanel(new BorderLayout(14, 0));
            top.setOpaque(false);
            top.add(progress, BorderLayout.WEST);
            top.add(timeBar, BorderLayout.CENTER);
            top.add(timeLabel, BorderLayout.EAST);
 
            questionText.setEditable(false);
            questionText.setOpaque(false);
            questionText.setLineWrap(true);
            questionText.setWrapStyleWord(true);
            questionText.setFont(font(Font.BOLD, 20));
            questionText.setForeground(INK);
            Card qCard = new Card(Color.WHITE);
            qCard.setLayout(new BorderLayout());
            qCard.add(questionText, BorderLayout.CENTER);
 
            JPanel choices = new JPanel(new GridLayout(0, 1, 0, 10));
            choices.setOpaque(false);
            for (int i = 0; i < 4; i++) {
                final int idx = i;
                opts[i] = new RoundButton("", optColors[i]);
                opts[i].setHorizontalAlignment(SwingConstants.LEFT);
                opts[i].setFont(font(Font.PLAIN, 16));
                opts[i].addActionListener(e -> choose(idx));
                choices.add(opts[i]);
            }
 
            next.addActionListener(e -> {
                if (!locked) {
                    return;
                }
                if (cur == total - 1) {
                    finish();
                } else {
                    cur++;
                    showQuestion();
                }
            });
            JPanel bottom = new JPanel(new BorderLayout(14, 0));
            bottom.setOpaque(false);
            bottom.add(feedback, BorderLayout.CENTER);
            bottom.add(next, BorderLayout.EAST);
 
            Card head = lines(LAV, label(quiz.getTopic() + "  (" + quiz.getDifficultyLevel() + ")", Font.BOLD, 18));
            JPanel main = new JPanel(new BorderLayout(0, 14));
            main.setOpaque(false);
            JPanel north = stack(head, top, qCard);
            main.add(north, BorderLayout.NORTH);
            main.add(choices, BorderLayout.CENTER);
            main.add(bottom, BorderLayout.SOUTH);
 
            setContent(main);
            tq.startTimer();
            showQuestion();
            tick();
            timer = new javax.swing.Timer(250, e -> tick());
            quizTimer = timer;
            timer.start();
        }
 
        private void tick() {
            int left = tq.getRemainingTime();
            timeLabel.setText(String.format("%d:%02d", left / 60, left % 60));
            timeBar.setValue((double) left / quiz.getTimeLimit());
            if (tq.checkTimeLimit() && timer != null) {
                timer.stop();
                JOptionPane.showMessageDialog(QuizApp.this, "Time is up! The quiz has ended.");
                finish();
            }
        }
 
        private void showQuestion() {
            Question q = quiz.getQuestion(cur);
            progress.setText("Question " + (cur + 1) + " of " + total);
            questionText.setText(q.getQuestionText());
            String[] ch = q.getChoices();
            for (int i = 0; i < 4; i++) {
                opts[i].setText((char) ('A' + i) + ".   " + ch[i]);
                opts[i].setBase(optColors[i]);
            }
            locked = false;
            feedback.setText(" ");
            next.setText(cur == total - 1 ? "Finish" : "Next");
            next.setEnabled(false);
        }
 
        private void choose(int i) {
            if (locked) {
                return;
            }
            locked = true;
            answers[cur] = i + 1;
            Question q = quiz.getQuestion(cur);
            int correct = q.getCorrectAnswer() - 1;
            for (int k = 0; k < 4; k++) {
                if (k == correct) {
                    opts[k].setBase(GOOD);
                } else if (k == i) {
                    opts[k].setBase(BAD);
                } else {
                    opts[k].setBase(mix(optColors[k], Color.WHITE, 0.6));
                }
            }
            if (i == correct) {
                feedback.setText("Correct! Nice one!");
                feedback.setForeground(new Color(0x2E8B57));
            } else {
                feedback.setText("Not quite. The answer is: " + q.getCorrectAnswerText());
                feedback.setForeground(new Color(0xD6406A));
            }
            next.setEnabled(true);
        }
 
        private void finish() {
            timer.stop();
            tq.stopTimer();
            Result prev = latestFor(student, quiz.getQuizId());
            boolean hadBadge = student.getPerfectResult(quiz.getQuizId()) != null;
            Result r = student.recordAttempt(quiz, answers, data);
            boolean newBadge = isPerfect(r) && !hadBadge;
            showResult(student, quiz, r, prev, newBadge);
        }
    }
 
    private Result latestFor(Student s, int quizId) {
        ArrayList<Result> h = s.getAttemptHistory();
        for (int i = h.size() - 1; i >= 0; i--) {
            if (h.get(i).getQuizId() == quizId) {
                return h.get(i);
            }
        }
        return null;
    }
 
    private void showResult(Student s, Quiz quiz, Result r, Result prev, boolean newBadge) {
        double pct = r.getPercentage();
        String feedback;
        Color fill;
        if (pct >= 80) {
            feedback = "Excellent work!";
            fill = MINT;
        } else if (pct >= 60) {
            feedback = "Good job, keep practicing.";
            fill = LEMON;
        } else {
            feedback = "You need more practice on " + quiz.getTopic() + ".";
            fill = PINK;
        }
 
        JLabel title = label("Quiz Finished!", Font.BOLD, 26);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel score = label(r.getScore() + " / " + r.getTotalItems(), Font.BOLD, 54);
        score.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel percent = label(String.format("%.0f%%", pct) + "  |  " + quiz.getTopic(), Font.PLAIN, 16, SOFT);
        percent.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel fb = label(feedback, Font.BOLD, 16);
        fb.setHorizontalAlignment(SwingConstants.CENTER);
 
        ArrayList<JComponent> items = new ArrayList<>();
        items.add(lines(fill, title, score, percent, fb));
 
        if (prev != null) {
            JLabel cmp = label("Compared to your last attempt: " + r.compareAttempts(prev), Font.PLAIN, 15);
            cmp.setHorizontalAlignment(SwingConstants.CENTER);
            items.add(lines(Color.WHITE, cmp));
        }
 
        if (isPerfect(r)) {
            Card bc = new Card(LEMON);
            bc.setLayout(new BorderLayout(0, 6));
            bc.add(new BadgeIcon(true, badgeColor(quiz.getQuizId()), 96), BorderLayout.CENTER);
            JLabel t1 = label(newBadge ? "NEW BADGE UNLOCKED!" : "Perfect score again!", Font.BOLD, 18);
            t1.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel t2 = label(quiz.getTopic() + " Master", Font.PLAIN, 15, SOFT);
            t2.setHorizontalAlignment(SwingConstants.CENTER);
            JPanel tx = new JPanel(new GridLayout(0, 1));
            tx.setOpaque(false);
            tx.add(t1);
            tx.add(t2);
            bc.add(tx, BorderLayout.SOUTH);
            items.add(bc);
        }
 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        RoundButton retake = new RoundButton("Retake Quiz", MINT);
        RoundButton more = new RoundButton("More Quizzes", PEACH);
        RoundButton badges = new RoundButton("My Badges", LAV);
        retake.addActionListener(e -> new QuizSession(s, quiz));
        more.addActionListener(e -> setContent(takeQuizPage(s)));
        badges.addActionListener(e -> setContent(profilePage(s)));
        buttons.add(retake);
        buttons.add(more);
        buttons.add(badges);
        items.add(buttons);
 
        setContent(page("Results", null, stack(items.toArray(new JComponent[0]))));
 
        if (newBadge) {
            SwingUtilities.invokeLater(() -> showBadgeDialog(quiz));
        }
    }
 
    private void showBadgeDialog(Quiz quiz) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 40, 10, 40));
        JLabel t = label("You earned a badge!", Font.BOLD, 20);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel s = label("Perfect score on " + quiz.getTopic(), Font.PLAIN, 15, SOFT);
        s.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel tx = new JPanel(new GridLayout(0, 1));
        tx.setOpaque(false);
        tx.add(t);
        tx.add(s);
        p.add(tx, BorderLayout.NORTH);
        p.add(new BadgeIcon(true, badgeColor(quiz.getQuizId()), 120), BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, p, "Badge Unlocked!", JOptionPane.PLAIN_MESSAGE);
    }
 
    private JComponent latestResultPage(Student s) {
        if (s.getAttemptHistory().isEmpty()) {
            return page("Latest Result", null,
                    lines(LEMON, label("You have no quiz results yet. Take a quiz first!", Font.PLAIN, 15)));
        }
        Result r = s.getAttemptHistory().get(s.getAttemptHistory().size() - 1);
        Card c = lines(MINT,
                label(r.getTopic() + " (" + r.getDifficulty() + ")", Font.BOLD, 20),
                label(r.getScore() + " / " + r.getTotalItems() + "   -   "
                        + String.format("%.0f%%", r.getPercentage()), Font.BOLD, 34),
                label("Attempt #" + r.getAttemptNumber() + "  |  " + r.getDateTaken(), Font.PLAIN, 14, SOFT));
        return page("Latest Result", "Your most recent attempt", c);
    }
 
    private JComponent historyPage(Student s) {
        if (s.getAttemptHistory().isEmpty()) {
            return page("Attempt History", null,
                    lines(LEMON, label("You have no attempts yet.", Font.PLAIN, 15)));
        }
        List<Object[]> rows = new ArrayList<>();
        for (Result r : s.getAttemptHistory()) {
            rows.add(new Object[]{r.getDateTaken(), r.getTopic(), r.getDifficulty(), "#" + r.getAttemptNumber(),
                    r.getScore() + "/" + r.getTotalItems(), String.format("%.0f%%", r.getPercentage())});
        }
        return page("Attempt History", "Every quiz you have taken",
                tableOf(new String[]{"Date", "Topic", "Difficulty", "Attempt", "Score", "Percent"}, rows));
    }
 
    private JComponent studyPage(Student s) {
        if (s.getAttemptHistory().isEmpty()) {
            return page("Study Tips", null,
                    lines(LEMON, label("Take a quiz first so we can give you suggestions.", Font.PLAIN, 15)));
        }
        
        LinkedHashSet<Integer> seen = new LinkedHashSet<>();
        ArrayList<JComponent> items = new ArrayList<>();
        ArrayList<Result> h = s.getAttemptHistory();
        for (int i = h.size() - 1; i >= 0; i--) {
            Result r = h.get(i);
            if (!seen.add(r.getQuizId())) {
                continue;
            }
            String weak = r.getWeakTopics();
            if (!weak.equals("None")) {
                items.add(lines(PINK,
                        label("Review \"" + weak + "\"", Font.BOLD, 16),
                        label("Your latest score was " + String.format("%.0f%%", r.getPercentage())
                                + ". Try the " + r.getDifficulty() + " quiz again!", Font.PLAIN, 14)));
            }
        }
        if (items.isEmpty()) {
            items.add(lines(MINT, label("You are doing great!", Font.BOLD, 18),
                    label("Try a higher difficulty level.", Font.PLAIN, 14)));
        }
        return page("Study Tips", "Topics to practice", stack(items.toArray(new JComponent[0])));
    }
 
    
    private JComponent badgesBody(Student s) {
        int have = badgeCount(s);
        int all = quizzes.size();
        if (all == 0) {
            return lines(LEMON, label("No badges to collect yet.", Font.BOLD, 17),
                    label("Badges appear here once your instructor uploads a quiz.", Font.PLAIN, 14));
        }
 
        JLabel count = label(have + " of " + all + " badges collected", Font.BOLD, 18);
        Bar bar = new Bar(all == 0 ? 0 : (double) have / all, GOOD);
        bar.setPreferredSize(new Dimension(200, 16));
        Card summary = lines(LAV, count,
                label(have == all && all > 0 ? "You collected them all! Amazing!"
                        : "Get a perfect score on a quiz to earn its badge.", Font.PLAIN, 14), bar);
 
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        for (Quiz q : quizzes) {
            Result pr = s.getPerfectResult(q.getQuizId());
            boolean earned = pr != null;
            Card c = new Card(earned ? mix(badgeColor(q.getQuizId()), Color.WHITE, 0.6) : Color.WHITE);
            c.setLayout(new BorderLayout(0, 6));
            c.add(new BadgeIcon(earned, badgeColor(q.getQuizId()), 92), BorderLayout.CENTER);
 
            JLabel topic = new JLabel("<html><div style='text-align:center;width:150px'>" + q.getTopic() + "</div></html>");
            topic.setFont(font(Font.BOLD, 14));
            topic.setForeground(INK);
            topic.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel st = label(earned ? "Earned " + pr.getDateTaken() : "Locked - " + q.getDifficultyLevel(),
                    Font.PLAIN, 12, SOFT);
            st.setHorizontalAlignment(SwingConstants.CENTER);
            JPanel tx = new JPanel(new GridLayout(0, 1, 0, 2));
            tx.setOpaque(false);
            tx.add(topic);
            tx.add(st);
            c.add(tx, BorderLayout.SOUTH);
            grid.add(c);
        }
        // pad the last row so cards keep the same width
        while (grid.getComponentCount() % 3 != 0) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            grid.add(filler);
        }
        return stack(summary, grid);
    }
 
   
    private void instructorHome(Instructor ins) {
        Map<String, Runnable> nav = new LinkedHashMap<>();
        nav.put("Student Scores", () -> setContent(scoresPage(ins)));
        nav.put("Class Performance", () -> setContent(performancePage()));
        nav.put("Common Difficulties", () -> setContent(difficultiesPage()));
        nav.put("Create a Quiz", () -> setContent(createQuizPage(ins)));
        nav.put("Problem Sets", () -> setContent(problemSetsPage(ins)));
        shell(ins, ins.getDepartment(), nav, () -> setContent(scoresPage(ins)));
    }
 
    private JComponent scoresPage(Instructor ins) {
        List<Object[]> rows = new ArrayList<>();
        for (Student st : ins.getStudents()) {
            boolean any = false;
            for (Result r : data.getResults()) {
                if (r.getStudentId().equals(st.getStudentId())) {
                    rows.add(new Object[]{st.getName(), r.getTopic(), r.getDifficulty(), "#" + r.getAttemptNumber(),
                            r.getScore() + "/" + r.getTotalItems(), String.format("%.0f%%", r.getPercentage())});
                    any = true;
                }
            }
            if (!any) {
                rows.add(new Object[]{st.getName(), "No attempts yet", "", "", "", ""});
            }
        }
        return page("Student Scores", "Every attempt by your students",
                tableOf(new String[]{"Student", "Topic", "Difficulty", "Attempt", "Score", "Percent"}, rows));
    }
 
    private double[] averageFor(Quiz q) {
        double total = 0;
        int count = 0;
        for (Result r : data.getResults()) {
            if (r.getQuizId() == q.getQuizId()) {
                total += r.getPercentage();
                count++;
            }
        }
        return new double[]{count == 0 ? 0 : total / count, count, total};
    }
 
    private JComponent performancePage() {
        ArrayList<JComponent> items = new ArrayList<>();
        double overallTotal = 0;
        int overallCount = 0;
        int i = 0;
        for (Quiz q : quizzes) {
            double[] a = averageFor(q);
            if (a[1] == 0) {
                continue;
            }
            overallTotal += a[2];
            overallCount += (int) a[1];
            Bar bar = new Bar(a[0] / 100.0, NAV_COLORS[i++ % NAV_COLORS.length]);
            bar.setPreferredSize(new Dimension(200, 18));
            items.add(lines(Color.WHITE,
                    label(q.getTopic() + " (" + q.getDifficultyLevel() + ")", Font.BOLD, 16),
                    label("Average " + String.format("%.0f%%", a[0]) + " from " + (int) a[1] + " attempt(s)",
                            Font.PLAIN, 13, SOFT),
                    bar));
        }
        if (overallCount == 0) {
            items.add(lines(LEMON, label("No quiz attempts yet.", Font.PLAIN, 15)));
        } else {
            items.add(0, lines(MINT, label("Overall class average", Font.PLAIN, 14, SOFT),
                    label(String.format("%.0f%%", overallTotal / overallCount), Font.BOLD, 34)));
        }
        return page("Class Performance", "Average score per quiz", stack(items.toArray(new JComponent[0])));
    }
 
    private JComponent difficultiesPage() {
        ArrayList<JComponent> items = new ArrayList<>();
        for (Quiz q : quizzes) {
            double[] a = averageFor(q);
            if (a[1] > 0 && a[0] < 60) {
                items.add(lines(PINK, label(q.getTopic() + " (" + q.getDifficultyLevel() + ")", Font.BOLD, 16),
                        label("Class average: " + String.format("%.0f%%", a[0]), Font.PLAIN, 14)));
            }
        }
        if (items.isEmpty()) {
            items.add(lines(MINT, label("No topics with a low class average. Good job, class!", Font.BOLD, 16)));
        }
        return page("Common Difficulties", "Topics with an average below 60%", stack(items.toArray(new JComponent[0])));
    }
 
    
    private int nextQuizId() {
        int max = 0;
        for (Quiz q : quizzes) {
            max = Math.max(max, q.getQuizId());
        }
        for (Result r : data.getResults()) {
            max = Math.max(max, r.getQuizId());
        }
        return max + 1;
    }
 
    private static String esc(String t) {
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
 
    
    private JComponent problemSetPage(Instructor ins, Quiz quiz) {
        int n = quiz.getTotalQuestions();
        ArrayList<JComponent> items = new ArrayList<>();
 
        
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < n; i++) {
            key.append(i + 1).append("-").append((char) ('A' + quiz.getQuestion(i).getCorrectAnswer() - 1)).append("     ");
        }
        items.add(lines(LAV,
                label(quiz.getTopic(), Font.BOLD, 22),
                label(quiz.getDifficultyLevel() + "  |  " + n + " items  |  " + quiz.getTimeLimit() + " seconds",
                        Font.PLAIN, 14),
                label("Answer key:   " + key.toString().trim(), Font.BOLD, 15)));
 
        for (int i = 0; i < n; i++) {
            Question q = quiz.getQuestion(i);
            Card c = new Card(Color.WHITE);
            c.setLayout(new GridLayout(0, 1, 0, 6));
            JLabel qt = new JLabel("<html><div style='width:520px'>" + (i + 1) + ". " + esc(q.getQuestionText())
                    + "</div></html>");
            qt.setFont(font(Font.BOLD, 16));
            qt.setForeground(INK);
            c.add(qt);
 
            String[] ch = q.getChoices();
            for (int k = 0; k < ch.length; k++) {
                boolean right = (k + 1) == q.getCorrectAnswer();
                JLabel row = new JLabel("<html><div style='width:480px'>" + (char) ('A' + k) + ".   " + esc(ch[k])
                        + (right ? "   <b>(Correct answer)</b>" : "") + "</div></html>");
                row.setFont(font(Font.PLAIN, 15));
                row.setForeground(INK);
                if (right) {
                    Card hl = new Card(mix(GOOD, Color.WHITE, 0.45));
                    hl.setBorder(new EmptyBorder(6, 12, 6, 12));
                    hl.setLayout(new BorderLayout());
                    hl.add(row, BorderLayout.CENTER);
                    c.add(hl);
                } else {
                    row.setBorder(new EmptyBorder(6, 12, 6, 12));
                    c.add(row);
                }
            }
            items.add(c);
        }
 
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttons.setOpaque(false);
        RoundButton another = new RoundButton("Create Another Quiz", MINT);
        RoundButton all = new RoundButton("All Problem Sets", PEACH);
        another.addActionListener(e -> setContent(createQuizPage(ins)));
        all.addActionListener(e -> setContent(problemSetsPage(ins)));
        buttons.add(another);
        buttons.add(all);
        items.add(buttons);
 
        return page("Problem Set", "Questions with the answer key", stack(items.toArray(new JComponent[0])));
    }
 
    
    private JComponent problemSetsPage(Instructor ins) {
        ArrayList<JComponent> items = new ArrayList<>();
        for (Quiz q : createdQuizzes) {
            Card c = new Card(Color.WHITE);
            c.setLayout(new BorderLayout(16, 0));
            JPanel text = new JPanel(new GridLayout(0, 1, 0, 2));
            text.setOpaque(false);
            text.add(label(q.getTopic(), Font.BOLD, 17));
            text.add(label(q.getDifficultyLevel() + "  |  " + q.getTotalQuestions() + " items  |  "
                    + q.getTimeLimit() + " seconds", Font.PLAIN, 13, SOFT));
            c.add(text, BorderLayout.CENTER);
            RoundButton view = new RoundButton("View Answer Key", LAV);
            view.addActionListener(e -> setContent(problemSetPage(ins, q)));
            JPanel east = new JPanel(new GridBagLayout());
            east.setOpaque(false);
            east.add(view);
            c.add(east, BorderLayout.EAST);
            items.add(c);
        }
        if (items.isEmpty()) {
            RoundButton create = new RoundButton("Create a Quiz", MINT);
            create.addActionListener(e -> setContent(createQuizPage(ins)));
            JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            btn.setOpaque(false);
            btn.add(create);
            items.add(lines(LEMON,
                    label("No problem sets yet.", Font.BOLD, 17),
                    label("Quizzes you create will be saved here with their questions and answer key.",
                            Font.PLAIN, 14)));
            items.add(btn);
        }
        return page("Problem Sets", "Quizzes you created for your students",
                stack(items.toArray(new JComponent[0])));
    }
 
    
    private static class QForm {
        final JTextField text = field();
        final JTextField[] choices = new JTextField[4];
        final JRadioButton[] correct = new JRadioButton[4];
 
        Card build(int number) {
            Card c = new Card(Color.WHITE);
            c.setLayout(new GridLayout(0, 1, 0, 6));
            c.add(label("Question " + number, Font.BOLD, 16));
            c.add(text);
            ButtonGroup g = new ButtonGroup();
            for (int i = 0; i < 4; i++) {
                choices[i] = field();
                correct[i] = new JRadioButton();
                correct[i].setOpaque(false);
                correct[i].setToolTipText("Mark as the correct answer");
                g.add(correct[i]);
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setOpaque(false);
                row.add(correct[i], BorderLayout.WEST);
                row.add(choices[i], BorderLayout.CENTER);
                c.add(row);
            }
            correct[0].setSelected(true);
            c.add(label("Select the circle next to the correct choice.", Font.PLAIN, 12, SOFT));
            return c;
        }
    }
 
    private JComponent createQuizPage(Instructor ins) {
        JTextField topic = field();
        JComboBox<String> diff = new JComboBox<>(new String[]{"Basic", "Average", "Advanced"});
        JSpinner time = new JSpinner(new SpinnerNumberModel(60, 10, 3600, 10));
        JSpinner num = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        JPanel qHolder = new JPanel(new BorderLayout());
        qHolder.setOpaque(false);
        ArrayList<QForm> forms = new ArrayList<>();
        RoundButton setup = new RoundButton("Set up questions", MINT);
        RoundButton save = new RoundButton("Save quiz", PINK);
        save.setVisible(false);
 
        Card details = new Card(LAV);
        details.setLayout(new GridLayout(0, 2, 12, 8));
        details.add(label("Topic", Font.BOLD, 14));
        details.add(topic);
        details.add(label("Difficulty", Font.BOLD, 14));
        details.add(diff);
        details.add(label("Time limit (seconds, 10-3600)", Font.BOLD, 14));
        details.add(time);
        details.add(label("Number of questions (1-10)", Font.BOLD, 14));
        details.add(num);
 
        setup.addActionListener(e -> {
            forms.clear();
            ArrayList<JComponent> cards = new ArrayList<>();
            int n = (Integer) num.getValue();
            for (int i = 1; i <= n; i++) {
                QForm f = new QForm();
                forms.add(f);
                cards.add(f.build(i));
            }
            qHolder.removeAll();
            qHolder.add(stack(cards.toArray(new JComponent[0])), BorderLayout.NORTH);
            qHolder.revalidate();
            qHolder.repaint();
            save.setVisible(true);
        });
 
        save.addActionListener(e -> {
            if (topic.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a topic.");
                return;
            }
            for (QForm f : forms) {
                boolean empty = f.text.getText().trim().isEmpty();
                for (JTextField c : f.choices) {
                    empty |= c.getText().trim().isEmpty();
                }
                if (empty) {
                    JOptionPane.showMessageDialog(this, "Please fill in every question and choice.");
                    return;
                }
            }
            Quiz quiz = new Quiz(nextQuizId(), topic.getText().trim(), (String) diff.getSelectedItem(),
                    (Integer) time.getValue());
            for (QForm f : forms) {
                String[] ch = new String[4];
                int correct = 1;
                for (int i = 0; i < 4; i++) {
                    ch[i] = f.choices[i].getText().trim();
                    if (f.correct[i].isSelected()) {
                        correct = i + 1;
                    }
                }
                quiz.addQuestion(new Question(f.text.getText().trim(), ch, correct));
            }
            quizzes.add(quiz);
            createdQuizzes.add(quiz);
            JOptionPane.showMessageDialog(this, "Quiz \"" + quiz.getTopic() + "\" was created successfully!");
            setContent(problemSetPage(ins, quiz));
        });
 
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        btns.add(setup);
        btns.add(save);
        return page("Create a Quiz", "Students will see it right away", stack(details, btns, qHolder));
    }
}
 




