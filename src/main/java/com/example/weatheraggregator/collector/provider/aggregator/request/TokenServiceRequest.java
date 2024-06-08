package com.example.weatheraggregator.collector.provider.aggregator.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenServiceRequest {
    @SerializedName(value = "client_id")
    private String clientId;

    @SerializedName(value = "client_secret")
    private String clientSecret;

    @SerializedName(value = "audience")
    private String audience;

    @SerializedName(value = "grant_type")
    private String grantType;
}
