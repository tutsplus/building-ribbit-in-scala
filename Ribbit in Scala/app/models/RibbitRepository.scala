package models

import org.mindrot.jbcrypt.BCrypt
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import java.util.Calendar
import java.text.SimpleDateFormat


case class RibbitRepository(content: String, sender: String, dateTime: String)

object RibbitRepository {

	object Ribbit extends Table[(String, String, String)]("RIBBITS"){
		def content = column[String]("CONTENT")
		def sender = column[String]("SENDER")
		def dateTime = column[String]("DATETIME", O.PrimaryKey)

		def * = content ~ sender ~ dateTime
	}

	def create(content: String, sender: Option[String]): (String,String,String,String) = {
		Database.forURL("jdbc:h2:ribbits", driver = "org.h2.Driver") withSession {
			try {
				Ribbit.ddl.create
			} catch {
				case e: org.h2.jdbc.JdbcSQLException => println("Skipping table createion. It already exists.")
			}

			def senderEmail = sender match {
				case Some(email) => email
				case None => "Unknown@email.address"
			}

			Ribbit.insert(content, senderEmail, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance.getTime))
		}
		findAll().last
	}

	def findAll(): Seq[(String,String,String,String)] = {
		val allRibbits = Database.forURL("jdbc:h2:ribbits", driver = "org.h2.Driver") withSession {
			try {
				Ribbit.ddl.create
			} catch {
				case e: org.h2.jdbc.JdbcSQLException => println("Skipping table createion. It already exists.")
			}

			val foundRibbits = for {
				r <- Ribbit
				u <- User if u.email.toLowerCase === r.sender.toLowerCase
			} yield((r.content, r.sender, r.dateTime, u.name))
			foundRibbits.list
		}
		allRibbits
	}

}

