package com.condominio.novaalianca.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OpenSearchAppender extends AppenderBase<ILoggingEvent> {

    private String url;
    private String username;
    private String password;
    private String index = "condominio-logs";

    private HttpClient httpClient;
    private ExecutorService executor;
    private String authHeader;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public void start() {
        if (url == null) {
            addError("Nenhuma URL configurada para o OpenSearchAppender");
            return;
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }

        // Criando pool de threads leve para não travar a aplicação principal
        executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "opensearch-log-worker");
            t.setDaemon(true);
            return t;
        });

        // Configura SSLContext para confiar em certificados autoassinados (comum no OpenSearch local/interno)
        HttpClient.Builder clientBuilder = HttpClient.newBuilder().executor(executor);
        try {
            // Desativa a verificação de hostname do HttpClient do JDK (necessário para IP local como 192.168.15.10)
            System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");

            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            }, new java.security.SecureRandom());
            clientBuilder.sslContext(sslContext);
        } catch (Exception e) {
            addError("Erro ao configurar SSLContext para o HttpClient", e);
        }

        httpClient = clientBuilder.build();

        if (username != null && password != null && !username.trim().isEmpty()) {
            String auth = username + ":" + password;
            authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        }

        super.start();
    }

    @Override
    public void stop() {
        if (executor != null) {
            executor.shutdown();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }

        try {
            String jsonPayload = buildJson(event);
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url + index + "/_doc"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

            if (authHeader != null) {
                requestBuilder.header("Authorization", authHeader);
            }

            HttpRequest request = requestBuilder.build();

            // Envio assíncrono para o OpenSearch para performance ideal
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .whenComplete((response, throwable) -> {
                        if (throwable != null) {
                            addError("Falha ao enviar log para o OpenSearch", throwable);
                        } else if (response != null && response.statusCode() >= 400) {
                            addError("Erro de resposta do OpenSearch: " + response.statusCode());
                        }
                    });

        } catch (Exception e) {
            addError("Falha ao processar evento de log", e);
        }
    }

    private String buildJson(ILoggingEvent event) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        appendField(json, "timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(event.getTimeStamp())), true);
        appendField(json, "level", event.getLevel().toString(), true);
        appendField(json, "thread", event.getThreadName(), true);
        appendField(json, "logger", event.getLoggerName(), true);
        appendField(json, "message", escapeJson(event.getFormattedMessage()), true);
        
        if (event.getThrowableProxy() != null) {
            appendField(json, "stacktrace", escapeJson(getStackTrace(event.getThrowableProxy())), false);
        } else {
            // Remove a última vírgula se não houver stacktrace
            if (json.length() > 1) {
                json.setLength(json.length() - 1);
            }
        }
        
        json.append("}");
        return json.toString();
    }

    private void appendField(StringBuilder json, String key, String value, boolean hasNext) {
        json.append("\"").append(key).append("\":\"").append(value).append("\"");
        if (hasNext) {
            json.append(",");
        }
    }

    private String getStackTrace(IThrowableProxy tp) {
        StringBuilder sb = new StringBuilder();
        while (tp != null) {
            sb.append(tp.getClassName()).append(": ").append(tp.getMessage()).append("\n");
            for (StackTraceElementProxy step : tp.getStackTraceElementProxyArray()) {
                sb.append("\tat ").append(step.toString()).append("\n");
            }
            tp = tp.getCause();
        }
        return sb.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String hex = "000" + Integer.toHexString(ch);
                        sb.append("\\u").append(hex.substring(hex.length() - 4));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
