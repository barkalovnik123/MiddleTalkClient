package org.maverick.middletalkclient.component;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxEditor {
    // Простой лексер для Java-ключевых слов
    private static final Pattern PATTERN = Pattern.compile(
            // Ключевые слова
            "(?<KEYWORD>\\b(public|private|protected|class|void|return|if|else|for|while|new|static|final|int|String|boolean)\\b)" +

                    // Строки и символы
                    "|(?<STRING>\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\")" +
                    "|(?<CHAR>'[^'\\\\]*(?:\\\\.[^'\\\\]*)*')" +

                    //  Числа (целые и с плавающей точкой)
                    "|(?<NUMBER>\\b\\d+(\\.\\d+)?[fFdD]?\\b)" +

                    // Комментарии
                    "|(?<COMMENT>//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/)" +

                    // Операторы и знаки препинания
                    "|(?<OPERATOR>[+\\-*/%=<>!&|^~?:]+)" +
                    "|(?<PUNCTUATION>[{}()\\[\\];,.])" +

                    // Идентификаторы (переменные, классы, методы) — это и есть "всё остальное"
                    "|(?<IDENTIFIER>\\b[a-zA-Z_][a-zA-Z0-9_]*\\b)",

            Pattern.COMMENTS
    );

    private final CodeArea codeArea = new CodeArea();

    public CodeArea getCodeArea() {
        // Автоматическая пересчёт подсветки при изменении текста
        codeArea.textProperty().addListener((obs, old, newText) -> {
            computeHighlighting();
        });
        return codeArea;
    }

    private void computeHighlighting() {
        String text = codeArea.getText();
        Matcher matcher = PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastEnd = 0;
        while (matcher.find()) {
            // Определяем стиль по имени группы
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("STRING") != null ? "string" :
                                    matcher.group("CHAR") != null ? "string" :
                                            matcher.group("NUMBER") != null ? "number" :
                                                    matcher.group("COMMENT") != null ? "comment" :
                                                            matcher.group("OPERATOR") != null ? "operator" :
                                                                    matcher.group("PUNCTUATION") != null ? "punctuation" :
                                                                            matcher.group("IDENTIFIER") != null ? "identifier" : null; // ← "всё остальное"

            // Добавляем промежуток без стиля
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastEnd);
            // Добавляем стилизованный токен
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        // Добавляем остаток текста после последнего совпадения
        spansBuilder.add(Collections.emptyList(), text.length() - lastEnd);

        codeArea.setStyleSpans(0, spansBuilder.create());
    }
}