package br.com.txbank.notificacao.listener;

import br.com.txbank.notificacao.dto.EventoContaCriadaDTO;
import br.com.txbank.notificacao.servico.EmailServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContaCriadaListener {

    private final EmailServico emailServico;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "ContaCriada", groupId = "${spring.kafka.consumer.group-id}")
    public void ouvir(String mensagemJson) throws Exception {
        log.info("Mensagem recebida: {}", mensagemJson);

        // Converte o JSON recebido para o Objeto Java
        EventoContaCriadaDTO evento = objectMapper.readValue(mensagemJson, EventoContaCriadaDTO.class);

        // Chama o serviço de email
        // Se der erro aqui, a exceção sobe, o Spring Kafka detecta e tenta de novo (retry)
        emailServico.enviarEmailBoasVindas(String.valueOf(evento.clienteId()), String.valueOf(evento.id()));
        log.info("E-mail enviado com sucesso!");
    }

}
