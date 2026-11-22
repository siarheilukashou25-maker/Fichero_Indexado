
package Indexado;
/**
 *
 * @author Pablo Azogue
 */
public enum ModoApertura {

    CREACION("rw"),
    LECTURA("r"),
    LECTURA_ESCRITURA("rw");

    private String modo;

    private ModoApertura(String modo) {
        this.modo = modo;
    }

    public String getModo() {
        return modo;
    }
}
