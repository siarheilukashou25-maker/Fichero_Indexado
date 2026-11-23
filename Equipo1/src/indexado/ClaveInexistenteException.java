package indexado;

/**
 *
 * @author LukashouSiarhei
 */
public class ClaveInexistenteException extends Exception {

    public ClaveInexistenteException(Comparable clave) {
        super("No existe la clave " + clave);
    }
}
