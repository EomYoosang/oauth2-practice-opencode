package com.eomyoosang.oauth2.auth.application.command;

public record EmailLoginCommand(String email, String password, String deviceId) {
}
