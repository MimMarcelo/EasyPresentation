package com.mimmarcelo.classes;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mimmarcelo.easypresentation.R;

public abstract class Util {

	/**
	 * RECUPERA IMAGEM DO "Resource" ATRAVÉS DE UMA STRING
	 * @param resName NOME DA IMAGEM
	 * @param c REFERÊNCIA DA CLASSE DE "Resource" QUE INDICA O TIPO DE IMAGEM
	 * @return A IMAGEM ENCONTRADA
	 */
	public static int getResourcePorString(String resName, Class<R.id> c) {

	    try {
	        Field idField = c.getDeclaredField(resName);
	        return idField.getInt(idField);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } 
	}

	/**
	 * CRIA POPUP QUE LISTA OS ITENS ENVIADOS
	 * @param titulo TÍTULO DA POPUP
	 * @param itens LISTA QUE DEVE SER EXIBIDA NA POPUP
	 */
	public static void popupDetalhes(Context context, String titulo, ArrayList<String> itens){
		AlertDialog.Builder alerta = new AlertDialog.Builder(context);
		alerta.setTitle(titulo);

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
				context,
		        android.R.layout.simple_list_item_1, itens);

		alerta.setAdapter(adaptador, null);
		alerta.setPositiveButton(R.string.ok, null);
		alerta.show();
	}

	/**
	 * CRIA POPUP ONDE PODE SER REALIZADA UMA ESCOLHA
	 * @param titulo TÍTULO DA POPUP
	 * @param itens LISTA DE ITENS QUE PODEM SER ESCOLHIDOS
	 * @param classe REFERÊNCIA PARA A ACTIVITY QUE ACIONOU ESSE MÉTODO
	 * @param requisicao IDENTIFICADOR DESSA REQUISIÇÃO
	 */
	public static void popupEscolha(Context context, String titulo, ArrayList<String> itens, final IEscolha classe, final int requisicao) {
		AlertDialog.Builder alerta = new AlertDialog.Builder(context);
		classe.escolha(0);//ESCOLHA PADRÃO
		
		alerta.setTitle(titulo);

		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(
				context,
		        android.R.layout.select_dialog_singlechoice, itens);

		alerta.setAdapter(adaptador, null);
		
		alerta.setSingleChoiceItems(adaptador, 0, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int index) {
				classe.escolha(index);
			}
			
		});
		alerta.setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int index) {
				classe.acao(requisicao);
			}
		});
		alerta.show();
		
	}

	public static void alerta(Context context, String mensagem) {
		Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
	}

}
