package com.gateops.bitbucket.hooks;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;
import com.atlassian.bitbucket.setting.Settings;

import javax.annotation.Nonnull;

public class ExternalApiPreReceiveHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryPushHookRequest request) {
        Settings settings = context.getSettings();
        String apiUrl = settings.getString("apiUrl", "");
        String branchesConfig = settings.getString("branches", "");
        if (apiUrl.isEmpty() || branchesConfig.isEmpty()) {
            return RepositoryHookResult.accepted();
        }

        return RepositoryHookResult.rejected(
                "External API call failed",
                "Push rejected: ");

    }
}