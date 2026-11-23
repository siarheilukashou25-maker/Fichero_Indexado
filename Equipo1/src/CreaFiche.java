import fiche.FicheDAO;
import fiche.FicheRegistro;
import indexado.ClaveDuplicadaException;
import indexado.FicheroIndexado;
import indexado.ModoApertura;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author LukashouSiarhei
 */
public class CreaFiche {

    public static void main(String[] args) {
        File origen;
        File destino;
        BufferedReader teclado;
        DataInputStream entrada;
        FicheroIndexado fichero;
        FicheDAO dao;
        FicheRegistro registro;
        String nombre;
        String dni;
        boolean fin;
        boolean grabado;
        int grabados;

        origen = new File("FICHE.DAT");
        destino = new File("FICHENDX.DAT");
        if (!origen.isFile()) {
            System.err.println("No existe el fichero " + origen.getName());
            System.exit(1);
        }
        dao = new FicheDAO();
        if (origen.length() % dao.getLongitudDatos() != 0) {
            System.err.println("Fichero " + origen.getName() + " no valido");
            System.exit(1);
        }
        teclado = new BufferedReader(new InputStreamReader(System.in));
        entrada = null;
        fichero = null;
        grabados = 0;
        try {
            entrada = new DataInputStream(new BufferedInputStream(new FileInputStream(origen)));
            fichero = new FicheroIndexado(destino, ModoApertura.CREACION, dao);
            fin = false;
            while (!fin) {
                try {
                    entrada.mark(dao.getLongitudDatos());
                    nombre = leerNombre(entrada);
                    entrada.reset();
                    grabado = false;
                    while (!grabado && !fin) {
                        System.out.print("DNI de " + nombre + ": ");
                        dni = teclado.readLine();
                        if (dni == null) {
                            fin = true;
                        } else if (!FicheRegistro.esDniValido(dni)) {
                            System.err.println("DNI de " + FicheRegistro.LONGITUD_DNI + " caracteres");
                        } else if (fichero.existe(dni.trim().toUpperCase())) {
                            System.err.println("DNI repetido");
                        } else {
                            registro = (FicheRegistro) dao.leerDatos(entrada, dni.trim().toUpperCase());
                            fichero.escribir(registro);
                            grabado = true;
                            grabados++;
                        }
                    }
                } catch (EOFException e) {
                    fin = true;
                }
            }
            System.out.println("Registros grabados " + grabados);
        } catch (ClaveDuplicadaException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Error " + e.getMessage());
        } finally {
            cerrar(entrada);
            if (fichero != null) {
                try {
                    fichero.cerrar();
                } catch (IOException e) {
                    System.err.println("Error " + e.getMessage());
                }
            }
        }
    }

    private static String leerNombre(DataInputStream entrada) throws IOException {
        StringBuilder nombre;
        byte i;

        nombre = new StringBuilder();
        for (i = 0; i < FicheRegistro.LONGITUD_NOMBRE; i++) {
            nombre.append(entrada.readChar());
        }
        return nombre.toString().trim();
    }

    private static void cerrar(DataInputStream entrada) {
        if (entrada != null) {
            try {
                entrada.close();
            } catch (IOException e) {
                System.err.println("Error " + e.getMessage());
            }
        }
    }
}
