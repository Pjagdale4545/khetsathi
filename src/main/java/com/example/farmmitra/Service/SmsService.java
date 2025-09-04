package com.example.farmmitra.Service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {

    public void sendSms(String mobileNumber, String message) {
        // This is a placeholder for a real SMS sending implementation.
        // In a production environment, you would integrate a third-party SMS gateway here.
        System.out.println("Sending SMS to " + mobileNumber + ": " + message);
        // Example: Twilio API call
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(new com.twilio.type.PhoneNumber(mobileNumber),
        //                 new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
        //                 message).create();
    }
}