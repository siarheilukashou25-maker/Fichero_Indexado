package fichedat;

import fiche.Fecha;
import fiche.Provincia;
import fiche.Sexo;
import fiche.TipoEmpleado;

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

    public Sexo getSexo() {
        return sexo;
    }

    public float getSalario() {
        return salario;
    }

    public Fecha getFechaIngreso() {
        return fechaIngreso;
    }

    public TipoEmpleado getTipoEmpleado() {
        return tipoEmpleado;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    @Override
    public String toString(){
        return "Empleado: "+ this.nombreYApe + " || Sexo: " + this.sexo.getCodigo() + " || Salario: " + this.salario
                + " || Fecha de ingreso: " + this.fechaIngreso
                + " || Tipo: " + this.tipoEmpleado.getDescripcion() +  " || Provincia: " + this.provincia.getNombre();
    }

}
