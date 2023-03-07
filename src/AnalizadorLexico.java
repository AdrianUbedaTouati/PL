import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

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
        String caracterString;

        do{
            caracterString = LecturaFichero();

            token.columna = columnaFichero;
            token.fila = filaFichero;

            switch (caracterString) {
                case "(":
                    token.lexema = caracterString;
                    token.tipo = 0;
                    break;

                case ")":
                    token.lexema = caracterString;
                    token.tipo = 1;
                    break;

                case "*":
                    token.lexema = caracterString;
                    token.tipo = 2;
                    break;
                case "/":
                    token.lexema = caracterString;
                    token.tipo = 2;
                    caracterString = LecturaFichero();
                    if (Objects.equals(caracterString, "/")) {
                        token.lexema += caracterString;
                    } else {
                        RetrocederFichero();
                    }
                    break;

                case "+", "-":
                    token.lexema = caracterString;
                    token.tipo = 3;
                    break;

                case "<":
                    token.lexema = caracterString;
                    token.tipo = 4;
                    caracterString = LecturaFichero();
                    if (Objects.equals(caracterString, "=") || Objects.equals(caracterString, ">")) {
                        token.lexema += caracterString;
                    } else {
                        RetrocederFichero();
                    }
                    break;
                case ">":
                    token.lexema = caracterString;
                    token.tipo = 4;
                    caracterString = LecturaFichero();
                    if (Objects.equals(caracterString, "=")) {
                        token.lexema += caracterString;
                    } else {
                        RetrocederFichero();
                    }
                    break;
                case "=":
                    token.lexema = caracterString;
                    token.tipo = 4;
                    break;

                case ";":
                    token.lexema = caracterString;
                    token.tipo = 5;
                    break;

                case ":":
                    token.lexema = caracterString;
                    token.tipo = 6;
                    caracterString = LecturaFichero();
                    if (Objects.equals(caracterString, "=")) {
                        token.lexema += caracterString;
                        token.tipo = 8;
                    } else {
                        RetrocederFichero();
                    }
                    break;

                case ",":
                    token.lexema = caracterString;
                    token.tipo = 7;
                    break;
                case "\n":
                    columnaFichero = 0;
                    filaFichero++;
                    break;

                default:
                    if (IsANum(caracterString) || IsAlfa(caracterString)) {
                        RetrocederFichero();
                        token = LexemaExtensible(token);
                    }
            }
        }while (token.lexema == "" && caracterString != "/n");

        return token;
    }

    /**
     * Es el metodo encargado de leer cada caracter del fichero,
     * si detecta el fin del fichero cierra el programa con System.exit(-1)
     * @return Caracter a leer
     */
    private String LecturaFichero(){
        String caracterString = new String();
        try{
            int caracterAscii = texto.read();
            //Fin del fichero
            if(caracterAscii == -1){
                System.out.println("Error lexico: fin de fichero inesperado");
                System.exit(-1);
            }

            caracterString = Character.toString((char) caracterAscii);
            columnaFichero++;
        } catch (IOException e) {
            System.out.println("Error al leer el fichero" + e.getMessage());
        }
        return caracterString;
    }

    /**
     * Se encagar de enviar el puntero de la lectura del fichero uno mas atras,
     * cuando retrocede, columnaFichero disminuye en 1
     */
    private void RetrocederFichero(){
        try{
            texto.seek(texto.getFilePointer()-1);
            columnaFichero--;
        } catch (IOException e) {
            System.out.println("Error al leer el fichero" + e.getMessage());
        }
    }

    /**
     * Es el metodo encargado de crear los tokens variables
     * @param token
     * @return token
     */
    private Token LexemaExtensible(Token token){
        String caracterString = LecturaFichero();
        if(IsANum(caracterString)){
            token.tipo = 24;
            do{
                if(caracterString != ".") {
                    token.lexema += caracterString;
                } else if(caracterString == "."){
                    caracterString = LecturaFichero();
                    RetrocederFichero();
                    if(!IsANum(caracterString)){
                        break;
                    }else{
                        token.lexema += caracterString;
                        token.tipo = 25;
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
                caracterString = LecturaFichero();
            }while(IsAlfa(caracterString) || IsANum(caracterString));
            RetrocederFichero();
        }
        IsComand(token);

        return token;
    }

    /**
     * Verifica si el caracter pasado como parametro es un numero
     * @param lexema
     * @return
     */
    public boolean IsANum(String lexema){
        return lexema != null && lexema.matches("[0-9]{1}");
    }

    /**
     * Verifica si el caracter pasado como parametro es una letra Minuscula o Mayuscula
     * @param lexema
     * @return
     */
    public boolean IsAlfa(String lexema){
        return lexema.matches("[a-zA-Z]{1}");
    }

    /**
     * Verifica que el token no es un comando, antes de llamar a este metodo los tokens seran
     * tratados como ID
     * @param token
     */
    private void IsComand(Token token){
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
