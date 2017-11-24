package br.com.contatos.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.File;

import br.com.contatos.R;
import br.com.contatos.activity.MainActivity;
import br.com.contatos.bean.ContatoBean;
import br.com.contatos.dao.ContatosDao;
import br.com.contatos.helper.SessionManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by ANDRE on 10/11/2017.
 */

public class ContatoFragment extends Fragment {

    private Button buttonPesquisarEndereco;
    private String caminhoFoto = "";
    private int REQUEST_CODE = 200;
    private int CAMERA = 10;
    private int GALERIA = 11;
    private String enderecoContato;
    private FloatingActionButton floatingActionButtonTirarFoto;
    private FloatingActionButton floatingActionButtonExibeGaleria;
    private ImageView imageViewFotoContato;
    private SessionManager sessionManager;
    private View view;
    private Button cadastra;
    private ContatoBean contatoExibido;
    private boolean fragmentoCriado = false;

    public ContatoFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cadastro_contato, container, false);
        buttonPesquisarEndereco = (Button) view.findViewById(R.id.endereco_usuario);
        floatingActionButtonExibeGaleria = (FloatingActionButton) view.findViewById(R.id.acessa_galeria);
        floatingActionButtonTirarFoto = (FloatingActionButton) view.findViewById(R.id.tira_foto);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        imageViewFotoContato = (ImageView) view.findViewById(R.id.foto_contato);
        if(!sessionManager.cadastro()){
            contatoExibido = sessionManager.retornaUsuario();
        }
        cadastra = (Button) view.findViewById(R.id.cadastrar);
        if(contatoExibido != null){
            ((EditText) view.findViewById(R.id.nome_contato_cadastro)).setText(contatoExibido.getNome());
            ((MaskedEditText) view.findViewById(R.id.telefone_usuario_cadastro)).setText(contatoExibido.getCelular());
            ((EditText) view.findViewById(R.id.email_contato_cadastro)).setText(contatoExibido.getEmail());
            ((EditText) view.findViewById(R.id.site_contato_cadastro)).setText(contatoExibido.getSiteFavorito());
            ((EditText) view.findViewById(R.id.musica_contato_cadastro)).setText(contatoExibido.getMusicaFavorita());
            ((Button) view.findViewById(R.id.endereco_usuario)).setText(contatoExibido.getEndereco());
            ((EditText) view.findViewById(R.id.video_contato_cadastro)).setText(contatoExibido.getVideoFavorito());
            if(contatoExibido.getCaminhoFoto() != null){
                carregarImagem(imageViewFotoContato, contatoExibido.getCaminhoFoto());
                imageViewFotoContato.setTag(contatoExibido.getCaminhoFoto());
                Log.i("caminho",contatoExibido.getCaminhoFoto());
            }
            if(contatoExibido.isFavorito()){
                ((CheckBox) view.findViewById(R.id.contato_favorito)).setChecked(true);
            }
            cadastra.setText("ALTERAR - 103");
        }
        else {
            cadastra.setText("CADASTRAR - 106");
        }
        cadastra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.limpaVariaveis();
                String nome = ((EditText) view.findViewById(R.id.nome_contato_cadastro)).getText().toString().trim();
                String telefone = ((MaskedEditText) view.findViewById(R.id.telefone_usuario_cadastro)).getText().toString().trim();
                String email = ((EditText) view.findViewById(R.id.email_contato_cadastro)).getText().toString().trim();
                String site = ((EditText) view.findViewById(R.id.site_contato_cadastro)).getText().toString().trim();
                String musica = ((EditText) view.findViewById(R.id.musica_contato_cadastro)).getText().toString().trim();
                String endereco = ((Button) view.findViewById(R.id.endereco_usuario)).getText().toString().trim();
                String video = ((EditText) view.findViewById(R.id.video_contato_cadastro)).getText().toString().trim();
                String foto = "";
                if(imageViewFotoContato.getTag() != null){
                    foto = imageViewFotoContato.getTag().toString();
                }
                boolean favorito;
                if (((CheckBox) view.findViewById(R.id.contato_favorito)).isChecked())
                    favorito = true;
                else favorito = false;

                //Verifico se nome e telefone não estão vazios

                if(nome.isEmpty() || telefone.isEmpty()){
                    Toast.makeText(getActivity().getBaseContext(),"NOME OU TELEFONE ESTÃO VAZIOS",Toast.LENGTH_LONG).show();
                }
                else{
                    ContatoBean contatoBeanCadastro = new ContatoBean(video,musica,email,nome,telefone,endereco,site,foto,favorito);
                    int id ;
                    if(contatoExibido.getId() != 0 && contatoExibido.getId() != 0){
                         id = contatoExibido.getId();
                    }
                    else{
                        id = 0;
                    }
                    contatoBeanCadastro.setId(id);
                    ContatosDao contatosDao = new ContatosDao(getActivity().getBaseContext());
                    id = (int) contatosDao.inserirContato(contatoBeanCadastro);
                    if(id != -1){
                        sessionManager = new SessionManager(getActivity().getBaseContext());
                        sessionManager.limpaVariaveis();
                        sessionManager.setCadastro(false);
                        if(id != contatoExibido.getId()){
                            Toast.makeText(getActivity().getBaseContext(),"Usuário Atualizado",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getActivity().getBaseContext(),"Usuário Cadastrado",Toast.LENGTH_LONG).show();
                        }
                        startActivity(new Intent(getActivity().getBaseContext(), MainActivity.class));
                    }
                    else{
                        Toast.makeText(getActivity().getBaseContext(),"Usuário não foi cadastrado",Toast.LENGTH_LONG);
                    }
                }
            }
        });
        if(sessionManager.getFoto() != null){
            caminhoFoto = sessionManager.getFoto();
            carregarImagem(imageViewFotoContato,caminhoFoto);
            Log.i("caminho",contatoExibido.getCaminhoFoto());
        }
        if(sessionManager.getEndereco() != null){
            buttonPesquisarEndereco.setText(sessionManager.getEndereco());
        }
        else{
            buttonPesquisarEndereco.setText("Alterar Endereço");
        }
        buttonPesquisarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivityForResult(new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity()), REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {
                    Toast.makeText(getActivity().getBaseContext(),"Erro no apliactivo GooglePlayServices",Toast.LENGTH_LONG);
                    Log.e("GooglePlayServiceError",e.getMessage());
                }
                catch(GooglePlayServicesNotAvailableException e){
                    Toast.makeText(getActivity().getBaseContext(),"Instale o aplicativo GooglePlayServices",Toast.LENGTH_LONG);
                    Log.e("GooglePlayServiceError",e.getMessage());
                }
            }
        });
        floatingActionButtonExibeGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GALERIA);
            }
        });

        floatingActionButtonTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tiraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(tiraFoto.resolveActivity(getActivity().getPackageManager()) != null){
                    // Coletando caminho da foto
                    caminhoFoto = getActivity().getExternalFilesDir(null) + "/" + System.currentTimeMillis()+".jpg";
                    Uri localFoto = Uri.fromFile(new File(caminhoFoto));
                    tiraFoto.putExtra(MediaStore.EXTRA_OUTPUT, localFoto);
                    startActivityForResult(tiraFoto, CAMERA);
                }
            }
        });
        setupToolbar(view);
        fragmentoCriado = true;
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(fragmentoCriado){
            int id = contatoExibido.getId() != 0 ? contatoExibido.getId() : 0;
            String nome = ((EditText) view.findViewById(R.id.nome_contato_cadastro)).getText().toString().trim();
            String telefone = ((MaskedEditText) view.findViewById(R.id.telefone_usuario_cadastro)).getText().toString().trim();
            String email = ((EditText) view.findViewById(R.id.email_contato_cadastro)).getText().toString().trim();
            String site = ((EditText) view.findViewById(R.id.site_contato_cadastro)).getText().toString().trim();
            String musica = ((EditText) view.findViewById(R.id.musica_contato_cadastro)).getText().toString().trim();
            String video = ((EditText) view.findViewById(R.id.video_contato_cadastro)).getText().toString().trim();
            String endereco = buttonPesquisarEndereco.getText().toString();
            boolean favorito = ((CheckBox) view.findViewById(R.id.contato_favorito)).isChecked();
            contatoExibido = new ContatoBean(video,musica,email,nome,telefone,site,favorito);
            if(imageViewFotoContato.getTag() != null){
                caminhoFoto = imageViewFotoContato.getTag().toString();
                Log.i("caminho",caminhoFoto);
                contatoExibido.setCaminhoFoto(caminhoFoto);
            }
            contatoExibido.setId(id);
            contatoExibido.setEndereco(endereco);
            SessionManager sessionManager = new SessionManager(getActivity().getBaseContext());
            sessionManager.insereContato(contatoExibido);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(fragmentoCriado){

            SessionManager sessionManager = new SessionManager(getActivity().getBaseContext());
            if(!sessionManager.cadastro()){
                contatoExibido = sessionManager.retornaUsuario();
            }
            Log.i("ID_USUARIO",contatoExibido.getId()+"");
            if(contatoExibido.getId() != 0){
                ((EditText) view.findViewById(R.id.nome_contato_cadastro)).setText(contatoExibido.getNome());
                ((MaskedEditText) view.findViewById(R.id.telefone_usuario_cadastro)).setText(contatoExibido.getCelular());
                ((EditText) view.findViewById(R.id.email_contato_cadastro)).setText(contatoExibido.getEmail());
                ((EditText) view.findViewById(R.id.site_contato_cadastro)).setText(contatoExibido.getSiteFavorito());
                ((EditText) view.findViewById(R.id.musica_contato_cadastro)).setText(contatoExibido.getMusicaFavorita());
                ((EditText) view.findViewById(R.id.video_contato_cadastro)).setText(contatoExibido.getVideoFavorito());
                if(contatoExibido.getCaminhoFoto() != null){
                    carregarImagem(imageViewFotoContato,contatoExibido.getCaminhoFoto());
                    Log.i("caminho",contatoExibido.getCaminhoFoto());
                }
                if(contatoExibido.isFavorito()){
                    ((CheckBox) view.findViewById(R.id.contato_favorito)).setChecked(true);
                }
                if(sessionManager.getEndereco() != null){
                    buttonPesquisarEndereco.setText(sessionManager.getEndereco());
                }
                else{
                    buttonPesquisarEndereco.setText("Alterar Endereço");
                }
                cadastra.setText("ALTERAR- 240");
            }
            else {
                ((EditText) view.findViewById(R.id.nome_contato_cadastro)).setText(contatoExibido.getNome());
                ((MaskedEditText) view.findViewById(R.id.telefone_usuario_cadastro)).setText(contatoExibido.getCelular());
                ((EditText) view.findViewById(R.id.email_contato_cadastro)).setText(contatoExibido.getEmail());
                ((EditText) view.findViewById(R.id.site_contato_cadastro)).setText(contatoExibido.getSiteFavorito());
                ((EditText) view.findViewById(R.id.musica_contato_cadastro)).setText(contatoExibido.getMusicaFavorita());
                ((EditText) view.findViewById(R.id.video_contato_cadastro)).setText(contatoExibido.getVideoFavorito());
                if(contatoExibido.getCaminhoFoto() != null){
                    carregarImagem(imageViewFotoContato,contatoExibido.getCaminhoFoto());
                }
                if(contatoExibido.isFavorito()){
                    ((CheckBox) view.findViewById(R.id.contato_favorito)).setChecked(true);
                }
                if(sessionManager.getEndereco() != null){
                    buttonPesquisarEndereco.setText(sessionManager.getEndereco());
                }
                else{
                    buttonPesquisarEndereco.setText("Alterar Endereço");
                }
                cadastra.setText("CADASTRAR - 257");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Verificando resposta da API de Endereços
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place endereco = PlaceAutocomplete.getPlace(getActivity().getBaseContext(),data);
                enderecoContato = endereco.getAddress().toString();
                buttonPesquisarEndereco.setText(enderecoContato);
                sessionManager.setEndereco(enderecoContato);
            }
            else if(resultCode == PlaceAutocomplete.RESULT_ERROR){
                Status status = PlaceAutocomplete.getStatus(getActivity().getBaseContext(), data);
                Log.i("Erro", status.getStatusMessage());
            }
            else if(resultCode == RESULT_CANCELED){
                Log.i("Operação cancelada", "Usuário cancelou a consulta");
            }
        }
        // Verificando resposta da Intent da camera
        if(requestCode == CAMERA && resultCode == RESULT_OK){
                carregarImagem(imageViewFotoContato,caminhoFoto);
                sessionManager.setFoto(caminhoFoto);
        }
        //Verificando resposta da Intent da galeria
        if (resultCode == RESULT_OK && requestCode == GALERIA) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getActivity().getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            caminhoFoto = c.getString(columnIndex);
            c.close();
            sessionManager.setFoto(caminhoFoto);
            carregarImagem(imageViewFotoContato,caminhoFoto);
        }
    }

    private void carregarImagem(ImageView imagem, String caminhoFoto){
        int larguraDesejada = imagem.getWidth();
        int alturaDesejada = 200;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(caminhoFoto,options);
        int altura = options.outHeight;
        int largura = options.outWidth;
        int fator = 1;
        if(altura > alturaDesejada || largura > larguraDesejada){
            final int metadeAltura = altura/2;
            final int metadeLargura = largura/2;

            while((metadeAltura/fator)> alturaDesejada && (metadeLargura/fator) > larguraDesejada){
                fator *= 2;
            }
        }
        if(fator > 8){
            fator = 8;
        }
        options.inSampleSize = fator;
        options.inJustDecodeBounds = false;
        imagem.setImageBitmap(BitmapFactory.decodeFile(caminhoFoto,options));
        imagem.setTag(caminhoFoto);
    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        final ActionBar bar = appCompatActivity.getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setShowHideAnimationEnabled(true);
            bar.setTitle("Cadastro de Contatos");
        }
    }
}
