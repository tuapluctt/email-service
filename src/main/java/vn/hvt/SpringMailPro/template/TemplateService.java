package vn.hvt.SpringMailPro.template;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import vn.hvt.SpringMailPro.exception.EmailException;
import vn.hvt.SpringMailPro.exception.ErrorCode;


import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    public String processTemplate(String templateName, Object model) {
        try {
            Context context = new Context();

            if (model instanceof Map) {
                Map<String, Object> modelMap = (Map<String, Object>) model;
                modelMap.forEach((key, value) -> {
                    if (key != null) {
                        context.setVariable(key, value);
                    }
                });
            } else if (model != null) {
                context.setVariable("model", model);
            }

            return templateEngine.process(templateName, context);
        } catch (TemplateInputException e) {
            log.error("Template not found: {}", templateName, e);
            throw new EmailException(ErrorCode.TEMPLATE_NOT_FOUND);
        } catch (TemplateProcessingException e) {
            log.error("Error processing template: {}", templateName, e);
            throw new EmailException(ErrorCode.TEMPLATE_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error processing template: {}", templateName, e);
            throw new EmailException(ErrorCode.TEMPLATE_ERROR,
                    "Unexpected error processing template");
        }
    }
}