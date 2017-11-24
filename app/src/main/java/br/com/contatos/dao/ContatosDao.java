package br.com.contatos.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.contatos.bean.ContatoBean;
import br.com.contatos.helper.DataBaseHelper;

/**
 * Created by ANDRE on 10/11/2017.
 */

public class ContatosDao {

    private static DataBaseHelper helper = null;
    private static SQLiteDatabase db = null;
    private List<Map<String,Object>> contatos;

    public ContatosDao(Context context){
        helper = new DataBaseHelper(context);
    }

    private ContatoBean criaConta(Cursor cursor){
        boolean favorito =  (cursor.getInt(cursor.getColumnIndex(DataBaseHelper.Contato.FAVORITO))) == 1;
        ContatoBean contatoBean = new ContatoBean
                (cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.VIDEO)),
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.MUSICA)),
                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.EMAIL)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.NOME)),
                                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.CELULAR)),
                                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.ENDERECO)),
                                                        cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.SITE)),
                                                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.Contato.FOTO)),
                                                                       favorito);
        contatoBean.setId(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.Contato._ID)));
        return contatoBean;
    }

    public long inserirContato(ContatoBean contatoBean){
        db = helper.getWritableDatabase();
        long resultado;
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.Contato.VIDEO,contatoBean.getVideoFavorito());
        values.put(DataBaseHelper.Contato.MUSICA,contatoBean.getMusicaFavorita());
        values.put(DataBaseHelper.Contato.EMAIL,contatoBean.getEmail());
        values.put(DataBaseHelper.Contato.ENDERECO,contatoBean.getEndereco());
        values.put(DataBaseHelper.Contato.NOME,contatoBean.getNome());
        values.put(DataBaseHelper.Contato.CELULAR,contatoBean.getCelular());
        values.put(DataBaseHelper.Contato.SITE,contatoBean.getSiteFavorito());
        values.put(DataBaseHelper.Contato.FOTO,contatoBean.getCaminhoFoto());

        if(contatoBean.isFavorito()){
            values.put(DataBaseHelper.Contato.FAVORITO,"1");
        }
        else {
            values.put(DataBaseHelper.Contato.FAVORITO,"0");
        }

        if(contatoBean.getId() == 0){
            resultado = db.insert(DataBaseHelper.Contato.TABELA, null , values);
        }
        else {
            String[] whereArgs = new String[]{contatoBean.getId()+""};
            resultado = db.update(DataBaseHelper.Contato.TABELA,values," _id = ?", whereArgs);
        }
        db.close();
        return resultado;
    }

    public List<Map<String, Object>> listarContatos(){
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DataBaseHelper.Contato.TABELA+" ORDER BY "+DataBaseHelper.Contato.NOME+" ASC",null);
        cursor.moveToFirst();
        contatos = new ArrayList<>();

        for(int i=0;i<cursor.getCount();i++){
            Map<String,Object> item = new HashMap<>();

            ContatoBean contatoBean = criaConta(cursor);

            item.put(DataBaseHelper.Contato.VIDEO,contatoBean.getVideoFavorito());
            item.put(DataBaseHelper.Contato.MUSICA,contatoBean.getMusicaFavorita());
            item.put(DataBaseHelper.Contato._ID,contatoBean.getId());
            item.put(DataBaseHelper.Contato.EMAIL,contatoBean.getEmail());
            item.put(DataBaseHelper.Contato.ENDERECO,contatoBean.getEndereco());
            item.put(DataBaseHelper.Contato.NOME,contatoBean.getNome());
            item.put(DataBaseHelper.Contato.CELULAR,contatoBean.getCelular());
            item.put(DataBaseHelper.Contato.SITE,contatoBean.getSiteFavorito());
            item.put(DataBaseHelper.Contato.FOTO,contatoBean.getCaminhoFoto());
            if(contatoBean.isFavorito()){
                item.put(DataBaseHelper.Contato.FAVORITO,"TRUE");
            }
            else{
                item.put(DataBaseHelper.Contato.FAVORITO,"FALSE");
            }
            contatos.add(item);
            cursor.moveToNext();
        }
        return contatos;
    }

    public int delete(Integer id){
        db = helper.getWritableDatabase();
        String where[] = new String[] {id.toString()};
        int resultado = db.delete(DataBaseHelper.Contato.TABELA,"_id = ?",where);
        db.close();
        return resultado;
    }

    public List<Map<String, Object>> listaContatosFavoritos(){
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+DataBaseHelper.Contato.TABELA+" WHERE "+DataBaseHelper.Contato.FAVORITO+" = 1 ORDER BY "+DataBaseHelper.Contato.NOME+" ASC",null);
        cursor.moveToFirst();
        contatos = new ArrayList<>();

        for(int i=0;i<cursor.getCount();i++){
            Map<String,Object> item = new HashMap<>();

            ContatoBean contatoBean = criaConta(cursor);

            item.put(DataBaseHelper.Contato.VIDEO,contatoBean.getVideoFavorito());
            item.put(DataBaseHelper.Contato.MUSICA,contatoBean.getMusicaFavorita());
            item.put(DataBaseHelper.Contato._ID,contatoBean.getId());
            item.put(DataBaseHelper.Contato.EMAIL,contatoBean.getEmail());
            item.put(DataBaseHelper.Contato.ENDERECO,contatoBean.getEndereco());
            item.put(DataBaseHelper.Contato.NOME,contatoBean.getNome());
            item.put(DataBaseHelper.Contato.CELULAR,contatoBean.getCelular());
            item.put(DataBaseHelper.Contato.SITE,contatoBean.getSiteFavorito());
            item.put(DataBaseHelper.Contato.FOTO,contatoBean.getCaminhoFoto());
            item.put(DataBaseHelper.Contato.FAVORITO,"TRUE");
            contatos.add(item);
            cursor.moveToNext();
        }
        return contatos;
    }

}
