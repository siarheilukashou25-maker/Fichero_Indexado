import fiche.Fecha;
import fiche.FicheDAO;
import fiche.FicheRegistro;
import fiche.Provincia;
import fiche.Sexo;
import fiche.TipoEmpleado;
import indexado.FicheroIndexado;
import indexado.ModoApertura;
import indexado.OrdenLectura;
import indexado.RegistroIndexado;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class Practica {

    private static BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    private static FicheroIndexado fichero;

    public static void main(String[] args) {
        File archivoDatos;
        ModoApertura modo;
        int opcion;

        archivoDatos = new File("FICHENDX.DAT");

        try {
            modo = archivoDatos.exists() ? ModoApertura.LECTURA_ESCRITURA : ModoApertura.CREACION;
            fichero = new FicheroIndexado(archivoDatos, modo, new FicheDAO());

            do {
                opcion = mostrarMenu();
                switch (opcion) {
                    case 1:
                        altaEmpleado();
                        break;
                    case 2:
                        bajaEmpleado();
                        break;
                    case 3:
                        modificarSalario();
                        break;
                    case 4:
                        listarEmpleados(OrdenLectura.CLAVE);
                        break;
                    case 5:
                        listarEmpleados(OrdenLectura.FISICO);
                        break;
                    case 6:
                        System.out.println("Cerrando el fichero y saliendo...");
                        fichero.cerrar();
                        break;
                    default:
                        System.out.println("Opción incorrecta. Intente de nuevo.");
                }
            } while (opcion != 6);

        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
        }
    }

    private static int mostrarMenu() throws IOException {
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
            System.out.print("Elija una opción: ");
            entrada = teclado.readLine().trim();
            try {
                opcion = Integer.parseInt(entrada);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Error: Por favor, introduzca un número válido.");
            }
        } while (!valido);
        
        return opcion; 
    }

    private static void altaEmpleado() throws IOException {
        String dni;
        String nombre;
        Sexo sexo;
        float salario;
        Fecha fecha;
        TipoEmpleado tipoEmpleado;
        Provincia provincia;
        FicheRegistro nuevoRegistro;

        System.out.println("\n--- 1. ALTA DE EMPLEADO ---");
        dni = pedirDni();

        if (fichero.existe(dni)) {
            System.out.println("Error: Ya existe un empleado con ese DNI.");
        } else {
            nombre = pedirNombreYApe();
            sexo = pedirSexo();
            salario = pedirSalario();
            fecha = pedirFechaIngreso();
            tipoEmpleado = pedirTipoEmpleado();
            provincia = pedirProvincia();
            
            try {
                nuevoRegistro = new FicheRegistro(dni, nombre, sexo, salario, fecha, tipoEmpleado, provincia);
                
                if (fichero.escribir(nuevoRegistro)) {
                    System.out.println("Empleado dado de alta correctamente.");
                } else {
                    System.out.println("No se pudo dar de alta al empleado.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error al crear el registro: " + e.getMessage());
            }
        }
    }

    private static void bajaEmpleado() throws IOException {
        String dni;
        FicheRegistro registro;
        String confirmacion;

        System.out.println("\n--- 2. BAJA DE EMPLEADO ---");
        dni = pedirDni();

        registro = (FicheRegistro) fichero.leer(dni);
        
        if (registro != null) {
            System.out.println("Datos del empleado:");
            System.out.println(registro.toString());
            
            System.out.print("¿Está seguro de que desea eliminar a este empleado? (S/N): ");
            confirmacion = teclado.readLine().trim().toUpperCase();

            if (confirmacion.equals("S")) {
                if (fichero.eliminar(dni)) {
                    System.out.println("Empleado eliminado correctamente.");
                } else {
                    System.out.println("Error al intentar eliminar el empleado.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } else {
            System.out.println("No se encontró ningún empleado con ese DNI.");
        }
    }

    private static void modificarSalario() throws IOException {
        String dni;
        FicheRegistro registro;
        float nuevoSalario;

        System.out.println("\n--- 3. MODIFICAR SALARIO ---");
        dni = pedirDni();

        registro = (FicheRegistro) fichero.leer(dni);
        
        if (registro != null) {
            System.out.printf("El salario actual del empleado %s es: %.2f\n", registro.getNombreApe(), registro.getSalario());
            
            nuevoSalario = pedirSalario();
            
            try {
                registro.setSalario(nuevoSalario);
                
                if (fichero.reescribir(registro)) {
                    System.out.println("Salario modificado correctamente.");
                } else {
                    System.out.println("Error al guardar la modificación.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró ningún empleado con ese DNI.");
        }
    }

    private static void listarEmpleados(OrdenLectura orden) throws IOException {
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

    private static String pedirDni() throws IOException {
        String dni;
        boolean valido;

        valido = false;
        do {
            System.out.print("Introduzca DNI (9 caracteres): ");
            dni = teclado.readLine().trim().toUpperCase();
            if (FicheRegistro.esDniValido(dni)) {
                valido = true;
            } else {
                System.out.println("Error: El DNI debe tener exactamente 9 caracteres.");
            }
        } while (!valido);

        return dni;
    }

    private static String pedirNombreYApe() throws IOException {
        String nombre;
        boolean valido;

        valido = false;
        do {
            System.out.print("Nombre y apellidos (máx 30 caracteres): ");
            nombre = teclado.readLine().trim().toUpperCase().replaceAll("\\s+", " ");
            
            if (FicheRegistro.esNombreValido(nombre) && nombre.matches("[A-ZÁÉÍÓÚÜÑ]+( [A-ZÁÉÍÓÚÜÑ]+)*")) {
                valido = true;
            } else {
                System.out.println("Error: Formato incorrecto o excede los 30 caracteres.");
            }
        } while (!valido);

        return nombre;
    }

    private static Sexo pedirSexo() throws IOException {
        String cadena;
        Sexo sexo;

        sexo = null;
        do {
            System.out.print("Introduzca el sexo del empleado (H/M): ");
            cadena = teclado.readLine().trim().toUpperCase();

            if (cadena.length() == 1) {
                try {
                    sexo = Sexo.desdeCodigo(cadena.charAt(0));
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Sexo no válido.");
                }
            } else {
                System.out.println("Error: Debe introducir un único carácter.");
            }
        } while (sexo == null);

        return sexo;
    }

    private static float pedirSalario() throws IOException {
        float salario;
        String cadena;
        boolean valido;

        salario = -1f;
        valido = false;
        do {
            System.out.print("Salario: ");
            cadena = teclado.readLine().trim().replace(',', '.');
            
            try {
                salario = Float.parseFloat(cadena);
                if (FicheRegistro.esSalarioValido(salario)) {
                    valido = true;
                } else {
                    System.out.println("Error: El salario debe estar entre 0.01 y " + FicheRegistro.SALARIO_MAXIMO + " (2 decimales máx).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Formato numérico incorrecto.");
            }
        } while (!valido);

        return salario;
    }

    private static Fecha pedirFechaIngreso() throws IOException {
        String cadena;
        Fecha fecha;

        fecha = null;
        do {
            System.out.print("Fecha de ingreso (dd/mm/aaaa): ");
            cadena = teclado.readLine().trim();
            
            try {
                fecha = Fecha.parse(cadena);
            } catch (ParseException e) {
                System.out.println("Error: Fecha incorrecta o con formato inválido.");
            }
        } while (fecha == null);

        return fecha;
    }

    private static TipoEmpleado pedirTipoEmpleado() throws IOException {
        String cadena;
        TipoEmpleado tipo;

        tipo = null;
        do {
            System.out.print("Tipo empleado (C, F o D): ");
            cadena = teclado.readLine().trim().toUpperCase();

            if (cadena.length() == 1) {
                try {
                    tipo = TipoEmpleado.desdeCodigo(cadena.charAt(0));
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Tipo de empleado no válido.");
                }
            } else {
                System.out.println("Error: Debe introducir un único carácter.");
            }
        } while (tipo == null);

        return tipo;
    }

    private static Provincia pedirProvincia() throws IOException {
        String cadena;
        byte codigo;
        Provincia provincia;

        provincia = null;
        do {
            System.out.print("Código de Provincia (1-Almería ... 8-Sevilla): ");
            cadena = teclado.readLine().trim();
            
            try {
                codigo = Byte.parseByte(cadena);
                provincia = Provincia.desdeCodigo(codigo);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número entero.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (provincia == null);

        return provincia;
    }
}