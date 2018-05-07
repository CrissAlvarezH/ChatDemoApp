package miercoles.dsl.chatdemo;

public class Mensaje {
    public static final int ENVIADO = 1;
    public static final int RECIVIDO = 2;

    private String nombre, mensaje;
    private int tipo;

    public Mensaje(String nombre, String mensaje, int tipo) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
