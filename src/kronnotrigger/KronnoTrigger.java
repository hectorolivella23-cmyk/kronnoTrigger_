package kronnotrigger;
import controlador.GestorUsuarios;
import GUI.login;
import persistencia.EscrituraLectura;

public class KronnoTrigger {

    public static void main(String[] args) {
        EscrituraLectura archivos = new EscrituraLectura();
        GestorUsuarios gestor = new GestorUsuarios();
        java.awt.EventQueue.invokeLater(() -> new login(gestor).setVisible(true));
    }
}
