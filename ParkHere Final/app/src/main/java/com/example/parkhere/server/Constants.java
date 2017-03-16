package com.example.parkhere.server;

public class Constants {
    public static final String BASE_URL = "http://10.0.2.2/";
    public static final String REGISTER_OPERATION = "register";
    public static final String LOGIN_OPERATION = "login";
    public static final String FORGOT_PASSWORD_OPERATION = "forgot_password";
    public static final String UPLOAD_GOV_ID_OPERATION = "upload_gov_id";
    public static final String CREATE_PROVIDER_PROFILE_OPERATION = "create_provider_profile";
    public static final String CHANGE_PASSWORD_OPERATION = "forgot_password";
    public static final String GET_PROVIDER_PROFILE_OPERATION = "get_provider_profile";
    public static final String UPDATE_PROFILE_PERSONAL_OPERATION = "update_profile_personal";
    public static final String UPDATE_PROFILE_PICTURE_OPERATION = "update_profile_picture";
    public static final String UPDATE_PROVIDER_PAYMENT_OPERATION = "update_provider_payment";
    public static final String CREATE_SEEKER_PROFILE_OPERATION = "create_seeker_profile";
    public static final String ADD_PROVIDER_ROLE_OPERATION = "add_provider_role";
    public static final String ADD_SEEKER_ROLE_OPERATION = "add_seeker_role";
    public static final String CHANGE_ROLE_OPERATION = "change_role";
    public static final String GET_SPACES_OPERATION = "get_spaces";
    public static final String DELETE_SPACE_OPERATION = "delete_space";
    public static final String GET_PARKING_SPACE_RESERVATIONS_OPERATION = "get_parking_space_reservations";
    public static final String GET_PARKING_SPACE_RATINGS_OPERATION = "get_parking_space_ratings";
    public static final String GET_CURRENT_TIME_OPERATION = "get_current_time";
    public static final String GET_NOTIFICATIONS_OPERATION = "get_notifications";
    public static final String DELETE_NOTIFICATION_OPERATION = "delete_notification";
    public static final String GET_SEEKER_RESERVATIONS_OPERATION = "get_seeker_reservations";
    public static final String ADD_PARKING_SPACE_OPERATION = "add_parking_space";
    public static final String ADD_PARKING_SPACE_AVAILABILITY_OPERATION = "add_parking_space_availability";
    public static final String UPDATE_PARKING_SPACE_OPERATION = "update_parking_space";
    public static final String EDIT_PARKING_SPACE_AVAILABILITY_OPERATION = "edit_parking_space_availability";
    public static final String DELETE_PARKING_SPACE_AVAILABILITY_OPERATION = "delete_parking_space_availability";
    public static final String GET_SEEKER_PROFILE_OPERATION = "get_seeker_profile";
    public static final String DELETE_VEHICLE_OPERATION = "delete_vehicle";
    public static final String UPDATE_VEHICLE_OPERATION = "update_vehicle";
    public static final String ADD_VEHICLE_OPERATION = "add_vehicle";
    public static final String DELETE_SEEKER_PAYMENT_OPERATION = "delete_seeker_payment";
    public static final String ADD_SEEKER_PAYMENT_OPERATION = "add_seeker_payment";
    public static final String ADD_PARKING_SPACE_REPEATED_AVAILABILITY_OPERATION = "add_parking_space_repeated_availability";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    //ADDED TO POOJA'S
    public static final String SEARCH_OPERATION = "search";
    public static final String INITIAL_SEARCH_OPERATION = "initialSearch";
    public static final String FILTER_SEARCH_OPERATION = "filterSearch";
    public static final String PARKING_RESERVATION = "createReservation";
    public static final String HISTORY = "history";
    public static final String RATINGS = "saveRatings";
    public static final String CARDS = "paymentOptions";
    public static final String NEW_PAYMENT = "newSeekerPayment";
    public static final String SEARCH_REPEATED_OPERATION = "searchRepeated";
    public static final String VEHICLE_OPTIONS = "vehicleOptions";

    public static final int[] DAYS_IN_MONTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
}
