/*
* Copyright 2014 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.jayway.awaitility.groovy

import com.jayway.awaitility.core.ConditionFactory

import java.util.concurrent.Callable

import static com.jayway.awaitility.spi.Timeout.timeout_message

trait AwaitilityTrait {
  private Initializer initializer = new Initializer()

  static class Initializer {
    Initializer() {
      timeout_message = "Condition was not fulfilled"

      def originalMethod = ConditionFactory.metaClass.getMetaMethod("until", Runnable.class)
      ConditionFactory.metaClass.until { Runnable runnable ->
        if (runnable instanceof Closure) {
          delegate.until(new Callable<Boolean>() {
            Boolean call() {
              return (runnable as Closure).call();
            }
          });
        } else {
          originalMethod.invoke(delegate, runnable)
        }
        // Return true to signal that everything went OK (for spock tests)
        true
      }
    }
  }
}