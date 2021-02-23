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

import java.io.IOException;

import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.MONGODB_DATABASE;
import static com.wholegrainsoftware.springmongotest.AnnotationHandlerHelper.MONGODB_URI;

public class MongoDBTestException extends RuntimeException {

    public MongoDBTestException(String message) {
        super(message);
    }

    public MongoDBTestException(String message, Exception reason) {
        super(message, reason);
    }

    public static MongoDBTestException unspecifiedDatabase() {
        return new MongoDBTestException("Failed to determine database. You need to provide either '" +
                MONGODB_DATABASE + "' or '" + MONGODB_URI + "' property in application.yaml.");
    }

    public static MongoDBTestException failedToReadFile(IOException ex) {
        return new MongoDBTestException("Failed to read file.", ex);
    }

    public static MongoDBTestException fileNameShouldNotBeNull() {
        return new MongoDBTestException("Filename should not be null.");
    }
}
