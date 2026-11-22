
package fichendx;

import java.math.BigDecimal;
import indexado.RegistroIndexado;

/**
 *
 * @author Santiago González
 */
public class FichendxRegistro extends RegistroIndexado {

    public static final byte LONGITUD_DNI = 9;
    public static final byte LONGITUD_NOMBRE = 30;
    public static final float SALARIO_MAXIMO = 9999.99f;
    private String DNI;
    private String nombreApe;
    private Sexo sexo;
    private float salario;
    private Fecha fechaIngreso;
    private TipoEmpleado tipoEmpleado;
    private Provincia provincia;

    public FichendxRegistro(String dni, String nombreApe, Sexo sexo, float salario, Fecha fechaIngreso,
            TipoEmpleado tipoEmpleado, Provincia provincia) {
        super(comprobarDni(dni));
        this.DNI=dni;
        setNombreApe(nombreApe);
        setSalario(salario);
        this.sexo = sexo;
        this.fechaIngreso = fechaIngreso;
        this.tipoEmpleado = tipoEmpleado;
        this.provincia = provincia;
    }

    private static String comprobarDni(String dni) {
        if (!esDniValido(dni)) {
            throw new IllegalArgumentException("El DNI debe tener " + LONGITUD_DNI + " caracteres");
        }
        return dni.trim().toUpperCase();
    }

    public static boolean esDniValido(String dni) {
        return dni != null && dni.trim().length() == LONGITUD_DNI;
    }

    public static boolean esNombreValido(String nombre) {
        String limpio = nombre.trim();
        return !limpio.isEmpty() && limpio.length() <= LONGITUD_NOMBRE;
    }

    public static boolean esSalarioValido(float salario) {
        return salario > 0 && salario <= SALARIO_MAXIMO && new BigDecimal(Float.toString(salario)).scale() <= 2;
    }

    public String getNombreApe() {
        return this.nombreApe;
    }

    public final void setNombreApe(String nombreApe) {
        if (!esNombreValido(nombreApe)) {
            throw new IllegalArgumentException("El nombre debe tener entre 1 y " + LONGITUD_NOMBRE + " caracteres");
        }
        this.nombreApe = nombreApe.trim();
    }

    public Sexo getSexo() {
        return this.sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public float getSalario() {
        return this.salario;
    }

    public final void setSalario(float salario) {
        if (!esSalarioValido(salario)) {
            throw new IllegalArgumentException("El salario debe estar entre 0 y " + SALARIO_MAXIMO + " con dos decimales como maximo");
        }
        this.salario = salario;
    }

    public Fecha getFechaIngreso() {
        return this.fechaIngreso;
    }

    public void setFechaIngreso(Fecha fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public TipoEmpleado getTipoEmpleado() {
        return this.tipoEmpleado;
    }

    public void setTipoEmpleado(TipoEmpleado tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public Provincia getProvincia() {
        return this.provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    @Override
    public String toString() {
        String texto = String.format("%-9s %-30s %c %8.2f %s %-11s %s", getClave(), this.nombreApe,
                this.sexo.getCodigo(), this.salario, this.fechaIngreso,
                this.tipoEmpleado.getDescripcion(), this.provincia.getNombre());
        return texto;
    }
}
