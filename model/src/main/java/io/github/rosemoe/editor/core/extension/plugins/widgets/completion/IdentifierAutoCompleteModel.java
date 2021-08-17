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
package io.github.rosemoe.editor.core.extension.plugins.widgets.completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rosemoe.editor.core.util.Logger;

/**
 * identifier is just an other analysis result => eg java filling.
 *                                                eg python filling.
 *     result analysis are identifiers alias keywords.
 *
 * Identifier auto-completion
 * You can use it to provide identifiers
 * <strong>Note:</strong> To use this, you must use {@link Identifiers} as
 *
 */
public class IdentifierAutoCompleteModel {

    String[] mKeywords;
    boolean mKeywordsAreLowCase = true;

    public void setKeywords(String[] keywords) {
        mKeywords = keywords;
    }

    public String[] getKeywords() {
        Logger.debug("key words in the autocomplete provider : ", mKeywords.length);
        return mKeywords;
    }

    public static class Identifiers {

        private final List<String> identifiers = new ArrayList<>();
        private HashMap<String, Object> cache = new HashMap<>();
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

        public List<String> getIdentifiers() {
            return identifiers;
        }

    }
}
