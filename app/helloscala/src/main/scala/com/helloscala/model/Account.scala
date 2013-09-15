package com.helloscala.model

case class Account(id: String, user: MUser, data: Option[AnyRef] = None)
