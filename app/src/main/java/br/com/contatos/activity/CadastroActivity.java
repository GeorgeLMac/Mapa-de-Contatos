package br.com.contatos.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.instantapps.ActivityCompat;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.File;

import br.com.contatos.R;
import br.com.contatos.bean.ContatoBean;
import br.com.contatos.dao.ContatosDao;
import br.com.contatos.fragments.ContatoFragment;
import br.com.contatos.helper.SessionManager;

/**
 * Created by ANDRE on 15/11/2017.
 */

public class CadastroActivity extends AppCompatActivity {

    private ContatoBean contatoSelecionado;
    private ImageView imageViewfoto;
    private FloatingActionButton foto;
    private FloatingActionButton galeria;
    private EditText nome;
    private MaskedEditText celular;
    private EditText email;
    private EditText site;
    private EditText musica;
    private EditText video;
    private Button enderecoPesquisado;
    private CheckBox favorito;
    private Button salva;
    private SessionManager sessionManager;
    private int REQUEST_CODE = 200;
    private int CAMERA = 10;
    private int GALERIA = 11;
    private String caminhoFoto;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro_contato);
        contatoSelecionado = (getIntent().hasExtra("contato")) ? (ContatoBean) getIntent().getSerializableExtra("contato") : null;
        sessionManager = new SessionManager(getBaseContext());
        iniciaComponentes();
        inseriCamposExistentes();
        setupToolbar();
        enderecoPesquisado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesquisaEndereco();
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tiraFoto();
            }
        });
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibeGaleria();
            }
        });
        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvaContato();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Verificando resposta da API de Endereços
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Place endereco = PlaceAutocomplete.getPlace(getBaseContext(),data);
                String enderecoContato = endereco.getAddress().toString();
                enderecoPesquisado.setText(enderecoContato);
                sessionManager.setEndereco(enderecoContato);
            }
            else if(resultCode == PlaceAutocomplete.RESULT_ERROR){
                Status status = PlaceAutocomplete.getStatus(getBaseContext(), data);
                Log.i("Erro", status.getStatusMessage());
            }
            else if(resultCode == RESULT_CANCELED){
                Log.i("Operação cancelada", "Usuário cancelou a consulta");
            }
        }
        // Verificando resposta da Intent da camera
        if(requestCode == CAMERA && resultCode == RESULT_OK){
            carregarImagem(imageViewfoto,caminhoFoto);
        }
        //Verificando resposta da Intent da galeria
        if (resultCode == RESULT_OK && requestCode == GALERIA) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            caminhoFoto = c.getString(columnIndex);
            c.close();
            carregarImagem(imageViewfoto,caminhoFoto);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.setCadastro(false);
        sessionManager.limpaVariaveis();
        startActivity(new Intent(CadastroActivity.this,MainActivity.class));
    }

    public void pesquisaEndereco(){
        try {
            startActivityForResult(new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this), REQUEST_CODE);

        }
        catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(getBaseContext(),"Erro no apliactivo GooglePlayServices",Toast.LENGTH_LONG);
            Log.e("GooglePlayServiceError",e.getMessage());
        }
        catch(GooglePlayServicesNotAvailableException e){
            Toast.makeText(getBaseContext(),"Instale o aplicativo GooglePlayServices",Toast.LENGTH_LONG);
            Log.e("GooglePlayServiceError",e.getMessage());
        }
    }

    public void iniciaComponentes(){
        favorito = (CheckBox) findViewById(R.id.contato_favorito);
        imageViewfoto = (ImageView) findViewById(R.id.foto_contato);
        foto = (FloatingActionButton) findViewById(R.id.tira_foto);
        galeria = (FloatingActionButton) findViewById(R.id.acessa_galeria);
        nome = (EditText) findViewById(R.id.nome_contato_cadastro);
        celular = (MaskedEditText) findViewById(R.id.telefone_usuario_cadastro);
        email = (EditText) findViewById(R.id.email_contato_cadastro);
        site = (EditText) findViewById(R.id.site_contato_cadastro);
        musica = (EditText) findViewById(R.id.musica_contato_cadastro);
        video = (EditText) findViewById(R.id.video_contato_cadastro);
        enderecoPesquisado = (Button) findViewById(R.id.endereco_usuario);
        salva = (Button) findViewById(R.id.cadastrar);
        if(sessionManager.getFoto() != null){
            caminhoFoto = sessionManager.getFoto();
            carregarImagem(imageViewfoto,caminhoFoto);
        }
        if(sessionManager.getEndereco() != null){
            enderecoPesquisado.setText(sessionManager.getEndereco());
        }
        else{
            enderecoPesquisado.setText("Alterar Endereço");
        }
    }

    public void tiraFoto(){
        Intent tiraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(tiraFoto.resolveActivity(getPackageManager()) != null){
            // Coletando caminho da foto
            caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis()+".jpg";
            Uri localFoto = Uri.fromFile(new File(caminhoFoto));
            tiraFoto.putExtra(MediaStore.EXTRA_OUTPUT, localFoto);
            startActivityForResult(tiraFoto, CAMERA);
        }
    }

    public void exibeGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALERIA);
    }

    public void inseriCamposExistentes(){
        enderecoPesquisado.setText("ALTERAR ENDEREÇO");
        salva.setText("CADASTRAR");
        if(contatoSelecionado != null){
            salva.setText("ALTERAR");
            video.setText(contatoSelecionado.getVideoFavorito());
            musica.setText(contatoSelecionado.getMusicaFavorita());
            site.setText(contatoSelecionado.getSiteFavorito());
            email.setText(contatoSelecionado.getEmail());
            celular.setText(contatoSelecionado.getCelular());
            nome.setText(contatoSelecionado.getNome());
            if(contatoSelecionado.isFavorito()){
                favorito.setChecked(true);
            }
            if(contatoSelecionado.getEndereco() != null){
                enderecoPesquisado.setText(contatoSelecionado.getEndereco());
            }
            if(contatoSelecionado.getCaminhoFoto() != null){
                carregarImagem(imageViewfoto,contatoSelecionado.getCaminhoFoto());
            }
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
        sessionManager.setFoto(caminhoFoto);
    }

    public void salvaContato(){

        String nomeSalvo = nome.getText().toString().trim();
        String telefoneSalvo = celular.getText().toString().trim();
        String emailSalvo = email.getText().toString().trim();
        String siteSalvo = site.getText().toString().trim();
        String musicaSalva = musica.getText().toString().trim();
        String endereco = enderecoPesquisado.getText().toString().trim();
        String enderecoSalvo = !endereco.equals("ALTERAR ENDEREÇO")? endereco: "";
        String videoSalvo = video.getText().toString().trim();
        String foto = null;
        if(imageViewfoto.getTag() != null){
            foto = imageViewfoto.getTag().toString();
        }
        boolean favoritoSalvo;
        if (favorito.isChecked())
            favoritoSalvo = true;
        else favoritoSalvo = false;

        //Verifico se nome e telefone não estão vazios

        if(nome.getText().toString().isEmpty() || celular.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(),"NOME OU TELEFONE ESTÃO VAZIOS",Toast.LENGTH_LONG).show();
        }
        else{
            ContatoBean contatoBeanCadastro = new ContatoBean(videoSalvo,musicaSalva,emailSalvo,nomeSalvo,telefoneSalvo,enderecoSalvo,siteSalvo,foto,favoritoSalvo);
            int id = 0;
            if(getIntent().hasExtra("contato")){
                id = contatoSelecionado.getId();
            }
            contatoBeanCadastro.setId(id);
            ContatosDao contatosDao = new ContatosDao(getBaseContext());
            long idContatoSalvo = contatosDao.inserirContato(contatoBeanCadastro);
            if(idContatoSalvo != -1){
                // Limpando a exibição da imagem e do endereço
                sessionManager.setFoto(null);
                sessionManager.setEndereco(null);
                if(id == 0){
                    Toast.makeText(getBaseContext(),"Contato Criado",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getBaseContext(),"Contato Alterado",Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
            else{
                Toast.makeText(getBaseContext(),"Usuário não foi cadastrado",Toast.LENGTH_LONG);
            }
        }
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setShowHideAnimationEnabled(true);
            bar.setTitle("Cadastro de Contatos");
        }
    }
}
