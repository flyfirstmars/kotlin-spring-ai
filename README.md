# SpringAI-Based LLM API Example using Kotlin

This project demonstrates how to build a flexible, stateless API for interacting with Large Language Models (LLMs) using **SpringAI**. It provides two approaches:

- **ChatClient API**: For managing conversational interactions with contextual memory.
- **ChatModel API**: For stateless interactions with LLMs, supporting both text and image-based inputs.

The project includes REST and GraphQL APIs, offering flexibility for various client implementations.

---

## Features

- **Contextual Memory Management** (ChatClient API):
  - Maintain conversation context using in-memory storage.
  - Retrieve and clear conversation histories as needed.
- **Stateless Interactions** (ChatModel API):
  - Handle independent API calls for text and image-based inputs.
  - Streaming and non-streaming response modes supported.
- **Dynamic Prompt Building**:
  - Construct flexible prompts for text or image inputs.
  - Attach media to prompts for tasks like image analysis.
- **Multi-API Support**:
  - REST and GraphQL APIs for chat completions, image analysis, and streaming.

---

## Prerequisites

Ensure you have the following installed:
- **Java 21+**
- **Kotlin 2.0+**
- **Spring Boot 3.4+**
- **Gradle** for dependency management.

---

## Getting Started

---

# SpringAI-Based LLM API Example: ChatClient and ChatModel

This project demonstrates how to build a flexible, stateless API for interacting with Large Language Models (LLMs) using **SpringAI**. It provides two approaches:

- **ChatClient API**: For managing conversational interactions with contextual memory.
- **ChatModel API**: For stateless interactions with LLMs, supporting both text and image-based inputs.

The project includes REST and GraphQL APIs, offering flexibility for various client implementations.

---

## Features

- **Contextual Memory Management** (ChatClient API):
  - Maintain conversation context using in-memory storage.
  - Retrieve and clear conversation histories as needed.
- **Stateless Interactions** (ChatModel API):
  - Handle independent API calls for text and image-based inputs.
  - Streaming and non-streaming response modes supported.
- **Dynamic Prompt Building**:
  - Construct flexible prompts for text or image inputs.
  - Attach media to prompts for tasks like image analysis.
- **Multi-API Support**:
  - REST and GraphQL APIs for chat completions, image analysis, and streaming.

---

## Environment Variables

The application reads the following environment variables to configure the OpenAI integration (**you can use any other vendor currently supported by SpringAI**) via `application.yml`:

```yaml
ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    chat:
      options:
        model: ${OPENAI_MODEL}
        temperature: ${OPENAI_TEMPERATURE}
        max-tokens: ${OPENAI_MAX_TOKENS}
        top-p: ${OPENAI_TOP_P}
```

### **Environment Variables**

| Variable Name        | Description                                                                      |
|-----------------------|----------------------------------------------------------------------------------|
| `OPENAI_API_KEY`      | The API key for authenticating with OpenAI's services.                           |
| `OPENAI_MODEL`        | The name of the OpenAI model to use (e.g., `gpt-4o-mini`).                       |
| `OPENAI_TEMPERATURE`  | Sampling temperature for response generation. Higher values increase randomness. |
| `OPENAI_MAX_TOKENS`   | Maximum number of tokens to generate in a response.                              |
| `OPENAI_TOP_P`        | Nucleus sampling parameter, controlling the diversity of tokens.                 |

### **Setting Environment Variables**

#### On Linux/MacOS
Add the following to your shell configuration file (e.g., `~/.bashrc` or `~/.zshrc`):
```bash
export OPENAI_API_KEY=
export OPENAI_MODEL=
export OPENAI_TEMPERATURE=
export OPENAI_MAX_TOKENS=
export OPENAI_TOP_P=
```

Then reload your shell:
```bash
source ~/.bashrc
```

### Build the Project
```bash
./gradlew build
```

### Run the Application
```bash
./gradlew bootRun
```

---

## API Documentation

