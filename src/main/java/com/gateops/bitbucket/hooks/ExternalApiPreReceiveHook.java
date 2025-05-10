package com.gateops.bitbucket.hooks;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;
import com.atlassian.bitbucket.setting.Settings;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

public class ExternalApiPreReceiveHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryPushHookRequest request) {
        Settings settings = context.getSettings();
        String apiUrl = settings.getString("apiUrl", "");
        String branchesConfig = settings.getString("branches", "");
        if (apiUrl.isBlank() || branchesConfig.isBlank()) {
            return RepositoryHookResult.accepted();
        }

        String[] branches = branchesConfig.split("[,\s]+");
        boolean shouldCallExternal = request.getRefChanges().stream()
            .map(rc -> rc.getRef().getDisplayId())
            .anyMatch(b -> java.util.Arrays.asList(branches).contains(b));
        if (!shouldCallExternal) {
            return RepositoryHookResult.accepted();
        }

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForEntity(apiUrl, Void.class);
            return RepositoryHookResult.accepted();
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 400) {
                return RepositoryHookResult.rejected(
                        "External API returned 400",
                        "Push rejected because external system returned HTTP 400."
                );
            }
            return RepositoryHookResult.rejected(
                    "External API error",
                    "Push rejected: HTTP " + e.getRawStatusCode()
            );
        } catch (RestClientException e) {
            return RepositoryHookResult.rejected(
                    "External API call failed",
                    "Push rejected: " + e.getMessage()
            );
        }
    }
}