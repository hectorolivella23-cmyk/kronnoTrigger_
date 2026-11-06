package GUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.MatteBorder;

/**
 * Vista mensual de calendario para KronnoTrigger.
 *
 * Idea central: un mes siempre cabe en una cuadrícula de 6 semanas x 7 días.
 * Se calcula qué fecha cae en la primera celda (el lunes de la semana en que
 * empieza el mes) y desde ahí se van sumando días. java.time hace todo el
 * trabajo de fechas (años bisiestos, meses de 28/30/31 días, etc.).
 *
 * Uso dentro de un JFrame:
 *     CalendarPanel cal = new CalendarPanel();
 *     cal.setOnDayClicked(fecha -> new event().setVisible(true));
 *     getContentPane().add(cal, BorderLayout.CENTER);
 */
public class CalendarPanel extends JPanel {

    // ---- Paleta KronnoTrigger (la misma de login/register/event) ----
    private static final Color NARANJA      = new Color(249, 111, 11);
    private static final Color NARANJA_SUAVE = new Color(254, 236, 222);
    private static final Color FONDO        = Color.WHITE;
    private static final Color GRIS_TEXTO   = new Color(153, 153, 153);
    private static final Color GRIS_LINEA   = new Color(230, 230, 230);
    private static final Color TEXTO        = new Color(40, 40, 40);
    private static final Color FUERA_DE_MES = new Color(250, 250, 250);

    private static final Locale ES = Locale.forLanguageTag("es-CO");
    private static final DayOfWeek PRIMER_DIA = DayOfWeek.MONDAY; // cambia a SUNDAY si prefieres

    private YearMonth mesActual = YearMonth.now();
    private final JLabel lblMes = new JLabel();
    private final JPanel grid = new JPanel(new GridLayout(6, 7));

    /** Eventos de prueba / reales: fecha -> lista de títulos. */
    private final Map<LocalDate, List<String>> eventos = new HashMap<>();
    private Consumer<LocalDate> onDayClicked = fecha -> {};

    public CalendarPanel() {
        setLayout(new BorderLayout());
        setBackground(FONDO);
        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.add(crearFilaDias(), BorderLayout.NORTH);
        grid.setBackground(FONDO);
        centro.add(grid, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        refrescar();
    }

    // ------------------------------------------------------------------ API

    /** Qué hacer cuando el usuario hace clic en un día (p.ej. abrir "Nuevo evento"). */
    public void setOnDayClicked(Consumer<LocalDate> accion) {
        this.onDayClicked = Objects.requireNonNull(accion);
    }

    public void agregarEvento(LocalDate fecha, String titulo) {
        eventos.computeIfAbsent(fecha, f -> new ArrayList<>()).add(titulo);
        refrescar();
    }

    public void irAMes(YearMonth mes) {
        mesActual = mes;
        refrescar();
    }

    public YearMonth getMesActual() {
        return mesActual;
    }

    // --------------------------------------------------------- construcción

    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        lblMes.setFont(new Font("Roboto Medium", Font.PLAIN, 22));
        lblMes.setForeground(TEXTO);

        JButton btnAnterior = botonPlano("◀");
        JButton btnSiguiente = botonPlano("▶");
        JButton btnHoy = botonPlano("Hoy");
        btnAnterior.addActionListener(e -> irAMes(mesActual.minusMonths(1)));
        btnSiguiente.addActionListener(e -> irAMes(mesActual.plusMonths(1)));
        btnHoy.addActionListener(e -> irAMes(YearMonth.now()));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        izquierda.add(btnAnterior);
        izquierda.add(btnSiguiente);
        izquierda.add(Box.createHorizontalStrut(8));
        izquierda.add(lblMes);

        JButton btnNuevo = new JButton("+ Nuevo evento");
        btnNuevo.setBackground(NARANJA);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> onDayClicked.accept(LocalDate.now()));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        derecha.add(btnHoy);
        derecha.add(btnNuevo);

