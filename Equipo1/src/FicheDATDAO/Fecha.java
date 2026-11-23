/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

/**
 *
 * @author Pablo Azogue
 */
import java.util.GregorianCalendar;

public class Fecha {

    private short anio;
    private byte mes;
    private byte dia;

    public Fecha(short anio, byte mes, byte dia) {
        comprobarFecha(anio, mes, dia);
        this.anio = anio;
        this.mes = mes;
        this.dia = dia;
    }

    private void comprobarFecha(short anio, byte mes, byte dia) throws IllegalArgumentException {
        GregorianCalendar fecha = new GregorianCalendar(anio, mes - 1, dia);
        fecha.setLenient(false);
        fecha.getTime();
    }

    public short getAnio() {
        return anio;
    }

    public byte getMes() {
        return mes;
    }

    public byte getDia() {
        return dia;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public void setMes(byte mes) {
        this.mes = mes;
    }

    public void setDia(byte dia) {
        this.dia = dia;
    }
    @Override
    public String toString(){
        return this.anio+"/"+this.mes+"/"+this.dia;
    }
}
