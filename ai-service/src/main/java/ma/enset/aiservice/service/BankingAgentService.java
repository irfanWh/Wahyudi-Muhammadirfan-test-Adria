package ma.enset.aiservice.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BankingAgentService {

    private final ChatClient chatClient;

    public BankingAgentService(ChatClient.Builder builder,
                               ToolCallbackProvider mcpTools) {
        this.chatClient = builder
                .defaultSystem("""
                        Tu es un assistant bancaire. Utilise les outils MCP pour consulter
                        les comptes et les transactions. N'invente jamais de donnees bancaires.
                        Reponds de maniere courte et claire en francais.
                        """)
                .defaultTools(mcpTools)
                .build();
    }

    public String chat(String message) {
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Le message est obligatoire");
        }

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
