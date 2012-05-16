package java.lang;

import lejos.nxt.Button;
import lejos.nxt.Sound;

public class MathTest {
    
    private static final double[] FLOORCEIL_INPUT = {
        Double.NEGATIVE_INFINITY, -2.5, -2.3, -2.0, -1.7, -1.5, -1.3, -1.0, -0.7, -0.5, -0.3, -0.0,
        0.0, 0.3, 0.5, 0.7, 1.0, 1.3, 1.5, 1.7, 2.0, 2.3, 2.5, Double.POSITIVE_INFINITY,
    };
    private static final double[] FLOOR_EXPECT = {
        Double.NEGATIVE_INFINITY, -3.0, -3.0, -2.0, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0,
        0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, Double.POSITIVE_INFINITY,
    };
    private static final double[] CEIL_EXPECT = {
        Double.NEGATIVE_INFINITY, -2.0, -2.0, -2.0, -1.0, -1.0, -1.0, -1.0, -0.0, -0.0, -0.0, -0.0,
        0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 3.0, 3.0, Double.POSITIVE_INFINITY,
    };
    
    private static final float[] FLOAT_ORDER = {
        Float.NEGATIVE_INFINITY, -2.5f, -2.3f, -2.0f, -1.7f, -1.5f, -1.3f, -1.0f, -0.7f, -0.5f, -0.3f, -0.0f,
        0.0f, 0.3f, 0.5f, 0.7f, 1.0f, 1.3f, 1.5f, 1.7f, 2.0f, 2.3f, 2.5f, Float.POSITIVE_INFINITY,
    };
    private static final double[] DOUBLE_ORDER = {
        Double.NEGATIVE_INFINITY, -2.5, -2.3, -2.0, -1.7, -1.5, -1.3, -1.0, -0.7, -0.5, -0.3, -0.0,
        0.0, 0.3, 0.5, 0.7, 1.0, 1.3, 1.5, 1.7, 2.0, 2.3, 2.5, Double.POSITIVE_INFINITY,
    };
    
    private static final float[] FLOAT_NAN = {
        Float.intBitsToFloat(0x7fc00000),
        Float.intBitsToFloat(0x7f800001),
        Float.intBitsToFloat(0xffc00000),
        Float.intBitsToFloat(0xff800001),
    };
    private static final double[] DOUBLE_NAN = {
        Double.longBitsToDouble(0x7ff8000000000000L),
        Double.longBitsToDouble(0x7ff0000000000001L),
        Double.longBitsToDouble(0xfff8000000000000L),
        Double.longBitsToDouble(0xfff0000000000001L),
    };
    
    private static void assertNaN(float actual) {
        if (actual == actual)
            emergencyExit("expected NaN but was "+actual);
    }

    private static void assertNaN(double actual) {
        if (actual == actual)
            emergencyExit("expected NaN but was "+actual);
    }

    private static void assertEqualsExact(float actual, float expected) {
        int a2 = Float.floatToIntBits(actual);
        int e2 = Float.floatToIntBits(expected);
        if (a2 != e2)
            emergencyExit("expected "+expected+" but was "+actual);
    }

    private static void assertEqualsExact(double actual, double expected) {
        long a2 = Double.doubleToLongBits(actual);
        long e2 = Double.doubleToLongBits(expected);
        if (a2 != e2)
            emergencyExit("expected "+expected+" but was "+actual);
    }


    private static void assertEquals(long actual, long expected) {
        if (actual != expected)
        	emergencyExit("expected "+expected+" but was "+actual);
    }
    
    private static void emergencyExit(String s)
    {
    	System.out.println(s);
    	Sound.buzz();
    	Button.waitForAnyPress();
    }
    
    
    private void testFloor() {
        for (int j=0; j<DOUBLE_NAN.length; j++)
        	assertNaN(Math.floor(DOUBLE_NAN[j]));
        for (int i=0; i<FLOORCEIL_INPUT.length; i++)
            assertEqualsExact(Math.floor(FLOORCEIL_INPUT[i]), FLOOR_EXPECT[i]);
    }
    
    private void testCeil() {
        for (int j=0; j<DOUBLE_NAN.length; j++)
        	assertNaN(Math.ceil(DOUBLE_NAN[j]));
        for (int i=0; i<FLOORCEIL_INPUT.length; i++)
            assertEqualsExact(Math.ceil(FLOORCEIL_INPUT[i]), CEIL_EXPECT[i]);
    }
    
    private void testFloatCompare() {
        for (int i=0; i<FLOAT_NAN.length; i++)
        {
            for (int j=0; j<FLOAT_NAN.length; j++)
            {
                assertEquals(Float.compare(FLOAT_NAN[i], FLOAT_NAN[j]), 0);
                assertEquals(Float.compare(FLOAT_NAN[j], FLOAT_NAN[i]), 0);
            }
        }
        for (int i=0; i<FLOAT_ORDER.length; i++)
        {
            assertEquals(Float.compare(FLOAT_ORDER[i], FLOAT_ORDER[i]), 0);
            if (i > 0)
            {
                assertEquals(Float.compare(FLOAT_ORDER[i-1], FLOAT_ORDER[i]), -1);
                assertEquals(Float.compare(FLOAT_ORDER[i], FLOAT_ORDER[i-1]), 1);
            }
            for (int j=0; j<FLOAT_NAN.length; j++)
            {
                assertEquals(Float.compare(FLOAT_ORDER[i], FLOAT_NAN[j]), -1);
                assertEquals(Float.compare(FLOAT_NAN[j], FLOAT_ORDER[i]), 1);
            }
        }
    }

