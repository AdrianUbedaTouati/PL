import java.io.IOException;
import java.io.RandomAccessFile;

public class AnalizadorLexico {
    RandomAccessFile texto;
    AnalizadorLexico(RandomAccessFile entrada) {
        texto = entrada;
    }
    public String siguienteToken(){
        String token = new String();
        try{
            int caracterAscii = texto.read();
            String caracterString = Character.toString((char) caracterAscii);
            return caracterString;
        } catch (IOException e) {
            System.out.println("Error al leer el fichero" + e.getMessage());
        }
        return token;
    }
}
