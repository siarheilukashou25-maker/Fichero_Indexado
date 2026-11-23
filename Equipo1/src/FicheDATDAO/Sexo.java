/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

/**
 *
 * @author Pablo Azogue
 */
public enum Sexo {
    H('H',"Hombre"),
    M('M',"Mujer");

    private char codigo;
    private String descripcion;

    private Sexo(char codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public char getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
