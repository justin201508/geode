/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire;

/**
 * A <code>UnmodifiableException</code> is thrown when a
 * an attempt is made to modify a GemFire member configuration attribute
 * that can not currently be modified. In most cases the reason it can
 * not be modified is that the member is active.
 */
public class UnmodifiableException extends GemFireException {
private static final long serialVersionUID = -1043243260052395455L;

  //////////////////////  Constructors  //////////////////////

  /**
   * Creates a new <code>UnmodifiableException</code>.
   */
  public UnmodifiableException(String message) {
    super(message);
  }
}
