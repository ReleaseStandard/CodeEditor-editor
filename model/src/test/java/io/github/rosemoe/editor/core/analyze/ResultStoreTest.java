package io.github.rosemoe.editor.core.analyze;

import org.junit.Test;

import java.util.Iterator;

import io.github.rosemoe.editor.core.analyze.result.AnalyzerResult;
import io.github.rosemoe.editor.core.content.CodeAnalyzerResultContent;
import io.github.rosemoe.editor.core.util.Random;

import static io.github.rosemoe.editor.core.analyze.ResultStore.RES_CONTENT;
import static org.junit.Assert.*;

public class ResultStoreTest {

    Random r = new Random();

    @Test
    public void updateView() {
        {
            ResultStore rs = new ResultStore();
            rs.init(2);
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) rs.getResult(RES_CONTENT);
            content.behaviourOnCellSplit = -1234;
            assertTrue(content.behaviourOnCellSplit == -1234);
            rs.updateView();
            content = (CodeAnalyzerResultContent) rs.getResult(RES_CONTENT);
            assertTrue(content.behaviourOnCellSplit != -1234);
            rs.updateView();
            content = (CodeAnalyzerResultContent) rs.getResult(RES_CONTENT);
            assertTrue(content.behaviourOnCellSplit == -1234);
        }
        {
            ResultStore rs = new ResultStore();
            CodeAnalyzerResultContent content = (CodeAnalyzerResultContent) rs.getResult(RES_CONTENT);
            CodeAnalyzerResultContent contentInB = (CodeAnalyzerResultContent) rs.getResultInBuild(RES_CONTENT);
            contentInB.behaviourOnCellSplit = 1234;
            rs.updateView();
            content = (CodeAnalyzerResultContent) rs.getResult(RES_CONTENT);
            contentInB = (CodeAnalyzerResultContent) rs.getResultInBuild(RES_CONTENT);
            assertTrue(content.behaviourOnCellSplit == 1234);
        }
    }

    @Test
    public void clearBuilded() {
        ResultStore rs = new ResultStore();
        int choosed = r.nextUint(10);
        rs.init(choosed);
        rs.clearBuilded();
        assertTrue(rs.get(RES_CONTENT).size() == (choosed-1));
    }

    @Test
    public void clearInBuild() {
        ResultStore rs = new ResultStore();
        rs.init(r.nextUint(10));
        rs.clearInBuild();
        assertTrue(rs.get(RES_CONTENT).size() == 1);
    }
}