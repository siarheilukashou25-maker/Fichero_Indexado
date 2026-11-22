
package indexado;

/**
 *
 * @author Elías Pérez Arroyo
 */
public abstract class RegistroIndexado {
    
    private final Comparable clave;
    
    protected RegistroIndexado(Comparable clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula");
        }
        this.clave = clave;
    }
    
    public Comparable getClave() { 
        return this.clave; }
}
