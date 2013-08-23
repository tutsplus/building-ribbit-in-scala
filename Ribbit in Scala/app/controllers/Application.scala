package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller {

	val loginForm = Form(
		tuple(
			"email" -> text,
			"password" -> text
		) verifying ("Invalid email or password", result => result match {
				case (email, password) => Account.authenticate(email, password).isDefined
			})
	)
	val createForm = Form(
		tuple(
			"name" -> text,
			"email" -> text,
			"password" -> text,
			"confirm" -> text
		) verifying ("Invalid email or password", result => result match {
				case (name, email, password, confirm) => Account.create(name, email, password, confirm).isDefined
			})
	)

	def index = Action { implicit request =>
		Ok(html.index(loginForm, createForm))
	}

	def authenticate = Action { implicit request =>
		loginForm.bindFromRequest.fold(
			formWithErrors => BadRequest(html.index(formWithErrors, createForm)),
			user => Redirect(routes.Ribbits.public).withSession("email" -> user._1)
		)
	}

	def logout = Action {
		Redirect(routes.Application.index).withNewSession.flashing(
			"success" -> "You've been logged out"
		)
	}

//	def public = Action { implicit request =>
//		def getLoggedInEmail = request.session.get("email") match {
//			case Some(email) => email
//			case None => ""
//		}
//		def getUserFromOption(user: Option[Account]) = user match {
//			case Some(account) => account
//			case None => new NullAccount
//		}
//		val user = getUserFromOption(Account.findByEmail(getLoggedInEmail))
//		Ok(html.public("Logged in")(user))
//	}

	def createAccount = Action { implicit request =>
		createForm.bindFromRequest.fold(
			formWithErrors => BadRequest(html.index(loginForm,formWithErrors)),
			user => Redirect(routes.Ribbits.public).withSession("email" -> user._2)
		)
	}

}


