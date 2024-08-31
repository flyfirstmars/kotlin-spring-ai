package dev.natig.gandalf.config

import java.io.IOException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartException

@RestControllerAdvice
class GlobalControllerAdvice {

    private val logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        logger.error("Illegal argument exception: ${e.message}", e)
        return ResponseEntity("Invalid input provided", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIOException(e: IOException): ResponseEntity<String> {
        logger.error("IO exception: ${e.message}", e)
        return ResponseEntity("IO error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MultipartException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMultipartException(e: MultipartException): ResponseEntity<String> {
        logger.error("Multipart exception: ${e.message}", e)
        return ResponseEntity("File upload error", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(e: Exception): ResponseEntity<String> {
        logger.error("Unexpected exception: ${e.message}", e)
        return ResponseEntity("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}