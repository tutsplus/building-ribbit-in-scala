package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._


object Ribbits extends Controller {

	def createForm (session: Session) = Form(
		single("ribbit" -> text) verifying ("Could not add Ribbit. Sorry.", result => result match {
				case (ribbit) => RibbitRepository.create(ribbit, session.get("email"))._1.equals(ribbit)
			})
	)

	def getLoggedInUser(session: Session): Account = {
		def getLoggedInEmail = session.get("email") match {
			case Some(email) => email
			case None => ""
		}
		def getUserFromOption(user: Option[Account]): Account = user match {
			case Some(account) => account
			case None => new NullAccount
		}
		getUserFromOption(Account.findByEmail(getLoggedInEmail))
	}

	def public = Action { implicit request =>
		val user = getLoggedInUser(request.session)
		Ok(html.public("Logged in")(user)(createForm(request.session))(RibbitRepository.findAll))
	}

	def createRibbit = Action { implicit request =>
		createForm(request.session).bindFromRequest.fold(
			formWithErrors => BadRequest(html.public("Logged in")(getLoggedInUser(request.session))(formWithErrors)(RibbitRepository.findAll)),
			ribbit => Redirect(routes.Ribbits.public)
		)
	}
}
