package indexado;

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
public class VolcadoIndexado {

    private static final byte BYTES_LINEA = 16;

    public static void main(String[] args) {
        File fichero;
        short longitud;

        longitud = 0;
        if (args.length != 2) {
            System.err.println("Uso: java indexado.VolcadoIndexado fichero longitudRegistro");
            System.exit(1);
        }
        try {
            longitud = Short.parseShort(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Longitud no valida " + args[1]);
            System.exit(1);
        }
        fichero = new File(args[0]);
        if (longitud <= 0) {
            System.err.println("Longitud no valida " + args[1]);
            System.exit(1);
        } else if (!fichero.isFile()) {
            System.err.println("No existe el fichero " + fichero.getName());
            System.exit(1);
        } else {
            volcarDatos(fichero, longitud);
            volcarIndice(new File(nombreIndice(fichero)));
        }
    }

    private static void volcarDatos(File fichero, short longitud) {
        FileInputStream entrada;
        byte[] registro;
        int leidos;
        int numero;
        int activos;

        System.out.println("Fichero " + fichero.getName() + " | " + fichero.length() + " bytes | "
                + (fichero.length() / longitud) + " registros de " + longitud + " bytes");
        if (fichero.length() % longitud != 0) {
            System.out.println("AVISO: la longitud del fichero no es multiplo de " + longitud);
        }
        entrada = null;
        registro = new byte[longitud];
        numero = 0;
        activos = 0;
        try {
            entrada = new FileInputStream(fichero);
            leidos = entrada.read(registro);
            while (leidos != -1) {
                System.out.println();
                System.out.print("Registro " + numero + " (byte " + ((long) numero * longitud) + ") ");
                if (registro[0] != 0) {
                    System.out.print("ACTIVO");
                    activos++;
                } else {
                    System.out.print("HUECO");
                }
                if (leidos < longitud) {
                    System.out.print(" INCOMPLETO (" + leidos + " bytes)");
                }
                System.out.println();
                mostrarBytes(registro, leidos);
                numero++;
                leidos = entrada.read(registro);
            }
            System.out.println();
            System.out.println("Activos " + activos + " | Huecos " + (numero - activos));
        } catch (IOException e) {
            System.err.println("Error " + e.getMessage());
        } finally {
            cerrar(entrada);
        }
    }

    private static void mostrarBytes(byte[] registro, int leidos) {
        StringBuilder hex;
        StringBuilder caracteres;
        int i;
        int b;

        hex = new StringBuilder();
        caracteres = new StringBuilder();
        for (i = 0; i < leidos; i++) {
            b = registro[i] & 0xFF;
            if (b < 16) {
                hex.append('0');
            }
            hex.append(Integer.toHexString(b).toUpperCase()).append(' ');
            if (b == ' ') {
                caracteres.append('_');
            } else if (b > ' ' && b < 127) {
                caracteres.append((char) b);
            } else {
                caracteres.append('.');
            }
            if ((i + 1) % BYTES_LINEA == 0 || i == leidos - 1) {
                System.out.println("  " + rellenar(hex.toString(), BYTES_LINEA * 3) + " | " + caracteres);
                hex.setLength(0);
                caracteres.setLength(0);
            }
        }
    }

    private static void volcarIndice(File indice) {
        ObjectInputStream entrada;
        boolean abierto;
        TreeMap entradas;
        Iterator it;
        Map.Entry par;

        System.out.println();
        if (!indice.isFile()) {
            System.out.println("No existe el indice " + indice.getName());
        } else {
            entrada = null;
            try {
                entrada = new ObjectInputStream(new FileInputStream(indice));
                abierto = entrada.readBoolean();
                System.out.println("Indice " + indice.getName() + " | abierto=" + abierto);
                if (abierto) {
                    System.out.println("El fichero no se cerro bien: el indice se reconstruira al abrirlo");
                } else {
                    entradas = (TreeMap) entrada.readObject();
                    System.out.println(entradas.size() + " entradas");
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
                cerrar(entrada);
            }
        }
    }

    private static String nombreIndice(File fichero) {
        String ruta;
        int punto;

        ruta = fichero.getPath();
        punto = ruta.lastIndexOf('.');
        if (punto > ruta.lastIndexOf(File.separatorChar)) {
            ruta = ruta.substring(0, punto);
        }
        return ruta + ".NDX";
    }

    private static String rellenar(String texto, int ancho) {
        StringBuilder resultado;

        resultado = new StringBuilder(texto);
        while (resultado.length() < ancho) {
            resultado.append(' ');
        }
        return resultado.toString();
    }

    private static void cerrar(java.io.Closeable flujo) {
        if (flujo != null) {
            try {
                flujo.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar " + e.getMessage());
            }
        }
    }
}
