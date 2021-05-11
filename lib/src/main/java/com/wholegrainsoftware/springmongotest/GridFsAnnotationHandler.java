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

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.util.TestContextResourceUtils;

import java.util.List;
import java.util.function.Consumer;

import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.*;

class GridFsAnnotationHandler implements AnnotationHandler<GridFsFile> {
    private static final int CHUNK_SIZE = 1024;

    @Override
    public void runScript(TestContext context, GridFsFile annotation) {
        Class<?> testClass = context.getTestClass();
        ApplicationContext applicationContext = context.getApplicationContext();

        String[] fileNames = TestContextResourceUtils.convertToClasspathResourcePaths(testClass, annotation.filePath());
        List<Resource> resources = TestContextResourceUtils.convertToResourceList(applicationContext, fileNames);
        Resource file = resources.get(0);

        inGridFsSession(context, annotation.db(), (bucket) -> {
            bucket.uploadFromStream(id(annotation), getName(file), getStream(file), options(annotation));
        });
    }

    @Override
    public void cleanup(TestContext context) {
        for (String dbName : determineDatabaseNames(context)) {
            inGridFsSession(context, dbName, GridFSBucket::drop);
        }
    }

    private void inGridFsSession(TestContext context, String databaseName, Consumer<GridFSBucket> script) {
        String dbName = databaseName.isEmpty() ? getDatabaseName(context) : databaseName;
        MongoClient client = context.getApplicationContext().getBean(MongoClient.class);
        GridFSBucket bucket = GridFSBuckets.create(client.getDatabase(dbName));
        script.accept(bucket);
    }

    private BsonValue id(GridFsFile annotation) {
        return new BsonObjectId(new ObjectId(annotation.id()));
    }

    private GridFSUploadOptions options(GridFsFile annotation) {
        return new GridFSUploadOptions()
                .chunkSizeBytes(CHUNK_SIZE)
                .metadata(Document.parse(annotation.metadata()));
    }
}
