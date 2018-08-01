package com.mimmarcelo.classes;

/**
 * PERMITE O RETORNO DA UTILIZAÇÃO DA FUNÇÃO "popupEscolha" DA CLASSE "Util"
 */
public interface IEscolha {

	/**
	 * RECEBE O INDEX DA OPÇÃO ESCOLHIDA PELO USUÁRIO DA FUNÇÃO "popupEscolha" DA CLASSE "Util"
	 * NORMALMENTE PRECISA SER ARMAZENADA EM UMA VARIÁVEL NA ACTIVITY
	 * UTILIZE ESSA INFORMAÇÃO PARA ESCOLHER O QUE DEVE ACONTECER QUANDO O MÉTODO "acao" FOR DISPARADO
	 * @param escolha INDEX DA ESCOLHA DO USUÁRIO
	 */
	public void escolha(int escolha);

	/**
	 * DISPARADO QUANDO O USUÁRIO APERTA "Ok" NA POPUP GERADA PELA FUNÇÃO "popupEscolha" DA CLASSE "Util"
	 * @param requisicao CÓDIGO DE REQUISIÇÃO QUE PERMITE SEPARAR DIFERENTES REQUISIÇÕES EM UMA TELA QUE FAÇA MAIS DE UMA
	 */
	public void acao(int requisicao);
}
