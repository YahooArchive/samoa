package com.yahoo.labs.samoa.core;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2013 Yahoo! Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DoubleVectorTest {
    private DoubleVector emptyVector, array5Vector;

    @Before
    public void setUp() {
        emptyVector = new DoubleVector();
        array5Vector = new DoubleVector(new double[] { 1.1, 2.5, 0, 4.7, 0 });
    }

    @Test
    public void testGetArrayRef() {
        assertThat(emptyVector.getArrayRef(), notNullValue());
        assertTrue(emptyVector.getArrayRef() == emptyVector.getArrayRef());
        assertEquals(5, array5Vector.getArrayRef().length);
    }

    @Test
    public void testGetArrayCopy() {
        double[] arrayRef;
        arrayRef = emptyVector.getArrayRef();
        assertTrue(arrayRef != emptyVector.getArrayCopy());
        assertThat(arrayRef, is(equalTo(emptyVector.getArrayCopy())));

        arrayRef = array5Vector.getArrayRef();
        assertTrue(arrayRef != array5Vector.getArrayCopy());
        assertThat(arrayRef, is(equalTo(array5Vector.getArrayCopy())));
    }

    @Test
    public void testNumNonZeroEntries() {
        assertEquals(0, emptyVector.numNonZeroEntries());
        assertEquals(3, array5Vector.numNonZeroEntries());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetValueOutOfBound() {
        @SuppressWarnings("unused")
        double value = emptyVector.getArrayRef()[0];
    }

    @Test()
    public void testSetValue() {
        // test automatic vector enlargement
        emptyVector.setValue(0, 1.0);
        assertEquals(1, emptyVector.getArrayRef().length);
        assertEquals(1.0, emptyVector.getArrayRef()[0], 0.0); // should be exactly the same, so delta=0.0

        emptyVector.setValue(5, 5.5);
        assertEquals(6, emptyVector.getArrayRef().length);
        assertEquals(2, emptyVector.numNonZeroEntries());
        assertEquals(5.5, emptyVector.getArrayRef()[5], 0.0); // should be exactly the same, so delta=0.0
    }

    @Test
    public void testAddToValue() {
        array5Vector.addToValue(2, 5.0);
        assertEquals(5, array5Vector.getArrayRef()[2], 0.0); // should be exactly the same, so delta=0.0

        // test automatic vector enlargement
        emptyVector.addToValue(0, 1.0);
        assertEquals(1, emptyVector.getArrayRef()[0], 0.0); // should be exactly the same, so delta=0.0
    }

    @Test
    public void testSumOfValues() {
        assertEquals(1.1 + 2.5 + 4.7, array5Vector.sumOfValues(), Double.MIN_NORMAL);
    }

}
