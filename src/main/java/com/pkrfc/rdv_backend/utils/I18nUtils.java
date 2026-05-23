package com.pkrfc.rdv_backend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

public class I18nUtils {

    private static final ResourceBundleMessageSource messageSource;

    static {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    public static String getMessage(String code) {
        return getMessage(code, null);
    }
    public static HttpServletRequest getRequestContexte() {
        RequestAttributes requestContexte = RequestContextHolder.getRequestAttributes();
        return Objects.nonNull(requestContexte) ? ((ServletRequestAttributes) requestContexte).getRequest() : null;
    }

    public static String getMessage(String code, Object[] params) {
        Locale locale = getCurrentLocale();
        String message = messageSource.getMessage(code, params, locale);
        return MessageFormat.format(message, params);
    }
    public static Locale getCurrentLocale() {
        // Récupérer la locale à partir de l'en-tête de la requête
        HttpServletRequest request =getRequestContexte();
        String headerLang = request != null ? request.getHeader("codeisolang") : null;

        if (headerLang == null || headerLang.isEmpty()) {
            return new Locale("fr"); // Locale par défaut
        }

        return Locale.forLanguageTag(headerLang);
    }
}
