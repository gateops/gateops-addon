package com.gateops.bitbucket.hooks;

import com.atlassian.bitbucket.hook.repository.PreRepositoryHook;
import com.atlassian.bitbucket.hook.repository.PreRepositoryHookContext;
import com.atlassian.bitbucket.hook.repository.RepositoryHookResult;
import com.atlassian.bitbucket.hook.repository.RepositoryPushHookRequest;

import javax.annotation.Nonnull;

public class ExternalApiPreReceiveHook implements PreRepositoryHook<RepositoryPushHookRequest> {

    @Nonnull
    @Override
    public RepositoryHookResult preUpdate(@Nonnull PreRepositoryHookContext context,
                                          @Nonnull RepositoryPushHookRequest request) {

        return RepositoryHookResult.rejected("External API call failed", "Push rejected: ");

    }
}