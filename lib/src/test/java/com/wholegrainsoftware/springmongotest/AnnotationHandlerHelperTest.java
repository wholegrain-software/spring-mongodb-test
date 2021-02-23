/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Wholegrain Software, Jimi Steidl <www.wholegrain-software.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.wholegrainsoftware.springmongotest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnnotationHandlerHelperTest {
    private final TestContext ctx = mock(TestContext.class);
    private final ApplicationContext appCtx = mock(ApplicationContext.class);
    private final Environment env = mock(Environment.class);

    @BeforeEach
    public void setup() {
        when(ctx.getApplicationContext()).thenReturn(appCtx);
        when(appCtx.getEnvironment()).thenReturn(env);
    }

    @Test
    public void getDatabaseNameReturnsDatabaseFromDatabaseProperty() {
        when(env.getProperty("spring.data.mongodb.database")).thenReturn("my_database");
        when(env.getProperty("spring.data.mongodb.uri")).thenReturn("mongodb://localhost:27017/my_other_db");

        assertEquals(getDatabaseName(ctx), "my_database");
    }

    @Test
    public void getDatabaseNameReturnsDatabaseFromUriProperty() {
        when(env.getProperty("spring.data.mongodb.database")).thenReturn(null);
        when(env.getProperty("spring.data.mongodb.uri")).thenReturn("mongodb://localhost:27017/my_other_db");

        assertEquals(getDatabaseName(ctx), "my_other_db");
    }

    @Test
    public void getDatabaseNameThrowsExceptionIfNeitherPropertyIsSet() {
        MongoDBTestException ex = assertThrows(MongoDBTestException.class, () -> {
            getDatabaseName(ctx);
        });

        assertEquals(ex.getMessage(), "Failed to determine database. You need to provide either 'spring.data.mongodb.database' or 'spring.data.mongodb.uri' property in application.yaml.");
    }

    @Test
    public void asStringReadsASpringResourceIntoString() throws Exception {
        Resource res = mock(Resource.class);
        when(res.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));

        assertEquals(asString(res), "test");
    }

    @Test
    public void asStringThrowsExceptionIfFileCannotBeRead() throws Exception {
        IOException ioex = new IOException();
        Resource res = mock(Resource.class);
        when(res.getInputStream()).thenThrow(ioex);

        MongoDBTestException ex = assertThrows(MongoDBTestException.class, () -> asString(res));

        assertEquals(ex.getMessage(), "Failed to read file.");
        assertEquals(ex.getCause(), ioex);
    }

    @Test
    public void getStreamThrowsExceptionIfFileCannotBeRead() throws Exception {
        IOException ioex = new IOException();
        Resource res = mock(Resource.class);
        when(res.getInputStream()).thenThrow(ioex);

        MongoDBTestException ex = assertThrows(MongoDBTestException.class, () -> getStream(res));

        assertEquals(ex.getMessage(), "Failed to read file.");
        assertEquals(ex.getCause(), ioex);
    }

    @Test
    public void getNameThrowsExceptionIfNameIsNull() {
        Resource res = mock(Resource.class);
        when(res.getFilename()).thenReturn(null);

        MongoDBTestException ex = assertThrows(MongoDBTestException.class, () -> getName(res));

        assertEquals(ex.getMessage(), "Filename should not be null.");
    }
}