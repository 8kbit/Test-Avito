package ru.checkout.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity<Object> validate() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails)
            return new ResponseEntity(SecurityContextHolder.getContext().getAuthentication().getPrincipal(), HttpStatus.OK);
        else return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }
}
