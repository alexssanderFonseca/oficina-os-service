package br.com.alexsdm.postech.oficina.ordem_servico.core.domain.entity;

public enum Status {
    EM_DIAGNOSTICO,
    AGUARDANDO_APROVACAO,
    AGUARDANDO_EXECUCAO,
    AGUARDANDO_PAGAMENTO,
    EM_EXECUCAO,
    FINALIZADA,
    ENTREGUE,
    FALHA
}

