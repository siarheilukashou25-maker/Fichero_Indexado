import fiche.Fecha;
import fiche.FicheDAO;
import fiche.FicheRegistro;
import fiche.Provincia;
import fiche.Sexo;
import fiche.TipoEmpleado;
import indexado.ClaveDuplicadaException;
import indexado.ClaveInexistenteException;
import indexado.FicheroIndexado;
import indexado.ModoApertura;
import indexado.OrdenLectura;
import indexado.RegistroIndexado;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

/**
 *
 * @author Pablo Azogue
 */
public class Practica {

    public static void main(String[] args) {
        BufferedReader teclado;
        FicheroIndexado fichero;
        File archivoDatos;
        int opcion;

        archivoDatos = new File("FICHENDX.DAT");
        if (!archivoDatos.isFile()) {
            System.err.println("No existe el fichero " + archivoDatos.getName() + ", ejecute antes CreaFiche");
            System.exit(1);
        }
        teclado = new BufferedReader(new InputStreamReader(System.in));
        fichero = null;
        try {
            fichero = new FicheroIndexado(archivoDatos, ModoApertura.LECTURA_ESCRITURA, new FicheDAO());
            do {
                opcion = mostrarMenu(teclado);
                switch (opcion) {
                    case 1:
                        altaEmpleado(teclado, fichero);
                        break;
                    case 2:
                        bajaEmpleado(teclado, fichero);
                        break;
                    case 3:
                        modificarSalario(teclado, fichero);
                        break;
                    case 4:
                        listarEmpleados(fichero, OrdenLectura.CLAVE);
                        break;
                    case 5:
                        listarEmpleados(fichero, OrdenLectura.ALMACENAMIENTO);
                        break;
                    case 6:
                        System.out.println("Cerrando el fichero y saliendo...");
                        break;
                    default:
                        System.err.println("Opcion incorrecta. Intente de nuevo.");
                }
            } while (opcion != 6);
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        } finally {
            if (fichero != null) {
                try {
                    fichero.cerrar();
                } catch (IOException e) {
                    System.err.println("Error al cerrar: " + e.getMessage());
                }
            }
        }
    }

    private static String leerLinea(BufferedReader teclado) throws IOException {
        String linea;

        linea = teclado.readLine();
        if (linea != null) {
            linea = linea.trim();
        }
        return linea;
    }

    private static int mostrarMenu(BufferedReader teclado) throws IOException {
        int opcion;
        String entrada;
        boolean valido;

        opcion = -1;
        valido = false;

        System.out.println("\n--- MANTENIMIENTO DE EMPLEADOS ---");
        System.out.println("1. Altas");
        System.out.println("2. Bajas");
        System.out.println("3. Modificar salario");
        System.out.println("4. Listado de empleados, ordenado por DNI");
        System.out.println("5. Listado de empleados por orden de almacenamiento");
        System.out.println("6. Salir");

        do {
            System.out.print("Elija una opcion: ");
            entrada = leerLinea(teclado);
            if (entrada == null) {
                opcion = 6;
                valido = true;
            } else {
                try {
                    opcion = Integer.parseInt(entrada);
                    valido = true;
                } catch (NumberFormatException e) {
                    System.err.println("Error: Por favor, introduzca un numero valido.");
                }
            }
        } while (!valido);

        return opcion;
    }

    private static void altaEmpleado(BufferedReader teclado, FicheroIndexado fichero) throws IOException {
        String dni;
        String nombre;
        Sexo sexo;
        Float salario;
        Fecha fecha;
        TipoEmpleado tipoEmpleado;
        Provincia provincia;

        System.out.println("\n--- 1. ALTA DE EMPLEADO ---");
        dni = pedirDni(teclado);

        if (dni != null && fichero.existe(dni)) {
            System.err.println("Error: Ya existe un empleado con ese DNI.");
        } else if (dni != null) {
            nombre = pedirNombreYApe(teclado);
            sexo = nombre == null ? null : pedirSexo(teclado);
            salario = sexo == null ? null : pedirSalario(teclado);
            fecha = salario == null ? null : pedirFechaIngreso(teclado);
            tipoEmpleado = fecha == null ? null : pedirTipoEmpleado(teclado);
            provincia = tipoEmpleado == null ? null : pedirProvincia(teclado);

            if (provincia == null) {
                System.err.println("Alta cancelada.");
            } else {
                try {
                    fichero.escribir(new FicheRegistro(dni, nombre, sexo, salario, fecha, tipoEmpleado, provincia));
                    System.out.println("Empleado dado de alta correctamente.");
                } catch (ClaveDuplicadaException e) {
                    System.err.println("Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Error al crear el registro: " + e.getMessage());
                }
            }
        }
    }

