
package fiche;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Santiago González
 */
public class FicheDAO implements FormatoRegistro {

        public int getLongitudClave() {
        return FicheRegistro.LONGITUD_DNI * 2;
    }

        public int getLongitudDatos() {
        return FicheRegistro.LONGITUD_NOMBRE * 2 + 2 + 4 + 2 + 1 + 1 + 2 + 1;
    }

        public void escribirClave(DataOutput salida, String clave) throws IOException {
        escribirTexto(salida, clave, FicheRegistro.LONGITUD_DNI);
    }

        public String leerClave(DataInput entrada) throws IOException {
        return leerTexto(entrada, FicheRegistro.LONGITUD_DNI);
    }

        public void escribirDatos(DataOutput salida, RegistroIndexado registro) throws IOException {
        FicheRegistro reg = (FicheRegistro) registro;
        escribirTexto(salida, reg.getNombreApe(), FicheRegistro.LONGITUD_NOMBRE);
        salida.writeChar(reg.getSexo().getCodigo());
        salida.writeFloat(reg.getSalario());
        salida.writeShort(reg.getFechaIngreso().getAnio());
        salida.writeByte(reg.getFechaIngreso().getMes());
        salida.writeByte(reg.getFechaIngreso().getDia());
        salida.writeChar(reg.getTipoEmpleado().getCodigo());
        salida.writeByte(reg.getProvincia().getCodigo());
    }

        public FicheRegistro leerDatos(DataInput entrada, String clave) throws IOException {
        FicheRegistro registro;
        String nombre = leerTexto(entrada, FicheRegistro.LONGITUD_NOMBRE);
        char sexo = entrada.readChar();
        float salario = entrada.readFloat();
        short anio = entrada.readShort();
        byte mes = entrada.readByte();
        byte dia = entrada.readByte();
        char tipo = entrada.readChar();
        byte provincia = entrada.readByte();
        try {
            registro = new FicheRegistro(clave, nombre, Sexo.desdeCodigo(sexo), salario,
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
