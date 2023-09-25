package com.project.contactmessage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ContactMessageUpdateRequest {


    @Size(min=3, max =16, message = "Your name should be at least 3 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your name must consist of the character .")
    private String name;


    @Size(min=5, max =20, message = "Your email should be at least 5 chars")
    @Email(message = "Please enter valid email")
    private String email;


    @Size(min=4, max =20, message = "Your subject should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your subject must consist of the character .")
    private String subject;


    @Size(min=4, max =50, message = "Your message should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+",message = "Your message must consist of the character .")
    private String message;
}