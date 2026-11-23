
import fichedat.Empleado;
import fichedat.EmpleadoDAO;
import fiche.FicheDAO;
import fiche.FicheRegistro;
import indexado.ClaveDuplicadaException;
import indexado.FicheroIndexado;
import indexado.ModoApertura;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 *
 * @author Elías Pérez Arroyo
 */
public class CreaFiche {

    public static void main(String[] args) {
        File ficheroOrigen=new File("ficheros", "FICHE.DAT");
        File ficheroIndice=new File("ficheros", "FICHENDX.DAT");
        FicheroIndexado ficheroIndexadoFinal=null;
        FicheRegistro registroIndexado;
        BufferedReader teclado;
        String dni;
        boolean fin;
        boolean valido;
        int grabados;
        EmpleadoDAO daoFicheroOrigen=null;
        Empleado empOrigen;

        if(!ficheroOrigen.isFile()){
            System.err.println("No existe FICHE.DAT");
            System.exit(1);
        }
        if(ficheroOrigen.length() % EmpleadoDAO.LONGITUD_REGISTRO != 0){
            System.err.println("FICHE.DAT no valido");
            System.exit(1);
        }
        teclado = new BufferedReader(new InputStreamReader(System.in));
        grabados=0;
        try{
           daoFicheroOrigen=new EmpleadoDAO(ficheroOrigen);
           daoFicheroOrigen.abrirLectura();
           ficheroIndexadoFinal=new FicheroIndexado(ficheroIndice,ModoApertura.CREACION,new FicheDAO());
           fin=false;
           empOrigen=daoFicheroOrigen.leer();
           while(empOrigen!=null && !fin){
               valido=false;
               dni=null;
               do{
                   System.out.println("\nEMPLEADO: " + empOrigen.toString());
                   System.out.println("Introduce el DNI del empleado");
                   dni=teclado.readLine();
                   if(dni==null){
                       fin=true;
                   }else if(!FicheRegistro.esDniValido(dni)){
                       System.err.println("El DNI debe tener " + FicheRegistro.LONGITUD_DNI + " caracteres");
                   }else if(ficheroIndexadoFinal.existe(dni.trim().toUpperCase())){
                       System.err.println("Ya existe un empleado con ese DNI");
                   }else{
                       valido=true;
                   }
               }while(!valido && !fin);
               if(valido){
                   registroIndexado=new FicheRegistro(dni,
                           empOrigen.getNombreYApe(),
                           empOrigen.getSexo(),
                           empOrigen.getSalario(),
                           empOrigen.getFechaIngreso(),
                           empOrigen.getTipoEmpleado(),
                           empOrigen.getProvincia());
                   ficheroIndexadoFinal.escribir(registroIndexado);
                   grabados++;
                   empOrigen=daoFicheroOrigen.leer();
               }
           }
           System.out.println("\nProceso finalizado. Registros grabados: " + grabados);
        } catch (ClaveDuplicadaException cde) {
            System.err.println(cde.getMessage());
        } catch (IllegalArgumentException iae) {
            System.err.println("Datos no validos: "+iae.getMessage());
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
        }
    }
}
