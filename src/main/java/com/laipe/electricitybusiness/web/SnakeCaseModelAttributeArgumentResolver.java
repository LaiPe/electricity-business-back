package com.laipe.electricitybusiness.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.JsonDeserializer;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Argument resolver that maps request parameters (query params in snake_case) to DTOs using Jackson.
 * Supports Jackson @JsonProperty annotations on DTO fields for proper snake_case mapping.
 */
@Slf4j
public class SnakeCaseModelAttributeArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public SnakeCaseModelAttributeArgumentResolver() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        // Custom deserializer for LocalDateTime to accept ISO_OFFSET datetimes with 'Z'
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
            @Override
            public LocalDateTime deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
                String text = p.getText();
                if (text == null || text.isEmpty()) return null;
                try {
                    // Try parsing as OffsetDateTime then convert to LocalDateTime
                    OffsetDateTime odt = OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                    return odt.toLocalDateTime();
                } catch (Exception ex) {
                    // Fallback to LocalDateTime parse
                    return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }
        });
        objectMapper.registerModule(module);

        log.info("SnakeCaseModelAttributeArgumentResolver initialized");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Support parameters with @QueryDto or @ModelAttribute
        boolean hasQueryDto = parameter.hasParameterAnnotation(QueryDto.class);
        boolean hasModelAttribute = parameter.hasParameterAnnotation(ModelAttribute.class);
        boolean supports = hasQueryDto || hasModelAttribute;

        if (supports) {
            log.info("SnakeCaseModelAttributeArgumentResolver supports parameter: {} (hasQueryDto={}, hasModelAttribute={})",
                parameter.getParameterType().getSimpleName(), hasQueryDto, hasModelAttribute);
        }

        return supports;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) throws Exception {
        log.info("Resolving argument for parameter: {}", parameter.getParameterType().getSimpleName());

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        assert request != null;
        Map<String, String[]> paramMap = request.getParameterMap();

        // Flatten multi-value params to single values
        Map<String, Object> flatParams = new HashMap<>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                flatParams.put(key, values.length == 1 ? values[0] : values);
            }
        }

        log.debug("Request parameters: {}", flatParams);

        Class<?> paramType = parameter.getParameterType();

        // Convert map to JSON string then deserialize to DTO
        // This ensures @JsonProperty annotations are respected
        String json = objectMapper.writeValueAsString(flatParams);
        log.debug("JSON representation: {}", json);

        Object dto = objectMapper.readValue(json, paramType);
        log.debug("Deserialized DTO: {}", dto);

        // Validate the DTO using Spring's DataBinder
        String paramName = parameter.getParameterName() != null ? parameter.getParameterName() : "dto";
        DataBinder binder = binderFactory.createBinder(webRequest, dto, paramName);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            log.warn("Validation errors: {}", binder.getBindingResult().getAllErrors());
            throw new BindException(binder.getBindingResult());
        }

        return dto;
    }
}

