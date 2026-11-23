
import FicheDATDAO.Empleado;
import FicheDATDAO.EmpleadoDAO;
import fiche.Fecha;
import fiche.FicheDAO;
import fiche.FicheRegistro;
import fiche.Provincia;
import fiche.Sexo;
import fiche.TipoEmpleado;
import indexado.FicheroIndexado;
import indexado.ModoApertura;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 *
 * @author Elías Pérez Arroyo
 */
public class CreaFiche {

    public static void main(String[] args) {
        File ficheroIndice=new File("Ficheros"+ File.separatorChar +"FICHENDX.DAT");
        FicheroIndexado ficheroIndexadoFinal=null;
        FicheRegistro registroIndexado;
        BufferedReader teclado=null;
        String dni;
        EmpleadoDAO daoFicheroOrigen=null;
        try{
           daoFicheroOrigen=new EmpleadoDAO("Ficheros"+ File.separatorChar +"FICHE.DAT");
           daoFicheroOrigen.abrirLectura();
           teclado = new BufferedReader(new InputStreamReader(System.in));
           if(!ficheroIndice.exists()){
               ficheroIndexadoFinal=new FicheroIndexado(ficheroIndice,ModoApertura.CREACION,new FicheDAO());
           }else{
               ficheroIndexadoFinal=new FicheroIndexado(ficheroIndice,ModoApertura.LECTURA_ESCRITURA,new FicheDAO());
           }
           Empleado empOrigen=daoFicheroOrigen.leer();
           while(empOrigen!=null){
               do{
                   System.out.println("\nEMPLEADO: " + empOrigen.toString());
                   System.out.println("Introduce el DNI del empleado");
                   dni=teclado.readLine().toUpperCase();                 
                   if(ficheroIndexadoFinal.existe(dni)){
                       System.out.println("Ya existe un empleado con ese DNI");
                   }
               }while(!FicheRegistro.esDniValido(dni) && ficheroIndexadoFinal.existe(dni));
               registroIndexado=new FicheRegistro(dni,
                       empOrigen.getNombreYApe(),
                       Sexo.desdeCodigo(empOrigen.getSexo().getCodigo()),
                       empOrigen.getSalario(),
                       new Fecha(
                        empOrigen.getFechaIngreso().getAnio(),
                        empOrigen.getFechaIngreso().getMes(),
                        empOrigen.getFechaIngreso().getDia()),
                        TipoEmpleado.desdeCodigo(empOrigen.getTipoEmpleado().getCodigo()),
                        Provincia.desdeCodigo((byte)empOrigen.getProvincia().getCodigo())
                        );
               ficheroIndexadoFinal.escribir(registroIndexado);
               daoFicheroOrigen.leer();
           }
           System.out.println("\nProceso finalizado.");
        }catch(FileNotFoundException fnfe){
            System.err.println("No existe FICHE.DAT: "+ fnfe.getMessage());
        } catch (IOException ex) {
            System.err.println("Error de E/S: "+ex.getMessage());
        }finally{
            if(daoFicheroOrigen!=null){
                try {
                    daoFicheroOrigen.close();
                } catch (IOException ioe) {
                    System.err.println("Error al cerrar FICHE.DAT " + ioe.getMessage());
                }
            }
            if (ficheroIndexadoFinal != null) {
                try {
                    ficheroIndexadoFinal.cerrar();
                } catch (IOException ioe) {
                    System.err.println("Error al cerrar FICHENDX.DAT: " + ioe.getMessage());
                }
            }
            if (teclado != null) {
                try {
                    teclado.close();
                } catch (IOException ex) {
                    System.err.println("Error entrada/salida: " + ex.getMessage());
                }
            }
        }
    }
}
