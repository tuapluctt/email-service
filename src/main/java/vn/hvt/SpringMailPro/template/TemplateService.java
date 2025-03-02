package vn.hvt.SpringMailPro.template;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    public String processTemplate(String templateName, Object model) {
        Context context = new Context();

        if (model instanceof Map<?,?>) {
            Map<String, Object> modelMap = (Map<String, Object>) model;
            modelMap.forEach(context::setVariable);
        } else {
            context.setVariable("model", model);
        }

        return templateEngine.process(templateName, context);
    }
}