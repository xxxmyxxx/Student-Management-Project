package com.project.payload.messages;

public class ErrorMessages {

    private  ErrorMessages(){}

    public static final String NOT_PERMITTED_METHOD_MESSAGE = "You do not have any permission to do this operation";
    public static final String PASSWORD_NOT_MATCHED = "Your passwords are not matched";
    public static final String ALREADY_REGISTER_MESSAGE_USERNAME = "Error : User with username %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_SSN = "Error : User with ssn %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_PHONE = "Error : User with phone %s is already registered";
    public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "Error : User with email %s is already registered";
    public static final String ROLE_NOT_FOUND = "There is no role like that, check the database";
    public static final String NOT_FOUND_USER_USERROLE_MESSAGE = "Error: User not found with user-role %s";
    public static final String NOT_FOUND_USER_MESSAGE = "Error: User not found with id %s";
    public static final String NOT_FOUND_TEACHER_MESSAGE = "Error: Teacher not found with id %s";
    public static final String NOT_FOUND_ADVISOR_MESSAGE = "Error:  Advisor Teacher not found with id %s";
    public static final String NOT_EXIST_ADVISOR_MESSAGE = "Error:  Advisor Teacher with id %s is not exist";
    public static final String ALREADY_EXIST_ADVISOR_MESSAGE = "Error:  Advisor Teacher with id %s is already exist";
    public static final String NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME = "Error: Advisor Teacher with username %s not found" ;

    public static final String NOT_FOUND_STUDENT_MESSAGE = "Error: Student not found with id %s";
    public static final String NOT_FOUND_LESSON_PROGRAM_WITHOUT_ID_MESSAGE = "Error: Lesson Program with this field not found ";
    public static final String LESSON_PROGRAM_ALREADY_EXIST = "Error: Course schedule can not be selected for the same hour and time  ";

    public static final String EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE = "Error: The start date can not be earlier than the last registration date";
    public static final String EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE = "Error: The end date can not be earlier than the start date";

    public static final String EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR = "Error: Education Term with Term and Year already exist ";

    public static  final String EDUCATION_TERM_CONFLICT_MESSAGE = "Error: There is a conflict regarding the dates of the education terms.";

    public static final String EDUCATION_TERM_NOT_FOUND_MESSAGE = "Error: Education Term not found with id %s";

    public static final String LESSON_ALREADY_EXIST = "Error: Lesson is already exist ";
    public static final String LESSON_ALREADY_EXIST_WITH_LESSON_NAME = "Error: Lesson is already exist with Lesson Name : %s ";
    public static final String NOT_FOUND_LESSON_MESSAGE = "Error: Lesson not found with id %s";
    public static final String NOT_FOUND_LESSON_MESSAGE_WITH_LESSON_NAME = "Error: Lesson not found with lesson name %s";
    public static final String NOT_FOUND_LESSON_IN_LIST = "Error: Lesson not found in the list";
    public static final String TIME_NOT_VALID_MESSAGE = "Error: incorrect time";

    public static final String NOT_FOUND_LESSON_PROGRAM_MESSAGE = "Error: Lesson Program not found with id %s";

    public static final String ALREADY_REGISTER_LESSON_MESSAGE = "Error : Lesson with lesson name %s is already registered";

    public static final String STUDENT_INFO_NOT_FOUND = "Error: Student Info not found with id %s";
    public static final String STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID = "Error: Student Info not found with student id %s";

    public static final String NOT_FOUND_USER_WITH_ROLE_MESSAGE = "Error: The role information of the user with id %s is not role: %s" ;

    public static final String MEET_HOURS_CONFLICT = "Error: Meet hours has conflict with existing meets";
    public static final String MEET_NOT_FOUND_MESSAGE = "Error: Meet not found with id %s";
}
