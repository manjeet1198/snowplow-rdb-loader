package com.snowplowanalytics.snowplow.rdbloader.db

import cats.implicits._
import java.sql.{ResultSet, SQLException}

import Decoder._
import Entities._

trait Decoder[A] {
  def decode(resultSet: ResultSet): Either[JdbcDecodeError, A]
}

object Decoder {

  final case class JdbcDecodeError(message: String)

  implicit val countDecoder: Decoder[Option[Count]] =
    new Decoder[Option[Count]] {
      final def decode(resultSet: ResultSet): Either[JdbcDecodeError, Option[Count]] = {
        var buffer: Count = null
        try {
          if (resultSet.next()) {
            val count = resultSet.getLong("count")
            buffer = Count(count)
            Option(buffer).asRight[JdbcDecodeError]
          } else None.asRight[JdbcDecodeError]
        } catch {
          case s: SQLException =>
            JdbcDecodeError(s.getMessage).asLeft[Option[Count]]
        } finally {
          resultSet.close()
        }
      }
    }

  implicit val timestampDecoder: Decoder[Option[Timestamp]] =
    new Decoder[Option[Timestamp]] {
      final def decode(resultSet: ResultSet): Either[JdbcDecodeError, Option[Timestamp]] = {
        var buffer: Timestamp = null
        try {
          if (resultSet.next()) {
            val etlTstamp = resultSet.getTimestamp("etl_tstamp")
            buffer = Timestamp(etlTstamp)
            Option(buffer).asRight[JdbcDecodeError]
          } else None.asRight[JdbcDecodeError]
        } catch {
          case s: SQLException =>
            JdbcDecodeError(s.getMessage).asLeft[Option[Timestamp]]
        } finally {
          resultSet.close()
        }
      }
    }

  implicit val manifestItemDecoder: Decoder[Option[LoadManifestItem]] =
    new Decoder[Option[LoadManifestItem]] {
      final def decode(resultSet: ResultSet): Either[JdbcDecodeError, Option[LoadManifestItem]] = {
        var buffer: LoadManifestItem = null
        try {
          if (resultSet.next()) {
            val etlTstamp = resultSet.getTimestamp("etl_tstamp")
            val commitTstamp = resultSet.getTimestamp("commit_tstamp")
            val eventCount = resultSet.getInt("event_count")
            val shreddedCardinality = resultSet.getInt("shredded_cardinality")

            eventCount.toString ++ shreddedCardinality.toString // forcing NPE

            buffer = LoadManifestItem(etlTstamp, commitTstamp, eventCount, shreddedCardinality)

            Option(buffer).asRight[JdbcDecodeError]
          } else None.asRight[JdbcDecodeError]
        } catch {
          case s: SQLException =>
            JdbcDecodeError(s.getMessage).asLeft[Option[LoadManifestItem]]
          case _: NullPointerException =>
            val message = "Error while decoding Load Manifest Item. Not all expected values are available"
            JdbcDecodeError(message).asLeft[Option[LoadManifestItem]]
        } finally {
          resultSet.close()
        }
      }
    }
}