package br.com.contatos.bean;

import java.io.Serializable;

/**
 * Created by ANDRE on 12/11/2017.
 */

public class ContatoBean implements Serializable {

    private int id;
    private String videoFavorito;
    private String musicaFavorita;
    private String email;
    private String nome;
    private String celular;
    private String endereco;
    private String siteFavorito;
    private String caminhoFoto;
    private boolean favorito;

    public ContatoBean(){

    }

    public ContatoBean(String videoFavorito, String musicaFavorita, String email, String nome, String celular, String endereco, String siteFavorito, String caminhoFoto, boolean favorito) {
        this.videoFavorito = videoFavorito;
        this.musicaFavorita = musicaFavorita;
        this.email = email;
        this.nome = nome;
        this.celular = celular;
        this.endereco = endereco;
        this.siteFavorito = siteFavorito;
        this.caminhoFoto = caminhoFoto;
        this.favorito = favorito;
    }

    public ContatoBean(String videoFavorito, String musicaFavorita, String email, String nome, String celular, String siteFavorito, boolean favorito) {
        this.videoFavorito = videoFavorito;
        this.musicaFavorita = musicaFavorita;
        this.email = email;
        this.nome = nome;
        this.celular = celular;
        this.siteFavorito = siteFavorito;
        this.favorito = favorito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoFavorito() {
        return videoFavorito;
    }

    public void setVideoFavorito(String videoFavorito) {
        this.videoFavorito = videoFavorito;
    }

    public String getMusicaFavorita() {
        return musicaFavorita;
    }

    public void setMusicaFavorita(String musicaFavorita) {
        this.musicaFavorita = musicaFavorita;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSiteFavorito() {
        return siteFavorito;
    }

    public void setSiteFavorito(String siteFavorito) {
        this.siteFavorito = siteFavorito;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    @Override
    public String toString() {
        return "ContatoBean{" +
                "id=" + id +
                ", musicaFavorita='" + musicaFavorita + '\'' +
                ", email='" + email + '\'' +
                ", nome='" + nome + '\'' +
                ", celular='" + celular + '\'' +
                '}';
    }
}
