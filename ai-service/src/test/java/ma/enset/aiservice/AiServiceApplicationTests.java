package ma.enset.aiservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.ai.openai.api-key=test",
        "spring.ai.mcp.client.initialized=false"
})
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
