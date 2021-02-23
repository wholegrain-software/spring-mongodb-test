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

package com.wholegrainsoftware.example.mongodb;

import com.wholegrainsoftware.example.MongoDbTest;
import com.wholegrainsoftware.example.department.Department;
import com.wholegrainsoftware.example.department.DepartmentRepository;
import com.wholegrainsoftware.example.util.InsertPeople;
import com.wholegrainsoftware.example.util.InsertSalesDepartment;
import com.wholegrainsoftware.springmongotest.MongoDBTest;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@InsertPeople
@InsertSalesDepartment
public class MongoDbScriptsTest extends MongoDbTest {
    private static final ObjectId JOHN_DOE_ID = new ObjectId("6032473edd63e50bd3565171");
    private static final ObjectId JANE_DOE_ID = new ObjectId("6032473edd63e50bd3565172");
    private static final Person JOHN_DOE = new Person(JOHN_DOE_ID, "John", "Doe");
    private static final Person JANE_DOE = new Person(JANE_DOE_ID, "Jane", "Doe");
    private static final ObjectId SALES_ID = new ObjectId("60324824dbc0a320d7544ae1");
    private static final Department SALES = new Department(SALES_ID, Arrays.asList(JOHN_DOE, JANE_DOE));

    @Autowired
    private PersonRepository personRepo;
    @Autowired
    private DepartmentRepository departmentRepo;

    @Test
    @MongoDBTest
    public void personCanBeRetrievedFromDatabase() {
        List<ObjectId> ids = Arrays.asList(JOHN_DOE_ID, JANE_DOE_ID);
        Iterable<Person> persons = personRepo.findAllById(ids);

        assertThat(persons).contains(JOHN_DOE, JANE_DOE);
    }

    @Test
    @MongoDBTest
    public void departmentCanBeRetrievedFromDatabase() {
        Optional<Department> department = departmentRepo.findById(SALES_ID);

        assertThat(department.isPresent()).isTrue();
        assertThat(department.get()).isEqualTo(SALES);
    }
}
