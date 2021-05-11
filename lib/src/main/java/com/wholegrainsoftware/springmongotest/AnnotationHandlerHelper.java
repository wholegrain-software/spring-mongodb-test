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

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestContext;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.wholegrainsoftware.springmongotest.MongoDBTestException.*;
import static java.nio.charset.StandardCharsets.UTF_8;

class AnnotationHandlerHelper {
    static final String MONGODB_URI = "spring.data.mongodb.uri";
    static final String MONGODB_DATABASE = "spring.data.mongodb.database";
    private static final List<String> EXCLUDED_DB_NAMES = Arrays.asList("config", "admin", "local");

    public static String getDatabaseName(TestContext context) {
        Environment env = context.getApplicationContext().getEnvironment();
        String database = env.getProperty(MONGODB_DATABASE);
        if (database != null) return database;
        String uri = env.getProperty(MONGODB_URI);
        if (uri != null) return new ConnectionString(uri).getDatabase();
        throw unspecifiedDatabase();
    }

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException ex) {
            throw failedToReadFile(ex);
        }
    }

    public static InputStream getStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException ex) {
            throw failedToReadFile(ex);
        }
    }

    public static String getName(Resource resource) {
        String name = resource.getFilename();
        if (name == null) throw fileNameShouldNotBeNull();
        return name;
    }

    public static List<String> determineDatabaseNames(TestContext context) {
        MongoClient client = context.getApplicationContext().getBean(MongoClient.class);
        return StreamSupport
                .stream(client.listDatabaseNames().spliterator(), false)
                .filter(dbName -> !EXCLUDED_DB_NAMES.contains(dbName))
                .collect(Collectors.toList());
    }
}
