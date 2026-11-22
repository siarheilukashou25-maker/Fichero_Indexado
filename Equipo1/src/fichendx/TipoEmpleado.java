package fichendx;

/**
 * @author LukashouSiarhei
 */
public enum TipoEmpleado {
    COMISION('C', "A comision"),
    FIJO('F', "Fijos"),
    DOMICILIO('D', "A domicilio");

    private final char codigo;
    private final String descripcion;

    private TipoEmpleado(char codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public char getCodigo() {
        return this.codigo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public static TipoEmpleado desdeCodigo(char codigo) {
        TipoEmpleado resultado = null;
        TipoEmpleado[] valores = values();
        char buscado = Character.toUpperCase(codigo);
        int i = 0;
        while (resultado == null && i < valores.length) {
            if (valores[i].codigo == buscado) {
                resultado = valores[i];
            }
            i++;
        }
        if (resultado == null) {
            throw new IllegalArgumentException("Tipo no valido " + codigo);
        }
        return resultado;
    }
}
