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

package com.wholegrainsoftware.example.gridfs;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.wholegrainsoftware.example.MongoDbTest;
import com.wholegrainsoftware.example.util.InsertTestPdf;
import com.wholegrainsoftware.example.util.InsertTestPng;
import com.wholegrainsoftware.springmongotest.MongoDBTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class GridFsScriptsTest extends MongoDbTest {
    private static final ObjectId FILE_OID = new ObjectId(InsertTestPdf.FILE_ID);
    private static final ObjectId PRODUCT_FILE_OID = new ObjectId(InsertTestPng.FILE_ID);
    private static final String CREATED_BY_ID = "60327cc5dbc0a320d7544ae3";
    private static final ObjectId CREATED_BY_OID = new ObjectId(CREATED_BY_ID);

    @Autowired
    private GridFsTemplate gridFs;

    @Autowired
    @Qualifier("productDbGridFsTemplate")
    private GridFsTemplate productDbGridFs;

    @Test
    @MongoDBTest
    @InsertTestPdf
    public void fileIsPersistedToDatabase() {
        GridFSFile file = gridFs.findOne(new Query().addCriteria(
                where("_id").is(FILE_OID)
                        .and("metadata.createdBy").is(CREATED_BY_OID)
        ));

        assertThat(file).isNotNull();
        assertThat(file.getMetadata()).isNotNull();
        assertThat(file.getMetadata().get("createdBy")).isEqualTo(CREATED_BY_OID);
        assertThat(file.getFilename()).isEqualTo("test.pdf");
        assertThat(file.getLength()).isEqualTo(78279L);
    }

    @Test
    @MongoDBTest
    @InsertTestPng
    public void fileIsPersistedToProductDatabase() {
        GridFSFile file = productDbGridFs.findOne(new Query().addCriteria(
                where("_id").is(PRODUCT_FILE_OID)
                        .and("metadata.createdBy").is(CREATED_BY_OID)
        ));

        assertThat(file).isNotNull();
        assertThat(file.getMetadata()).isNotNull();
        assertThat(file.getMetadata().get("createdBy")).isEqualTo(CREATED_BY_OID);
        assertThat(file.getFilename()).isEqualTo("test.png");
        assertThat(file.getLength()).isEqualTo(3850L);
    }
}
