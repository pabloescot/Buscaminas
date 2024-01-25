package es.riberadeltajo.buscaminas;

public class Mina {
    private String nombre;
    private int imagen;

    public Mina(String nombre, int imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }
    public Mina() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int nuevaImagen) {
        this.imagen = nuevaImagen;
    }
}
