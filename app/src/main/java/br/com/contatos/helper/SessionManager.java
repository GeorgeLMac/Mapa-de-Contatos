package br.com.contatos.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.contatos.bean.ContatoBean;

import static br.com.contatos.helper.DataBaseHelper.Contato.CELULAR;
import static br.com.contatos.helper.DataBaseHelper.Contato.EMAIL;
import static br.com.contatos.helper.DataBaseHelper.Contato.ENDERECO;
import static br.com.contatos.helper.DataBaseHelper.Contato.FAVORITO;
import static br.com.contatos.helper.DataBaseHelper.Contato.FOTO;
import static br.com.contatos.helper.DataBaseHelper.Contato.MUSICA;
import static br.com.contatos.helper.DataBaseHelper.Contato.NOME;
import static br.com.contatos.helper.DataBaseHelper.Contato.SITE;
import static br.com.contatos.helper.DataBaseHelper.Contato.VIDEO;
import static br.com.contatos.helper.DataBaseHelper.Contato._ID;

/**
 * Created by ANDRE on 13/11/2017.
 */

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    String CADASTRO = "cadastro";

    int PRIVATE_MODE = 0;


    public SessionManager(Context context){
        this.context = context;
        pref = context.getSharedPreferences("Contato",PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean cadastro(){
        return pref.getBoolean(CADASTRO,false);
    }

    public void setCadastro(boolean cadastro){
        editor.putBoolean(CADASTRO,cadastro);
    }

    public void insereContato(ContatoBean contatoBean){
        limpaVariaveis();
        if(contatoBean.getId() != 0){
            editor.putLong(_ID,contatoBean.getId());
        }
        if(contatoBean.getNome() != null){
            editor.putString(NOME,contatoBean.getNome());
        }
        if(contatoBean.getCelular() != null){
            editor.putString(CELULAR,contatoBean.getCelular());
        }
        if(contatoBean.getEmail() != null){
            editor.putString(EMAIL,contatoBean.getEmail());
        }
        if(contatoBean.getSiteFavorito() != null){
            editor.putString(SITE,contatoBean.getSiteFavorito());
        }
        if(contatoBean.getMusicaFavorita() != null){
            editor.putString(MUSICA,contatoBean.getMusicaFavorita());
        }
        if(contatoBean.getVideoFavorito() != null){
            editor.putString(VIDEO,contatoBean.getVideoFavorito());
        }
        if(contatoBean.getCaminhoFoto() != null){
            editor.putString(FOTO,contatoBean.getCaminhoFoto());
        }
        if(contatoBean.getEndereco() != null){
            editor.putString(ENDERECO,contatoBean.getEndereco());
        }
        editor.putBoolean(FAVORITO,contatoBean.isFavorito());
        editor.commit();
    }

    public ContatoBean retornaUsuario(){
        ContatoBean contato = new ContatoBean();
        contato.setId(pref.getInt(_ID,0));
        contato.setNome(pref.getString(NOME,""));
        contato.setCelular(pref.getString(CELULAR,""));
        contato.setEmail(pref.getString(EMAIL,""));
        contato.setSiteFavorito(pref.getString(SITE,""));
        contato.setMusicaFavorita(pref.getString(MUSICA,""));
        contato.setEndereco(pref.getString(ENDERECO,null));
        contato.setVideoFavorito(pref.getString(VIDEO,""));
        contato.setFavorito(pref.getBoolean(FAVORITO,false));
        contato.setCaminhoFoto(pref.getString(FOTO,null));
        return contato;
    }

    public void setFoto(String caminhofoto){
        editor.putString(FOTO,caminhofoto);
        editor.commit();
    }

    public String getFoto(){
        return pref.getString(FOTO,null);
    }

    public void setEndereco(String endereco){
        editor.putString(ENDERECO,endereco);
        editor.commit();
    }

    public String getEndereco(){
        return pref.getString(ENDERECO,null);
    }

    public void limpaVariaveis(){
        editor.remove(_ID).commit();
        editor.remove(NOME).commit();
        editor.remove(CELULAR).commit();
        editor.remove(EMAIL).commit();
        editor.remove(SITE).commit();
        editor.remove(MUSICA).commit();
        editor.remove(ENDERECO).commit();
        editor.remove(VIDEO).commit();
        editor.remove(FAVORITO).commit();
        editor.remove(FOTO).commit();
        editor.clear().commit();;
    }

}
