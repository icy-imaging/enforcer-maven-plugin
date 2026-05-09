/*
 * Copyright (c) 2010-2026. Institut Pasteur.
 *
 * This file is part of Icy.
 * Icy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Icy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Icy. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.icy.maven.plugin.enforcer;

import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.regex.Pattern;

@Named("validateVersion")
public class ValidateVersion extends AbstractEnforcerRule {
    @Inject
    MavenProject project;

    /**
     * Enforces a valid version number format for the Maven project.
     * A valid version must match one of the following patterns:
     * - Stable release: X.X.X
     * - Alpha release: X.X.X-a.X
     * - Beta release: X.X.X-b.X
     * - Release candidate: X.X.X-rc.X
     * Optionally, the version can also have a suffix of "-SNAPSHOT" for snapshot builds.
     * <p>
     * If the version does not conform to any of the above patterns, an error is logged, and an
     * {@link EnforcerRuleException} is thrown. Additionally, a warning is logged if the version ends with "-SNAPSHOT".
     *
     * @throws EnforcerRuleException if the project's version does not match a valid format.
     */
    @Override
    public void execute() throws EnforcerRuleException {
        final String version = project.getVersion();
        final Pattern pattern = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-(a|b|rc)\\.\\d+)?(-SNAPSHOT)?$");
        if (!pattern.matcher(version).matches()) {
            getLog().error("Invalid version number: " + version);
            getLog().error("The version number should match one of the patterns below:");
            getLog().error("- Stable release: X.X.X");
            getLog().error("- Alpha release: X.X.X-a.X");
            getLog().error("- Beta release: X.X.X-b.X");
            getLog().error("- Release candidate: X.X.X-rc.X");
            throw new EnforcerRuleException("Invalid version number");
        }

        if (version.endsWith("-SNAPSHOT")) {
            getLog().warn("The version number ends with '-SNAPSHOT'. This is not recommended for production use.");
        }
    }
}
