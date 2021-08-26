package io.github.rosemoe.editor.core.analyze.analyzer.content;

import org.junit.Test;

import io.github.rosemoe.editor.core.CodeEditorModel;
import io.github.rosemoe.editor.core.analyze.ResultStore;
import io.github.rosemoe.editor.core.analyze.signal.Routes;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.util.Random;
import manifold.ext.rt.api.Jailbreak;

import static org.junit.Assert.*;

public class ContentActionStackAnalyzerTest {

    Random r = new Random();

    @Test
    public void testUndo() {
        {
            // "text" > CTRL+Z
            CodeEditorModel editor = new CodeEditorModel();
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) editor.resultStore.getResultInBuild(ResultStore.RES_CONTENT);
            @Jailbreak ContentActionStackAnalyzer casa = new ContentActionStackAnalyzer(editor.resultStore);
            casa.replaceMark = false;
            int bound = 10 + r.nextUint(10);
            String text = r.nextString(bound);
            assertTrue(content.get(0).getWidth() == 0);
            content.insert(0, 0, text);
            assertTrue(content.get(0).getWidth() == text.length());
            assertTrue(casa.stack.size() == 0 && casa.canUndo() == false && casa.canRedo() == false);
            casa.afterInsert(0, 0, 0, text.length(), text);
            assertTrue(casa.stack.size() == 1 && casa.canUndo() == true && casa.canRedo() == false);
            assertTrue(content.get(0).getWidth() == text.length());
            casa.route(Routes.ACTION_UNDO,null);
            assertTrue(casa.stack.size() == 1);
            assertTrue(content.get(0).getWidth() == 0);
        }
    }

    @Test
    public void testRedo() {
        {
            // "text" > CTRL+Z > CTRL+SHIFT+Z
            CodeEditorModel editor = new CodeEditorModel();
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) editor.resultStore.getResultInBuild(ResultStore.RES_CONTENT);
            @Jailbreak ContentActionStackAnalyzer casa = new ContentActionStackAnalyzer(editor.resultStore);
            casa.replaceMark = false;
            String text = r.nextString(10 + r.nextUint(10));
            assertTrue(content.get(0).getWidth() == 0);
            content.insert(0, 0, text);
            casa.afterInsert(0, 0, 0, text.length(), text);
            System.out.println("After insert");
            content.dump();
            assertTrue(casa.stack.size() == 1);
            assertTrue(content.get(0).getWidth() == text.length());

            casa.route(Routes.ACTION_UNDO,null);
            System.out.println("After undo");
            content.dump();
            assertTrue(casa.stack.size() == 1);
            assertTrue(content.get(0).getWidth() == 0);

            casa.route(Routes.ACTION_REDO,null);
            assertTrue(casa.stack.size() == 1);
            assertTrue(content.get(0).getWidth() == text.length());
        }
    }

    @Test
    public void testUndoRedo() {
        CodeEditorModel editor = new CodeEditorModel();
        CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) editor.resultStore.getResultInBuild(ResultStore.RES_CONTENT);
        assertTrue(content.size()==1);
        @Jailbreak ContentActionStackAnalyzer casa = new ContentActionStackAnalyzer(editor.resultStore);
        int n = 100;
        for(int a = 0; a < n; a=a+1) {
            switch (r.nextUint(2)) {
                case 0: {
                    String i = r.nextString(100);
                    content.insert(0,0,i);
                    casa.afterInsert(0,0,0,i.length(),i);
                }
                case 1: {
                    int bound = content.get(0).getWidth();
                    int i = r.nextUint(bound);
                    int j = r.nextUint(bound);
                    if ( i > j ) {
                        int k = i;
                        i = j;
                        j = k;
                    }
                    String deletedContent = content.delete(i,j);
                    casa.afterDelete(0,i,0,j, deletedContent);
                }
                case 2: {

                }
            }
        }
        assertTrue(casa.stack.size() == n);
        for(int a = 0; a < n; a=a+1) {
            casa.undo();
        }
        assertTrue(content.get(0).size()==0);
    }

}