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

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * {@code TestExecutionListener} that provides support for inserting documents and
 * files into mongodb as well as cleanup.
 *
 * <h3>Database Selection</h3>
 * The database against which these operations are performed is determined by the Spring
 * datasource configuration properties in this order:
 * <ol>
 *     <li>spring.data.mongodb.database</li>
 *     <li>spring.data.mongodb.uri</li>
 * </ol>
 *
 * <h3>Cleanup</h3>
 * If a test method is annotated with {@link MongoDBTest}, the listener will:
 * <ul>
 *     <li>delete all documents from all collections in the given database.</li>
 *     <li>delete all files from GridFs in the given database.</li>
 * </ul>
 *
 * <h3>Document Insertion</h3>
 * If a test class or test method is annotated with {@link Doc}, the listener will
 * retrieve the given files and insert them into the defined collection. This will happen
 * for each {@link Doc} and {@link Docs} annotation found.
 *
 * <h3>File Insertion</h3>
 * If a test class or test method is annotated with {@link GridFsFile}, the listener will
 * insert the given files into GridFS with the defined id as well as optional metadata. This
 * will happen for each {@link GridFsFile} and {@link GridFsFiles} annotation found.
 *
 * <h3>Order of Insertion</h3>
 * Annotations at class-level are handled before annotations at method-level.
 * Annotation on the same level are handling in order of declaration.
 *
 * <h3>Activation</h3>
 * Activating the TestExecutionListener can be achieved by the following statement:
 * <pre>
 * &#64;TestExecutionListeners(
 *         value = {MongoDBTestExecutionListener.class},
 *         mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
 * )
 * </pre>
 *
 * <b>Note</b>: This {@code TestExecutionListener} has only been tested with sequential test execution. Using
 * parallel test execution might introduce unintended side effects.
 *
 * @author Jimi Steidl
 * @see Doc
 * @see Docs
 * @see GridFsFile
 * @see GridFsFiles
 * @see MongoDBTest
 * @see org.springframework.test.context.TestExecutionListener
 * @since 0.0.1
 */
public class MongoDBTestExecutionListener extends AbstractTestExecutionListener {
    private final MongoDBAnnotationHandler mongoDB = new MongoDBAnnotationHandler();
    private final GridFsAnnotationHandler gridFs = new GridFsAnnotationHandler();

    @Override
    public void beforeTestMethod(TestContext context) {
        if (hasMongoDbTestAnnotation(context)) {
            mongoDB.cleanup(context);
            gridFs.cleanup(context);
        }

        executePreparation(context, mongoDB, Doc.class);
        executePreparation(context, gridFs, GridFsFile.class);
    }

    private <T extends Annotation> void executePreparation(TestContext context, AnnotationHandler<T> preparator, Class<T> clazz) {
        Set<T> ca = AnnotatedElementUtils.getMergedRepeatableAnnotations(context.getTestClass(), clazz);
        Set<T> ma = AnnotatedElementUtils.getMergedRepeatableAnnotations(context.getTestMethod(), clazz);

        ca.forEach(a -> preparator.runScript(context, a));
        ma.forEach(a -> preparator.runScript(context, a));
    }

    private boolean hasMongoDbTestAnnotation(TestContext context) {
        return context.getTestMethod().isAnnotationPresent(MongoDBTest.class);
    }
}
