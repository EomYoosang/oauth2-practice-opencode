package com.eomyoosang.oauth2.auth.application.command;

public record EmailSignupCommand(String email, String password, String name) {
}
