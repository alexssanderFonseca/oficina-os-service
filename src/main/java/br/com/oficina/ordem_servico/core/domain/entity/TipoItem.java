package br.com.oficina.ordem_servico.core.domain.entity;

public enum TipoItem {
    PECA,
    SERVICO;

    public static TipoItem fromString(String tipo) {
        for (TipoItem tipoItem : TipoItem.values()) {
            if (tipoItem.name().equalsIgnoreCase(tipo)) {
                return tipoItem;
            }
        }
        throw new IllegalArgumentException("Tipo de item invalid: " + tipo);
    }
}
