package indexado;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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

    public boolean cargar(FormatoRegistro formato, int numRegistros) throws IOException {
        boolean valido = false;
        if (this.ficheroIndice.exists()) {
            DataInputStream entrada = new DataInputStream(
                    new BufferedInputStream(new FileInputStream(this.ficheroIndice)));
            try {
                boolean abierto = entrada.readBoolean();
                if (!abierto) {
                    int cantidad = entrada.readInt();
                    this.entradas.clear();
                    for (int i = 0; i < cantidad; i++) {
                        Comparable clave = formato.leerClave(entrada);
                        this.entradas.put(clave, entrada.readInt());
                    }
                    this.calcularHuecos(numRegistros);
                    valido = true;
                }
            } catch (EOFException e) {
                this.entradas.clear();
                valido = false;
            } finally {
                entrada.close();
            }
        }
        return valido;
    }

    public void marcarAbierto() throws IOException {
        DataOutputStream salida = new DataOutputStream(new FileOutputStream(this.ficheroIndice));
        try {
            salida.writeBoolean(true);
            salida.writeInt(0);
        } finally {
            salida.close();
        }
    }

    public void guardar(FormatoRegistro formato) throws IOException {
        DataOutputStream salida = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(this.ficheroIndice)));
        try {
            salida.writeBoolean(false);
            salida.writeInt(this.entradas.size());
            for (Object o : this.entradas.entrySet()) {
                Map.Entry e = (Map.Entry) o;
                formato.escribirClave(salida, (Comparable) e.getKey());
                salida.writeInt((Integer) e.getValue());
            }
        } finally {
            salida.close();
        }
    }
}