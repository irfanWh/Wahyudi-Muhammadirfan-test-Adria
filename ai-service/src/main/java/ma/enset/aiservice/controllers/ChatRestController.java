package ma.enset.aiservice.controllers;

import ma.enset.aiservice.models.ChatRequest;
import ma.enset.aiservice.models.ChatResponse;
import ma.enset.aiservice.service.BankingAgentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    private final BankingAgentService bankingAgentService;

    public ChatRestController(BankingAgentService bankingAgentService) {
        this.bankingAgentService = bankingAgentService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse(bankingAgentService.chat(request.message()));
    }
}
