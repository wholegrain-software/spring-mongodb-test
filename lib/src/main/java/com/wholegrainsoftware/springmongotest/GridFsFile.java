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

import java.lang.annotation.*;

/**
 * {@code GridFsFile} is used to annotate a test class or test method to configure
 * insertion of a file into GridFs in MongoDB.
 *
 * <p>File insertion is performed by {@link MongoDBTestExecutionListener}
 * which has to be enabled manually.</p>
 *
 * @author Jimi Steidl
 * @see GridFsFiles
 * @see MongoDBTestExecutionListener
 * @since 0.0.1
 */
@Inherited
@Documented
@Repeatable(GridFsFiles.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface GridFsFile {
    /**
     * This specifies the database into which the documents should be inserted.
     */
    String db() default "";

    /**
     * The custom id value of the file in GridFs.
     */
    String id();

    /**
     * The path to the file that should be inserted into GridFs.
     * It will be interpreted as a Spring {@link org.springframework.core.io.Resource}.
     */
    String filePath();

    /**
     * <i>Optional</i> metadata that can be added to the file in GridFs.
     */
    String metadata() default "{}";
}
