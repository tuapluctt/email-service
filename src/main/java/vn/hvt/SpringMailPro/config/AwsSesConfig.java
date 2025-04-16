package vn.hvt.SpringMailPro.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSesConfig {


    @Value("${spring.aws.accessKey}")
    private String accessKey;

    @Value("${spring.aws.secretKey}")
    private String secretKey;

    @Value("${spring.aws.region}")
    private String region;

    @Value("${spring.aws.ses.sender}")
    private String senderEmail;

    @Bean
    public AmazonSimpleEmailService amazonSES() {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(region)
                .build();
    }
}