    private void testDoubleCompare() {
        for (int i=0; i<DOUBLE_NAN.length; i++)
        {
            for (int j=0; j<DOUBLE_NAN.length; j++)
            {
                assertEquals(Double.compare(DOUBLE_NAN[i], DOUBLE_NAN[j]), 0);
                assertEquals(Double.compare(DOUBLE_NAN[j], DOUBLE_NAN[i]), 0);
            }
        }
        for (int i=0; i<DOUBLE_ORDER.length; i++)
        {
            assertEquals(Double.compare(DOUBLE_ORDER[i], DOUBLE_ORDER[i]), 0);
            if (i > 0)
            {
                assertEquals(Double.compare(DOUBLE_ORDER[i-1], DOUBLE_ORDER[i]), -1);
                assertEquals(Double.compare(DOUBLE_ORDER[i], DOUBLE_ORDER[i-1]), 1);
            }
            for (int j=0; j<DOUBLE_NAN.length; j++)
            {
                assertEquals(Double.compare(DOUBLE_ORDER[i], DOUBLE_NAN[j]), -1);
                assertEquals(Double.compare(DOUBLE_NAN[j], DOUBLE_ORDER[i]), 1);
            }
        }
    }

    private void testMinFloat() {
        for (int i=0; i<FLOAT_ORDER.length; i++)
        {
            for (int j=0; j<FLOAT_NAN.length; j++)
            {
                assertNaN(Math.min(FLOAT_NAN[j], FLOAT_ORDER[i]));
                assertNaN(Math.min(FLOAT_ORDER[i], FLOAT_NAN[j]));
            }
        }
        for (int i=1; i<FLOAT_ORDER.length; i++)
        {
            assertEqualsExact(Math.min(FLOAT_ORDER[i-1], FLOAT_ORDER[i]), FLOAT_ORDER[i-1]);
            assertEqualsExact(Math.min(FLOAT_ORDER[i], FLOAT_ORDER[i-1]), FLOAT_ORDER[i-1]);
        }
        
    }

    private void testMinDouble() {
        for (int i=0; i<DOUBLE_ORDER.length; i++)
        {
            for (int j=0; j<DOUBLE_NAN.length; j++)
            {
                assertNaN(Math.min(DOUBLE_NAN[j], DOUBLE_ORDER[i]));
                assertNaN(Math.min(DOUBLE_ORDER[i], DOUBLE_NAN[j]));
            }
        }
        for (int i=1; i<DOUBLE_ORDER.length; i++)
        {
            assertEqualsExact(Math.min(DOUBLE_ORDER[i-1], DOUBLE_ORDER[i]), DOUBLE_ORDER[i-1]);
            assertEqualsExact(Math.min(DOUBLE_ORDER[i], DOUBLE_ORDER[i-1]), DOUBLE_ORDER[i-1]);
        }
        
    }

    private void testMaxFloat() {
        for (int i=0; i<FLOAT_ORDER.length; i++)
        {
            for (int j=0; j<FLOAT_NAN.length; j++)
            {
                assertNaN(Math.max(FLOAT_NAN[j], FLOAT_ORDER[i]));
                assertNaN(Math.max(FLOAT_ORDER[i], FLOAT_NAN[j]));
            }
        }
        for (int i=1; i<FLOAT_ORDER.length; i++)
        {
            assertEqualsExact(Math.max(FLOAT_ORDER[i-1], FLOAT_ORDER[i]), FLOAT_ORDER[i]);
            assertEqualsExact(Math.max(FLOAT_ORDER[i], FLOAT_ORDER[i-1]), FLOAT_ORDER[i]);
        }
        
    }

    private void testMaxDouble() {
        for (int i=0; i<DOUBLE_ORDER.length; i++)
        {
            for (int j=0; j<DOUBLE_NAN.length; j++)
            {
                assertNaN(Math.max(DOUBLE_NAN[j], DOUBLE_ORDER[i]));
                assertNaN(Math.max(DOUBLE_ORDER[i], DOUBLE_NAN[j]));
            }
        }
        for (int i=1; i<DOUBLE_ORDER.length; i++)
        {
            assertEqualsExact(Math.max(DOUBLE_ORDER[i-1], DOUBLE_ORDER[i]), DOUBLE_ORDER[i]);
            assertEqualsExact(Math.max(DOUBLE_ORDER[i], DOUBLE_ORDER[i-1]), DOUBLE_ORDER[i]);
        }
        
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        MathTest t = new MathTest();
        t.testFloor();
        t.testCeil();
        t.testFloatCompare();
        t.testDoubleCompare();
        t.testMinFloat();
        t.testMinDouble();
        t.testMaxFloat();
        t.testMaxDouble();
        System.out.println("OK");
        Sound.beepSequenceUp();
    	Button.waitForAnyPress();
    }


}
