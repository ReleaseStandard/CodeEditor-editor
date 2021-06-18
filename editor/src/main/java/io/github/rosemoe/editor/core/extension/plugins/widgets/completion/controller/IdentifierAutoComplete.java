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
package io.github.rosemoe.editor.core.extension.plugins.widgets.completion.controller;

import io.github.rosemoe.editor.core.extension.plugins.widgets.colorAnalyzer.codeanalysis.CodeAnalyzerResultColor;
import io.github.rosemoe.editor.core.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Identifier auto-completion
 * You can use it to provide identifiers
 * <strong>Note:</strong> To use this, you must use {@link Identifiers} as
 */
public class IdentifierAutoComplete implements AutoCompleteProviderController {

    private String[] mKeywords;
    private boolean mKeywordsAreLowCase;
    private Object mExtra = new Object();
    public IdentifierAutoComplete() {

    }

    public IdentifierAutoComplete(String[] keywords) {
        setKeywords(keywords);
    }

    public void setKeywords(String[] keywords) {
        mKeywords = keywords;
        mKeywordsAreLowCase = true;
    }

    public String[] getKeywords() {
        Logger.debug("key words in the autocomplete provider : ", mKeywords.length);
        return mKeywords;
    }

    public static class Identifiers {

        private final List<String> identifiers = new ArrayList<>();
        private HashMap<String, Object> cache;
        private final static Object SIGN = new Object();

        public void addIdentifier(String identifier) {
            if (cache == null) {
                throw new IllegalStateException("begin() has not been called");
            }
            if (cache.put(identifier, SIGN) == SIGN) {
                return;
            }
            identifiers.add(identifier);
        }

        public void begin() {
            cache = new HashMap<>();
        }

        public void finish() {
            cache.clear();
            cache = null;
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

    }

    @Override
    public List<CompletionItemController> getAutoCompleteItems(String prefix, boolean isInCodeBlock, CodeAnalyzerResultColor colors, int line) {
        List<CompletionItemController> keywords = new ArrayList<>();
        final String[] keywordArray = mKeywords;
        final boolean lowCase = mKeywordsAreLowCase;
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

        // completion analyzer
        Collections.sort(keywords, CompletionItemController.COMPARATOR_BY_NAME);
        Object extra = mExtra;
        Identifiers userIdentifiers = (extra instanceof Identifiers) ? (Identifiers) extra : null;
        if (userIdentifiers != null) {
            List<CompletionItemController> words = new ArrayList<>();
            for (String word : userIdentifiers.getIdentifiers()) {
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
