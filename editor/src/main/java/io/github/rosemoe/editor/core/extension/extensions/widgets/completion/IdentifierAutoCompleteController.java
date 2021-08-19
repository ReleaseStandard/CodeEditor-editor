/*
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package io.github.rosemoe.editor.core.extension.extensions.widgets.completion;

import io.github.rosemoe.editor.core.analyzer.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.analyzer.result.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.analysis.CodeAnalyzerResultCompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * identifier is just an other analysis result => eg java filling.
 *                                                eg python filling.
 *     result analysis are identifiers alias keywords.
 *
 * Identifier auto-completion
 * You can use it to provide identifiers
 * <strong>Note:</strong> To use this, you must use {@link IdentifierAutoCompleteModel.Identifiers} as
 *
 */
public class IdentifierAutoCompleteController implements AutoCompleteController {

    private ArrayList<CompletionItemController> items = new ArrayList<>();
    public IdentifierAutoCompleteModel model = new IdentifierAutoCompleteModel();

    public CodeAnalyzer codeAnalyzer;

    public IdentifierAutoCompleteController(CodeAnalyzer a) {
        codeAnalyzer = a;
    }

    public IdentifierAutoCompleteController(CodeAnalyzer a, String[] keywords) {
        this(a);
        model.setKeywords(keywords);
    }


    @Override
    public void handleTokenInput(String token) {

    }

    /**
     * A
     * @param prefix        The prefix of input to match
     * @param isInCodeBlock Whether auto complete position is in code block
     * @param colors        Last analyze result
     * @param line          The line of cursor
     * @return
     */
    @Override
    public List<CompletionItemController> getAutoCompleteItems(String prefix, boolean isInCodeBlock, CodeAnalyzerResultColor colors, int line) {
        List<CompletionItemController> keywords = new ArrayList<>();
        final String[] keywordArray = model.mKeywords;
        final boolean lowCase = model.mKeywordsAreLowCase;
        String match = prefix.toLowerCase();
        if (keywordArray != null) {
            if (lowCase) {
                for (String kw : keywordArray) {
                    if (kw.startsWith(match)) {
                        keywords.add(new CompletionItemController(kw, "Keyword"));
                    }
                }
            } else {
                for (String kw : keywordArray) {
                    if (kw.toLowerCase().startsWith(match)) {
                        keywords.add(new CompletionItemController(kw, "Keyword"));
                    }
                }
            }
        }
        Collections.sort(keywords, CompletionItemController.COMPARATOR_BY_NAME);

        // completion analyzer
        CodeAnalyzerResultCompletion result = (CodeAnalyzerResultCompletion) codeAnalyzer.getResult("completion");
        if (result != null) {
            List<CompletionItemController> words = new ArrayList<>();
            for (String word : result.identifiers.getIdentifiers()) {
                if (word.toLowerCase().startsWith(match)) {
                    words.add(new CompletionItemController(word, "Identifier"));
                }
            }
            Collections.sort(words, CompletionItemController.COMPARATOR_BY_NAME);
            keywords.addAll(words);
        }
        return keywords;
    }


}
