/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Pablo Azogue
 */
public class EmpleadoDAO {

    private File fichero;
    private DataInputStream reader;
    private DataOutputStream writer;

    public EmpleadoDAO(String ruta) {
        this.fichero = new File(ruta);
    }

    public void abrirLectura() throws FileNotFoundException {
        this.reader = new DataInputStream(new FileInputStream(this.fichero));
    }

    public void abrirEscritura() throws FileNotFoundException {
        this.writer = new DataOutputStream(new FileOutputStream(this.fichero));
    }

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
            this.reader = null;
        }

        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
        }
    }

    public Empleado leer() throws IOException {
        Empleado empleado;
        try {
            empleado = new Empleado(reader.readUTF(), Sexo.valueOf(String.valueOf(reader.readChar())), reader.readFloat(), this.leerFecha(),
                    TipoEmpleado.valueOf(String.valueOf(reader.readChar())), Provincia.values()[reader.readByte() - 1]);
        } catch (EOFException eofe) {
            empleado = null;
        }
        return empleado;
    }

    public void escribir(Empleado empleado) throws IOException, IllegalArgumentException {
        if (empleado.getNombreYApe().length() > 30) {
            throw new IllegalArgumentException("El nombre y apellidos no pueden superar los 30 caracteres");
        } else if (empleado.getSalario() <= 0 || empleado.getSalario() >= 10000f) {
            throw new IllegalArgumentException("El salario debe estar entre 0.01 y 9999.99");
        } else {
            writer.writeUTF(empleado.getNombreYApe());
            writer.writeChar(empleado.getSexo().getCodigo());
            writer.writeFloat(empleado.getSalario());
            writer.writeShort(empleado.getFechaIngreso().getAnio());
            writer.writeByte(empleado.getFechaIngreso().getMes());
            writer.writeByte(empleado.getFechaIngreso().getDia());
            writer.writeChar(empleado.getTipoEmpleado().getCodigo());
            writer.writeByte(empleado.getProvincia().getCodigo());
        }
    }

    private Fecha leerFecha() throws IOException {
        Fecha fecha;
        fecha = new Fecha(reader.readShort(), reader.readByte(), reader.readByte());
        return fecha;
    }
}
