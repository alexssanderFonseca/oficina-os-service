Feature: Fluxo de Execução de Ordem de Serviço
  Como um usuário do sistema
  Eu quero registrar e acompanhar o fluxo completo de uma ordem de serviço
  Para garantir que a integração do serviço de Ordem de Serviço com seus dependentes funcione corretamente.

  Scenario: Execução de uma Ordem de Serviço do início ao fim (incluindo pagamento aprovado)
    # PRÉ-CONDIÇÕES (Mocks para os serviços dependentes)
    Given que um cliente e seu veiculo existem
    And que itens de catalogo existem para o diagnostico
    And que o servico de orcamento esta funcionando
    And que o servico de pagamento esta funcionando

    # FLUXO PRINCIPAL (Ordem de Serviço)
    When eu abro uma nova ordem de serviço
    And eu inicio o diagnóstico da ordem de serviço
    And eu finalizo o diagnóstico informando os itens necessários
    And eu aprovo o orçamento gerado
    And eu inicio a execução da ordem de serviço
    Then o pagamento da ordem de serviço deve ser solicitado
    When o pagamento da ordem de serviço e aprovado
    And eu finalizo a execução da ordem de serviço
    Then eu registro a entrega do veículo ao cliente com sucesso

  Scenario: Execução de uma Ordem de Serviço com Pagamento Recusado e Estorno de Estoque
    # PRÉ-CONDIÇÕES (Mocks para os serviços dependentes)
    Given que um cliente e seu veiculo existem
    And que itens de catalogo existem para o diagnostico
    And que o servico de orcamento esta funcionando
    And que o servico de pagamento esta funcionando

    # FLUXO PRINCIPAL (Ordem de Serviço)
    When eu abro uma nova ordem de serviço
    And eu inicio o diagnóstico da ordem de serviço
    And eu finalizo o diagnóstico informando os itens necessários
    And eu aprovo o orçamento gerado
    And eu inicio a execução da ordem de serviço
    Then o pagamento da ordem de serviço deve ser solicitado
    When o pagamento da ordem de serviço e recusado
    Then os itens de estoque devem ser repostos
    And a ordem de servico deve ter o status de pagamento recusado

  Scenario: Tentar finalizar Ordem de Serviço com Pagamento Pendente ou Recusado
    # PRÉ-CONDIÇÕES (Mocks para os serviços dependentes)
    Given que um cliente e seu veiculo existem
    And que itens de catalogo existem para o diagnostico
    And que o servico de orcamento esta funcionando
    And que o servico de pagamento esta funcionando
    When eu abro uma nova ordem de serviço
    And eu inicio o diagnóstico da ordem de serviço
    And eu finalizo o diagnóstico informando os itens necessários
    And eu aprovo o orçamento gerado
    And eu inicio a execução da ordem de serviço
    Then o pagamento da ordem de serviço deve ser solicitado
    And o status de pagamento da ordem de serviço esta como "PENDENTE"
    When eu tento finalizar a execução da ordem de serviço
    Then eu devo ver uma mensagem de erro "A ordem de serviço só pode ser finalizada se o pagamento estiver aprovado."

  Scenario: Tentar finalizar Ordem de Serviço com Pagamento Recusado
    # PRÉ-CONDIÇÕES (Mocks para os serviços dependentes)
    Given que um cliente e seu veiculo existem
    And que itens de catalogo existem para o diagnostico
    And que o servico de orcamento esta funcionando
    And que o servico de pagamento esta funcionando
    When eu abro uma nova ordem de serviço
    And eu inicio o diagnóstico da ordem de serviço
    And eu finalizo o diagnóstico informando os itens necessários
    And eu aprovo o orçamento gerado
    And eu inicio a execução da ordem de serviço
    Then o pagamento da ordem de serviço deve ser solicitado
    When o pagamento da ordem de serviço e recusado
    Then a ordem de servico deve ter o status de pagamento recusado
    When eu tento finalizar a execução da ordem de serviço
    Then eu devo ver uma mensagem de erro "A ordem de serviço só pode ser finalizada se o pagamento estiver aprovado."