        header.add(izquierda, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);
        return header;
    }

    private JPanel crearFilaDias() {
        JPanel fila = new JPanel(new GridLayout(1, 7));
        fila.setBackground(FONDO);
        fila.setBorder(new MatteBorder(0, 0, 1, 0, GRIS_LINEA));
        for (int i = 0; i < 7; i++) {
            DayOfWeek dia = PRIMER_DIA.plus(i);
            String nombre = dia.getDisplayName(TextStyle.SHORT, ES).replace(".", "").toUpperCase(ES);
            JLabel lbl = new JLabel(nombre, SwingConstants.CENTER);
            lbl.setFont(new Font("Roboto Medium", Font.PLAIN, 12));
            lbl.setForeground(GRIS_TEXTO);
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            fila.add(lbl);
        }
        return fila;
    }

    /** Reconstruye las 42 celdas para el mes actual. */
    private void refrescar() {
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, ES);
        lblMes.setText(Character.toUpperCase(nombreMes.charAt(0)) + nombreMes.substring(1)
                + " " + mesActual.getYear());

        grid.removeAll();

        // Primera celda: retrocede desde el día 1 hasta el PRIMER_DIA de esa semana
        LocalDate primeroDelMes = mesActual.atDay(1);
        int desfase = (primeroDelMes.getDayOfWeek().getValue() - PRIMER_DIA.getValue() + 7) % 7;
        LocalDate fecha = primeroDelMes.minusDays(desfase);

        for (int i = 0; i < 42; i++) {
            grid.add(crearCelda(fecha, i));
            fecha = fecha.plusDays(1);
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel crearCelda(LocalDate fecha, int indice) {
        boolean delMes = YearMonth.from(fecha).equals(mesActual);
        boolean esHoy = fecha.equals(LocalDate.now());

        JPanel celda = new JPanel();
        celda.setLayout(new BoxLayout(celda, BoxLayout.Y_AXIS));
        celda.setBackground(delMes ? FONDO : FUERA_DE_MES);
        // Línea derecha e inferior para dibujar la cuadrícula (sin doble borde)
        int derecha = (indice % 7 == 6) ? 0 : 1;
        celda.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, derecha, GRIS_LINEA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));

        JLabel numero = new DiaLabel(String.valueOf(fecha.getDayOfMonth()), esHoy);
        numero.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
        numero.setForeground(esHoy ? Color.WHITE : (delMes ? TEXTO : GRIS_TEXTO));
        numero.setAlignmentX(LEFT_ALIGNMENT);
        celda.add(numero);
        celda.add(Box.createVerticalStrut(4));

        // Hasta 3 eventos visibles; el resto se resume en "+N más"
        List<String> lista = eventos.getOrDefault(fecha, List.of());
        for (int i = 0; i < Math.min(3, lista.size()); i++) {
            celda.add(chipEvento(lista.get(i)));
            celda.add(Box.createVerticalStrut(2));
        }
        if (lista.size() > 3) {
            JLabel mas = new JLabel("+" + (lista.size() - 3) + " más");
            mas.setFont(new Font("Roboto", Font.PLAIN, 11));
            mas.setForeground(GRIS_TEXTO);
            mas.setAlignmentX(LEFT_ALIGNMENT);
            celda.add(mas);
        }

        celda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color fondoNormal = celda.getBackground();
        celda.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { celda.setBackground(NARANJA_SUAVE); }
            @Override public void mouseExited(MouseEvent e)  { celda.setBackground(fondoNormal); }
            @Override public void mouseClicked(MouseEvent e) { onDayClicked.accept(fecha); }
        });
        return celda;
    }

    private JLabel chipEvento(String titulo) {
        JLabel chip = new JLabel(" " + titulo);
        chip.setOpaque(true);
        chip.setBackground(NARANJA);
        chip.setForeground(Color.WHITE);
        chip.setFont(new Font("Roboto", Font.PLAIN, 11));
        chip.setAlignmentX(LEFT_ALIGNMENT);
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return chip;
    }

    private static JButton botonPlano(String texto) {
        JButton b = new JButton(texto);
        b.setFocusPainted(false);
        b.setBackground(FONDO);
        b.setForeground(NARANJA);
        b.setFont(new Font("Roboto Medium", Font.PLAIN, 13));
        return b;
    }

    /** Número del día; si es hoy, se dibuja dentro de un círculo naranja. */
    private static class DiaLabel extends JLabel {
        private final boolean resaltado;

        DiaLabel(String texto, boolean resaltado) {
            super(texto, SwingConstants.CENTER);
            this.resaltado = resaltado;
            Dimension d = new Dimension(26, 26);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (resaltado) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(NARANJA);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------- prueba independiente

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("KronnoTrigger - Calendario");
            CalendarPanel cal = new CalendarPanel();
            LocalDate hoy = LocalDate.now();
            cal.agregarEvento(hoy, "Reunión de equipo");
            cal.agregarEvento(hoy.plusDays(2), "Entrega GUI");
            cal.agregarEvento(hoy.plusDays(5), "Revisión");
            cal.agregarEvento(hoy.plusDays(5), "Pruebas");
            cal.setOnDayClicked(fecha -> JOptionPane.showMessageDialog(f, "Día: " + fecha));
            f.add(cal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setMinimumSize(new Dimension(1024, 680));
            f.setSize(1280, 800);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
