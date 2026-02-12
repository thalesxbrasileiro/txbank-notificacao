package br.com.txbank.notificacao.servico;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServico {

    private final JavaMailSender javaMailSender;

    @Value("${email.destino}")
    private String destinatarioPadrao;

    public void enviarEmailBoasVindas(String clienteId, String contaId) {
        try {
            log.info("Tentando enviar e-mail para cliente: {}", clienteId);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@txbank.com.br");
            message.setTo(destinatarioPadrao); // Em produção, buscaria o e-mail do cliente no banco
            message.setSubject("Bem-vindo ao TxBank!");
            message.setText("Olá! Sua conta foi criada com sucesso.\nID da Conta: " + contaId + "\nID do Cliente: " + clienteId);

            javaMailSender.send(message);
            log.info("E-mail enviado com sucesso para: {}", destinatarioPadrao);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail: ", e);
            throw e; // Relança a exceção para o Kafka tentar novamente
        }
    }
}