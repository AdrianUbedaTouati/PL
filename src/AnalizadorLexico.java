import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorLexico {
    private int filaFichero = 0;
    private int columnaFichero = 0;
    RandomAccessFile texto;
    AnalizadorLexico(RandomAccessFile entrada) {
        texto = entrada;
    }

    /**
     * Devuelve el siguiente token del fichero
     * @return Token
     */
    public Token siguienteToken(){
        Token token = new Token();
        token.lexema = "";

        String caracterString = LecturaFichero();

        //System.out.println(caracterString);

        switch (Objects.requireNonNull(caracterString)){
            case "(":
                token.lexema = caracterString;
                token.tipo = 0;
                token.columna = columnaFichero;
                break;

            case ")":
                token.lexema = caracterString;
                token.tipo = 1;
                token.columna = columnaFichero;
                break;

            case "*":
                token.lexema = caracterString;
                token.tipo = 2;
                token.columna = columnaFichero;
                break;
            case "/":
                token.lexema = caracterString;
                token.tipo = 2;
                token.columna = columnaFichero;
                caracterString = LecturaFichero();
                if(Objects.equals(caracterString, "/")) {
                    token.lexema += caracterString;
                }else{
                    RetrocederFichero();
                }
                break;

            case "+", "-":
                token.lexema = caracterString;
                token.tipo = 3;
                token.columna = columnaFichero;
                break;

            case "<":
                token.lexema = caracterString;
                token.tipo = 4;
                token.columna = columnaFichero;
                caracterString = LecturaFichero();
                if(Objects.equals(caracterString, "=") || Objects.equals(caracterString, ">")) {
                    token.lexema += caracterString;
                }else{
                    RetrocederFichero();
                }
                break;
            case ">":
                token.lexema = caracterString;
                token.tipo = 4;
                token.columna = columnaFichero;
                caracterString = LecturaFichero();
                if(Objects.equals(caracterString, "=")) {
                    token.lexema += caracterString;
                }else{
                    RetrocederFichero();
                }
                break;
            case "=":
                token.lexema = caracterString;
                token.tipo = 4;
                token.columna = columnaFichero;
                break;

            case ";":
                token.lexema = caracterString;
                token.tipo = 5;
                token.columna = columnaFichero;
                break;

            case ":":
                token.lexema = caracterString;
                token.tipo = 6;
                token.columna = columnaFichero;
                caracterString = LecturaFichero();
                if(Objects.equals(caracterString, "=")) {
                    token.lexema += caracterString;
                    token.tipo = 8;
                    token.columna = columnaFichero;
                }else{
                    RetrocederFichero();
                }
                break;

            case ",":
                token.lexema = caracterString;
                token.tipo = 7;
                token.columna = columnaFichero;
                break;

            default:
                RetrocederFichero();
                token = LexemaExtensible(token);
        }

        return token;
    }

    private String LecturaFichero(){
        try{
            int caracterAscii = texto.read();

            String caracterString = Character.toString((char) caracterAscii);

            columnaFichero++;
            return caracterString;

        } catch (IOException e) {
            System.out.println("Error al leer el fichero" + e.getMessage());
        }
        return null;
    }

    private void RetrocederFichero(){
        try{
            texto.seek(texto.getFilePointer()-1);
            columnaFichero--;
        } catch (IOException e) {
            System.out.println("Error al leer el fichero" + e.getMessage());
        }
    }

    private Token LexemaExtensible(Token token){
        String caracterString = LecturaFichero();
        if(IsANum(caracterString)){
            token.tipo = 24;
            do{
                if(caracterString != ".") {
                    token.lexema += caracterString;
                    token.columna = columnaFichero;
                } else if(caracterString == "."){
                    caracterString = LecturaFichero();
                    RetrocederFichero();
                    if(!IsANum(caracterString)){
                        break;
                    }else{
                        token.lexema += caracterString;
                        token.tipo = 25;
                        token.columna = columnaFichero;
                    }
                }
                caracterString = LecturaFichero();
            }while(IsANum(caracterString) || caracterString == ".");
            RetrocederFichero();
        } else if(IsAlfa(caracterString)){
            token.tipo = 23;
            //System.out.println("hola");
            do{
                //System.out.println(caracterString);
                token.lexema += caracterString;
                token.columna = columnaFichero;
                caracterString = LecturaFichero();
            }while(IsAlfa(caracterString) || IsANum(caracterString));
        }

        return token;
    }

    public boolean IsANum(String lexema){
        return lexema != null && lexema.matches("[0-9]{1}");
    }

    public boolean IsAlfa(String lexema){
        return lexema.matches("[a-zA-Z]{1}");
    }

    private void EsComando(Token token){
        switch (token.lexema){
            case "var":
                token.tipo = 9;
                token.lexema = "'var'";
                break;
            case "real":
                token.tipo = 10;
                token.lexema = "'real'";
                break;
            case "entero":
                token.tipo = 11;
                token.lexema = "'entero'";
                break;
            case "algoritmo":
                token.tipo = 12;
                token.lexema = "'algoritmo'";
                break;
            case "blq":
                token.tipo = 13;
                token.lexema = "'blq'";
                break;
            case "fblq":
                token.tipo = 14;
                token.lexema = "'fblq'";
                break;
            case "funcion":
                token.tipo = 15;
                token.lexema = "'funcion'";
                break;
            case "si":
                token.tipo = 16;
                token.lexema = "'si'";
                break;
            case "entonces":
                token.tipo = 17;
                token.lexema = "'entonces'";
                break;
            case "sino":
                token.tipo = 18;
                token.lexema = "'sino'";
                break;
            case "fsi":
                token.tipo = 19;
                token.lexema = "'fsi'";
                break;
            case "mientras":
                token.tipo = 20;
                token.lexema = "'mientras'";
                break;
            case "hacer":
                token.tipo = 21;
                token.lexema = "'hacer'";
                break;
            case "escribir":
                token.tipo = 22;
                token.lexema = "'escribir'";
                break;
        }
    }
}
