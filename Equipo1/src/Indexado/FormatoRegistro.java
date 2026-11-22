
package Indexado;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Elías Pére Arroyo
 */
public interface  FormatoRegistro {
    public short getLongitudClave();
    public short getLongitudDatos();
    public void escribirClave(DataOutput salida, Comparable clave)throws IOException; 
    public Comparable leerClave(DataInput entrada)throws IOException;
    public void escribirDatos(DataOutput salida,RegistroIndexado registro)throws IOException;
    public RegistroIndexado leerDatos(DataInput entrada,Comparable clave)throws IOException;    
    
}
