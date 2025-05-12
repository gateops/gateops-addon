package com.gateops.bitbucket.validator;

import com.atlassian.bitbucket.scope.Scope;
import com.atlassian.bitbucket.setting.Settings;
import com.atlassian.bitbucket.setting.SettingsValidationErrors;
import com.atlassian.bitbucket.setting.SettingsValidator;

import javax.annotation.Nonnull;

public class ExternalApiHookValidator implements SettingsValidator {

    @Override
    public void validate(@Nonnull Settings settings, @Nonnull SettingsValidationErrors errors, @Nonnull Scope scope) {
        String apiUrl = settings.getString("apiUrl", "");
        String branches = settings.getString("branches", "");

        if (apiUrl.isEmpty()) {
            errors.addFieldError("apiUrl", "API URL is required.");
        }
        if (branches.isEmpty()) {
            errors.addFieldError("branches", "At least one branch must be specified.");
        }
    }
}