package it.younify.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountSummary {

    private String accountName;
    private String accountEmail;
}
