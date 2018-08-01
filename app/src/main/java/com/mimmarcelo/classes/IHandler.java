package com.mimmarcelo.classes;

/**
 * PERMITE A COMUNICAÇÃO ENTRE A ACTIVITY E A THREAD DE CONEXÃO
 */
public interface IHandler {

	/**
	 * RECEBE A MENSAGEM RECEBIDA ATRAVÉS DA THREAD DE CONEXÃO
	 * @param mensagem MESAGEM RECEBIDA
	 */
	void recebeMensagem(String mensagem);
}
