package cz.cvut.fel.attendance.service.config;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@UtilityClass
public class SecurityEndpoints {
    final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.POST,"/auth/**")
    );

    final RequestMatcher PARENT_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.GET,"/parent/**/training-unit/upcoming"),
            antMatcher(HttpMethod.GET,"/parent/**/children"),
            antMatcher(HttpMethod.GET,"/school"),
            antMatcher(HttpMethod.GET,"/school/**/trainings"),
            antMatcher(HttpMethod.POST, "/parent/**/create-child"),
            antMatcher(HttpMethod.GET, "/parent/**/children/event-status/**"),
            antMatcher(HttpMethod.PATCH, "/child/**/register-to-event/**"),
            antMatcher(HttpMethod.POST, "/event/**/register-child/**"),
            antMatcher(HttpMethod.GET, "/child/**/attendance-detail")
    );

    final RequestMatcher TRAINER_URLS = new OrRequestMatcher(
            // TrainingUnits
            antMatcher(HttpMethod.GET,"/trainer/**/training-unit/upcoming"),
            antMatcher(HttpMethod.GET,"/trainer/**/training-unit/past"),
            antMatcher(HttpMethod.PATCH,"/training-unit/**/description"),
            antMatcher(HttpMethod.GET,"/training-unit/**"),
            // Attendances
            antMatcher(HttpMethod.GET,"/training-unit/**/trainer-attendance"),
            antMatcher(HttpMethod.GET,"/training-unit/**/child-attendance"),
            antMatcher(HttpMethod.PATCH,"/child-attendance/**/mark-present"),
            antMatcher(HttpMethod.PATCH,"/child-attendance/**/mark-absent"),
            antMatcher(HttpMethod.PATCH,"/trainer-attendance/**/mark-present"),
            antMatcher(HttpMethod.PATCH,"/trainer-attendance/**/mark-absent"),
            antMatcher(HttpMethod.GET,"/child-attendance/**/parent-contact"),
            // Report
            antMatcher(HttpMethod.GET,"/trainer/**/report/current-month"),
            // Event
            antMatcher(HttpMethod.GET,"/event/**/registered-children"),
            antMatcher(HttpMethod.PATCH, "/child-event-attendance/**/toggle-attendance")
    );

    final RequestMatcher MULTI_ROLE_URLS = new OrRequestMatcher(
            antMatcher(HttpMethod.GET,"/event"),
            antMatcher(HttpMethod.GET,"/news")
    );
}
