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
package io.github.rosemoe.editor.core.langs.empty;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.extension.extensions.widgets.completion.IdentifierAutoCompleteModel;
import io.github.rosemoe.editor.core.extension.extensions.langs.LanguagePlugin;
import io.github.rosemoe.editor.core.analyze.analyzer.CodeAnalyzer;
import io.github.rosemoe.editor.core.analyze.analyzer.CodeAnalyzerThread;

/**
 * Empty language without any effect,
 * no content analyzer, no color analyzer, nothing.
 *
 * @author Rose
 */
public class EmptyLanguage extends LanguagePlugin {

    public EmptyLanguage(CodeEditorModel editor) {
        super(editor);
        name = "Empty language";
        description = "Empty language";
        analyzer = new EmptyCodeAnalyzer(null);
    }
    @Override
    public IdentifierAutoCompleteModel getAutoCompleteProvider() {
        return new EmptyAutoCompleteProvider();
    }

    @Override
    public boolean isAutoCompleteChar(char ch) {
        return false;
    }

    public static class EmptyAutoCompleteProvider extends IdentifierAutoCompleteModel {

    }

    private static class EmptyCodeAnalyzer extends CodeAnalyzer {

        public EmptyCodeAnalyzer(ResultStore resultStore) {
            super(resultStore);
        }

        @Override
        protected void analyze(CharSequence content, CodeAnalyzerThread.Delegate delegate) {

        }

    }
}

