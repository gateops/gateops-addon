# External API PreReceive Hook

**Artifact:** `com.gateops.bitbucket:external-api-hook:1.0.0`

## Description

The External API PreReceive Hook is a Bitbucket Server/Data Center (8.18.x) pre-receive hook plugin that validates incoming push events against an external API. If the API response does not meet the defined criteria, the push is rejected.

## Features

* **HTTP API Call:** Sends a configurable GET request before accepting a push.
* **Branch Protection:** Define branch patterns (e.g., `^(main|release/.*)$`) to which the hook applies.
* **Admin UI:** Configure via Bitbucket Administration panel:

    * **Enabled:** Toggle hook on/off
    * **API URL:** Endpoint to call (e.g., `https://api.example.com/validate`)
    * **Branches:** Regex for protected branches
* **Settings Validation:** Ensures valid API URL and regex through `ExternalApiHookValidator`.
* **Dynamic Form:** Velocity (`.vm`) and Soy (`.soy`) templates for the configuration UI.

## Requirements

* Java 11 or higher
* Atlassian Plugin SDK 8.x
* Bitbucket Server/Data Center 8.18.x

## Build & Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-org/external-api-hook.git
   cd external-api-hook
   ```
2. Build the plugin:

   ```bash
   atlas-mvn clean package
   ```
3. Deploy the JAR to Bitbucket’s plugin directory:

   ```bash
   kubectl exec -it -n bitbucket bitbucket-0 -- rm /var/atlassian/application-data/bitbucket/shared/plugins/installed-plugins/external-api-hook-1.0.0.jar   
   kubectl cp -n bitbucket external-api-hook-1.0.0.jar bitbucket-0:/var/atlassian/application-data/bitbucket/shared/plugins/installed-plugins/    
   ```
4. Restart Bitbucket:

   ```bash
   kubectl delete pod bitbucket-0 -n bitbucket
   ```
5. Port Routing:

   ```bash
   kubectl port-forward svc/bitbucket 7990:7990 -n bitbucket 
   ```

6. Logging Port:

   ```bash
   kubectl logs -f bitbucket-0 -n bitbucket
   ```

## Kubernetes Deployment

If your Bitbucket instance runs in Kubernetes, use the following commands to update the plugin without downtime:

1. **Remove the existing plugin JAR from the running pod**

   ```bash
   kubectl exec -n <namespace> <pod-name> -- rm /var/atlassian/application-data/bitbucket/shared/plugins/installed-plugins/external-api-hook-1.0.0.jar
   ```
2. **Copy the new JAR into the pod**

   ```bash
   kubectl cp target/external-api-hook-1.0.0.jar \
     <namespace>/<pod-name>:/var/atlassian/application-data/bitbucket/shared/plugins/installed-plugins/external-api-hook-1.0.0.jar
   ```
3. **Restart the Bitbucket pod**

    * Via rollout restart (for StatefulSets):

      ```bash
      kubectl rollout restart statefulset/<statefulset-name> -n <namespace>
      ```
    * Or delete the pod to let the controller recreate it:

      ```bash
      kubectl delete pod <pod-name> -n <namespace>
      ```

## Configuration

In Bitbucket Administration → Hooks → External API Hook, configure the following:

* **Enabled:** Activate or deactivate the hook
* **API URL:** The HTTP endpoint to call
* **Branches:** Regular expression defining which branches to protect

## Usage

1. Configure the hook settings.
2. Attempt to push to a protected branch.
3. The plugin sends a request to the API; if validation fails, the push is blocked.

## Development

* **Templates:** Located under `src/main/resources/templates` (`.soy` & `.vm`).
* **Hook Logic:** `src/main/java/com/gateops/bitbucket/hook/ExternalApiPreReceiveHook.java`.
* **Settings Validator:** `src/main/java/com/gateops/bitbucket/validator/ExternalApiHookValidator.java`.
* **To add new fields:**

    1. Update the Soy template with new inputs.
    2. Edit `atlassian-plugin.xml` to include web-resource and transformer entries.
    3. Read and handle the new settings in Java code.

## Troubleshooting

* **Configuration form not rendering:** Ensure `.soy.js` files are generated and packaged (check your Maven plugin config).
* **Build extension errors:** Use `atlas-mvn` or verify your SDK repository settings.
* **Regex validation:** Test your branch patterns with a Java regex tester.

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/xyz`).
3. Commit your changes and open a pull request.

## License

MIT © 2025 GateOps
