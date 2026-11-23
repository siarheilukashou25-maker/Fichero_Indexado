/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

/**
 *
 * @author Pablo Azogue
 */
public enum Provincia {
    ALMERIA(1, "Almería"),
    CADIZ(2, "Cádiz"),
    CORDOBA(3, "Córdoba"),
    GRANADA(4, "Granada"),
    HUELVA(5, "Huelva"),
    JAEN(6, "Jaén"),
    MALAGA(7, "Málaga"),
    SEVILLA(8, "Sevilla");

    private int codigo;
    private String descripcion;

    private Provincia(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

