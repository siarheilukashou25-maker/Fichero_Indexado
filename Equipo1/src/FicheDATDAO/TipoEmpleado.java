/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

/**
 *
 * @author Pablo Azogue
 */
public enum TipoEmpleado {
    C('C',"A comisión"),
    F('F',"Fijo"),
    D('D',"A domicilio");

    private char codigo;
    private String descripcion;

    private TipoEmpleado(char codigo, String descripcion) {
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
