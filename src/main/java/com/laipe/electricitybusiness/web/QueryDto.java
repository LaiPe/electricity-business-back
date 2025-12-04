package com.laipe.electricitybusiness.web;

import java.lang.annotation.*;

/**
 * Annotation to mark method parameters that should be resolved from query parameters to a DTO.
 * This annotation allows automatic mapping of query parameters (in snake_case or camelCase)
 * to DTO fields using Jackson @JsonProperty annotations.
 * <p>
 * Example:
 * <pre>
 * &#64;GetMapping("/search")
 * public ResponseEntity&lt;List&lt;Result&gt;&gt; search(&#64;QueryDto &#64;Valid SearchDTO dto) {
 *     // Query params like radius_in_km will be mapped to dto.radiusInKm
 * }
 * </pre>
 *
 * This annotation is used in conjunction with SnakeCaseModelAttributeArgumentResolver.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryDto {
}

