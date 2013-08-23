package models

import org.mindrot.jbcrypt.BCrypt
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession

case class Account(email: String, password: String, name: String)

object User extends Table[(String, String, String)]("USERS"){
	def email = column[String]("EMAIL", O.PrimaryKey)
	def password = column[String]("PASSWORD")
	def name = column[String]("NAME")

	def * = email ~ password ~ name
}

object Account {

	def authenticate(email: String, password: String): Option[Account] = {
		findByEmail(email).filter { account => BCrypt.checkpw(password, account.password) }
	}

	def create(name: String, email: String, password: String, confirm: String): Option[Account] = {
		if (password != confirm) None
		else {
			Database.forURL("jdbc:h2:ribbits", driver = "org.h2.Driver") withSession {
				try {
					User.ddl.create
				} catch {
					case e: org.h2.jdbc.JdbcSQLException => println("Skipping table createion. It already exists.")
				}
				User.insert(email, BCrypt.hashpw(password, BCrypt.gensalt()), name)
			}
			findByEmail(email)
		}
	}

	def findByEmail(email: String): Option[Account] = findBy("email", email)

	def findByName(name: String): Option[Account] = findBy("name", name)

	def findAll(): Seq[Account] = findByName("John Doe").toSeq

	def findBy(field: String, value: String): Option[Account] = {
		val user = Database.forURL("jdbc:h2:ribbits", driver = "org.h2.Driver") withSession {
			val foundUser = field match {
				case "email" => for {
						u <- User if u.email.toLowerCase === value.toLowerCase
					} yield(u)
				case "name" => for {
						u <- User if u.name.toLowerCase === value.toLowerCase
					} yield(u)
			}

			foundUser.firstOption
		}

		user map {
			case (email, password, name) => new Account(email, password, name)
		}
	}

}

class NullAccount(email: String = "not@set", password: String = "", name: String = "Unknown")
extends Account(email: String, password: String, name: String)  {
}
