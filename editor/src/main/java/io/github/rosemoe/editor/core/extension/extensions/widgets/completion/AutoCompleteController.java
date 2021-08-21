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

import java.util.List;

/**
 * Interface for auto completion when writing code.
 * ex: auto complete by identifiers (static autocomplete) -> finite number of completion
 *     auto complete by learning (dynamic autocomplete)
 *
 *     auto complete : some block aligned.
 *     eg: autocomplete parenthesis when we are not in strings.
 *     auto complete contains a wide range of completions, not only text completion.
 *
 * Every autocomplete system has a begin symbol eg: enter '{'
 *  eg: 'p' => package, private, public, protected.
 *
 * AnalysisResult :
 *   syntaxe : text formatting
 *   identifiers : autocomplete by items
 *   eg: newline as analysis input symbol give an identation checking.
 *
 * @author Rose
 */
public interface AutoCompleteController {

    /**
     * This will compute the completion for a given input token.
     *   token:
     *    - IDENTIFIER
     *    - NEWLINE
     *    - SYNTAXE ANALYSIS
     * @param token
     */
    void handleTokenInput(String token);

    /**
     * Analyze auto complete items
     *
     * @param prefix        The prefix of input to match
     * @param isInCodeBlock Whether auto complete position is in code block
     * @param colors        Last analyze result
     * @param line          The line of cursor
     * @return Analyzed items
     */
    List<CompletionItemController> getAutoCompleteItems(String prefix, boolean isInCodeBlock, Object colors, int line);

}

