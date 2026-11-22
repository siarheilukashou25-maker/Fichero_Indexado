package fichendx;

/**
 * @author LukashouSiarhei
 */
public enum Sexo {
    HOMBRE('H'),
    MUJER('M');

    private final char codigo;

    private Sexo(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return this.codigo;
    }

    public static Sexo desdeCodigo(char codigo) {
        Sexo resultado = null;
        Sexo[] valores = values();
        char buscado = Character.toUpperCase(codigo);
        int i = 0;
        while (resultado == null && i < valores.length) {
            if (valores[i].codigo == buscado) {
                resultado = valores[i];
            }
            i++;
        }
        if (resultado == null) {
            throw new IllegalArgumentException("Sexo no valido " + codigo);
        }
        return resultado;
    }
}
