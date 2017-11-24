package br.com.contatos.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.contatos.R;
import br.com.contatos.activity.CadastroActivity;
import br.com.contatos.bean.ContatoBean;
import br.com.contatos.dao.ContatosDao;
import br.com.contatos.helper.DataBaseHelper;

/**
 * Created by ANDRE on 14/11/2017.
 */

public class ListarContatosFragment extends ListFragment {

    private List<Map<String,Object>> listaContatos;
    private ListView listaViewContatos;
    private HashMap<String, Object> contatoSelecionado;
    private static final int TELEFONE_CODE_REQUEST = 10;
    public ListarContatosFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)  {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.lista_contatos, container, false);
        FloatingActionButton linkCadatro = (FloatingActionButton) view.findViewById(R.id.link_cadastro);
        linkCadatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getBaseContext(), CadastroActivity.class));
            }
        });
        setupToolbar(view);
        return view;
    }

    public void listarContatos(){
        ContatosDao contatosDao = new ContatosDao(getActivity().getBaseContext());
        String []de = {DataBaseHelper.Contato.NOME,DataBaseHelper.Contato.CELULAR};
        int[] para = {R.id.nome,R.id.celular};
        listaContatos = contatosDao.listarContatos();
        SimpleAdapter adapter = new SimpleAdapter(getContext(),listaContatos,R.layout.linha_contatos,de,para);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ViewGroup viewGroup = (ViewGroup) v;
        String txtNome = ((TextView) viewGroup.findViewById(R.id.nome)).getText().toString();
        Toast.makeText(getActivity(),txtNome,Toast.LENGTH_LONG).show();
        Map<String, Object> contato = listaContatos.get(position);
        boolean favorito = (contato.get(DataBaseHelper.Contato.FAVORITO)).equals("TRUE");
        ContatoBean contatoSelecionado = new ContatoBean((String) contato.get(DataBaseHelper.Contato.VIDEO) ,
                (String) contato.get(DataBaseHelper.Contato.MUSICA),
                (String) contato.get(DataBaseHelper.Contato.EMAIL),
                (String) contato.get(DataBaseHelper.Contato.NOME),
                (String) contato.get(DataBaseHelper.Contato.CELULAR),
                (String) contato.get(DataBaseHelper.Contato.ENDERECO),
                (String) contato.get(DataBaseHelper.Contato.SITE),
                (String) contato.get(DataBaseHelper.Contato.FOTO),
                favorito);
        contatoSelecionado.setId((int) contato.get(DataBaseHelper.Contato._ID));
        Intent intent = new Intent(getActivity().getBaseContext(),CadastroActivity.class);
        intent.putExtra("contato",contatoSelecionado);
        Log.i("contatoSelecionado",contatoSelecionado.toString());
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listaViewContatos = getListView();
        listarContatos();
        registerForContextMenu(listaViewContatos);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        contatoSelecionado = (HashMap) listaViewContatos.getAdapter().getItem(info.position);

        //Fazer Ligação

        menu.setHeaderIcon(R.drawable.ic_menu_black_24dp);
        menu.setHeaderTitle("MENU DE OPÇÕES");

        MenuItem ligar = menu.add("LIGAR");
        ligar.setIcon(R.drawable.ic_call_black_24dp);
        MenuItem sms = menu.add("ENVIAR SMS");
        sms.setIcon(R.drawable.ic_textsms_black_24dp);
        MenuItem site = menu.add("ACESSAR SITE");
        site.setIcon(R.drawable.ic_http_black_24dp);
        MenuItem mapa = menu.add("VER ENDEREÇO NO MAPA");
        mapa.setIcon(R.drawable.ic_room_black_24dp);
        MenuItem email = menu.add("COMPOR EMIAL");
        mapa.setIcon(R.drawable.ic_email_black_24dp);
        MenuItem video = menu.add("LOCALIZAR VIDEO");
        video.setIcon(R.drawable.ic_subscriptions_black_24dp);
        MenuItem musica = menu.add("LOCALIZAR MÚSICA");
        musica.setIcon(R.drawable.ic_album_black_24dp);
        MenuItem deletar = menu.add("DELETAR");
        deletar.setIcon(R.drawable.ic_delete_black_24dp);

        ligar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String permissaoLigacao = Manifest.permission.CALL_PHONE;
                if(android.support.v4.app.ActivityCompat.checkSelfPermission(getActivity(), permissaoLigacao) == PackageManager.PERMISSION_GRANTED){
                    fazerLigacao();
                }
                else {
                    android.support.v4.app.ActivityCompat.requestPermissions(getActivity(),new String[]{permissaoLigacao},TELEFONE_CODE_REQUEST);
                }
                return false;
            }
        });

        sms.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
				enviaMensagem();
                return false;
            }
        });

        site.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                acessarSite();
                return false;
            }
        });

        mapa.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                abrirLocalizacao();
                return false;
            }
        });

        email.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                enviarEmail();
                return false;
            }
        });

        musica.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                pesquisaMusica();
                return false;
            }
        });

        video.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                pesquisarVideo();
                return false;
            }
        });

        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContatosDao contatosDao = new ContatosDao(getContext());
                if(contatosDao.delete((int)contatoSelecionado.get(DataBaseHelper.Contato._ID)) == 1){
                    listarContatos();
                }
                return false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == TELEFONE_CODE_REQUEST){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fazerLigacao();
            }
            else {
                Toast.makeText(getActivity(),"Permissão de ligação recusada",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void pesquisarVideo(){
            if(!((String)contatoSelecionado.get(DataBaseHelper.Contato.VIDEO)).isEmpty()){
                Intent intentVideo = new Intent(Intent.ACTION_VIEW,Uri.parse("https://m.youtube.com/results?q="+contatoSelecionado.get(DataBaseHelper.Contato.VIDEO)));
                if(intentVideo.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intentVideo);
                }
                else {
                    Snackbar.make(getView(),"Erro ao pesquisar video",Snackbar.LENGTH_SHORT).show();
                }
            }
            else{
                Snackbar.make(getView(),"Contato não tem video cadastrado",Snackbar.LENGTH_LONG).show();
            }
    }

    public void abrirLocalizacao(){
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        if(!((String)contatoSelecionado.get(DataBaseHelper.Contato.ENDERECO)).isEmpty()){
            intentMapa.setData(Uri.parse("geo:0,0?q="+contatoSelecionado.get(DataBaseHelper.Contato.ENDERECO)));
            if(intentMapa.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intentMapa);
            }
            else {
                Snackbar.make(getView(),"Erro ao localizar endereço",Snackbar.LENGTH_SHORT).show();
            }
        }
        else{
            Snackbar.make(getView(),"Contato sem endereço",Snackbar.LENGTH_LONG).show();
        }
    }

    public void pesquisaMusica(){
        if(!((String)contatoSelecionado.get(DataBaseHelper.Contato.MUSICA)).isEmpty()) {
            Intent intentPesquisa = new Intent(Intent.ACTION_WEB_SEARCH);
            intentPesquisa.putExtra(SearchManager.QUERY,"música "+contatoSelecionado.get(DataBaseHelper.Contato.MUSICA));
            if(intentPesquisa.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intentPesquisa);
            }
            else {
                Snackbar.make(getView(),"Erro ao pesquisar música",Snackbar.LENGTH_SHORT).show();
            }
        }
        else {
            Snackbar.make(getView(),"Música não cadastrada",Snackbar.LENGTH_SHORT).show();
        }
    }

    public void enviarEmail(){
        if(!((String)contatoSelecionado.get(DataBaseHelper.Contato.EMAIL)).isEmpty()) {
            Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
            intentEmail.setData(Uri.parse("mailto:"));
            String [] emails = {(String) contatoSelecionado.get(DataBaseHelper.Contato.EMAIL)};
            intentEmail.putExtra(Intent.EXTRA_EMAIL,emails);
            if(intentEmail.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intentEmail);
            }
            else {
                Snackbar.make(getView(),"Erro ao compor e-mail",Snackbar.LENGTH_SHORT).show();
            }
        }
        else {
            Snackbar.make(getView(),"E-mail não cadastrado",Snackbar.LENGTH_LONG).show();
        }
    }

    public void acessarSite(){
        if(!((String)contatoSelecionado.get(DataBaseHelper.Contato.SITE)).isEmpty()){
            Intent intentSite = new Intent(Intent.ACTION_VIEW,Uri.parse("http:"+contatoSelecionado.get(DataBaseHelper.Contato.SITE)));
            if(intentSite.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intentSite);
            }
            else {
                Snackbar.make(getView(),"Erro ao acessar site",Snackbar.LENGTH_SHORT).show();
            }
        }
        else{
            Snackbar.make(getView(),"Contato não tem site cadastrado",Snackbar.LENGTH_LONG).show();
        }
    }

    public void enviaMensagem(){
        Intent intentMensagem = new Intent(Intent.ACTION_SENDTO);
        intentMensagem.setData(Uri.parse("smsto:"+contatoSelecionado.get(DataBaseHelper.Contato.CELULAR)));
        if(intentMensagem.resolveActivity(getActivity().getPackageManager()) != null){
            startActivity(intentMensagem);
        }
        else {
            Snackbar.make(getView(),"Erro ao enviar mensagem",Snackbar.LENGTH_LONG).show();
        }
    }

    private void fazerLigacao() {
        Intent intentLigar = new Intent(Intent.ACTION_CALL);
        intentLigar.setData(Uri.parse("tel:" + contatoSelecionado.get(DataBaseHelper.Contato.CELULAR)));
        if(intentLigar.resolveActivity(getActivity().getPackageManager()) != null){
			startActivity(intentLigar);
		}
		else{
			Snackbar.make(getView(),"Erro ao ligar",Snackbar.LENGTH_LONG).show();
		}
    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        final ActionBar bar = appCompatActivity.getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setShowHideAnimationEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            bar.setTitle("Contatos");
        }
    }
}
