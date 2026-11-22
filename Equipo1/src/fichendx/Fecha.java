package fichendx;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Santiago González
 */
public class Fecha {

    private final short anio;
    private final byte mes;
    private final byte dia;

    public Fecha(short anio, byte mes, byte dia) {
        if (anio < 1900 || anio > 9999) {
            throw new IllegalArgumentException("Anio fuera de rango: " + anio);
        }
        GregorianCalendar calendario = new GregorianCalendar();
        calendario.setLenient(false);
        calendario.clear();
        calendario.set(anio, mes - 1, dia);
        try {
            calendario.getTime();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Fecha inexistente: " + dia + "/" + mes + "/" + anio);
        }
        this.anio = anio;
        this.mes = mes;
        this.dia = dia;
    }

    public static Fecha hoy() {
        GregorianCalendar calendario = new GregorianCalendar();
        Fecha hoy = new Fecha((short) calendario.get(GregorianCalendar.YEAR),
                (byte) (calendario.get(GregorianCalendar.MONTH) + 1),
                (byte) calendario.get(GregorianCalendar.DAY_OF_MONTH));
        return hoy;
    }

    public static Fecha parse(String texto) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        ParsePosition posicion = new ParsePosition(0);
        Date date = formato.parse(texto, posicion);
        if (date == null || posicion.getIndex() != texto.length()) {
            throw new ParseException("No existe o no tiene formato dd/mm/aaaa: " + texto, posicion.getErrorIndex());
        }
        GregorianCalendar calendario = new GregorianCalendar();
        calendario.setTime(date);
        Fecha fecha;
        try {
            fecha = new Fecha((short) calendario.get(GregorianCalendar.YEAR),
                    (byte) (calendario.get(GregorianCalendar.MONTH) + 1),
                    (byte) calendario.get(GregorianCalendar.DAY_OF_MONTH));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        return fecha;
    }

    public boolean esPosterior(Fecha otra) {
        boolean posterior;
        if (this.anio != otra.anio) {
            posterior = this.anio > otra.anio;
        } else if (this.mes != otra.mes) {
            posterior = this.mes > otra.mes;
        } else {
            posterior = this.dia > otra.dia;
        }
        return posterior;
    }

    public int aniosHasta(Fecha hasta) {
        int anios = hasta.anio - this.anio;
        if (hasta.mes < this.mes || (hasta.mes == this.mes && hasta.dia < this.dia)) {
            anios--;
        }
        if (anios < 0) {
            anios = 0;
        }
        return anios;
    }

    public short getAnio() {
        return this.anio;
    }

    public byte getMes() {
        return this.mes;
    }

    public byte getDia() {
        return this.dia;
    }

    @Override
    public String toString() {
        String texto = String.format("%02d/%02d/%04d", this.dia, this.mes, this.anio);
        return texto;
    }
}
