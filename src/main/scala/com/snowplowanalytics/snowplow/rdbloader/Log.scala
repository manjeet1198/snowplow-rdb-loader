/*
 * Copyright (c) 2012-2019 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.snowplow.rdbloader

/**
 * End-of-the-world result type.
 * Controls how RDB Loader exits
 */
sealed trait Log

object Log {

  /**
   * Loading succeeded. No messages, 0 exit code
   */
  case object LoadingSucceeded extends Log {
    override def toString: String = s"Completed successfully"
  }

  /**
   * Loading failed. Write error message. 1 exit code.
   */
  case class LoadingFailed(error: String) extends Log {
    override def toString: String =  s"Failed:\n$error"
  }
}