    private static void bajaEmpleado(BufferedReader teclado, FicheroIndexado fichero) throws IOException {
        String dni;
        FicheRegistro registro;
        String confirmacion;

        System.out.println("\n--- 2. BAJA DE EMPLEADO ---");
        dni = pedirDni(teclado);

        if (dni != null) {
            try {
                registro = (FicheRegistro) fichero.leer(dni);
                System.out.println("Datos del empleado:");
                System.out.println(registro.toString());

                System.out.print("Esta seguro de que desea eliminar a este empleado? (S/N): ");
                confirmacion = leerLinea(teclado);

                if (confirmacion != null && confirmacion.equalsIgnoreCase("S")) {
                    fichero.eliminar(dni);
                    System.out.println("Empleado eliminado correctamente.");
                } else {
                    System.out.println("Operacion cancelada.");
                }
            } catch (ClaveInexistenteException e) {
                System.err.println("No se encontro ningun empleado con ese DNI.");
            }
        }
    }

    private static void modificarSalario(BufferedReader teclado, FicheroIndexado fichero) throws IOException {
        String dni;
        FicheRegistro registro;
        Float nuevoSalario;

        System.out.println("\n--- 3. MODIFICAR SALARIO ---");
        dni = pedirDni(teclado);

        if (dni != null) {
            try {
                registro = (FicheRegistro) fichero.leer(dni);
                System.out.printf("El salario actual del empleado %s es: %.2f\n", registro.getNombreApe(), registro.getSalario());

                nuevoSalario = pedirSalario(teclado);
                if (nuevoSalario == null) {
                    System.err.println("Modificacion cancelada.");
                } else {
                    registro.setSalario(nuevoSalario);
                    fichero.reescribir(registro);
                    System.out.println("Salario modificado correctamente.");
                }
            } catch (ClaveInexistenteException e) {
                System.err.println("No se encontro ningun empleado con ese DNI.");
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static void listarEmpleados(FicheroIndexado fichero, OrdenLectura orden) throws IOException {
        String titulo;
        int contador;
        RegistroIndexado reg;
        FicheRegistro emp;

        titulo = (orden == OrdenLectura.CLAVE) ? "ORDENADO POR DNI" : "POR ORDEN DE ALMACENAMIENTO";
        contador = 0;

        System.out.println("\n--- 4/5. LISTADO DE EMPLEADOS (" + titulo + ") ---");
        System.out.printf("%-10s %-30s %-4s\n", "DNI", "NOMBRE Y APELLIDOS", "SEXO");
        System.out.println("-------------------------------------------------");

        fichero.iniciarLectura(orden);

        while (fichero.haySiguiente()) {
            reg = fichero.leerSiguiente();
            if (reg != null) {
                emp = (FicheRegistro) reg;
                System.out.printf("%-10s %-30s %-4c\n",
                        emp.getClave(),
                        emp.getNombreApe(),
                        emp.getSexo().getCodigo());
                contador++;
            }
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Total empleados listados: " + contador);
    }

    private static String pedirDni(BufferedReader teclado) throws IOException {
        String dni;
        boolean valido;

        valido = false;
        do {
            System.out.print("Introduzca DNI (9 caracteres): ");
            dni = leerLinea(teclado);
            if (dni == null || FicheRegistro.esDniValido(dni)) {
                valido = true;
            } else {
                System.err.println("Error: El DNI debe tener exactamente 9 caracteres.");
            }
        } while (!valido);

        return dni == null ? null : dni.toUpperCase();
    }

    private static String pedirNombreYApe(BufferedReader teclado) throws IOException {
        String nombre;
        boolean valido;

        valido = false;
        do {
            System.out.print("Nombre y apellidos (max 30 caracteres): ");
            nombre = leerLinea(teclado);
            if (nombre == null) {
                valido = true;
            } else {
                nombre = nombre.toUpperCase().replaceAll("\\s+", " ");
                if (FicheRegistro.esNombreValido(nombre) && nombre.matches("[A-ZÁÉÍÓÚÜÑ]+( [A-ZÁÉÍÓÚÜÑ]+)*")) {
                    valido = true;
                } else {
                    System.err.println("Error: Formato incorrecto o excede los 30 caracteres.");
                }
            }
        } while (!valido);

        return nombre;
    }

    private static Sexo pedirSexo(BufferedReader teclado) throws IOException {
        String cadena;
        Sexo sexo;
        boolean fin;

        sexo = null;
        fin = false;
        do {
            System.out.print("Introduzca el sexo del empleado (H/M): ");
            cadena = leerLinea(teclado);
            if (cadena == null) {
                fin = true;
            } else if (cadena.length() == 1) {
                try {
                    sexo = Sexo.desdeCodigo(cadena.charAt(0));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: Sexo no valido.");
                }
            } else {
                System.err.println("Error: Debe introducir un unico caracter.");
            }
        } while (sexo == null && !fin);

        return sexo;
    }

    private static Float pedirSalario(BufferedReader teclado) throws IOException {
        Float salario;
        float valor;
        String cadena;
        boolean fin;

        salario = null;
        fin = false;
        do {
            System.out.print("Salario: ");
            cadena = leerLinea(teclado);
            if (cadena == null) {
                fin = true;
            } else {
                try {
                    valor = Float.parseFloat(cadena.replace(',', '.'));
                    if (FicheRegistro.esSalarioValido(valor)) {
                        salario = valor;
                    } else {
                        System.err.println("Error: El salario debe estar entre 0.01 y " + FicheRegistro.SALARIO_MAXIMO + " (2 decimales max).");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error: Formato numerico incorrecto.");
                }
            }
        } while (salario == null && !fin);

        return salario;
    }

    private static Fecha pedirFechaIngreso(BufferedReader teclado) throws IOException {
        String cadena;
        Fecha fecha;
        boolean fin;

        fecha = null;
        fin = false;
        do {
            System.out.print("Fecha de ingreso (dd/mm/aaaa): ");
            cadena = leerLinea(teclado);
            if (cadena == null) {
                fin = true;
            } else {
                try {
                    fecha = Fecha.parse(cadena);
                    if (fecha.esPosterior(Fecha.hoy())) {
                        System.err.println("Error: La fecha no puede ser futura.");
                        fecha = null;
                    }
                } catch (ParseException e) {
                    System.err.println("Error: Fecha incorrecta o con formato invalido.");
                }
            }
        } while (fecha == null && !fin);

        return fecha;
    }

    private static TipoEmpleado pedirTipoEmpleado(BufferedReader teclado) throws IOException {
        String cadena;
        TipoEmpleado tipo;
        boolean fin;

        tipo = null;
        fin = false;
        do {
            System.out.print("Tipo empleado (C, F o D): ");
            cadena = leerLinea(teclado);
            if (cadena == null) {
                fin = true;
            } else if (cadena.length() == 1) {
                try {
                    tipo = TipoEmpleado.desdeCodigo(cadena.charAt(0));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: Tipo de empleado no valido.");
                }
            } else {
                System.err.println("Error: Debe introducir un unico caracter.");
            }
        } while (tipo == null && !fin);

        return tipo;
    }

    private static Provincia pedirProvincia(BufferedReader teclado) throws IOException {
        String cadena;
        Provincia provincia;
        boolean fin;

        provincia = null;
        fin = false;
        do {
            System.out.print("Codigo de Provincia (1-Almeria ... 8-Sevilla): ");
            cadena = leerLinea(teclado);
            if (cadena == null) {
                fin = true;
            } else {
                try {
                    provincia = Provincia.desdeCodigo(Byte.parseByte(cadena));
                } catch (NumberFormatException e) {
                    System.err.println("Error: Debe introducir un numero entero.");
                } catch (IllegalArgumentException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } while (provincia == null && !fin);

        return provincia;
    }
}
