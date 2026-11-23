package indexado;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Elías Pérez Arroyo
 */
public class VolcadoFicheNDX {

    public static void main(String[] args) {
        File datos;
        File indice;

        datos = new File("ficheros", "FICHENDX.DAT");
        indice = new File("ficheros", "FICHENDX.NDX");
        if (!datos.isFile()) {
            System.err.println("No existe " + datos.getPath());
            System.exit(1);
        }
        volcarDatos(datos);
        volcarIndice(indice);
    }

    private static void volcarDatos(File fichero) {
        DataInputStream entrada;
        boolean fin;
        int numero;
        int activos;
        boolean activo;
        String dni;
        String nombre;

        System.out.println("Fichero " + fichero.getPath() + " | " + fichero.length() + " bytes | "
                + (fichero.length() / 92) + " registros de 92 bytes");
        if (fichero.length() % 92 != 0) {
            System.out.println("AVISO: la longitud no es multiplo de 92");
        }
        entrada = null;
        fin = false;
        numero = 0;
        activos = 0;
        try {
            entrada = new DataInputStream(new BufferedInputStream(new FileInputStream(fichero)));
            while (!fin) {
                try {
                    activo = entrada.readBoolean();
                    dni = leerTexto(entrada, 9);
                    nombre = leerTexto(entrada, 30);
                    System.out.println();
                    if (activo) {
                        System.out.println("Registro " + numero + " ACTIVO");
                        activos++;
                    } else {
                        System.out.println("Registro " + numero + " HUECO");
                    }
                    System.out.println("  DNI       [" + dni + "]=> " + contarEspacios(dni));
                    System.out.println("  Nombre    [" + nombre + "]=> " + contarEspacios(nombre));
                    System.out.println("  Sexo      [" + entrada.readChar() + "]");
                    System.out.println("  Salario   [" + entrada.readFloat() + "]");
                    System.out.println("  Fecha     [" + entrada.readShort() + " / " + entrada.readByte() + " / " + entrada.readByte() + "]");
                    System.out.println("  Tipo      [" + entrada.readChar() + "]");
                    System.out.println("  Provincia [" + entrada.readByte() + "]");
                    numero++;
                } catch (EOFException e) {
                    fin = true;
                }
            }
            System.out.println();
            System.out.println("Activos " + activos + " | Huecos " + (numero - activos));
        } catch (IOException e) {
            System.err.println("Error " + e.getMessage());
        } finally {
            if (entrada != null) {
                try {
                    entrada.close();
                } catch (IOException e) {
                    System.err.println("Error " + e.getMessage());
                }
            }
        }
    }

    private static String leerTexto(DataInputStream entrada, int longitud) throws IOException {
        StringBuilder texto;
        int i;

        texto = new StringBuilder();
        for (i = 0; i < longitud; i++) {
            texto.append(entrada.readChar());
        }
        return texto.toString();
    }

    private static int contarEspacios(String texto) {
        int i;

        i = texto.length() - 1;
        while (i >= 0 && texto.charAt(i) == ' ') {
            i--;
        }
        return texto.length() - 1 - i;
    }

    private static void volcarIndice(File indice) {
        ObjectInputStream entrada;
        TreeMap entradas;
        Iterator it;
        Map.Entry par;

        System.out.println();
        if (!indice.isFile()) {
            System.out.println("No existe " + indice.getPath());
        } else {
            entrada = null;
            try {
                entrada = new ObjectInputStream(new FileInputStream(indice));
                if (entrada.readBoolean()) {
                    System.out.println("Indice " + indice.getPath() + " | abierto=true: el fichero no se cerro bien");
                } else {
                    entradas = (TreeMap) entrada.readObject();
                    System.out.println("Indice " + indice.getPath() + " | abierto=false | " + entradas.size() + " entradas");
                    it = entradas.entrySet().iterator();
                    while (it.hasNext()) {
                        par = (Map.Entry) it.next();
                        System.out.println("  [" + par.getKey() + "] -> registro " + par.getValue());
                    }
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Indice no valido " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error indice " + e.getMessage());
            } finally {
                if (entrada != null) {
                    try {
                        entrada.close();
                    } catch (IOException e) {
                        System.err.println("Error " + e.getMessage());
                    }
                }
            }
        }
    }
}
