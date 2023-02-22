package com.example.weatheraggregator.collector.provider.visualcrossing.response.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultVC {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotBlank
    @SerializedName(value = "address")
    private String city;
    @SerializedName(value = "days")
    @NotEmpty
    private List<@Valid DayData> dayData;
}
