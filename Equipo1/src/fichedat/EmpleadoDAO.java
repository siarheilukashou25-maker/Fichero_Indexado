package fichedat;

import fiche.Fecha;
import fiche.FicheRegistro;
import fiche.Provincia;
import fiche.Sexo;
import fiche.TipoEmpleado;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Pablo Azogue
 */
public class EmpleadoDAO {

    public static final byte LONGITUD_REGISTRO = 73;

    private File fichero;
    private DataInputStream reader;

    public EmpleadoDAO(File fichero) {
        this.fichero = fichero;
    }

    public void abrirLectura() throws FileNotFoundException {
        this.reader = new DataInputStream(new BufferedInputStream(new FileInputStream(this.fichero)));
    }

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }
    }

    public Empleado leer() throws IOException {
        Empleado empleado;
        String nombre;
        char sexo;
        float salario;
        Fecha fecha;
        char tipo;
        byte provincia;

        try {
            nombre = leerNombre();
            sexo = reader.readChar();
            salario = reader.readFloat();
            fecha = this.leerFecha();
            tipo = reader.readChar();
            provincia = reader.readByte();
            empleado = new Empleado(nombre, Sexo.desdeCodigo(sexo), salario, fecha,
                    TipoEmpleado.desdeCodigo(tipo), Provincia.desdeCodigo(provincia));
        } catch (EOFException eofe) {
            empleado = null;
        } catch (IllegalArgumentException iae) {
            throw new IOException("Registro no valido " + iae.getMessage());
        }
        return empleado;
    }

    private String leerNombre() throws IOException {
        StringBuilder nombre;
        byte i;

        nombre = new StringBuilder();
        for (i = 0; i < FicheRegistro.LONGITUD_NOMBRE; i++) {
            nombre.append(reader.readChar());
        }
        return nombre.toString().trim();
    }

    private Fecha leerFecha() throws IOException {
        Fecha fecha;
        fecha = new Fecha(reader.readShort(), reader.readByte(), reader.readByte());
        return fecha;
    }
}
