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

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.util.TestContextResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.asString;
import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.getDatabaseName;

class MongoDBAnnotationHandler implements AnnotationHandler<Doc> {
    private final List<String> databaseNames = new ArrayList<>();

    @Override
    public void runScript(TestContext context, Doc annotation) {
        Class<?> testClass = context.getTestClass();
        ApplicationContext applicationContext = context.getApplicationContext();

        String[] fileNames = TestContextResourceUtils.convertToClasspathResourcePaths(testClass, annotation.files());
        List<Resource> resources = TestContextResourceUtils.convertToResourceList(applicationContext, fileNames);
        List<Document> documents = resources.stream()
                .map((res) -> Document.parse(read(res)))
                .collect(Collectors.toList());

        inDbSession(context, annotation.db(), (db) -> db.getCollection(annotation.collection()).insertMany(documents));
    }

    @Override
    public void cleanup(TestContext context) {
        List<String> dbNames = new ArrayList<>(databaseNames);
        databaseNames.clear();
        for (String dbName : dbNames) {
            inDbSession(context, dbName, (db) -> db.listCollectionNames().forEach((name) -> db.getCollection(name).deleteMany(all())));
        }
    }

    private void inDbSession(TestContext context, String databaseName, Consumer<MongoDatabase> script) {
        String dbName = databaseName.isEmpty() ? getDatabaseName(context) : databaseName;
        databaseNames.add(dbName);
        MongoClient client = context.getApplicationContext().getBean(MongoClient.class);
        MongoDatabase database = client.getDatabase(dbName);
        ClientSession session = client.startSession();
        script.accept(database);
        session.close();
    }

    private Bson all() {
        return new Document();
    }

    private String read(Resource resource) {
        return asString(resource);
    }
}
