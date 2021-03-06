/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.beam.runners.samza.metrics;

import java.io.Closeable;
import org.apache.beam.sdk.metrics.MetricsEnvironment;

/** This class wraps a {@link java.util.function.Supplier} function call with BEAM metrics. */
public class FnWithMetricsWrapper {

  /** Interface for functions to be wrapped with metrics. */
  public interface SupplierWithException<T> {
    T get() throws Exception;
  }

  private final SamzaMetricsContainer metricsContainer;
  private final String stepName;

  public FnWithMetricsWrapper(SamzaMetricsContainer metricsContainer, String stepName) {
    this.metricsContainer = metricsContainer;
    this.stepName = stepName;
  }

  public <T> T wrap(SupplierWithException<T> fn) throws Exception {
    try (Closeable closeable =
        MetricsEnvironment.scopedMetricsContainer(metricsContainer.getContainer(stepName))) {
      T result = fn.get();
      metricsContainer.updateMetrics();
      return result;
    }
  }
}
