package indexado;

/**
 *
 * @author LukashouSiarhei
 */
public class ClaveDuplicadaException extends Exception {

    public ClaveDuplicadaException(Comparable clave) {
        super("Clave duplicada " + clave);
    }
}