### GraphQL Schema
The following schema defines the API structure for both `ChatClient` and `ChatModel` APIs.

```graphql
scalar Upload

type Query {
    getChatResponse(conversationId: String, logicType: LogicType, message: String): ChatResponse
    getAllMessages(conversationId: String!): [Message]
    chatCompletion(userPrompt: String!): String
}

type Mutation {
    clearConversation(conversationId: String!): Boolean
    imageAnalysis(image: Upload!): String
}

type Subscription {
    streamChatCompletion(userPrompt: String!): String
    streamChatCompletionWithImageAnalysis(image: Upload!): String
}

type ChatResponse {
    conversationId: String
    response: String
}

enum LogicType {
    ADVISOR
    NO_ADVISOR
}

type Message {
    messageType: String
    metadata: Metadata
    media: [Media]
    toolCalls: [ToolCall]
    text: String
    content: String
}

type Metadata {
    finishReason: String
    refusal: String
    index: Int
    role: String
    id: String
    messageType: String
}

type Media {
    type: String
    url: String
}

type ToolCall {
    name: String
    parameters: String
}
```

---

### Example Queries

#### 1. **Get Chat Response**
```graphql
query GetChatResponse($conversationId: String, $logicType: LogicType, $message: String) {
  getChatResponse(conversationId: $conversationId, logicType: $logicType, message: $message) {
    conversationId
    response
  }
}
```

**Variables:**
```json
{
  "conversationId": "12345",
  "logicType": "ADVISOR",
  "message": "What is the weather today?"
}
```

#### 2. **Get All Messages**
```graphql
query GetAllMessages($conversationId: String!) {
  getAllMessages(conversationId: $conversationId) {
    messageType
    text
    content
    metadata {
      finishReason
      refusal
      index
      role
      id
      messageType
    }
    media {
      type
      url
    }
    toolCalls {
      name
      parameters
    }
  }
}
```

**Variables:**
```json
{
  "conversationId": "12345"
}
```

#### 3. **Clear Conversation**
```graphql
mutation ClearConversation($conversationId: String!) {
  clearConversation(conversationId: $conversationId)
}
```

**Variables:**
```json
{
  "conversationId": "12345"
}
```

#### 4. **Chat Completion**
```graphql
query ChatCompletion($userPrompt: String!) {
  chatCompletion(userPrompt: $userPrompt)
}
```

**Variables:**
```json
{
  "userPrompt": "Explain quantum physics in simple terms."
}
```

---

### REST Endpoints

| HTTP Method | Endpoint                                   | Description                                |
|-------------|-------------------------------------------|--------------------------------------------|
| POST        | `/v1/chat-client/chat`                    | Get a chat response.                      |
| GET         | `/v1/chat-client/conversation-messages/{conversationId}` | Retrieve messages for a conversation.     |
| DELETE      | `/v1/chat-client/{conversationId}`        | Clear conversation history.               |
| POST        | `/v1/chat-model/chat-completion`          | Get a chat response or stream responses.  |
| POST        | `/v1/chat-model/image-analysis`           | Analyze an image or stream analysis results.|

---

## Project Structure

```plaintext
src/
├── main/
│   ├── kotlin/
│   │   ├── dev.natig.gandalf.common/        # Common utilities, constants, and enums
│   │   ├── dev.natig.gandalf.chatClient/    # ChatClient API implementation
│   │   ├── dev.natig.gandalf.chatModel/     # ChatModel API implementation
│   └── resources/
│       ├── application.yml                 # Application configuration
│       └── schema.graphqls                 # GraphQL schema
└── test/                                   # Unit and integration tests (to be added later)
```

---

## Configuration

- **Prompts**: Customize prompts in the `Prompts` object (e.g., `CHAT_SYSTEM_PROMPT`, `IMAGE_ANALYSIS_SYSTEM_PROMPT`).
- **Media Support**: Supported image formats include `JPEG` and `PNG`.


---

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

---
