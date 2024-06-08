package com.example.weatheraggregator.collector.provider.aggregator.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenServiceResponse {
    @SerializedName(value = "access_token")
    private String accessToken;

    @SerializedName(value = "expires_in")
    private String expiresIn;

    @SerializedName(value = "token_type")
    private String tokenType;
}
