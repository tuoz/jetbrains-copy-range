# 一、项目概述

## 1.1 项目名称

**Copy Reference with Range**

## 1.2 项目目标

在 JetBrains 系列 IDE（IntelliJ IDEA / PyCharm / CLion / WebStorm 等）中，实现如下能力：

> 当用户选中一段代码时，通过快捷键复制为：

```
main.rs:1-3
```

而非默认行为：

```
main.rs:1
```

---

# 二、功能需求

## 2.1 核心功能

### 功能描述

当存在文本选区时：

* 获取当前文件名（或路径）
* 获取选区起始行号（1-based）
* 获取选区结束行号（1-based）
* 输出格式：

```
<file_name>:<start_line>-<end_line>
```

---

### 示例

| 场景       | 输入      | 输出            |
| -------- | ------- | ------------- |
| 单行选区     | 第 5 行   | `main.rs:5`   |
| 多行选区     | 第 1–3 行 | `main.rs:1-3` |
| 无选区（仅光标） | 第 8 行   | `main.rs:8`   |

---

## 2.2 行为规则

### 规则 1：单行优化

如果：

```
start_line == end_line
```

输出：

```
file.rs:5
```

而不是：

```
file.rs:5-5
```

---

### 规则 2：选区边界定义

* `start_line` = selectionStart 所在行
* `end_line` = selectionEnd 所在行

注意：

* selectionEnd 若在下一行开头，应归属上一行（避免 off-by-one）

---

### 规则 3：文件路径策略（可配置）

支持三种模式：

1. **仅文件名（默认）**

   ```
   main.rs:1-3
   ```

2. **相对路径（相对于项目根）**

   ```
   src/main.rs:1-3
   ```

3. **绝对路径**

   ```
   /Users/xxx/project/src/main.rs:1-3
   ```

---

### 规则 4：无选区行为

* 若无选区，则行为等同 JetBrains 默认 Copy Reference：

  ```
  main.rs:line
  ```

---

## 2.3 触发方式

### 默认快捷键

macOS：

```
⌥⌘⇧C
```

Windows/Linux：

```
Ctrl+Alt+Shift+C
```

要求：

* 可在 Keymap 中修改
* 可与原生 Copy Reference 区分（建议命名不同 action）

---

## 2.4 输出目标

默认：

* 写入系统剪贴板

可选（扩展功能）：

* 同时显示 toast 提示：

  ```
  Copied: main.rs:1-3
  ```

---

# 三、非功能需求

## 3.1 性能

* 必须为 O(1) 操作（仅行号计算）
* 不允许阻塞 UI 线程超过 10ms

---

## 3.2 兼容性

支持：

* IntelliJ IDEA
* PyCharm
* WebStorm
* CLion
* Rider（可选）

最低版本：

* 2022.3+

---

## 3.3 稳定性

需处理：

* 无 editor
* 无文件（如 scratch）
* selection 为空
* selection 跨越极大文件

---

# 四、技术实现规范

## 4.1 核心 API

使用 IntelliJ Platform SDK：

### 获取 Editor

```kotlin
val editor = FileEditorManager.getInstance(project).selectedTextEditor
```

---

### 获取选区

```kotlin
val selectionModel = editor.selectionModel
val startOffset = selectionModel.selectionStart
val endOffset = selectionModel.selectionEnd
```

---

### 获取行号

```kotlin
val document = editor.document

val startLine = document.getLineNumber(startOffset) + 1
val endLine = document.getLineNumber(endOffset) + 1
```

---

### 修正 endLine（关键）

```kotlin
if (endOffset > 0 && endOffset == document.getLineStartOffset(endLine - 1)) {
    endLine -= 1
}
```

---

### 获取文件路径

```kotlin
val virtualFile = FileDocumentManager.getInstance().getFile(document)
val fileName = virtualFile?.name
val path = virtualFile?.path
```

---

### 写入剪贴板

```kotlin
val clipboard = CopyPasteManager.getInstance()
val content = StringSelection(result)
clipboard.setContents(content)
```

---

## 4.2 Action 定义

继承：

```kotlin
AnAction
```

关键方法：

```kotlin
override fun actionPerformed(e: AnActionEvent)
```

---

## 4.3 plugin.xml

需要声明：

```xml
<actions>
    <action id="CopyReferenceWithRange"
            class="your.package.CopyReferenceWithRangeAction"
            text="Copy Reference with Range"
            description="Copy file reference with line range">
    </action>
</actions>
```

---

# 五、配置项（Settings UI）

提供设置页面：

### 选项

1. 路径格式：

   * filename
   * relative
   * absolute

2. 是否显示 toast

   * yes / no

3. 是否覆盖默认快捷键

   * yes / no

---

# 六、测试用例

## 基础测试

* [ ] 单行选区
* [ ] 多行选区
* [ ] 空选区
* [ ] 整文件选区

---

## 边界测试

* [ ] selectionEnd 在下一行开头
* [ ] selectionStart == selectionEnd
* [ ] 文件第一行
* [ ] 文件最后一行

---

## 路径测试

* [ ] filename
* [ ] relative path
* [ ] absolute path

---

# 七、扩展功能（可选）

1. 支持复制为 Markdown 链接：

   ```
   main.rs:1-3
   ```

2. 支持 GitHub 格式：

   ```
   main.rs#L1-L3
   ```

3. 支持自动附带代码片段

---

# 八、验收标准

满足以下条件即视为完成：

* 能正确输出 `file:start-end`
* 行号无 off-by-one 错误
* 支持快捷键调用
* 剪贴板内容正确
* 无明显 UI 卡顿

---

# 九、交付物

* 完整 IntelliJ 插件项目（Gradle）
* 可安装 `.zip` 插件包
* README（说明快捷键与配置）



