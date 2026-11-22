
package fichendx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import indexado.FormatoRegistro;
import indexado.RegistroIndexado;

/**
 *
 * @author Santiago González
 */
public class FichendxDAO implements FormatoRegistro {

        public short getLongitudClave() {
        return (short) (FichendxRegistro.LONGITUD_DNI * 2);
    }

        public short getLongitudDatos() {
        return (short) (FichendxRegistro.LONGITUD_NOMBRE * 2 + 2 + 4 + 2 + 1 + 1 + 2 + 1);
    }

        public void escribirClave(DataOutput salida, Comparable clave) throws IOException {
        escribirTexto(salida, (String) clave, FichendxRegistro.LONGITUD_DNI);
    }

        public String leerClave(DataInput entrada) throws IOException {
        return leerTexto(entrada, FichendxRegistro.LONGITUD_DNI);
    }

        public void escribirDatos(DataOutput salida, RegistroIndexado registro) throws IOException {
        FichendxRegistro reg = (FichendxRegistro) registro;
        escribirTexto(salida, reg.getNombreApe(), FichendxRegistro.LONGITUD_NOMBRE);
        salida.writeChar(reg.getSexo().getCodigo());
        salida.writeFloat(reg.getSalario());
        salida.writeShort(reg.getFechaIngreso().getAnio());
        salida.writeByte(reg.getFechaIngreso().getMes());
        salida.writeByte(reg.getFechaIngreso().getDia());
        salida.writeChar(reg.getTipoEmpleado().getCodigo());
        salida.writeByte(reg.getProvincia().getCodigo());
    }

        public FichendxRegistro leerDatos(DataInput entrada, Comparable clave) throws IOException {
        FichendxRegistro registro;
        String nombre = leerTexto(entrada, FichendxRegistro.LONGITUD_NOMBRE);
        char sexo = entrada.readChar();
        float salario = entrada.readFloat();
        short anio = entrada.readShort();
        byte mes = entrada.readByte();
        byte dia = entrada.readByte();
        char tipo = entrada.readChar();
        byte provincia = entrada.readByte();
        try {
            registro = new FichendxRegistro((String) clave, nombre, Sexo.desdeCodigo(sexo), salario,
                    new Fecha(anio, mes, dia), TipoEmpleado.desdeCodigo(tipo), Provincia.desdeCodigo(provincia));
        } catch (IllegalArgumentException e) {
            throw new IOException("Registro con datos no validos: " + e.getMessage(), e);
        }
        return registro;
    }

    private void escribirTexto(DataOutput salida, String texto, byte longitud) throws IOException {
        for (int i = 0; i < longitud; i++) {
            char c = ' ';
            if (i < texto.length()) {
                c = texto.charAt(i);
            }
            salida.writeChar(c);
        }
    }

    private String leerTexto(DataInput entrada, byte longitud) throws IOException {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            texto.append(entrada.readChar());
        }
        return texto.toString().trim();
    }
}
