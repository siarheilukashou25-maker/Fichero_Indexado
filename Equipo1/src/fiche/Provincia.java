package fiche;

/**
 * @author LukashouSiarhei
 */
public enum Provincia {
    ALMERIA((byte) 1, "Almeria"),
    CADIZ((byte) 2, "Cadiz"),
    CORDOBA((byte) 3, "Cordoba"),
    GRANADA((byte) 4, "Granada"),
    HUELVA((byte) 5, "Huelva"),
    JAEN((byte) 6, "Jaen"),
    MALAGA((byte) 7, "Malaga"),
    SEVILLA((byte) 8, "Sevilla");

    private final byte codigo;
    private final String nombre;

    private Provincia(byte codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public byte getCodigo() {
        return this.codigo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public static Provincia desdeCodigo(byte codigo) {
        Provincia resultado = null;
        Provincia[] valores = values();
        int i = 0;
        while (resultado == null && i < valores.length) {
            if (valores[i].codigo == codigo) {
                resultado = valores[i];
            }
            i++;
        }
        if (resultado == null) {
            throw new IllegalArgumentException("Provincia no valida " + codigo);
        }
        return resultado;
    }
}
