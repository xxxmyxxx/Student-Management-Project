package com.project.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.payload.response.abstracts.BaseUserResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseUserResponse {

}
