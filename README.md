# AI Explainer Plugin (IntelliJ IDEA)

An IntelliJ IDEA plugin that integrates ChatGPT API to **explain code** and **generate code** directly inside the IDE.

---

## Features

### 1. Explain Selected Code
- Select any piece of code
- Right-click and choose Explainer Plugin > Explain Code
- Get a language explanation of the selected code in the tool window

### 2. Generate Code from Prompt
- Right click in any place in a file where you want to generate code and press Explainer Plugin > Generate Code
- Provide a prompt (e.g., "Create a REST API endpoint that does ....")
- Get a code snippet and option to accept / reject code snippet
- Plugin inserts generated code at cursor position

Here is a demo video to see how plugin works (it was tested on one of my Kotlin projects):
[Watch the demo](https://drive.google.com/file/d/1aq40mH6pOsZ4Beh3Y4ZLm0xVpVh0urwJ/view?usp=drive_link)

---

## Setup

### 1. Clone the Repository

### 2. Set OpenAI API Key

#### Windows (PowerShell)
```powershell
$env:OPENAI_API_KEY="your_api_key_here"
```
#### macOS / Linux
```bash
export OPENAI_API_KEY="your_api_key_here"
```

### 3. Run the Plugin
```bash
./gradlew runIde
```

---

## How It Works

1. User triggers an action (explain/generate)
2. Plugin extracts editor context using PSI:
   1. Code language
   2. Imports of the opened file
   3. Enclosing class and methods
   4. Selected code for explanation / cropped code before & after cursor for generation
3. Context is transformed into a structured prompt
4. Prompt is sent to LLM.
5. Explanation is displayed in the tool window / the user is asked to accept or decline the code and code is inserted into the editor at the cursor position if accepted.
6. All LLM requests are sent asynchronously, so the IDE doesn't freeze and UI is responsive. For each project opened there is a separate coroutines scope.

---

## Project Structure

```
com.example.ai_explainer_plugin
│
├── actions/            # Entry points of the plugin. Here defined how Explain and Generate actions are triggered in IDE.
├── context/            # Contains code responsible for collecting information from the current IDE state.
│   ├── dto/            # LLM needs context to understand what the user is working on. This package prepares that context.
│   └── extractors/
├── services/
    ├── prompts/        # Contains prompt constants and DTO to be used in LLM requests
    ├── AiActionService # Contains logic for triggering actions (explain/generate) and handling responses from LLM.
    ├── LLMService      # Contains logic for sending asynchronous requests to LLM API.
    ├── PromptBuilder   # Contains logic for building prompt from extracted context.
├── ui/                 # User interface components.
├── resources/          # Contains plugin configuration.
```

---

## Testing

```bash
./gradlew test
```
Currently, tests cover only the context extraction part.
---

## Limitations

- Context limited to open file since I use LLM API and not the agents in the IDE.
- No project-wide understanding yet
- LLM answer is not streamlined yet, so user needs to wait for the full answer to be generated after which it is displayed in IDE.

---

## Future Improvements

- Use agents that can understand the whole project
- Safe AI refactoring
- Inline explanations
- Code review suggestions

