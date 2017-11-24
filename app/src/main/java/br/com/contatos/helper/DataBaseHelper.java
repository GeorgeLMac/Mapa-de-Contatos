package br.com.contatos.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ANDRE on 12/11/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String BANCO_DADOS = "Contatos";
    private static final int VERSAO = 1;

    public static class Contato{
        public static final String TABELA = "contatos";
        public static final String _ID = "_id";
        public static final String VIDEO = "video";
        public static final String MUSICA = "musica";
        public static final String EMAIL = "email";
        public static final String NOME = "nome";
        public static final String CELULAR = "celuar";
        public static final String ENDERECO = "endereco";
        public static final String SITE = "site";
        public static final String FOTO = "caminho_foto";
        public static final String FAVORITO = "favorito";
        public static final String[] COLUNAS =
                new String[]{_ID,VIDEO,MUSICA,EMAIL,NOME,CELULAR,ENDERECO,SITE,FOTO,FAVORITO};
    }

    public DataBaseHelper(Context context){
        super(context, BANCO_DADOS, null,VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+Contato.TABELA+
                        " ("+Contato._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                             Contato.NOME+" TEXT, " +
                             Contato.EMAIL+" TEXT, " +
                             Contato.FOTO+" TEXT, " +
                             Contato.SITE+" TEXT, " +
                             Contato.VIDEO+" TEXT, " +
                             Contato.MUSICA+" TEXT, " +
                             Contato.CELULAR+" TEXT, " +
                             Contato.ENDERECO+" TEXT, " +
                             Contato.FAVORITO+" INTEGER );"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Contato.TABELA);
        onCreate(db);
    }
}
