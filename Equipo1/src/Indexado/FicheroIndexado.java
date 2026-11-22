package Indexado;

/**
 *
 * @author Pablo Azogue Castaño
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FicheroIndexado {

    private RandomAccessFile datos;
    private short longitudRegistro;
    private Comparable claveActual;
    private int posicionActual;
    private FormatoRegistro formato;
    private Indice indice;
    private OrdenLectura orden;

    public FicheroIndexado(File fichero, ModoApertura modo, FormatoRegistro formato) throws IOException {
        File ficheroIndice;
        boolean cargado;

        this.formato = formato;
        longitudRegistro = (short) (1 + formato.getLongitudClave() + formato.getLongitudDatos());
        datos = new RandomAccessFile(fichero, modo.getModo());
        ficheroIndice = new File("INDICE.DAT");
        indice = new Indice(ficheroIndice);
        if (modo == ModoApertura.CREACION) {
            datos.setLength(0);
        } else {
            cargado = indice.cargar(getNumRegistrosFisicos());
            if (!cargado) {
                reconstruirIndice();
            }
        }
        indice.marcarAbierto();
        posicionActual = -1;
        claveActual = null;
    }

    public void escribir(RegistroIndexado registro) throws IOException, ClaveDuplicadaException {
        Comparable clave;
        int posicion;

        clave = registro.getClave();
        if (indice.existe(clave)) {
            throw new ClaveDuplicadaException("La clave ya existe");
        }
        posicion = indice.posicionLibre(getNumRegistrosFisicos());
        escribirEnPosicion(posicion, registro);
        indice.anadir(clave, posicion);
        indice.guardar(formato);
    }

    public RegistroIndexado leer(Comparable clave) throws IOException, ClaveInexistenteException {
        int posicion;
        RegistroIndexado registro;

        posicion = indice.buscar(clave);
        registro = null;
        if (posicion == -1) {
            throw new ClaveInexistenteException("La clave no existe");
        }
        registro = leerEnPosicion(posicion);
        return registro;
    }

    public void reescribir(RegistroIndexado registro) throws IOException, ClaveInexistenteException {
        Comparable clave;
        int posicion;

        clave = registro.getClave();
        posicion = indice.buscar(clave);
        if (posicion == -1) {
            throw new ClaveInexistenteException("La clave no existe");
        }
        escribirEnPosicion(posicion, registro);
    }

    public void eliminar(Comparable clave) throws IOException, ClaveInexistenteException {
        int posicion;

        posicion = indice.buscar(clave);
        if (posicion == -1) {
            throw new ClaveInexistenteException("La clave no existe");
        }
        datos.seek((long) posicion * longitudRegistro);
        datos.writeBoolean(false);
        indice.quitar(clave);
        indice.calcularHuecos(getNumRegistrosFisicos());
        indice.guardar(formato);
    }

    public boolean existe(Comparable clave) {
        boolean resultado;

        resultado = indice.existe(clave);
        return resultado;
    }

    public void iniciarLectura(OrdenLectura orden) {
        this.orden = orden;
        posicionActual = -1;
        claveActual = null;
        if (orden == OrdenLectura.CLAVE) {
            claveActual = indice.primeraClave();
        }
    }

    public void posicionar(Comparable clave) {
        if (indice.existe(clave)) {
            claveActual = clave;
        } else {
            claveActual = null;
        }
    }

    public boolean haySiguiente() throws IOException {
        boolean resultado;
        int posicion;
        int total;

        resultado = false;
        if (orden == OrdenLectura.CLAVE) {
            resultado = claveActual != null;
        } else {
            posicion = posicionActual + 1;
            total = getNumRegistrosFisicos();
            while (posicion < total && !resultado) {
                datos.seek((long) posicion * longitudRegistro);
                if (datos.readBoolean()) {
                    resultado = true;
                }
                posicion++;
            }
        }
        return resultado;
    }

    public RegistroIndexado leerSiguiente() throws IOException {
        RegistroIndexado registro;
        Comparable clave;
        int posicion;
        int total;

        registro = null;
        if (orden == OrdenLectura.CLAVE) {
            if (claveActual != null) {
                clave = claveActual;
                posicion = indice.buscar(clave);
                claveActual = indice.siguienteClave(clave);
                registro = leerEnPosicion(posicion);
            }
        } else {
            total = getNumRegistrosFisicos();
            while (posicionActual + 1 < total && registro == null) {
                posicionActual++;
                registro = leerEnPosicion(posicionActual);
            }
        }
        return registro;
    }

    public int getNumRegistros() {
        int contador;
        Comparable clave;

        contador = 0;
        clave = indice.primeraClave();
        while (clave != null) {
            contador++;
            clave = indice.siguienteClave(clave);
        }
        return contador;
    }

    public void cerrar() throws IOException {
        indice.guardar(formato);
        indice.marcarAbierto();
        datos.close();
    }

    private void reconstruirIndice() throws IOException {
        int total;
        int posicion;
        boolean activo;
        Comparable clave;

        total = getNumRegistrosFisicos();
        posicion = 0;
        while (posicion < total) {
            datos.seek((long) posicion * longitudRegistro);
            activo = datos.readBoolean();
            if (activo) {
                clave = formato.leerClave(datos);
                indice.anadir(clave, posicion);
            }
            posicion++;
        }
        indice.calcularHuecos(total);
        indice.guardar(formato);
    }

    private RegistroIndexado leerEnPosicion(int posicion) throws IOException {
        boolean activo;
        Comparable clave;
        RegistroIndexado registro;

        activo = false;
        clave = null;
        registro = null;
        datos.seek((long) posicion * longitudRegistro);
        activo = datos.readBoolean();
        if (activo) {
            clave = formato.leerClave(datos);
            registro = formato.leerDatos(datos, clave);
        }
        return registro;
    }

    private void escribirEnPosicion(int posicion, RegistroIndexado registro) throws IOException {
        datos.seek((long) posicion * longitudRegistro);
        datos.writeBoolean(true);
        formato.escribirClave(datos, registro.getClave());
        formato.escribirDatos(datos, registro);
    }

    private int getNumRegistrosFisicos() throws IOException {
        int total;

        total = (int) (datos.length() / longitudRegistro);
        return total;
    }
}