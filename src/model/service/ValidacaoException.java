package model.service;

/**
 * Lançada quando uma regra de negócio ou validação é violada.
 * A mensagem é sempre apresentada ao usuário pela View.
 */
public class ValidacaoException extends Exception {
    private static final long serialVersionUID = 1L;

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
