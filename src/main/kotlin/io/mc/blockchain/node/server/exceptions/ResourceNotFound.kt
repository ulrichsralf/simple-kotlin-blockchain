package io.mc.blockchain.node.server.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Ralf Ulrich
 * 05.10.17
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFound : Exception()