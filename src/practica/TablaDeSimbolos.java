package practica;

import java.util.HashMap;
import java.util.TreeMap;

public class TablaDeSimbolos {
    TreeMap<String, ID> TDS;
    TreeMap<String, TreeMap<String, ID>> TDSFunc = new TreeMap<>();

    public TablaDeSimbolos(TreeMap<String, ID> TDS) {
        this.TDS = TDS;
        TDSFunc = new TreeMap<>();
    }
    public void addIdTDSFunc(String func,ID id) {
         TreeMap<String, ID> a=TDSFunc.get(func);
         a.put(id.lexema, id);
         TDSFunc.put(func, a);
    }
}


class ID {
    String lexema;
    String tipo;
    int desp;// No en func
    int numParam;
    TreeMap<Integer, Atributo> Atributos;
    // HashMap<Integer, Integer> ModoParam;
    String TipoRetorno;
    // String EtiqFunc;

    public ID(String lexema) {
        this.lexema = lexema;
        // this.tipo=tipo;
    }

}

class Atributo {
    String tipoAt;
    String lexAt;
    int desp;
    // int posAtr;
    //int tipoParam;

    public Atributo(String lex, String tipo, int desp) {
        this.tipoAt = tipo;
        this.lexAt = lex;
        this.desp = desp;
    }
}