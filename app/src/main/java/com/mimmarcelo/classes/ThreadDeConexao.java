package com.mimmarcelo.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.mimmarcelo.easypresentation.R;

public class ThreadDeConexao extends Thread{
	BluetoothSocket socketBluetooth = null;
    BluetoothServerSocket servidorBluetooth = null;
    InputStream input = null;
    OutputStream output = null;
    String enderecoMac = null;
    String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    boolean server;
    boolean running = false;
    IHandler iHandler;
    Context context;
    
    public boolean getServidor() {
		return server;
	}

    /**
     * CONSTRUTOR PARA CONEXÃO COMO SERVIDOR
     * @param iHandler REFERÊNCIA PARA A ACTIVITY QYE ACIONOU A CONEXÃO
     */
    public ThreadDeConexao(Context context, IHandler iHandler) {
        this.server = true;
        this.context = context;
        this.iHandler = iHandler;
    }

    /**
     * CONSTRUTOR PARA CONEXÃO COMO CLIENTE
     * @param enderecoMac MAC DO SERVIDOR SELECIONADO
     */
    public ThreadDeConexao(String enderecoMac) {
        this.server = false;
        this.enderecoMac = enderecoMac;
    }

    /**
     * CONSTRUTOR PARA CONEXÃO COMO CLIENTE COM REFERÊNCIA PARA A ACTIVITY QUE ACIONOU A CONEXÃO
     * @param enderecoMac MAC DO SERVIDOR SELECIONADO
     * @param iHandler REFERÊNCIA PARA A ACTIVITY QUE ACIONOU A CONEXÃO
     */
    public ThreadDeConexao(String enderecoMac, IHandler iHandler) {
        this.server = false;
        this.enderecoMac = enderecoMac;
        this.iHandler = iHandler;
    }

    /**
     * MÉTODO QUE INICIA E CONTROLA A CONEXÃO
     */
    public void run() {
		this.running = true;
        BluetoothAdapter antena = BluetoothAdapter.getDefaultAdapter();
        
        if(this.server) {//PARA O LADO SERVIDOR
            try {
            	//USA O SERVIDORBLUETOOTH PARA ESTABELECER A CONEXÃO
                servidorBluetooth = antena.listenUsingRfcommWithServiceRecord(context.getString(R.string.app_name), UUID.fromString(myUUID));
                
                //QUANDO A CONEXÃO É ACEITA, ATRIBUI AO SOCKET
                socketBluetooth = servidorBluetooth.accept();

                if(socketBluetooth != null)//PODE LIBERAR O SERVIDOR 
                    servidorBluetooth.close();
                
            }
            catch (IOException e) {
            	cancel();
                toMainActivity(M.msg.ERRO);
            } 
        }
        else {//PARA O LADO CLIENTE
        	try {
        		//CRIA O SOCKET COM BASE NO DISPOSITIVO E SEU ENDEREÇO MAC
        		BluetoothDevice btDevice = antena.getRemoteDevice(enderecoMac);
                socketBluetooth = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));
                antena.cancelDiscovery();//CANCELA NOVAS DESCOBERTAS
 
                if (socketBluetooth != null)//INICIA A CONEXÃO
                    socketBluetooth.connect();
 
            } catch (IOException e) {
                cancel();
                toMainActivity(M.msg.ERRO);
            }
        }

        //GERENCIA A CONEXÃO
	    if(socketBluetooth != null) {

	        toMainActivity(M.msg.SUCESSO);
	
	        try {
	        	//REFERÊNCIAS PARA OS FLUXOS DE ENTRADA E DE SAÍDA
	            input = socketBluetooth.getInputStream();
	            output = socketBluetooth.getOutputStream();
	
	            //ARRAY DE BYTES PARA ARMAZENAR A MENSAGEM RECEBIDA
	            byte[] buffer = new byte[1024];
	            int bytes;
	            
	            while(running) {
	                bytes = input.read(buffer);
	                toMainActivity(Arrays.copyOfRange(buffer, 0, bytes));	
	            }
	
	        } catch (IOException e) {
	        	cancel();
	            toMainActivity(M.msg.ERRO);
	        }
	    }
 
    }

    /**
     * ENVIA MENSAGEM PARA A ACTIVITY QUE ACIONOU A CONEXÃO
     * @param data MENSAGEM RECEBIDA
     */
	private void toMainActivity(final String data) {
		((Activity)context).runOnUiThread(new Runnable() {
		    public void run() {
		    	iHandler.recebeMensagem(data);
		    }
		});
	}

    /**
     * CONVERTE OS BYTES RECEBIDOS EM TEXTO
     * @param data
     */
    private void toMainActivity(byte[] data) {
    	toMainActivity(new String(data));
    }

    /**
     * CONVERTE A MENSAGEM EM BYTES E ENVIA AO DESTINATÁRIO
     * @param mensagem MENSAGEM A SER ENVIADA
     */
    public void enviarMensagem(String mensagem){
		byte[] data = mensagem.getBytes();
		
		if(output != null) {
			try {
				//TRANSMITE A MENSAGEM
				output.write(data);
				output.flush();
				toMainActivity(M.msg.SUCESSO);
			}
			catch (IOException e) {
                toMainActivity(M.msg.ERRO+": "+e.getMessage());
			}
		} 
		else {
			toMainActivity(M.msg.ERRO);
       }
	}

    /**
     * ENCERRA A THREAD DE CONEXÃO
     */
    public void cancel() {
    	if(input != null){
    		try{
    			input.close();
    		}
    		catch (Exception e) {}
    		input = null;
    	}

    	if(output != null){
    		try{
    			output.close();
    		}
    		catch (Exception e) {}
    		output = null;
    	}
    	
    	if (socketBluetooth != null){
    		try{
    			socketBluetooth.close();
    		}
    		catch (Exception e) {}
    		socketBluetooth = null;
    	}

    	if (servidorBluetooth  != null){
    		try{
    			servidorBluetooth .close();
    		}
    		catch (Exception e) {}
    		servidorBluetooth  = null;
    	}
        running = false;
    }
}
