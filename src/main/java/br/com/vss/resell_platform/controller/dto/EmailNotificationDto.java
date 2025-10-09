package br.com.vss.resell_platform.controller.dto;

public record EmailNotificationDto(
        String receiver,
        String notificationType,
        String item
) {
}
