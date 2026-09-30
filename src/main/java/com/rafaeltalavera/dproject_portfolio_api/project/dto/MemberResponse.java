package com.rafaeltalavera.dproject_portfolio_api.project.dto;

import java.util.UUID;

public record MemberResponse(UUID id, String externalId, String name, String assignment) {
}
