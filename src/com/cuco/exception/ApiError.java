package com.cuco.exception;

import com.google.gson.annotations.SerializedName;

public record ApiError(String result, @SerializedName("error-type") String errorType) {
}
