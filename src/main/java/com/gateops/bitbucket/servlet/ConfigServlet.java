package com.gateops.bitbucket.servlet;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Named("externalApiHookConfigServlet")
public class ConfigServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PluginSettingsFactory settingsFactory;
    private final RepositoryService repositoryService;
    private final TemplateRenderer templateRenderer;

    public ConfigServlet(@ComponentImport PluginSettingsFactory settingsFactory,
                         @ComponentImport RepositoryService repositoryService,
                         @ComponentImport TemplateRenderer templateRenderer) {
        this.settingsFactory   = settingsFactory;
        this.repositoryService = repositoryService;
        this.templateRenderer  = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String repoIdParam = req.getParameter("repoId");
        if (repoIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing repoId");
            return;
        }
        int repoId = Integer.parseInt(repoIdParam);
        Repository repo = repositoryService.getById(repoId);

        // settings key: plugin-key + ":" + repoId
        PluginSettings settings = settingsFactory
                .createSettingsForKey("com.gateops.bitbucket.external-api-hook:" + repoId);

        Map<String, Object> context = new HashMap<>();
        context.put("repository", repo);
        context.put("enabled", settings.get("enabled"));
        context.put("apiUrl", settings.get("apiUrl"));
        context.put("branches", settings.get("branches"));

        resp.setContentType("text/html;charset=UTF-8");
        templateRenderer.render("templates/config.vm", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String repoIdParam = req.getParameter("repoId");
        if (repoIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing repoId");
            return;
        }
        long repoId = Long.parseLong(repoIdParam);

        PluginSettings settings = settingsFactory
                .createSettingsForKey("com.gateops.bitbucket.external-api-hook:" + repoId);

        // form alanlarını kaydet
        settings.put("enabled", req.getParameter("enabled") != null ? "true" : "false");
        settings.put("apiUrl", req.getParameter("apiUrl"));
        settings.put("branches", req.getParameter("branches"));

        // geri config sayfasına yönlendir
        resp.sendRedirect(req.getContextPath()
                + "/plugins/servlet/external-api-hook/config?repoId=" + repoId);
    }
}
