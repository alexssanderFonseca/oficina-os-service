Feature: Fluxo de Execução de Ordem de Serviço
  Como um usuário do sistema
  Eu quero registrar e acompanhar o fluxo completo de uma ordem de serviço
  Para garantir que a integração do serviço de Ordem de Serviço com seus dependentes funcione corretamente.

  Scenario: Execução de uma Ordem de Serviço do início ao fim
    # PRÉ-CONDIÇÕES (Mocks para os serviços dependentes)
    Given que um cliente e seu veiculo existem
    And que itens de catalogo existem para o diagnostico
    And que o servico de orcamento esta funcionando

    # FLUXO PRINCIPAL (Ordem de Serviço)
    When eu abro uma nova ordem de serviço
    And eu inicio o diagnóstico da ordem de serviço
    And eu finalizo o diagnóstico informando os itens necessários
    And eu aprovo o orçamento gerado
    And eu inicio a execução da ordem de serviço
    And eu finalizo a execução da ordem de serviço
    Then eu registro a entrega do veículo ao cliente com sucesso
