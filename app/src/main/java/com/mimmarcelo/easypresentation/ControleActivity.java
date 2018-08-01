package com.mimmarcelo.easypresentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mimmarcelo.classes.IHandler;
import com.mimmarcelo.classes.M;
import com.mimmarcelo.classes.ThreadDeConexao;
import com.mimmarcelo.classes.Util;

public class ControleActivity extends AppCompatActivity implements IHandler{

    //RETÉM REFERÊNCIAS PARA OS CAMPOS DO LAYOUT
    private TextView txtStatus;
    private TextView txtMensagem;
    private ImageButton btnParaBaixo;
    private ImageButton btnParaCima;
    private ImageButton btnEsquerdo;
    private ImageButton btnDireito;
    private ImageButton btnEsc;
    private ImageView imgTouchPad;

    //RETÉM REFERÊNCIAS PARA OS COMPONENTES BLUETOOTH DO DISPOSITIVO
    private ThreadDeConexao conexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controle);
        getReferencias();
        
        // INICIAR CONEXÃO COMO SERVIDOR E AGUARDAR QUE ALGUÉM SE CONECTE
        conexao = new ThreadDeConexao(this, this);
        conexao.start();
        txtStatus.setText(R.string.aguardando_conexao);

        btnParaBaixo.setOnClickListener(paraBaixo);
        btnParaCima.setOnClickListener(paraCima);
        btnEsquerdo.setOnClickListener(mouseEsquerdo);
        btnDireito.setOnClickListener(mouseDireito);
        btnEsc.setOnClickListener(esc);
        imgTouchPad.setOnTouchListener(posicionaMouse);
    }

    private void getReferencias(){
        txtStatus = findViewById(R.id.txtStatus);
        txtMensagem = findViewById(R.id.txtMensagem);
        btnParaBaixo = findViewById(R.id.btnParaBaixo);
        btnParaCima = findViewById(R.id.btnParaCima);
        btnEsquerdo = findViewById(R.id.btnEsquerdo);
        btnDireito = findViewById(R.id.btnDireito);
        btnEsc = findViewById(R.id.btnEsc);
        imgTouchPad = findViewById(R.id.imgTouchPad);
    }

    private View.OnClickListener paraBaixo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enviarMensagem(M.msg.PARA_BAIXO);
        }
    };

    private View.OnClickListener paraCima = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enviarMensagem(M.msg.PARA_CIMA);
        }
    };
    private View.OnClickListener mouseEsquerdo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enviarMensagem("e\n");
        }
    };

    private View.OnClickListener mouseDireito = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enviarMensagem("d\n");
        }
    };
    private View.OnClickListener esc = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enviarMensagem("esc\n");
        }
    };
    private View.OnTouchListener posicionaMouse = new View.OnTouchListener() {
        int xAtual;
        int yAtual;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int aux;
            x = (x*2);
            y = (y*2);
//            if(x > 100)
//                x = 100;
//            if(x < 0)
//                x = 0;
//            if(y > 100)
//                y = 100;
//            if(y < 0)
//                y = 0;
            switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                if(xAtual == 0){
	                    xAtual = x;
	                    yAtual = y;
                    }
	                break;
                case MotionEvent.ACTION_MOVE:
                    aux = x;
                    x -= xAtual;
                    xAtual = aux;
                    aux = y;
                    y -= yAtual;
                    yAtual = aux;
                    enviarMensagem("x" +x + "y" + y + "\n");
                    break;
	            case MotionEvent.ACTION_UP:
                    xAtual = 0;
                    yAtual = 0;
	                break;
            }

            return true;
        }

    };

    /**
     * AÇÃO REALIZADA QUANDO O USUÁRIO CLICA NO BOTÃO PARA ENVIAR MENSAGEM
     * @param mensagem TEXTO A SER ENVIADO VIA BLUETOOTH
     */
    public void enviarMensagem(String mensagem){
        if(conexao == null){//SE A CONEXÃO NÃO EXISTIR
            txtStatus.setText(R.string.erro_na_conexao);
        }
        else{//SE A CONEXÃO EXISTIR
            conexao.enviarMensagem(M.msg.ACAO+mensagem);
        }
    }

    /**
     * MÉTODO DA INTERFACE "IHandler", QUE PERMITE RECEBER MENSAGENS DA THREAD DE CONEXÃO
     * @param mensagem - MENSAGEM RECEBIDA PELA THREAD DE CONEXÃO
     */
    @Override
    public void recebeMensagem(String mensagem) {

        txtMensagem.setText(mensagem);
        if(mensagem.startsWith(M.msg.ERRO)){//SE VIER MENSAGEM DE ERRO
            txtStatus.setText(R.string.erro_na_conexao);
        }
        else if(mensagem.startsWith(M.msg.SUCESSO)){//SE VIER MENSAGEM DE SUCESSO
            txtStatus.setText(R.string.conectado);
        }
//        else if(mensagem.startsWith(M.msg.ACAO)){//SE VIER AÇÃO, AÇÃO AQUI É ENTENDIDO COMO UMA MENSAGEM RECEBIDA
//
//        }

    }

}
