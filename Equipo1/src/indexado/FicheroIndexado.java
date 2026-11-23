package indexado;

/**
 *
 * @author Pablo Azogue
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
    private ModoApertura modo;

    public FicheroIndexado(File fichero, ModoApertura modo, FormatoRegistro formato) throws IOException {
        boolean cargado;

        this.formato = formato;
        this.modo = modo;
        longitudRegistro = (short) (1 + formato.getLongitudClave() + formato.getLongitudDatos());
        datos = new RandomAccessFile(fichero, modo.getModo());
        indice = new Indice(new File(nombreIndice(fichero)));
        if (modo == ModoApertura.CREACION) {
            datos.setLength(0);
        } else {
            cargado = indice.cargar(getNumRegistrosFisicos());
            if (!cargado) {
                reconstruirIndice();
            }
        }
        if (modo != ModoApertura.LECTURA) {
            indice.marcarAbierto();
        }
        orden = OrdenLectura.CLAVE;
        posicionActual = -1;
        claveActual = null;
    }

    public void escribir(RegistroIndexado registro) throws IOException, ClaveDuplicadaException {
        Comparable clave;
        int posicion;

        clave = registro.getClave();
        if (indice.existe(clave)) {
            throw new ClaveDuplicadaException(clave);
        }
        posicion = indice.posicionLibre(getNumRegistrosFisicos());
        escribirEnPosicion(posicion, registro);
        indice.anadir(clave, posicion);
    }

    public RegistroIndexado leer(Comparable clave) throws IOException, ClaveInexistenteException {
        RegistroIndexado registro;

        registro = leerEnPosicion(buscarPosicion(clave));
        return registro;
    }

    public void reescribir(RegistroIndexado registro) throws IOException, ClaveInexistenteException {
        escribirEnPosicion(buscarPosicion(registro.getClave()), registro);
    }

    public void eliminar(Comparable clave) throws IOException, ClaveInexistenteException {
        int posicion;

        posicion = buscarPosicion(clave);
        datos.seek((long) posicion * longitudRegistro);
        datos.writeBoolean(false);
        datos.write(new byte[longitudRegistro - 1]);
        indice.quitar(clave);
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
        orden = OrdenLectura.CLAVE;
        posicionActual = -1;
        claveActual = indice.claveDesde(clave);
    }

    public boolean haySiguiente() throws IOException {
        boolean resultado;

        if (orden == OrdenLectura.CLAVE) {
            resultado = claveActual != null;
        } else {
            resultado = siguienteOcupado(posicionActual) != -1;
        }
        return resultado;
    }

    public RegistroIndexado leerSiguiente() throws IOException {
        RegistroIndexado registro;
        int posicion;

        registro = null;
        if (orden == OrdenLectura.CLAVE) {
            if (claveActual != null) {
                posicion = indice.buscar(claveActual);
                claveActual = indice.siguienteClave(claveActual);
                registro = leerEnPosicion(posicion);
            }
        } else {
            posicion = siguienteOcupado(posicionActual);
            if (posicion != -1) {
                posicionActual = posicion;
                registro = leerEnPosicion(posicion);
            }
        }
        return registro;
    }

    public int getNumRegistros() {
        int contador;

        contador = indice.getNumEntradas();
        return contador;
    }

    public void cerrar() throws IOException {
        try {
            if (modo != ModoApertura.LECTURA) {
                indice.guardar();
            }
        } finally {
            datos.close();
        }
    }

    private int buscarPosicion(Comparable clave) throws ClaveInexistenteException {
        int posicion;

        posicion = indice.buscar(clave);
        if (posicion == -1) {
            throw new ClaveInexistenteException(clave);
        }
        return posicion;
    }

    private int siguienteOcupado(int desde) throws IOException {
        int posicion;
        int total;

        total = getNumRegistrosFisicos();
        posicion = desde + 1;
        while (posicion < total && indice.esHueco(posicion)) {
            posicion++;
        }
        if (posicion >= total) {
            posicion = -1;
        }
        return posicion;
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
    }

    private RegistroIndexado leerEnPosicion(int posicion) throws IOException {
        Comparable clave;
        RegistroIndexado registro;

        datos.seek((long) posicion * longitudRegistro + 1);
        clave = formato.leerClave(datos);
        registro = formato.leerDatos(datos, clave);
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

    private String nombreIndice(File fichero) {
        String ruta;
        int punto;

        ruta = fichero.getPath();
        punto = ruta.lastIndexOf('.');
        if (punto > ruta.lastIndexOf(File.separatorChar)) {
            ruta = ruta.substring(0, punto);
        }
        return ruta + ".NDX";
    }
}
