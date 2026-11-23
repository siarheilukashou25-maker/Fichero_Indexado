/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FicheDATDAO;

import java.util.Calendar;

/**
 *
 * @author Pablo Azogue 
 */
public class Empleado {
    private String nombreYApe;
    private Sexo sexo;
    private float salario;
    private Fecha fechaIngreso;
    private TipoEmpleado tipoEmpleado;
    private Provincia provincia;
    
    public Empleado(String nombreYApe , Sexo sexo , float salario , Fecha fechaIngreso, TipoEmpleado tipoEmpleado, Provincia provincia){
        this.nombreYApe = nombreYApe;
        this.sexo=sexo;
        this.salario=salario;
        this.fechaIngreso=fechaIngreso;
        this.tipoEmpleado=tipoEmpleado;
        this.provincia=provincia;
    }

    public String getNombreYApe() {
        return nombreYApe;
    }

    public void setNombreYApe(String nombreYApe) {
        this.nombreYApe = nombreYApe;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public float getSalario() {
        return salario;
    }

    public void setSalario(float salario) {
        this.salario = salario;
    }

    public Fecha getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Fecha fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(TipoEmpleado tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }
    
    public byte getAntiguedad(){
        Calendar hoy = Calendar.getInstance();
        byte antiguedad;
        
        antiguedad =(byte) (hoy.get(Calendar.YEAR)-this.fechaIngreso.getAnio());
        if(hoy.get(Calendar.MONTH)-this.fechaIngreso.getMes()<0){
            antiguedad --;
        }else if (hoy.get(Calendar.MONTH)-this.fechaIngreso.getMes()==0 && hoy.get(Calendar.DAY_OF_MONTH)-this.fechaIngreso.getDia()<0){
            antiguedad--;
        }
        return antiguedad;
    }
    
    
    @Override
    public String toString(){
        
        return "Empleado: "+ this.nombreYApe + " || Sexo: " + this.sexo.getDescripcion() + " || Salario: " + this.salario 
                + " || Fecha de ingreso: " +this.fechaIngreso.getDia()+"/"+this.fechaIngreso.getMes()+"/"+this.fechaIngreso.getAnio()
                + " || Tipo: " + this.tipoEmpleado.getDescripcion() +  " || Provincia: " + this.provincia.getDescripcion();
    }
    
}
