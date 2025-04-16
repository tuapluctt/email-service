package vn.hvt.SpringMailPro.provider;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import vn.hvt.SpringMailPro.dto.EmailResponse;
import vn.hvt.SpringMailPro.model.Email;


public interface EmailProvider {

//     Send an email using this provider
//     return Response with success status and message ID or error
    EmailResponse sendEmail(Email email) throws MessagingException;


//     Check if the provider is available
//     true if the provider is available, false otherwise
    boolean isAvailable();

//    Get the name of the provider
//    return Provider name
    String getName();

    int getPriority();
}