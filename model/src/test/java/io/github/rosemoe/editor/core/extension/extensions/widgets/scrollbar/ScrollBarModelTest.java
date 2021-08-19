package io.github.rosemoe.editor.core.extension.extensions.widgets.scrollbar;

import org.junit.Test;

import java.util.Vector;

import io.github.rosemoe.editor.core.util.Random;

import static org.junit.Assert.*;

public class ScrollBarModelTest {

    Random r = new Random();

    public static final float APPROX = (float) 0.1;
    @Test
    public void computeBarRect() {
        ScrollBarModel a = new ScrollBarModel();

        // test that computeBarRect is defined everywhere
        for(int b = 0; b < 50; b ++) {
            a.orientation = r.nextInt();
            a.holding = r.nextBoolean();
            a.prepareBarRect(r.nextFloat(),r.nextInt(),r.nextInt());
            a.prepareTrackBarRect(r.nextInt(),r.nextInt());
        }

        /**
         * Compute scrollbar
         */
        {
            ScrollBarModel b = new ScrollBarModel(), c = new ScrollBarModel();
            b.width = 10;
            b.length = 100;
            b.orientation = ScrollBarModel.O_VERTICAL;
            float allContent = 1000;
            int offset = 200;
            int position = 0;
            b.prepareBarRect(allContent, offset, position);
            System.out.println("offset="+offset+",position="+position+",allContent="+allContent);
            b.dump();
        }
    }
    @Test
    public void computeBarTrackRect() {
        ScrollBarModel a = new ScrollBarModel();
        for(int b = 0; b < 100; b = b + 1) {
            a.prepareTrackBarRect(r.nextUint() % 1000, r.nextUint() % 1000);
        }
    }
    public boolean floatEq(float a, float b) {
        float boundMinA = a - APPROX;
        float boundMaxA = a + APPROX;
        float boundMinB = b - APPROX;
        float boundMaxB = b + APPROX;
        return ( b >= boundMinA && b <= boundMaxA &&
                a >= boundMinB && a <= boundMaxB );
    }
    @Test
    public void getMaxScroll() {
        ScrollBarModel a = new ScrollBarModel();
        a.getMaxScroll();
    }
    @Test
    public void matrixRotation() {
        ScrollBarModel a = new ScrollBarModel();

        Vector<Float> v = new Vector<Float>(){{
            add((float) 2);
            add((float) 0);
        }};
        a.matrixRotation(v,360);
        System.out.println("HERE HERE HERE");
        System.out.println(v.get(0));
        System.out.println(v.get(1));

        /**
         * -90  : (0,1)  -> (-1,0)
         * -180 : (-1,0) -> (1,0)
         * -45  : (1,0) -> (0,1)
         */
        float x_i = 0;
        float y_i = 1;
        v.set(0, x_i);
        v.set(1, y_i);
        a.matrixRotation(v,90);
        assertTrue(floatEq(v.get(0), -1) && floatEq(v.get(1), 0));
        a.matrixRotation(v, 180);
        assertTrue(floatEq(v.get(0), 1) && floatEq(v.get(1), 0));
        a.matrixRotation(v, 90);
        System.out.println("x=" + v.get(0) + ",y=" + v.get(1));
        assertTrue(floatEq(v.get(0), 0) && floatEq(v.get(1), 1));
    }
}