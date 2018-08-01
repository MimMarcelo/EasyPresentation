package com.mimmarcelo.easypresentation;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mimmarcelo.classes.M;
import com.mimmarcelo.classes.Util;

public class MainActivity extends AppCompatActivity{

    //RETÉM REFERÊNCIAS PARA OS CAMPOS DO LAYOUT
    private TextView txtStatus;
    private Button btnLigarBluetooth;
    private Button btnVisibilidade;

    //RETÉM REFERÊNCIAS PARA OS COMPONENTES BLUETOOTH DO DISPOSITIVO
    private BluetoothAdapter antena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getReferencias();

        btnLigarBluetooth.setOnClickListener(ligarBluetooth);
        btnVisibilidade.setOnClickListener(ficarVisivel);

        //VERIFICA DISPONIBILIDADE DO BLUETOOTH NO DISPOSITIVO
        if(antena == null){//SE NÃO HOUVER ANTENA
            Util.alerta(MainActivity.this, getString(R.string.seu_dispositivo_nao_possui_bluetooth));
            habilitarCampos(false, R.string.seu_dispositivo_nao_possui_bluetooth);
        }
        else{//SE HOUVER ANTENA

            /*
                SE A VERSÃO DO ANDROID DO DISPOSITIVO FOR SUPERIOR OU IGUAL À 6, É NECESSÁRIO SOLICITAR PERMISSÃO
                PARA UTILIZAR O BLUETOOTH
                *É SOLICITADA APENAS NO PRIMEIRO ACESSO, OU SE O USUÁRIO RETIRAR A PERMISSÃO MANUALMENTE
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//VERIFICA SE A VERSÃO DO ANDROID DO DISPOSITIVO É >= 6

                //VERIFICA SE A PERMISSÃO AINDA NÃO FOI CONCEDIDA
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //REQUISITA A PERMISSÃO
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, M.codigo.REQUISITAR_PERMISSAO);
                }
                else{//SE A PERMISSÃO JÁ FOI CONCEDIDA
                    iniciarComponentes();
                }
            }
            else{//SE A VERSÃO DO ANDROID DO DISPOSITIVO FOR < 6
                iniciarComponentes();
            }
        }
    }

    private void getReferencias(){
        txtStatus = findViewById(R.id.txtStatus);
        btnLigarBluetooth = findViewById(R.id.btnLigarBluetooth);
        btnVisibilidade = findViewById(R.id.btnVisibilidade);

        antena = BluetoothAdapter.getDefaultAdapter();
    }

    private void habilitarCampos(boolean habilitar){
        btnLigarBluetooth.setEnabled(habilitar);
        btnVisibilidade.setEnabled(habilitar);
    }

    private void habilitarCampos(boolean habilitar, int Rstring){
        txtStatus.setText(Rstring);
        habilitarCampos(habilitar);
    }

    private void iniciarComponentes(){

        if(!antena.isEnabled()){//SE A ANTENA ESTIVER DESATIVADA, BLOQUEAR CAMPOS
            habilitarCampos(false, R.string.bluetooth_desligado);
            btnLigarBluetooth.setEnabled(true);
            btnLigarBluetooth.setText(R.string.ligar_bluetooth);
        }
        else{//SE A ANTENA ESTIVER LIGADA, LIBERAR CAMPOS
            habilitarCampos(true, R.string.bluetooth_ligado);
            btnLigarBluetooth.setText(R.string.desligar_bluetooth);
        }
    }

    /**
     * MÉTODO ATIVADO QUANDO O USUÁRIO ATIVA O BLUETOOTH ATRAVÉS DA INTERFACE DO PROGRAMA
     * @param codigoDaRequisicao INTEIRO IDENTIFICADOR DA REQUISIÇÃO
     * @param resultado INTEIRO RESULTADO DA ATIVAÇÃO
     * @param dados PARÂMETROS QUE POSSAM TER SIDO MANDADOS DE VOLTA
     */
    @Override
    protected void onActivityResult(int codigoDaRequisicao, int resultado, Intent dados) {
        switch (codigoDaRequisicao){
            case M.codigo.HABILITAR_BLUETOOTH://CASO SEJA REQUISIÇÃO PARA ATIVAR O BLUETOOTH
                if (resultado == RESULT_OK) {//SE FOR OK
                    habilitarCampos(true, R.string.bluetooth_ligado);
                    btnLigarBluetooth.setText(R.string.desligar_bluetooth);
                }
                else{//SE FOR NEGADA
                    habilitarCampos(false, R.string.bluetooth_desligado);
                    btnLigarBluetooth.setEnabled(true);
                    btnLigarBluetooth.setText(R.string.ligar_bluetooth);
                }
                break;

            case M.codigo.FICAR_VISIVEL://CASO SEJA REQUISIÇÃO PARA PERMITIR QUE OS OUTROS O ENCONTREM
                if(resultado == RESULT_CANCELED){//SE FOR NEGADA, MOSTRAR MENSAGEM
                    txtStatus.setText(R.string.seu_dispositivo_nao_pode_ser_encontrado_por_outros);
                }
                else{//SE FOR ACEITA, REDIRECIONAR PARA A TELA DE AÇÕES
                    Intent intent = new Intent(MainActivity.this, ControleActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    /**
     * MÉTODO ATIVADO QUANDO UMA REQUISIÇÃO DE PERMISSÃO É REALIZADA
     * @param codigoDaRequisicao INTEIRO IDENTIFICADOR DA REQUISIÇÃO
     * @param listaDePermissoes ARRAY DAS PERMISSÕES SOLICITADAS
     * @param listaDeConsessao RESPOSTA SE A PERMISSÃO FOI CONCEDIDA OU NÃO
     */
    @Override
    public void onRequestPermissionsResult(int codigoDaRequisicao, String listaDePermissoes[], int[] listaDeConsessao) {
        switch (codigoDaRequisicao) {
            case M.codigo.REQUISITAR_PERMISSAO: {

                //SE A PERMISSÃO FOR CONCEDIDA
                if (listaDeConsessao.length > 0 && listaDeConsessao[0] == PackageManager.PERMISSION_GRANTED) {
                    iniciarComponentes();
                }
                else {//SE A PERMISSÃO FOR NEGADA
                    Util.alerta(MainActivity.this, getString(R.string.o_easy_presentation_nao_funciona_sem_a_permissao_de_acesso));
                    habilitarCampos(false, R.string.o_easy_presentation_nao_funciona_sem_a_permissao_de_acesso);
                }
                break;
            }
        }
    }

    private View.OnClickListener ligarBluetooth = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(antena.isEnabled()){//SE A ANTENA ESTIVER LIGADA, ENTÃO DESLIGAR E BLOQUEAR CAMPOS
                antena.disable();
                habilitarCampos(false, R.string.bluetooth_desligado);
                btnLigarBluetooth.setEnabled(true);
                btnLigarBluetooth.setText(R.string.ligar_bluetooth);
            }
            else {//SE A ANTENA ESTIVER DESLIGADA, ENTÃO SOLICITAR QUE LIGUE
                txtStatus.setText(R.string.solicitando_ativacao_do_bluetooth);
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, M.codigo.HABILITAR_BLUETOOTH);
            }
        }
    };

    private View.OnClickListener ficarVisivel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //SOLICITAR QUE PERMITA SEU DISPOSITIVO FIQUE VISÍVEL PARA OUTREM
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);//POR 60 SEGUNDOS
            startActivityForResult(intent, M.codigo.FICAR_VISIVEL);
        }
    };
}
