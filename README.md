# Copy Reference with Range

IntelliJ Platform plugin that copies file reference with line range when text is selected.

## Usage

Select a block of code and press:

- **macOS**: `Cmd+Ctrl+Opt+C`
- **Windows/Linux**: `Ctrl+Alt+C`

Copies to clipboard as `<filename>:<start>-<end>`, e.g. `main.rs:1-3`.

When no text is selected, behaves like the default Copy Reference (single line).

## Build

```bash
./gradlew buildPlugin
```

Output: `build/distributions/*.zip`
