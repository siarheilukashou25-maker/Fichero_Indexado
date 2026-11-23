package indexado;

import java.io.*;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author LukashouSiarhei
 */
class Indice {

    private File ficheroIndice;
    private TreeMap entradas;
    private TreeSet huecos;

    public Indice(File ficheroIndice) {
        this.ficheroIndice = ficheroIndice;
        this.entradas = new TreeMap();
        this.huecos = new TreeSet();
    }

    public void anadir(Comparable clave, int posicion) {
        this.entradas.put(clave, posicion);
        this.huecos.remove(posicion);
    }

    public int buscar(Comparable clave) {
        int posicion = -1;
        Integer p = (Integer) this.entradas.get(clave);
        if (p != null) {
            posicion = p;
        }
        return posicion;
    }

    public int quitar(Comparable clave) {
        int posicion = (Integer) this.entradas.remove(clave);
        this.huecos.add(posicion);
        return posicion;
    }

    public boolean existe(Comparable clave) {
        return this.entradas.containsKey(clave);
    }

    public int posicionLibre(int numRegistros) {
        int posicion = numRegistros;
        if (!this.huecos.isEmpty()) {
            posicion = (Integer) this.huecos.first();
        }
        return posicion;
    }

    public boolean esHueco(int posicion) {
        return this.huecos.contains(posicion);
    }

    public int getNumEntradas() {
        return this.entradas.size();
    }

    public Comparable primeraClave() {
        Comparable clave = null;
        if (!this.entradas.isEmpty()) {
            clave = (Comparable) this.entradas.firstKey();
        }
        return clave;
    }

    public Comparable claveDesde(Comparable clave) {
        return (Comparable) this.entradas.ceilingKey(clave);
    }

    public Comparable siguienteClave(Comparable clave) {
        return (Comparable) this.entradas.higherKey(clave);
    }

    public void calcularHuecos(int numRegistros) {
        boolean[] ocupado = new boolean[numRegistros];
        for (Object posicion : this.entradas.values()) {
            ocupado[(Integer) posicion] = true;
        }
        this.huecos.clear();
        for (int i = 0; i < numRegistros; i++) {
            if (!ocupado[i]) {
                this.huecos.add(i);
            }
        }
    }

    public boolean cargar(int numRegistros) {
        boolean valido = false;
        ObjectInputStream entrada = null;
        if (this.ficheroIndice.exists()) {
            try {
                entrada = new ObjectInputStream(new FileInputStream(this.ficheroIndice));
                boolean abierto = entrada.readBoolean();
                if (!abierto) {
                    this.entradas = (TreeMap) entrada.readObject();
                    this.calcularHuecos(numRegistros);
                    valido = true;
                }
            } catch (IOException e) {
                valido = false;
            } catch (ClassNotFoundException e) {
                valido = false;
            } catch (ClassCastException e) {
                valido = false;
            } catch (ArrayIndexOutOfBoundsException e) {
                valido = false;
            } finally {
                cerrar(entrada);
            }
        }
        if (!valido) {
            this.entradas = new TreeMap();
            this.huecos.clear();
        }
        return valido;
    }

    public void marcarAbierto() throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(this.ficheroIndice));
        try {
            salida.writeBoolean(true);
        } finally {
            salida.close();
        }
    }

    public void guardar() throws IOException {
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(this.ficheroIndice));
        try {
            salida.writeBoolean(false);
            salida.writeObject(this.entradas);
        } finally {
            salida.close();
        }
    }

    private void cerrar(InputStream entrada) {
        if (entrada != null) {
            try {
                entrada.close();
            } catch (IOException e) {
                System.err.println("Error " + e.getMessage());
            }
        }
    }
}
