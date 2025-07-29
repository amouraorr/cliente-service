package com.fiap.cliente.exception;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void configurarLogCapture() {
        logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.DEBUG);
    }

    @Test
    void deveRetornarBadRequestQuandoIllegalArgumentException() {
        // Given
        String mensagemErro = "Argumento inválido fornecido";
        IllegalArgumentException exception = new IllegalArgumentException(mensagemErro);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Bad Request");
        assertThat(body.get("message")).isEqualTo(mensagemErro);
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);

        // Verificar log
        List<ILoggingEvent> loggingEvents = listAppender.list;
        assertThat(loggingEvents).hasSize(1);
        assertThat(loggingEvents.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvents.get(0).getFormattedMessage())
                .contains("Erro de argumento ilegal: " + mensagemErro);
    }

    @Test
    void deveRetornarBadRequestQuandoMethodArgumentNotValidException() {
        // Given
        String campo1 = "nome";
        String mensagem1 = "não pode estar vazio";
        String campo2 = "email";
        String mensagem2 = "deve ser um email válido";

        FieldError fieldError1 = new FieldError("cliente", campo1, mensagem1);
        FieldError fieldError2 = new FieldError("cliente", campo2, mensagem2);
        List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(400);
        assertThat(body.get("error")).isEqualTo("Validation Error");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);

        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) body.get("message");
        assertThat(messages).hasSize(2);
        assertThat(messages).contains(
                campo1 + ": " + mensagem1,
                campo2 + ": " + mensagem2
        );

        List<ILoggingEvent> loggingEvents = listAppender.list;
        assertThat(loggingEvents).hasSize(1);
        assertThat(loggingEvents.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvents.get(0).getFormattedMessage())
                .contains("Erro de validação: Validation failed");
    }

    @Test
    void deveRetornarBadRequestQuandoMethodArgumentNotValidExceptionSemErros() {
        // Given
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) body.get("message");
        assertThat(messages).isEmpty();
    }

    @Test
    void deveRetornarInternalServerErrorQuandoExceptionGenerica() {
        // Given
        String mensagemErro = "Erro inesperado no sistema";
        Exception exception = new RuntimeException(mensagemErro);

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleAllExceptions(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);

        List<ILoggingEvent> loggingEvents = listAppender.list;
        assertThat(loggingEvents).hasSize(1);
        assertThat(loggingEvents.get(0).getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvents.get(0).getFormattedMessage()).contains("Erro inesperado: ");
    }

    @Test
    void deveRetornarInternalServerErrorQuandoNullPointerException() {
        // Given
        NullPointerException exception = new NullPointerException("Valor nulo encontrado");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleAllExceptions(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.get("status")).isEqualTo(500);
        assertThat(body.get("error")).isEqualTo("Internal Server Error");
        assertThat(body.get("message")).isEqualTo("Ocorreu um erro inesperado. Tente novamente mais tarde.");
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
    }

    @Test
    void deveValidarEstruturaDaRespostaParaIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Teste");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleIllegalArgumentException(exception);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body).containsKeys("timestamp", "status", "error", "message");
        assertThat(body).hasSize(4);
    }

    @Test
    void deveValidarEstruturaDaRespostaParaValidationException() {
        // Given
        FieldError fieldError = new FieldError("objeto", "campo", "mensagem");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body).containsKeys("timestamp", "status", "error", "message");
        assertThat(body).hasSize(4);
    }

    @Test
    void deveValidarEstruturaDaRespostaParaExceptionGenerica() {
        // Given
        Exception exception = new Exception("Teste");

        // When
        ResponseEntity<Object> response = globalExceptionHandler.handleAllExceptions(exception);

        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertThat(body).isNotNull();
        assertThat(body).containsKeys("timestamp", "status", "error", "message");
        assertThat(body).hasSize(4);
    }
}