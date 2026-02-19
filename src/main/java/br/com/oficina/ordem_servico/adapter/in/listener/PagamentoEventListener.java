package br.com.oficina.ordem_servico.adapter.in.listener;

import br.com.oficina.ordem_servico.core.usecase.dto.events.PagamentoEventoDTO;
import br.com.oficina.ordem_servico.core.usecase.impl.ProcessarRetornoPagamentoUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class PagamentoEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoEventListener.class);
    private final ProcessarRetornoPagamentoUseCase processarRetornoPagamentoUseCase;
    private final ObjectMapper objectMapper;
    private final SqsClient sqsClient;

    @Value("${aws.sqs.payment-queue-url}") // URL da fila SQS a ser configurada
    private String paymentQueueUrl;

    @Scheduled(fixedDelayString = "${aws.sqs.polling-interval-ms:5000}") // Polling a cada 5 segundos por padrão
    public void pollMessages() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(paymentQueueUrl)
                .maxNumberOfMessages(10) // Quantidade máxima de mensagens para receber por vez
                .waitTimeSeconds(20) // Long polling
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

        if (messages.isEmpty()) {
            logger.debug("Nenhuma mensagem SQS recebida da fila: {}", paymentQueueUrl);
            return;
        }

        logger.info("Recebidas {} mensagens SQS da fila: {}", messages.size(), paymentQueueUrl);

        for (Message message : messages) {
            try {
                JsonNode snsMessage = objectMapper.readTree(message.body());
                String rawMessage = snsMessage.get("Message").asText();

                PagamentoEventoDTO pagamentoEvento = objectMapper.readValue(rawMessage, PagamentoEventoDTO.class);
                processarRetornoPagamentoUseCase.execute(pagamentoEvento);
                logger.info("Evento de pagamento processado com sucesso para OS ID: {}", pagamentoEvento.getExternalReference());

                deleteMessage(message);
            } catch (JsonProcessingException e) {
                logger.error("Erro ao desserializar mensagem SQS ou payload SNS: {}. Mensagem original: {}", e.getMessage(), message.body(), e);
                deleteMessage(message);
            } catch (Exception e) {
                logger.error("Erro ao processar evento de pagamento da SQS para a mensagem {}: {}", message.messageId(), e.getMessage(), e);
            }
        }
    }

    private void deleteMessage(Message message) {
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(paymentQueueUrl)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);
        logger.debug("Mensagem SQS {} deletada da fila.", message.messageId());
    }
}
